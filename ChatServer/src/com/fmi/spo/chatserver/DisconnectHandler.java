package com.fmi.spo.chatserver;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.fmi.spo.messages.ServerResponseWrapper;

public class DisconnectHandler implements Runnable {
	private Socket client;

	public DisconnectHandler(Socket client) {
		this.client = client;
	}

	@Override
	public void run() {
		try {
			ObjectOutputStream oStream = new ObjectOutputStream(client.getOutputStream());
			ObjectInputStream iStream = new ObjectInputStream(client.getInputStream());

			oStream.writeObject(new ServerResponseWrapper(
					"There is no room in the chat server for more people.Sorry, try again later", 500));

			System.out.println("A user tried to connect, but there is no more space for people");
			Thread.sleep(1000);
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
