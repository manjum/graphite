package com.talentica.graphite.exceptions;

import com.talentica.graphite.atom.StringAtom;

public class InvalidStringAtomException extends InvalidAtomException{
	private static final long serialVersionUID = 4275829704783658132L;

	public InvalidStringAtomException(StringAtom node, String message) {
		super(node, message);
	}

	public InvalidStringAtomException(String message, long id, String name) {
		super(message, id, name);
	}

	public InvalidStringAtomException(String name, long id, String message,
			Throwable cause) {
		super(name, id, message, cause);
	}

}
