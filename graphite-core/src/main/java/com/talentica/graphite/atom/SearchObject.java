package com.talentica.graphite.atom;


public  abstract class SearchObject implements Atom{
	private final String name;
	private final long id;
	private final AtomType type;
	int rank = 1;

	public SearchObject(final String name, long id, AtomType type){
		this.name = name.toLowerCase();
		this.id = id;
		this.type = type;
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

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		SearchObject other = (SearchObject) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SearchObject [name=" + name + ", id=" + id + "]";
	}
}
