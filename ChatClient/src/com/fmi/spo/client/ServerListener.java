package com.fmi.spo.client;

import java.io.ObjectInputStream;
import java.util.logging.Logger;

public class ServerListener implements Runnable {
	private final static Logger log = Logger.getLogger(Client.class.getName());

	private ObjectInputStream inputStream;

	public ServerListener(ObjectInputStream inputStream) {
		this.inputStream = inputStream;
	}

	@Override
	public void run() {
		while (true) {
			try {
				MessageReceiveWrapper message = (MessageReceiveWrapper) inputStream.readObject();
				log.info(message.toString());

			} catch (Exception e) {
				log.info("Error receiving response from server! " + e);
			}
		}
	}

}
