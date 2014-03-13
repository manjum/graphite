package com.talentica.graphite.exceptions;

import com.talentica.graphite.atom.Atom;

public class InvalidObjectAtomException extends InvalidAtomException{
	private static final long serialVersionUID = 6819567882554313257L;
	public InvalidObjectAtomException(Atom node, String message, Throwable cause) {
		super(node, message, cause);
	}

	public InvalidObjectAtomException(Atom node, String message) {
		super(node, message);
	}

	public InvalidObjectAtomException(String name, long id, String message,
			Throwable cause) {
		super(name, id, message, cause);
	}

	public InvalidObjectAtomException(String name, long id, String message) {
		super(name, id, message);
	}
}
