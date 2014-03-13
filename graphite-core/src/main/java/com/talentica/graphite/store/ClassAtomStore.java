package com.talentica.graphite.store;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.atom.ClassAtom;
import com.talentica.graphite.atom.PropertyAtom;
import com.talentica.graphite.bond.Bonds;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;

public class ClassAtomStore extends SearchObjectStore<ClassAtom>{
	private PropertyAtomStore propStore;

	ClassAtomStore(StoreResources storeResources) throws MissingAtomException, InvalidAtomException {
		super(storeResources);
	}

	@Override
	void init() throws InvalidAtomException, MissingAtomException {
		super.init();
		this.propStore = (PropertyAtomStore) this.storeResources.getAtomStore(AtomType.domain_prop);
	}

	@Override
	protected MissingAtomException getMissingAtomException(long id,
			String name, String errMessage, Throwable cause) {
		return new MissingAtomException(new ClassAtom(id, name), errMessage, cause);
	}

	@Override
	protected InvalidAtomException getInvalidAtomException(long id,
			String name, String errMessage, Throwable cause) {
		return new InvalidAtomException(new ClassAtom(id, name), errMessage, cause);
	}

	@Override
	protected ClassAtom getSearchObject(long id, String name) throws InvalidAtomException {
		Node node = this.storeResources.getGraphDb().getNodeById(id);
		if(!node.getProperty(TYPE_FIELD).equals(this.getAtomType())){
			throw new InvalidAtomException(name, id, "this node is not an object Atom");
		}
		return new ClassAtom(id, name);
	}

	public Set<PropertyAtom> getProperties(ClassAtom atom) throws MissingAtomException, InvalidAtomException{
		Set<PropertyAtom> properties = Collections.emptySet();
		Node node = this.getAtomNode(atom);
		Iterator<Relationship> relationships = node.getRelationships(Bonds.prop, Direction.OUTGOING).iterator();
		if(relationships.hasNext()){
			properties = new HashSet<PropertyAtom>();
		}
		while(relationships.hasNext()){
			Node propNode = relationships.next().getOtherNode(node);
			properties.add(new PropertyAtom(this.getAtomName(propNode.getId()), propNode.getId()));
		}
		return properties; 
	}

	public ClassAtom getAtomByName(String rawName) throws MissingAtomException, InvalidAtomException  {
		String name = rawName.toString();
		ClassAtom classAtom = super.getAtomByName(name);
		classAtom.addProperties(this.getProperties(classAtom));
		return classAtom;
	}

	public Set<ClassAtom> getRanges(ClassAtom classAtom) throws MissingAtomException, InvalidAtomException{
		Set<ClassAtom> range = Collections.emptySet();
		Node classNode = this.graphDb.getNodeById(classAtom.getId());
		Iterator<Relationship> relationships = classNode.getRelationships(Bonds.range, Direction.OUTGOING).iterator();
		while(relationships.hasNext()){
			PropertyAtom propAtom = this.propStore.getAtomById(relationships.next().getOtherNode(classNode).getId());
			range.addAll(this.propStore.getDomain(propAtom));
		}
		range.remove(classAtom);
		return range;
	}

	public Set<ClassAtom> getDomains(ClassAtom classAtom) throws MissingAtomException, InvalidAtomException{
		Set<ClassAtom> domain = Collections.emptySet();
		Set<PropertyAtom> properties = this.getProperties(classAtom);
		if(properties.size()>0){
			domain = new HashSet<>(properties.size());
		}
		for(PropertyAtom propAtom: properties){
			ClassAtom range = this.propStore.getRange(propAtom);
			if(range != classAtom){
				domain.add(range);
			}
		}
		return domain;

	}

	public void addProperty(ClassAtom clazz, PropertyAtom prop) throws MissingAtomException{
		Node classNode = this.getAtomNode(clazz);
		Node propNode = this.getAtomNode(prop);
		Transaction tx = graphDb.beginTx();
		try{
			tx.acquireWriteLock(classNode);
			tx.acquireWriteLock(propNode);
			if(!isBonded(classNode, propNode, Bonds.prop, Direction.OUTGOING)){
				classNode.createRelationshipTo(propNode, Bonds.prop);
			}
			if(!isBonded(classNode, propNode, Bonds.domain, Direction.INCOMING)){
				propNode.createRelationshipTo(classNode, Bonds.domain);
			}
			tx.success();
		}
		finally{
			tx.finish();
		}
	}

	@Override
	protected String getAtomType() {
		return AtomType.domain_class.name();
	}
}
