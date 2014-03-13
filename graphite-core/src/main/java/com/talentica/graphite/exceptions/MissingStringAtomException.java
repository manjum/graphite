package com.talentica.graphite.exceptions;

import com.talentica.graphite.atom.StringAtom;

public class MissingStringAtomException extends MissingAtomException{
	private static final long serialVersionUID = 7673727662287395575L;

	public MissingStringAtomException(StringAtom node, String message,
			Throwable cause) {
		super(node, message, cause);
	}

	public MissingStringAtomException(StringAtom node, String message) {
		super(node, message);
	}

	public MissingStringAtomException(long id, String name, String message) {
		super(id, name, message);
	}

	public MissingStringAtomException(String name, long id, String message,
			Throwable cause) {
		super(name, id, message, cause);
	}

}
