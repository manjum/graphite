package com.talentica.graphite.api.exception;

public class ObjectWriteException extends Exception{
	private static final long serialVersionUID = 6570008285553851541L;

	public ObjectWriteException(String message, Throwable cause) {
		super(message, cause);
	}

	public ObjectWriteException(String message) {
		super(message);
	}
}
