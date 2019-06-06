package com.fmi.spo.chatclient;

import java.io.ObjectInputStream;
import java.util.logging.Logger;

public class ServerListener implements Runnable {
	private final static Logger log = Logger.getLogger(ChatClient.class.getName());

	private ObjectInputStream inputStream;

	public ServerListener(ObjectInputStream inputStream) {
		this.inputStream = inputStream;
	}

	@Override
	public void run() {
		while (true) {
			try {
				ServerMessageWrapper message = (ServerMessageWrapper) inputStream.readObject();
				log.info(message.toString());

			} catch (Exception e) {
				log.info("Error receiving response from server! " + e);
			}
		}
	}

}
