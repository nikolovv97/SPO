package com.fmi.spo.client;

import java.io.Serializable;

public class MessageReceiveWrapper implements Serializable {

	private int statusCode;
	private String message;

	public MessageReceiveWrapper(String message, int statusCode) {
		this.message = message;
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return this.message;
	}

	public int getStatusCode() {
		return this.statusCode;
	}

	@Override
	public String toString() {
		return String.format("%d %s %s %s", this.statusCode, this.statusCode == 200 ? "ok" : "error", this.message);
	}
}
