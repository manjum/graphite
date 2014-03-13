package com.talentica.graphite.store;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import com.talentica.graphite.atom.Atom;
import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.atom.ClassAtom;
import com.talentica.graphite.atom.PhraseAtom;
import com.talentica.graphite.atom.PropertyAtom;
import com.talentica.graphite.bond.Bonds;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;

public class PropertyAtomStore extends SearchObjectStore<PropertyAtom>{
	private ObjectAtomStore objStore;

	PropertyAtomStore(StoreResources storeResources) throws MissingAtomException, InvalidAtomException {
		super(storeResources);
	}

	@Override
	void init() throws InvalidAtomException, MissingAtomException {
		super.init();
		this.objStore = (ObjectAtomStore) this.storeResources.getAtomStore(AtomType.domain_obj);
	}

	@Override
	protected PropertyAtom getSearchObject(long id, String name) throws InvalidAtomException {
		Node node = this.storeResources.getGraphDb().getNodeById(id);
		if(!node.getProperty(TYPE_FIELD).equals(this.getAtomType())){
			throw new InvalidAtomException(name, id, name + "is not an object Atom");
		}
		return new PropertyAtom(name, id);
	}

	@Override
	protected MissingAtomException getMissingAtomException(long id,
			String name, String errMessage, Throwable cause) {
		return new MissingAtomException(new PropertyAtom(name, id), errMessage, cause);
	}

	@Override
	protected InvalidAtomException getInvalidAtomException(long id,
			String name, String errMessage, Throwable cause) {
		return new InvalidAtomException(new PropertyAtom(name, id), errMessage, cause);
	}

	public ClassAtom getRange(PropertyAtom propAtom) throws MissingAtomException, InvalidAtomException{
		Node node = this.getAtomNode(propAtom);
		Iterator<Relationship> iterator = node.getRelationships(Bonds.range, Direction.OUTGOING).iterator();
		Node rangeNode = iterator.next().getOtherNode(node);
		return new ClassAtom(rangeNode.getId(), this.getAtomName(rangeNode.getId()));
	}

	public Set<ClassAtom> getDomain(PropertyAtom propAtom) throws MissingAtomException, InvalidAtomException{
		Set<ClassAtom> domain= Collections.emptySet();
		Node node = this.getAtomNode(propAtom);
		Iterator<Relationship> iterator = node.getRelationships(Bonds.domain, Direction.OUTGOING).iterator();
		if(iterator.hasNext()){
			domain = new HashSet<ClassAtom>();
		}
		while(iterator.hasNext()){
			Node domainNode = iterator.next().getOtherNode(node);
			domain.add(new ClassAtom(domainNode.getId(), this.getAtomName(domainNode.getId())));
		}
		return domain;
	}

	public void addDomain(PropertyAtom prop, ClassAtom domain) throws MissingAtomException{
		Node propNode = this.getAtomNode(prop);
		Node classNode = this.getAtomNode(domain);
		Transaction tx = graphDb.beginTx();
		try{
			tx.acquireWriteLock(propNode);
			tx.acquireWriteLock(classNode);
			if(!isBonded(propNode, classNode, Bonds.prop, Direction.INCOMING)){
				classNode.createRelationshipTo(propNode, Bonds.prop);
			}
			if(!isBonded(propNode, classNode, Bonds.prop, Direction.OUTGOING)){
				propNode.createRelationshipTo(classNode, Bonds.domain);
			}
			tx.success();
		}finally{
			tx.finish();
		}
	}

	public void addRange(PropertyAtom prop, ClassAtom range) throws MissingAtomException{
		Node propNode = this.getAtomNode(prop);
		Node classNode = this.getAtomNode(range);
		Transaction tx = graphDb.beginTx();
		try{
			tx.acquireWriteLock(propNode);
			tx.acquireWriteLock(classNode);
			if(!isBonded(propNode, classNode, Bonds.range, Direction.OUTGOING)){
				propNode.createRelationshipTo(classNode, Bonds.range);
			}
			tx.success();
		}finally{
			tx.finish();
		}
	}

	public String getQueryPhrase(PropertyAtom prop) throws InvalidAtomException, MissingAtomException{
		Node propNode = this.storeResources.getGraphDb().getNodeById(prop.getId());
		if(!propNode.hasRelationship(Direction.OUTGOING, Bonds.query_phrase)){
			throw new InvalidAtomException(prop, "property node does not have query phrase");
		}
		Iterator<Relationship> iterator = propNode.getRelationships(Direction.OUTGOING, Bonds.query_phrase).iterator();
		Node phrNode = iterator.next().getOtherNode(propNode);
		PhraseAtom phrAtom = this.phraseStore.getAtomById(phrNode.getId());
		if(iterator.hasNext()){
			throw new InvalidAtomException(prop, "property node has more than one query phrase");
		}
		return phrAtom.getName();
	}

	public void addQueryPhrase(PropertyAtom prop, String rawQueryPhrase) throws InvalidAtomException, MissingAtomException{
		String queryPhrase = rawQueryPhrase.toLowerCase();
		PhraseAtom phrAtom = this.phraseStore.createAtom(queryPhrase);
		Transaction tx = this.storeResources.getGraphDb().beginTx();
		try{
			Node phraseNode = this.getAtomNode(phrAtom);
			Node propNode = this.getAtomNode(prop);
			tx.acquireWriteLock(propNode);
			if(!propNode.hasRelationship(Direction.OUTGOING, Bonds.query_phrase)){
				propNode.createRelationshipTo(phraseNode, Bonds.query_phrase);
			}
			tx.success();
		}finally{
			tx.finish();
		}

	}

	public Set<PropertyAtom> getPropertyAtomsForValue(Atom valAtom) throws MissingAtomException, InvalidAtomException{
		Set<PropertyAtom> propAtoms = Collections.emptySet();
		Node valNode = graphDb.getNodeById(valAtom.getId());
		Iterator<Relationship> iterator = valNode.getRelationships(Direction.INCOMING, Bonds.val).iterator();
		if(iterator.hasNext()){
			propAtoms = new HashSet<PropertyAtom>();
		}
		while(iterator.hasNext()){
			Node propNode = iterator.next().getOtherNode(valNode);
			propAtoms.add((PropertyAtom) this.getAtom(propNode.getId()));
		}
		return propAtoms;
	}
	public Set<Atom> getPropertyValues(PropertyAtom propertyAtom) throws MissingAtomException, InvalidAtomException{
		Set<Atom> propVals = Collections.emptySet();
		Node propNode = graphDb.getNodeById(propertyAtom.getId());
		Iterator<Relationship> iterator = propNode.getRelationships(Direction.OUTGOING, Bonds.val).iterator();
		if(iterator.hasNext()){
			propVals = new HashSet<Atom>();
		}
		while(iterator.hasNext()){
			Node node = iterator.next().getOtherNode(propNode);
			String type = (String) node.getProperty(TYPE_FIELD);
			if( type.equals( AtomType.string.name() ) ){
				propVals.add(strStore.getAtomById( node.getId() ) );
			}
			else if( type.equals(AtomType.domain_obj.name())){
				propVals.add( this.objStore.getAtomById( node.getId() ) );
			}
		}
		return propVals;
	}

	@Override
	protected String getAtomType() {
		return AtomType.domain_prop.name();
	}
}
