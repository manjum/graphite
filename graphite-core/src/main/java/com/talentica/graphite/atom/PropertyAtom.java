package com.talentica.graphite.atom;

import java.util.Collections;
import java.util.List;

public class PropertyAtom extends SearchObject {
	public static final AtomType TYPE  = AtomType.domain_prop;
	private final List<ClassAtom> domain = Collections.emptyList();
	private final List<ClassAtom> range = Collections.emptyList();

	public PropertyAtom(final String name, long id){
		super(name, id, TYPE);
	}

	public PropertyAtom(final String name) {
		this(name, -1);
	}

	public List<ClassAtom> getDomainList() {
		return domain;
	}

	public List<ClassAtom> getRangeList() {
		return range;
	}

	public boolean hasDomain(ClassAtom domain){
		return this.domain.contains(domain);
	}

	public boolean hasRange(ClassAtom range){
		return this.range.contains(range);
	}

	public void addDomain(ClassAtom domain){
		this.domain.add(domain);
	}

	public void addRange(ClassAtom range){
		this.range.add(range);
	}

	@Override
	public String toString() {
		return "PropertyAtom [domain=" + domain + ", range=" + range
				+ ", id=" + getId() + ", name=" + getName() + "]";
	}
}