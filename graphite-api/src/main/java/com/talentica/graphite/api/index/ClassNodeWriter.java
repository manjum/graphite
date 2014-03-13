package com.talentica.graphite.api.index;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Set;

import com.talentica.graphite.api.exception.GraphiteAnnotationParseException;
import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.atom.ClassAtom;
import com.talentica.graphite.atom.PropertyAtom;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.store.ClassAtomStore;
import com.talentica.graphite.store.PropertyAtomStore;
import com.talentica.graphite.store.StoreResources;

public class ClassNodeWriter {
	private final ClassAtomStore classStore;
	private final PropertyAtomStore propStore;

	public ClassNodeWriter(StoreResources storeResources) throws MissingAtomException, InvalidAtomException{
		this.classStore = (ClassAtomStore) storeResources.getAtomStore(AtomType.domain_class);
		this.propStore = (PropertyAtomStore) storeResources.getAtomStore(AtomType.domain_prop);
	}

	public void write(Class<?> clazz) throws InvalidAtomException, MissingAtomException, GraphiteAnnotationParseException{
		ClassNode classNode = clazz.getAnnotation(ClassNode.class);
		int rank = classNode.rank();
		if(classNode.name().equals(Constants.NULL)){
			ClassAtom classAtom = this.classStore.createAtom(clazz.getSimpleName());
			this.classStore.setRank(rank, classAtom);
		}
		else{
			ClassAtom classAtom = this.classStore.createAtom(classNode.name());
			this.classStore.setRank(rank, classAtom);
		}

		for(Field field: clazz.getDeclaredFields()){
			if(!field.getName().equals("name") && !field.getName().equals("id")){
				this.parsePropertyNode(field);
			}
		}
	}

	//TODO: refine the thrown exceptions.
	private void parsePropertyNode(Field field) throws InvalidAtomException, MissingAtomException {
		Class<?> domainClazz = field.getDeclaringClass();
		ClassNode domainNode = domainClazz.getAnnotation(ClassNode.class);
		String domainName = "";
		if(null != domainNode) {
			domainName = domainNode.name().equals(Constants.NULL) ? domainClazz.getSimpleName() : domainNode.name();
		}
		ClassAtom domainAtom = this.classStore.createAtom(domainName);
		Class<?> rangeClass;
		if(field.getType().equals(List.class) || field.getType().equals(Set.class)){
			ParameterizedType listType = (ParameterizedType) field.getGenericType();
			rangeClass = (Class<?>) listType.getActualTypeArguments()[0];
		}
		else{
			rangeClass = field.getType();
		}
		ClassNode rangeNode = rangeClass.getAnnotation(ClassNode.class);
		String rangeName = (null != rangeNode && rangeNode.equals(Constants.NULL)) ? rangeNode.name():rangeClass.getSimpleName();
		ClassAtom rangeAtom = this.classStore.createAtom(rangeName);

		PropertyNode propNode = field.getAnnotation(PropertyNode.class);
		if(propNode != null){
			String propName = propNode.name().equals(Constants.NULL) ? field.getName() : propNode.name();
			int rank = propNode.rank();
			PropertyAtom propAtom = this.propStore.createAtom(propName);
			this.propStore.setRank(rank, propAtom);
			this.classStore.addProperty(domainAtom, propAtom);
			this.propStore.addDomain(propAtom, domainAtom);
			this.propStore.addRange(propAtom, rangeAtom);
		}
	}
}
