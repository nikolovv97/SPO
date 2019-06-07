package com.fmi.spo.messages;

import java.io.Serializable;

public class ClientMessageWrapper implements Serializable {
	private static final long serialVersionUID = 902685721200518336L;

	private String command;
	private String message;
	private String toUser;

	public ClientMessageWrapper(String command, String toUser, String message) throws Exception {
		this.command = command;
		this.message = message;
		this.toUser = toUser;
	}

	public String getMessage() {
		return this.message;
	}

	public String getCommand() {
		return this.command;
	}

	public String getToUser() {
		return this.toUser;
	}

	@Override
	public String toString() {
		return String.format("%s %s%s", this.command, this.toUser != null ? this.toUser + " " : "", this.message);
	}
}
