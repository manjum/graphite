package com.talentica.graphite.atom;



public class PhraseAtom implements Atom{
	private final long id;
	private final String name;
	private final AtomType type;

	public PhraseAtom(long id, String name) {
		this.id = id;
		this.name = name;
		this.type = AtomType.phrase;
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
		return "PhraseAtom [id=" + id + ", name=" + name + ", type=" + type
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PhraseAtom other = (PhraseAtom) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
