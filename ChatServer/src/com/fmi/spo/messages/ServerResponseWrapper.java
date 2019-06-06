package com.fmi.spo.messages;

public class ServerResponseWrapper extends ServerMessageWrapper {

	private static final long serialVersionUID = -188065013687422130L;

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
