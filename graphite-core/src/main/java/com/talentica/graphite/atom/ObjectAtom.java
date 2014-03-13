package com.talentica.graphite.atom;

import java.util.HashMap;
import java.util.Set;

import com.talentica.graphite.exceptions.InvalidObjectAtomException;

public class ObjectAtom extends SearchObject{
	public static final AtomType TYPE = AtomType.domain_obj;
	private final ClassAtom type;
	private final HashMap<PropertyAtom, ObjectAtom> propertyMap = new HashMap<PropertyAtom, ObjectAtom>();

	public ObjectAtom(final String name, final ClassAtom type, long id) {
		super(name, id, TYPE);
		this.type = type;
	}

	public ObjectAtom(final String name, final ClassAtom type) {
		this(name, type, -1);
	}

	public ClassAtom getType() {
		return type;
	}
	
	public Set<PropertyAtom> getProperties() {
		return propertyMap.keySet();
	}
	public ObjectAtom getPropertyValue(PropertyAtom property){
		return propertyMap.get(property);
	}
	public void addProperty(PropertyAtom property, ObjectAtom value) throws InvalidObjectAtomException {
		if(!this.type.hasProperty(property)){
			throw new InvalidObjectAtomException(this, "type:<" + this.type.getName() + "> does not have the property :<" + property.getName() + ">");
		}
		if(!property.hasDomain(this.type)){
			throw new InvalidObjectAtomException(this, "property :<" + property.getName() + "> does not have the domain :<" + this.type.getName() + ">");
		}
		if(!property.hasRange(value.getType())){
			throw new InvalidObjectAtomException(this, "property :<" + property.getName() + "> can't have value of type:<" + this.type.getName() + ">");
		}
		this.propertyMap.put(property, value);
	}
}
