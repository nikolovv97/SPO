package com.fmi.spo.messages;

import java.io.Serializable;

public abstract class ServerMessageWrapper implements Serializable {
	private static final long serialVersionUID = -4142546586810067051L;

	protected String message;

	protected ServerMessageWrapper(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}
}
