package com.talentica.graphite.exceptions;

import com.talentica.graphite.atom.Atom;

public class MissingNumberAtomException extends MissingAtomException{
	private static final long serialVersionUID = 880187536831535337L;
	public MissingNumberAtomException(Atom node, String message,
			Throwable cause) {
		super(node, message, cause);
	}

	public MissingNumberAtomException(Atom node, String message) {
		super(node, message);
	}

	public MissingNumberAtomException(long id, String name, String message) {
		super(id, name, message);
	}

	public MissingNumberAtomException(String name, long id, String message,
			Throwable cause) {
		super(name, id, message, cause);
	}
}
