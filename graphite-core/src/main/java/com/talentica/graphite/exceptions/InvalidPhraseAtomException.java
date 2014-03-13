package com.talentica.graphite.exceptions;

import com.talentica.graphite.atom.Atom;

public class InvalidPhraseAtomException extends InvalidAtomException{
	private static final long serialVersionUID = 4721025274809295024L;

	public InvalidPhraseAtomException(Atom node, String message) {
		super(node, message);
	}

	public InvalidPhraseAtomException(String message, long id, String name) {
		super(message, id, name);
	}

	public InvalidPhraseAtomException(Atom node, String message,
			Throwable cause) {
		super(node, message, cause);
	}

	public InvalidPhraseAtomException(String name, long id, String message,
			Throwable cause) {
		super(name, id, message, cause);
	}
}
