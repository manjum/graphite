package com.talentica.graphite.exceptions;

import com.talentica.graphite.atom.PhraseAtom;

public class MissingPhraseAtomException extends MissingAtomException{

	private static final long serialVersionUID = 6403019824346848150L;

	public MissingPhraseAtomException(PhraseAtom node, String message,
			Throwable cause) {
		super(node, message, cause);
	}

	public MissingPhraseAtomException(PhraseAtom node, String message) {
		super(node, message);
	}

	public MissingPhraseAtomException(long id, String name, String message) {
		super(id, name, message);
	}

	public MissingPhraseAtomException(String name, long id, String message,
			Throwable cause) {
		super(name, id, message, cause);
	}

}
