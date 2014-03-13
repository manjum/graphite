package com.talentica.graphite.atom;


public class NumberAtom implements Atom{
	private final long id;
	private final String name;
	private final AtomType type;

	public NumberAtom(long id, String name) {
		this.id = id;
		this.name = name;
		this.type = AtomType.number;
	}

	public long getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public AtomType getAtomType() {
		return this.type;
	}

	@Override
	public String toString() {
		return "NumberAtom [id=" + id + ", name=" + name + ", type=" + type
				+ "]";
	}
}
