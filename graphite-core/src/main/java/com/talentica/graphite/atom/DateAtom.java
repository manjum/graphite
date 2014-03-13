package com.talentica.graphite.atom;

import java.util.Date;

public class DateAtom implements Atom{
	private final long id;
	private final String name;
	private final AtomType type;

	public DateAtom(long id, String name) {
		this.id = id;
		this.name = name;
		this.type = AtomType.date;
	}

	public long getId() {
		return this.id;
	}

	public String getName() {
		//TODO: format to human readable date
		return this.name;
	}

	public AtomType getAtomType() {
		return this.type;
	}
	
	public Date getDate(){
		return new Date(Integer.valueOf(this.name));
	}

	@Override
	public String toString() {
		return "DateAtom [id=" + id + ", name=" + name + ", type=" + type + "]";
	}
}

