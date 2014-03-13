package com.talentica.graphite.store;

import java.util.Iterator;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

import com.talentica.graphite.atom.Atom;
import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.atom.ClassAtom;
import com.talentica.graphite.atom.ObjectAtom;
import com.talentica.graphite.atom.PhraseAtom;
import com.talentica.graphite.atom.PropertyAtom;
import com.talentica.graphite.atom.StringAtom;
import com.talentica.graphite.bond.Bonds;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;

public class ObjectAtomStore extends SearchObjectStore<ObjectAtom>{
	private ClassAtomStore classStore;
	private PropertyAtomStore propStore;

	ObjectAtomStore(StoreResources storeResources) throws MissingAtomException, InvalidAtomException {
		super(storeResources);
	}

	@Override
	void init() throws InvalidAtomException, MissingAtomException {
		super.init();
		this.classStore = (ClassAtomStore) this.storeResources.getAtomStore(AtomType.domain_class);
		this.propStore = (PropertyAtomStore) this.storeResources.getAtomStore(AtomType.domain_prop);
	}

	@Override
	protected ObjectAtom getSearchObject(long id, String name) throws InvalidAtomException {
		Node node = this.storeResources.getGraphDb().getNodeById(id);
		if(!node.getProperty(TYPE_FIELD).equals(this.getAtomType())){
			throw new InvalidAtomException(name, id, "this node is not an object Atom");
		}
		return new ObjectAtom(name, null, id);
	}

	@Override
	protected MissingAtomException getMissingAtomException(long id,
			String name, String errMessage, Throwable cause) {
		return new MissingAtomException(new ObjectAtom(name, null, id), errMessage, cause);
	}

	@Override
	protected InvalidAtomException getInvalidAtomException(long id,
			String name, String errMessage, Throwable cause) {
		return new InvalidAtomException(new ObjectAtom(name, null, id), errMessage, cause);
	}

	@Override
	protected String getAtomType() {
		return AtomType.domain_obj.name();
	}

	public ObjectAtom createAtom(String rawName, ClassAtom classAtom) throws InvalidAtomException, MissingAtomException{
		String name = rawName.toLowerCase();
		ObjectAtom objAtom = super.createAtom(name);
		int rank = this.classStore.getRank(classAtom);
		this.setRank(rank, objAtom);
		Transaction tx = graphDb.beginTx();
		try{
			Node objNode = this.getAtomNode(objAtom);
			Node classNode = this.getAtomNode(classAtom);
			tx.acquireWriteLock(objNode);
			tx.acquireWriteLock(classNode);
			if(!objNode.hasRelationship(Direction.OUTGOING, Bonds.type)){
				objNode.createRelationshipTo(classNode, Bonds.type);
			}
			tx.success();
		}finally{
			tx.finish();
		}
		return objAtom;
	}

	public ClassAtom getType(Atom objAtom) throws MissingAtomException, InvalidAtomException{
		Node node = this.getAtomNode(objAtom); 
		if(!node.hasRelationship(Direction.OUTGOING, Bonds.type)){
			throw new InvalidAtomException(objAtom, "object atom does not have a type link");
		}
		Iterator<Relationship> iterator = node.getRelationships(Direction.OUTGOING, Bonds.type).iterator();
		Node classNode = iterator.next().getOtherNode(node);
		return new ClassAtom(classNode.getId(), this.classStore.getAtomName(classNode.getId()));

	}

	public void addProperty(ObjectAtom obj, final PropertyAtom prop, Atom val) throws MissingAtomException, InvalidAtomException{
		Node valNode = this.getAtomNode(val);
		Node objNode = this.getAtomNode(obj);
		Node propNode = this.getAtomNode(prop);
		ClassAtom objType = this.getType(obj);
		ClassAtom valType = this.getType(val);
		Set<ClassAtom> domain = this.propStore.getDomain(prop);
		ClassAtom range = this.propStore.getRange(prop);

		if(!domain.contains(objType) || !range.equals(valType) ){
			throw new InvalidAtomException(obj, "can't add property");
		}
		Transaction tx = graphDb.beginTx();
		try{
			tx.acquireReadLock(valNode);
			tx.acquireReadLock(objNode);
			RelationshipType relationship = new RelationshipType() {
				public String name() {
					return prop.getName();
				}
			};
			Iterator<Relationship> relationships = objNode.getRelationships(Direction.OUTGOING, relationship).iterator();
			boolean create = true;
			while(relationships.hasNext()){
				Node node = relationships.next().getOtherNode(objNode);
				if(node.getId() == valNode.getId()){
					create = false;
					break;
				}
			}
			if(create){
				objNode.createRelationshipTo(valNode, relationship);
				propNode.createRelationshipTo(valNode, Bonds.val);
			}
			tx.success();
		}finally{
			tx.finish();
		}
	}

	public Atom getPropValue(final ObjectAtom objAtom, final PropertyAtom propertyAtom) throws MissingAtomException, InvalidAtomException{
		Node objNode = this.graphDb.getNodeById(objAtom.getId());
		Iterator<Relationship> relationships = objNode.getRelationships(Direction.OUTGOING, new RelationshipType() {
			public String name() {
				return propertyAtom.getName();
			}
		}).iterator();
		if(relationships.hasNext()){
			Node valueNode = relationships.next().getOtherNode(objNode);
			return this.getAtom(valueNode.getId());
		}
		else{
			throw new MissingAtomException(objAtom, "property does not exist");
		}
	}
	public ObjectAtom getAtomByName(String rawName) throws MissingAtomException, InvalidAtomException  {
		String name = rawName.toLowerCase().trim();
		Node nameNode;
		if(isPhrase(name)){
			PhraseAtom phraseAtom = this.phraseStore.getAtomByName(name);
			nameNode = this.graphDb.getNodeById(phraseAtom.getId());
		}else{
			StringAtom strAtom = this.strStore.getAtomByName(name);
			nameNode = this.graphDb.getNodeById(strAtom.getId());
		}
		if(null == nameNode || !nameNode.hasRelationship(Direction.INCOMING, Bonds.name)){
			throw new MissingAtomException(-1, name, "");
		}
		Iterator<Relationship> iterator = nameNode.getRelationships(Bonds.name, Direction.INCOMING).iterator();
		long id = iterator.next().getOtherNode(nameNode).getId();
		return this.getSearchObject(id, name);
	}
}