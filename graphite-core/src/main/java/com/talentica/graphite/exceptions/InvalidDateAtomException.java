package com.talentica.graphite.exceptions;

import com.talentica.graphite.atom.Atom;

public class InvalidDateAtomException extends InvalidAtomException{

	private static final long serialVersionUID = -145024506110550101L;

	public InvalidDateAtomException(Atom node, String message,
			Throwable cause) {
		super(node, message, cause);
	}

	public InvalidDateAtomException(Atom node, String message) {
		super(node, message);
	}

	public InvalidDateAtomException(String name, long id, String message,
			Throwable cause) {
		super(name, id, message, cause);
	}

	public InvalidDateAtomException(String name, long id, String message) {
		super(name, id, message);
	}
}
