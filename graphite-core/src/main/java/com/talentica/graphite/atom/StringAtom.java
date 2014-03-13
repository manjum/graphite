package com.talentica.graphite.atom;



public class StringAtom extends PhraseAtom implements Atom{
	private final AtomType type;
	public StringAtom(long id, String name) {
		super(id, name);
		this.type = AtomType.string;
	}

	public AtomType getAtomType() {
		return this.type;
	}

	@Override
	public String toString() {
		return "StringAtom [type=" + type + ", id=" + getId()
				+ ", name=" + getName() + "]";
	}
}
