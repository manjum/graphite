package com.talentica.graphite.api.index;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.talentica.graphite.api.exception.ObjectWriteException;
import com.talentica.graphite.atom.Atom;
import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.atom.ClassAtom;
import com.talentica.graphite.atom.ObjectAtom;
import com.talentica.graphite.atom.PropertyAtom;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.store.ClassAtomStore;
import com.talentica.graphite.store.DateAtomStore;
import com.talentica.graphite.store.NumberAtomStore;
import com.talentica.graphite.store.ObjectAtomStore;
import com.talentica.graphite.store.PropertyAtomStore;
import com.talentica.graphite.store.StoreResources;
import com.talentica.graphite.store.StringAtomStore;

public class ObjectNodeWriter {
	private final ObjectAtomStore objStore;
	private final PropertyAtomStore propStore;
	private final StringAtomStore strStore;
	private final ClassAtomStore classStore;
	private final DateAtomStore dateStore;
	private final NumberAtomStore numStore;

	public ObjectNodeWriter(StoreResources storeResources) throws MissingAtomException, InvalidAtomException {
		this.classStore = (ClassAtomStore) storeResources.getAtomStore(AtomType.domain_class);
		this.objStore = (ObjectAtomStore) storeResources.getAtomStore(AtomType.domain_obj);
		this.propStore = (PropertyAtomStore) storeResources.getAtomStore(AtomType.domain_prop);
		this.strStore = (StringAtomStore) storeResources.getAtomStore(AtomType.string);
		this.numStore = (NumberAtomStore) storeResources.getAtomStore(AtomType.number);
		this.dateStore = (DateAtomStore) storeResources.getAtomStore(AtomType.date);
	}

	//TODO: refine the exceptions thrown
	public long write(ObjectNode obj) throws ObjectWriteException, MissingAtomException, InvalidAtomException, IllegalArgumentException, IllegalAccessException{
		ObjectAtom objAtom = this.objStore.createAtom(obj.getName(), this.getClassAtom(obj));
		this.objStore.setRank(obj.getRank(), objAtom);
		Field[] fields = obj.getClass().getDeclaredFields();
		for(Field field : fields){
			PropertyAtom propAtom = this.getPropertyAtom(field);
			if(null == propAtom){
				continue;
			}
			field.setAccessible(true);
			Object val = field.get(obj);
			if(val instanceof List){
				Iterator iterator = ((List) val).iterator();
				while(iterator.hasNext()){
					this.writePropertyValue(propAtom, iterator.next(), obj, objAtom);
				}
			}else if(val instanceof Set){
				Iterator iterator = ((Set) val).iterator();
				while(iterator.hasNext()){
					this.writePropertyValue(propAtom, iterator.next(), obj, objAtom);
				}
			}
			else{
				this.writePropertyValue(propAtom, val, obj, objAtom);
			}

		}
		return objAtom.getId();
	}
	private void writePropertyValue(PropertyAtom propAtom, Object val, ObjectNode obj, ObjectAtom objAtom) throws MissingAtomException, InvalidAtomException, IllegalArgumentException, IllegalAccessException, ObjectWriteException{
		Atom valAtom;
		if(val instanceof ObjectNode){
			ObjectNode  objNode = (ObjectNode)val;
			try{
				valAtom = this.objStore.getAtomByName(objNode.getName());
			}catch(MissingAtomException e){
				//create the atom if it does not exist
				valAtom = this.objStore.getAtomById(this.write(objNode));
			}
		}
		else if (val instanceof String){
			valAtom = this.strStore.createAtom(val.toString());
		}
		else if (val instanceof Date){
			Date date = (Date) val;
			String name = String.valueOf(date.getTime());
			valAtom = this.dateStore.createAtom(name);
		}
		else if(val instanceof Number){
			String name = String.valueOf(val);
			valAtom = this.numStore.createAtom(name);	
		}
		else{
			return;
		}
		this.objStore.addProperty(objAtom, propAtom, valAtom);
	}

	public Set<String> getProperties(ObjectNode obj) throws MissingAtomException, InvalidAtomException{
		ClassAtom classAtom = getClassAtom(obj);
		Set<PropertyAtom> propertyAtoms = this.classStore.getProperties(classAtom);
		Set<String> properties = new HashSet<String>(propertyAtoms.size());
		for(PropertyAtom property : propertyAtoms){
			properties.add(property.getName());
		}
		return properties;
	}

	private PropertyAtom getPropertyAtom(Field field) throws MissingAtomException, InvalidAtomException{
		PropertyAtom propAtom = null;
		PropertyNode propNode = field.getAnnotation(PropertyNode.class);
		if(null != propNode){
			String propName = propNode.name().equals(Constants.NULL) ? field.getName() : propNode.name();
			propAtom = this.propStore.getAtomByName(propName);
		}
		return propAtom;
	}

	private ClassAtom getClassAtom(ObjectNode obj) throws MissingAtomException, InvalidAtomException{
		Class<? extends ObjectNode> type = obj.getClass();
		ClassNode classNode = type.getAnnotation(ClassNode.class);
		if(classNode.name().equals(Constants.NULL)){
			return this.classStore.getAtomByName(type.getSimpleName());
		}
		else{
			return this.classStore.getAtomByName(classNode.name());
		}
	}
}
