package com.fmi.spo.chatclient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import com.fmi.spo.messages.ChatUserMessageWrapper;
import com.fmi.spo.messages.ServerResponseWrapper;

public class ServerListener implements Runnable {
	private ObjectInputStream inputStream;

	public ServerListener(ObjectInputStream inputStream) throws IOException {
		this.inputStream = inputStream;
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

			} catch (IOException e) {
				System.out.println("Disconnected");
				break;
			} catch (Exception e) {
				System.out.println("Error receiving response from server! " + e);
			}
		}
	}

}
