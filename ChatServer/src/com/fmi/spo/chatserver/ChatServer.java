package com.fmi.spo.chatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import com.fmi.spo.messages.ChatUserMessageWrapper;
import com.fmi.spo.messages.ServerMessageWrapper;
import com.fmi.spo.messages.ServerResponseWrapper;

public class ChatServer {
	private final static Logger log = Logger.getLogger(ChatServer.class.getName());

	private static int counter = 0;

	private int serverPort;
	private ServerSocket serverSocket;
	private Hashtable<String, ClientHandler> users;

	public ChatServer(int serverPort) throws IOException {
		this.serverPort = serverPort;
		this.serverSocket = new ServerSocket(this.serverPort);
		this.users = new Hashtable<>();
	}

	public void start() {
		log.info("Server started");

		while (true) {
			try {
				Socket client = this.serverSocket.accept();
				++counter;
				ClientHandler newUser = new ClientHandler(client, this);
				Thread newUserThread = new Thread(newUser);
				newUserThread.start();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public void stop() throws IOException {
		serverSocket.close();
	}

	public boolean registerUser(String username, ClientHandler user) {
		if (this.users.get(username) != null) {
			return false;
		}
		users.put(username, user);

		log.info("User " + username + " registered");
		return true;
	}

	public boolean sendMessageToUser(String username, ServerMessageWrapper message) {
		if (this.users.get(username) == null) {
			return false;
		}

		log.info(message.toString());

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

			log.info(message.toString());
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

		ServerResponseWrapper userDisconnectedMessage = new ServerResponseWrapper("User" + username + "disconnected",
				404);
		for (ClientHandler user : this.users.values()) {
			user.writeMessage(userDisconnectedMessage);
		}
	}

	public static void main(String[] args) throws IOException {
		int serverPort = 8080;

		ChatServer server = new ChatServer(serverPort);

		server.start();

	}
}
