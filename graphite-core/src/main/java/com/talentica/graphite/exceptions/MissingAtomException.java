package com.talentica.graphite.exceptions;

import com.talentica.graphite.atom.Atom;

public class MissingAtomException extends AtomException{
	private static final long serialVersionUID = -7097189613042909520L;

	public MissingAtomException(Atom node, String message) {
		super(node, message);
	}

	public MissingAtomException(long id, String name, String message) {
		super(message, id, name);
	}


	public MissingAtomException(Atom node, String message,
			Throwable cause) {
		super(node, message, cause);
	}

	public MissingAtomException(String name, long id, String message,
			Throwable cause) {
		super(name, id, message, cause);
	}

	public long getMissingNodeId(){
		return this.id;
	}
	
	public String getMissingNodeName(){
		return this.name;
	}

}
