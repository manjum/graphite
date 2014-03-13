package com.talentica.graphite.exceptions;

import com.talentica.graphite.atom.Atom;

public abstract class AtomException extends Exception{
	private static final long serialVersionUID = -2657785529889378754L;
	protected final long id;
	protected final String name;
	protected final Atom node;
	
	public AtomException(Atom node, String message) {
		super(message);
		this.node = node;
		this.id = node.getId();
		this.name = node.getName();
	}

	public AtomException(Atom node, String message, Throwable cause) {
		super(message, cause);
		this.node = node;
		this.id = node.getId();
		this.name = node.getName();
	}

	public AtomException(String name,  long id, String message) {
		super(message);
		this.node = null;
		this.id = id;
		this.name = name;
	}

	public AtomException( String name, long id, String message, Throwable cause) {
		super(cause);
		this.node = null;
		this.id = id;
		this.name = name;
	}

	public Atom getFaultyNode(){
		return node;
	}
}
