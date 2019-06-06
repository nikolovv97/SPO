package com.fmi.spo.chatclient;

public class ChatUserMessageWrapper extends ServerMessageWrapper {
	private String username;

	public ChatUserMessageWrapper(String username, String message) {
		super(message);
		this.username = username;
	}

	@Override
	public String toString() {
		return String.format("%s: %s", this.username, this.message);
	}

}
