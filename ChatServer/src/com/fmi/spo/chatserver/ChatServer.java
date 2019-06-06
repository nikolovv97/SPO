package com.fmi.spo.chatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

public class ChatServer {
	private final static Logger log = Logger.getLogger(ChatServer.class.getName());

	private int serverPort;
	private ServerSocket serverSocket;
	private Hashtable<String, ClientHandler> users;

	public ChatServer(int serverPort) throws IOException {
		this.serverPort = serverPort;
		this.serverSocket = new ServerSocket(this.serverPort);
		this.users = new Hashtable<>();
	}

	public void start() {
		while (true) {
			try {
				Socket client = this.serverSocket.accept();
			} catch (Exception e) {

			}

		}
	}

	public boolean registerUser(String username, ClientHandler user) {
		if (this.users.get(username) != null) {
			return false;
		}
		users.put(username, user);
		return true;

	}

	public boolean sendMessageToUser(String username, ServerMessageWrapper message) {
		if (this.users.get(username) == null) {
			return false;
		}

		this.users.get(username).writeMessage(message);
		return true;
	}

	public boolean sendAll(ChatUserMessageWrapper message) {
		try {
			for (ClientHandler user : this.users.values()) {
				user.writeMessage(message);
			}
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
	}

	public static void main(String[] args) throws IOException {
		int serverPort = 8080;

		ChatServer server = new ChatServer(serverPort);

	}
}
