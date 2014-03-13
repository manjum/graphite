package com.talentica.graphite.search.exception;

public class InvalidLinkException extends Exception{

	private static final long serialVersionUID = 1L;

	public InvalidLinkException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidLinkException(String message) {
		super(message);
	}
}
