package com.fmi.spo.chatclient;

public class ServerResponseWrapper extends ServerMessageWrapper {

	private int statusCode;

	public ServerResponseWrapper(String message, int statusCode) {
		super(message);
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return this.statusCode;
	}

	@Override
	public String toString() {
		return String.format("Server responded with: %d %s", this.statusCode, this.message);
	}
}
