package com.talentica.graphite.atom;

public interface Atom {
	public static final String TYPE_FIELD = "type";
	long getId();
	String getName();
	AtomType getAtomType();
}
