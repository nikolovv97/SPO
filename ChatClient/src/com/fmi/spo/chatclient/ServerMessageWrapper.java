package com.fmi.spo.chatclient;

import java.io.Serializable;

public abstract class ServerMessageWrapper implements Serializable {

	protected String message;

	protected ServerMessageWrapper(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}
}
