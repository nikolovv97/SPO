package com.fmi.spo.messages;

public class ChatUserMessageWrapper extends ServerMessageWrapper {
	private static final long serialVersionUID = -8813666367590828440L;

	private String username;
	private boolean isPrivate;

	public ChatUserMessageWrapper(String username, String message, boolean isPrivate) {
		super(message);
		this.username = username;
		this.isPrivate = isPrivate;
	}

	public String getUsername() {
		return this.username;
	}

	public boolean isPrivate() {
		return this.isPrivate;
	}

	@Override
	public String toString() {
		return String.format("%s%s: %s", this.isPrivate ? "Private message from " : "", this.username, this.message);
	}

}
