package com.talentica.graphite.exceptions;

import com.talentica.graphite.atom.Atom;

public class MissingDateAtomException extends MissingAtomException{

	private static final long serialVersionUID = -6378459136537064930L;

	public MissingDateAtomException(Atom node, String message,
			Throwable cause) {
		super(node, message, cause);
	}

	public MissingDateAtomException(Atom node, String message) {
		super(node, message);
	}

	public MissingDateAtomException(long id, String name, String message) {
		super(id, name, message);
	}

	public MissingDateAtomException(String name, long id, String message,
			Throwable cause) {
		super(name, id, message, cause);
	}
}