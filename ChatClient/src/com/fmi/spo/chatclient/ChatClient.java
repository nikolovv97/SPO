package com.fmi.spo.chatclient;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;

import com.fmi.spo.messages.ClientMessageWrapper;

public class ChatClient {
	private ObjectOutputStream sOutput;
	private Socket socket;

	private String serverIp;
	private int serverPort;
	private String username;
	private Thread serverListener;

	public ChatClient(String serverIp, int serverPort, String username) {
		this.serverIp = serverIp;
		this.serverPort = serverPort;
		this.username = username;
	}

	public ChatClient() {
		// default ip and port
		this.serverIp = "localhost";
		this.serverPort = 8080;
		this.username = "unregistered";
	}

	public boolean connect() {
		try {
			Socket socket = new Socket(serverIp, serverPort);

			sOutput = new ObjectOutputStream(socket.getOutputStream());

			serverListener = new Thread(new ServerListener(socket));
			serverListener.start();

		} catch (Exception e) {
			System.out.println("Error!Could not connect to server " + e);
			return false;
		}

		return true;
	}

	public void sendMessage(String command, String username, String message) {
		try {
			ClientMessageWrapper wrappedMessage = new ClientMessageWrapper(command, username, message);
			sOutput.writeObject(wrappedMessage);
		} catch (Exception e) {
			System.out.println("Could not send message to server! " + e);
		}

	}

	public static void main(String[] args) {
		int portNumber = 8080;
		String serverAddress = "localhost";

		Scanner scan = new Scanner(System.in);
		System.out.println("Enter username:");
		String username = scan.next();

		ChatClient client = new ChatClient(serverAddress, portNumber, username);

		if (!client.connect()) {
			return;
		}

		System.out.println("Connected");

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
	}
}