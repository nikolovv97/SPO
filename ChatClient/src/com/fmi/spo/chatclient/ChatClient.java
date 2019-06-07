package com.fmi.spo.chatclient;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import com.fmi.spo.messages.ClientMessageWrapper;

public class ChatClient {
	private ObjectOutputStream sOutput;
	private ObjectInputStream sInput;
	private Socket socket;

	private String serverIp;
	private int serverPort;
	private Thread serverListener;

	public ChatClient(String serverIp, int serverPort) {
		this.serverIp = serverIp;
		this.serverPort = serverPort;
	}

	public ChatClient() {
		// default ip and port
		this.serverIp = "localhost";
		this.serverPort = 8080;
	}

	public boolean connect() {
		try {
			Socket socket = new Socket(this.serverIp, this.serverPort);
			System.out.println(String.format("Connected to %s:%d", this.serverIp, this.serverPort));

			sOutput = new ObjectOutputStream(socket.getOutputStream());
			sInput = new ObjectInputStream(socket.getInputStream());

			serverListener = new Thread(new ServerListener(sInput));
			serverListener.start();

			Thread.sleep(1000);
			return serverListener.isAlive();
		} catch (Exception e) {
			System.out.println("Disconnecting from server" + e);
			return false;
		}
	}

	public void sendMessage(String command, String username, String message) throws Exception {
		try {
			ClientMessageWrapper wrappedMessage = new ClientMessageWrapper(command, username, message);
			sOutput.writeObject(wrappedMessage);
		} catch (Exception e) {
			System.out.println("Error!Could not send message to the server. ");
			throw e;
		}
	}

	public static void main(String[] args) {
		int serverPort = 8080;
		String serverIP = "localhost";

		for (int i = 0; i < args.length - 1; i++) {
			if (args[i].equals("-serverIP")) {
				serverIP = args[i + 1];
			} else if (args[i].equals("-serverPort")) {
				serverPort = Integer.parseInt(args[i + 1]);
			}
		}

		ChatClient client = new ChatClient(serverIP, serverPort);
		if (!client.connect()) {
			return;
		}

		System.out.println("Enter your username:");
		Scanner scan = new Scanner(System.in);
		String username = scan.nextLine();
		try {
			client.sendMessage("user", username, null);

			while (true) {
				String command = scan.nextLine();
				String[] commandArgs = command.split(" ", 3);
				if (commandArgs.length == 1) {
					client.sendMessage(commandArgs[0], null, null);
					if (commandArgs[0].equals("bye")) {
						break;
					}
				} else if (commandArgs.length >= 2 && commandArgs[0].equals("send_all")) {
					client.sendMessage(commandArgs[0], null, command.split(" ", 2)[1]);
				} else if (commandArgs.length == 2 && commandArgs[0].equals("user")) {
					client.sendMessage(commandArgs[0], commandArgs[1], null);
				} else if (commandArgs.length == 3 && commandArgs[0].equals("send_to")) {
					client.sendMessage(commandArgs[0], commandArgs[1], commandArgs[2]);
				} else {
					System.out.println("Invalid input command");
				}
			}
		} catch (Exception e) {
			System.out.println("Disconnecting from server. " + e);
			return;
		}
	}
}