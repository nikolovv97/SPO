package com.fmi.spo.chatclient;

import java.io.Serializable;

public class ClientMessageWrapper implements Serializable {
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

	public ClientMessageWrapper(String command, String message) throws Exception {
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
		default:
			throw new Exception("Unknown command");
		}
	}

	public String getMessage() {
		return this.message;
	}

	public String getCommand() {
		return this.command.toString();
	}
}
