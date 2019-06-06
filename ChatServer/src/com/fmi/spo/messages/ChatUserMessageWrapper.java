package com.fmi.spo.messages;

public class ChatUserMessageWrapper extends ServerMessageWrapper {
	private static final long serialVersionUID = -8813666367590828440L;

	private String username;

	public ChatUserMessageWrapper(String username, String message) {
		super(message);
		this.username = username;
	}

	public String getUsername() {
		return this.username;
	}

	@Override
	public String toString() {
		return String.format("%s: %s", this.username, this.message);
	}

}
