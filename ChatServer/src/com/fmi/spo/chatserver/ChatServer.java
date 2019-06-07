package com.fmi.spo.chatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.fmi.spo.messages.ChatUserMessageWrapper;
import com.fmi.spo.messages.ServerMessageWrapper;
import com.fmi.spo.messages.ServerResponseWrapper;

public class ChatServer {
	private int serverPort;
	private ServerSocket serverSocket;
	private Hashtable<String, ClientHandler> users;
	private final int MAX_NUM_USERS;

	public ChatServer(int serverPort, int MAX_NUM_USERS) throws IOException {
		this.serverPort = serverPort;
		this.serverSocket = new ServerSocket(this.serverPort);
		this.users = new Hashtable<>();
		this.MAX_NUM_USERS = MAX_NUM_USERS;
	}

	public void start() {
		System.out.println("Server started");
		while (true) {
			try {
				Socket client = this.serverSocket.accept();
				if (users.size() >= this.MAX_NUM_USERS) {
					new Thread(new DisconnectHandler(client)).start();
				} else {
					ClientHandler newUser = new ClientHandler(client, this);
					newUser.start();
					users.put(newUser.getUsername(), newUser);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public void stop() throws IOException {
		serverSocket.close();
	}

	public boolean registerUser(String username, ClientHandler newUser) {
		if (this.users.get(username) != null) {
			return false;
		}
		users.put(username, newUser);
		users.remove(newUser.getUsername());
		System.out.println("User " + username + " registered");
		for (ClientHandler user : this.users.values()) {
			if (user.isRegistered() && !user.getUsername().equals(username)) {
				user.writeMessage(new ServerResponseWrapper("User " + username + " has registered", 200));
			}
		}
		return true;
	}

	public boolean sendMessageToUser(String username, ServerMessageWrapper message) {
		if (this.users.get(username) == null) {
			return false;
		}

		System.out.println(message.toString());

		this.users.get(username).writeMessage(message);
		return true;
	}

	public boolean sendAll(ChatUserMessageWrapper message) {
		try {
			for (ClientHandler user : this.users.values()) {
				if (!user.getUsername().equals(message.getUsername())) {
					user.writeMessage(message);
				}
			}

			System.out.println(message.toString());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<ClientHandler> getAllUsers() {
		return new ArrayList<>(this.users.values());
	}

	public void disconnectUser(String username) {
		this.users.remove(username);

		ServerResponseWrapper userDisconnectedMessage = new ServerResponseWrapper("User " + username + " disconnected",
				404);
		for (ClientHandler user : this.users.values()) {
			user.writeMessage(userDisconnectedMessage);
		}
	}

	public static void main(String[] args) throws IOException {
		int serverPort = 8080;
		int MAX_NUM_USERS = 2;

		for (int i = 0; i < args.length - 1; i++) {
			if (args[i].equals("-serverPort")) {
				serverPort = Integer.parseInt(args[i + 1]);
			} else if (args[i].equals("-num_users")) {
				MAX_NUM_USERS = Integer.parseInt(args[i + 1]);
			}
		}

		ChatServer server = new ChatServer(serverPort, MAX_NUM_USERS);

		server.start();
	}
}
