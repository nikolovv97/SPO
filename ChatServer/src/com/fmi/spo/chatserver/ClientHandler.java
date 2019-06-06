package com.fmi.spo.chatserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

import com.fmi.spo.chatserver.ClientMessageWrapper.Command;

public class ClientHandler implements Runnable {
	private Socket socket;
	private ChatServer server;
	private ObjectInputStream clientInput;
	private PrintWriter writer;
	private boolean isRegistered;
	private String username;

	public ClientHandler(Socket socket, ChatServer server, String username) throws IOException {
		this.socket = socket;
		this.server = server;
		this.clientInput = (ObjectInputStream) this.socket.getInputStream();
		this.writer = new PrintWriter((ObjectOutputStream) this.socket.getOutputStream());
		this.username = username;
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
				writeMessage(new ServerResponseWrapper("Server could not handle the command", 500));
				e.printStackTrace();
			}
		}
	}

	public synchronized void writeMessage(ServerMessageWrapper message) {
		this.writer.print(message);
	}

	private ServerResponseWrapper handleMessage(ClientMessageWrapper receivedMessage) {
		switch (receivedMessage.getCommand()) {
		case "user":
			return registerUser(receivedMessage.getMessage());
		case "send_to":
			return sendMessageToUser(receivedMessage.getMessage());
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
		if (this.server.registerUser(username, this)) {
			this.isRegistered = true;
			this.username = username;

			return new ServerResponseWrapper("User {} successfully registered" + username, 200);
		} else {
			return new ServerResponseWrapper("Error! Username {} already taken!" + username, 100);
		}
	}

	private ServerResponseWrapper sendMessageToUser(String message) {
		if (!this.isRegistered) {
			return new ServerResponseWrapper("Error!Unregistered clients cant send messages", 403);
		}
		return this.server.sendMessageToUser(username, new ChatUserMessageWrapper(this.username, message))
				? new ServerResponseWrapper("Ok! Message to {} sent successfully" + username, 200)
				: new ServerResponseWrapper("Error! User {} not found" + username, 404);
	}

	private ServerResponseWrapper sendToAll(String message) {
		if (!this.isRegistered) {
			return new ServerResponseWrapper("Error!Unregistered clients cant send messages", 403);
		}

		return this.server.sendAll(new ChatUserMessageWrapper(this.username, message))
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

		return new ServerResponseWrapper("Goodbye {}!" + this.username, 200);
	}

}
