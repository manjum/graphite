package com.talentica.graphite.atom;

import java.util.ArrayList;
import java.util.Set;

public class ClassAtom extends SearchObject{
	public static final AtomType TYPE = AtomType.domain_class;
	
	private final ArrayList<PropertyAtom> properties = new ArrayList<PropertyAtom>();
	
	public ClassAtom(long id, String name) {
		super(name, id, TYPE);
	}
	
	public ClassAtom(String name) {
		this(-1, name);
	}
	
	public ArrayList<PropertyAtom> getProperties() {
		return properties;
	}
	
	public boolean hasProperty(PropertyAtom property){
		return this.properties.contains(property);
	}
	
	public void addProperty(PropertyAtom domainProperty){
		this.properties.add(domainProperty);
	}
	
	public void addProperties(Set<PropertyAtom> properties){
		this.properties.addAll(properties);
	}

	@Override
	public String toString() {
		return "ClassAtom [properties=" + properties + ", id=" + getId()
				+ ", name=" + getName() + "]";
	}

}
