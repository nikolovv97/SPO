package com.fmi.spo.chatserver;

import java.io.Serializable;

public abstract class ServerMessageWrapper implements Serializable {

	protected String message;

	public ServerMessageWrapper(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}
}
