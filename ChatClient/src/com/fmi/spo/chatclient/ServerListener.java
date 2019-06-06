package com.fmi.spo.chatclient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Logger;

import com.fmi.spo.messages.ChatUserMessageWrapper;
import com.fmi.spo.messages.ServerResponseWrapper;

public class ServerListener implements Runnable {
	private ObjectInputStream inputStream;

	public ServerListener(Socket socket) throws IOException {
		this.inputStream = new ObjectInputStream(socket.getInputStream());
	}

	@Override
	public void run() {
		while (true) {
			try {
				Object message = inputStream.readObject();
				if (message instanceof ServerResponseWrapper) {
					message = (ServerResponseWrapper) message;
				} else {
					message = (ChatUserMessageWrapper) message;
				}
				System.out.println(message.toString());

			} catch (Exception e) {
				System.out.println("Error receiving response from server! " + e);
			}
		}
	}

}
