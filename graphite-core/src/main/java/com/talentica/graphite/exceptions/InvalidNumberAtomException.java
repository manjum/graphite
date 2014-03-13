package com.talentica.graphite.exceptions;

import com.talentica.graphite.atom.Atom;

public class InvalidNumberAtomException extends InvalidAtomException{

	public InvalidNumberAtomException(Atom node, String message,
			Throwable cause) {
		super(node, message, cause);
	}

	public InvalidNumberAtomException(Atom node, String message) {
		super(node, message);
	}

	public InvalidNumberAtomException(String name, long id, String message,
			Throwable cause) {
		super(name, id, message, cause);
	}

	public InvalidNumberAtomException(String name, long id, String message) {
		super(name, id, message);
	}

	private static final long serialVersionUID = -7893902211596727048L;

}
