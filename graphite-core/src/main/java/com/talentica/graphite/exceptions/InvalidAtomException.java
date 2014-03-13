package com.talentica.graphite.exceptions;

import com.talentica.graphite.atom.Atom;

public class InvalidAtomException extends AtomException{
	private static final long serialVersionUID = -8563488855260828174L;
	

	public InvalidAtomException(Atom node, String message,
			Throwable cause) {
		super(node, message, cause);
	}

	public InvalidAtomException(Atom node, String message) {
		super(node, message);
	}

	public InvalidAtomException(String name, long id, String message,
			Throwable cause) {
		super(name, id, message, cause);
	}

	public InvalidAtomException(String name, long id, String message) {
		super(name, id, message);
	}

	public long getInvalidNodeId(){
		return this.id;
	}
	
	public String getInvalidNodeName(){
		return this.name;
	}
}
