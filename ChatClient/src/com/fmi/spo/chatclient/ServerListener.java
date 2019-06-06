package com.fmi.spo.chatclient;

import java.io.ObjectInputStream;
import java.util.logging.Logger;

import com.fmi.spo.messages.ChatUserMessageWrapper;
import com.fmi.spo.messages.ServerResponseWrapper;

public class ServerListener implements Runnable {
	private final static Logger log = Logger.getLogger(ServerListener.class.getName());

	private ObjectInputStream inputStream;

	public ServerListener(ObjectInputStream inputStream) {
		this.inputStream = inputStream;
	}

	@Override
	public void run() {
		System.out.println("Connected to the server");
		log.info("Connected to the server");
		while (true) {
			try {
				Object message = inputStream.readObject();
				if (message instanceof ServerResponseWrapper) {
					message = (ServerResponseWrapper) message;
				} else {
					message = (ChatUserMessageWrapper) message;
				}
				log.info(message.toString());

			} catch (Exception e) {
				log.info("Error receiving response from server! " + e);
			}
		}
	}

}
