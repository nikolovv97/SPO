package com.fmi.spo.chatserver;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.stream.Collectors;

import com.fmi.spo.messages.ChatUserMessageWrapper;
import com.fmi.spo.messages.ClientMessageWrapper;
import com.fmi.spo.messages.ServerMessageWrapper;
import com.fmi.spo.messages.ServerResponseWrapper;

public class ClientHandler extends Thread {
	private static int counter = 0;

	private Socket socket;
	private ChatServer server;
	private ObjectInputStream clientInput;
	private ObjectOutputStream clientOutput;
	private boolean isRegistered;
	private String username;

	public ClientHandler(Socket socket, ChatServer server) throws IOException {
		this.socket = socket;
		this.server = server;
		this.clientOutput = new ObjectOutputStream(this.socket.getOutputStream());
		this.clientInput = new ObjectInputStream(this.socket.getInputStream());
		this.username = "Anonymous" + counter;
		++counter;
		this.isRegistered = false;

	}

	public boolean isRegistered() {
		return this.isRegistered;
	}

	public String getUsername() {
		return this.username;
	}

	@Override
	public void run() {
		while (true) {
			try {
				ClientMessageWrapper receivedMessage = (ClientMessageWrapper) this.clientInput.readObject();
				ServerResponseWrapper serverResponse = handleMessage(receivedMessage);
				writeMessage(serverResponse);
				if (serverResponse.getMessage().startsWith("Goodbye")) {
					this.socket.close();
					break;
				}
			} catch (Exception e) {
				System.out.println("User " + this.username + " disconnected");
				this.server.disconnectUser(this.username);
				break;
			}
		}
	}

	public synchronized void writeMessage(ServerMessageWrapper message) {
		try {
			this.clientOutput.writeObject(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ServerResponseWrapper handleMessage(ClientMessageWrapper receivedMessage) {
		switch (receivedMessage.getCommand()) {
		case "user":
			return registerUser(receivedMessage.getToUser());
		case "send_to":
			return sendMessageToUser(receivedMessage.getToUser(), receivedMessage.getMessage());
		case "send_all":
			return sendToAll(receivedMessage.getMessage());
		case "list":
			return getRegisteredUsers();
		case "send_file_to":
			return null;
		case "bye":
			return disconnectUser();
		default:
			return new ServerResponseWrapper("Unknown command", 400);
		}
	}

	private ServerResponseWrapper registerUser(String username) {
		if (username == null || username.isEmpty()) {
			return new ServerResponseWrapper("Error! Username is empty", 400);
		} else if (this.isRegistered) {
			return new ServerResponseWrapper(
					"Error! You are already registered with username \"" + this.username + "\"", 400);
		} else if (this.server.registerUser(username, this)) {
			this.isRegistered = true;
			this.username = username;

			return new ServerResponseWrapper("User " + username + " successfully registered", 200);
		} else {
			return new ServerResponseWrapper("Error! Username " + username + " already taken!", 100);
		}
	}

	private ServerResponseWrapper sendMessageToUser(String toUser, String message) {
		if (!this.isRegistered) {
			return new ServerResponseWrapper("Error!Unregistered clients cant send messages", 403);
		} else if (username == null || username.isEmpty()) {
			return new ServerResponseWrapper("Error! Username is empty", 400);
		} else if (message == null || message.isEmpty()) {
			return new ServerResponseWrapper("Error! Message is empty", 400);
		}

		return this.server.sendMessageToUser(toUser, new ChatUserMessageWrapper(this.username, message, true))
				? new ServerResponseWrapper("Ok! Message to " + toUser + " sent successfully", 200)
				: new ServerResponseWrapper("Error! User " + toUser + " not found", 404);
	}

	private ServerResponseWrapper sendToAll(String message) {
		if (!this.isRegistered) {
			return new ServerResponseWrapper("Error!Unregistered clients cant send messages", 403);
		} else if (message == null || message.isEmpty()) {
			return new ServerResponseWrapper("Error! Message is empty", 400);
		}

		return this.server.sendAll(new ChatUserMessageWrapper(this.username, message, false))
				? new ServerResponseWrapper("Ok message to all sent successfully", 200)
				: new ServerResponseWrapper("Internal server error!", 500);
	}

	private ServerResponseWrapper getRegisteredUsers() {
		if (!this.isRegistered) {
			return new ServerResponseWrapper("Error! Unregistered clients cant get list of users", 403);
		}

		String users = this.server.getAllUsers().stream().filter(user -> user.isRegistered())
				.map(user -> user.getUsername()).collect(Collectors.joining(","));

		return new ServerResponseWrapper(users, 200);
	}

	private ServerResponseWrapper disconnectUser() {
		this.server.disconnectUser(this.username);
		System.out.println("User " + this.username + " has disconnected");
		return new ServerResponseWrapper("Goodbye " + this.username + "!", 200);
	}

}
