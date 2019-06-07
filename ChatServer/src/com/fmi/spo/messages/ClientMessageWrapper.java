package com.fmi.spo.messages;

import java.io.Serializable;

public class ClientMessageWrapper implements Serializable {
	private static final long serialVersionUID = 902685721200518336L;

	public enum Command {
		USER("user"), SEND_TO("send_to"), SEND_ALL("send_all"), LIST("list"), SEND_FILE_TO("send_file_to"), BYE("bye");

		private String command;

		private Command(String command) {
			this.command = command;
		}

		@Override
		public String toString() {
			return this.command;
		}
	}

	private String message;
	private Command command;
	private String toUser;

	public ClientMessageWrapper(String command, String toUser, String message) throws Exception {
		switch (command) {
		case "user":
			this.command = Command.USER;
			break;
		case "send_to":
			this.command = Command.SEND_TO;
			break;
		case "send_all":
			this.command = Command.SEND_ALL;
			break;
		case "list":
			this.command = Command.LIST;
			break;
		case "send_file_to":
			this.command = Command.SEND_FILE_TO;
			break;
		case "bye":
			this.command = Command.BYE;
			break;
		default:
			throw new Exception("Unknown command \"" + command + "\"");
		}
		this.message = message;
		this.toUser = toUser;
	}

	public String getMessage() {
		return this.message;
	}

	public String getCommand() {
		return this.command.toString();
	}

	public String getToUser() {
		return this.toUser;
	}

	@Override
	public String toString() {
		return String.format("%s %s%s", this.command, this.toUser != null ? this.toUser + " " : "", this.message);
	}
}
