package com.fmi.spo.chatclient;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;

public class ChatClient {
	private final static Logger log = Logger.getLogger(ChatClient.class.getName());

	private ObjectInputStream sInput;
	private ObjectOutputStream sOutput;
	private Socket socket;

	private String serverIp;
	private int serverPort;
	private String username;

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
			sInput = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());

			Thread t = new Thread(new ServerListener(sInput));
			t.start();
			
		} catch (Exception e) {
			log.info("Error!Could not connect to server " + e);
			return false;
		}

		return true;
	}

	public void sendMessage(String command, String message) {
		try {
			ClientMessageWrapper wrappedMessage = new ClientMessageWrapper(command, message);

			sOutput.writeObject(wrappedMessage);
		} catch (Exception e) {
			log.info("Could not send message to server! " + e);
		}

	}

	public static void main(String[] args) {
		int portNumber = 8080;
		String serverAddress = "localhost";

		Scanner scan = new Scanner(System.in);

		String username = scan.nextLine();

		ChatClient client = new ChatClient(serverAddress, portNumber, username);

		if (!client.connect()) {
			return;
		}
		client.sendMessage("user", username);

		while (true) {
			String command = scan.next();
			String message = scan.nextLine();

			client.sendMessage(command, message);
		}
	}
}