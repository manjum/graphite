package com.talentica.graphite.store;

import java.util.Iterator;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.atom.ClassAtom;
import com.talentica.graphite.atom.DateAtom;
import com.talentica.graphite.atom.NumberAtom;
import com.talentica.graphite.bond.Bonds;
import com.talentica.graphite.bond.DateBonds;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.InvalidDateAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.exceptions.MissingDateAtomException;


public class DateAtomStore extends AtomStore<DateAtom>{
	private long dateClassAtomId = -1;
	private NumberAtomStore numStore;

	DateAtomStore(StoreResources storeResources) throws MissingAtomException, InvalidAtomException {
		super(storeResources);
	}

	@Override
	void init() throws InvalidAtomException, MissingAtomException {
		ClassAtomStore classStore = (ClassAtomStore) this.storeResources.getAtomStore(AtomType.domain_class);
		ClassAtom dateClassAtom = classStore.createAtom(AtomType.date.name());
		this.dateClassAtomId = dateClassAtom.getId();
		this.numStore = (NumberAtomStore) this.storeResources.getAtomStore(AtomType.number);
	}

	public long getDateClassAtomId() {
		return dateClassAtomId;
	}

	public void setDateClassAtomId(long dateClassAtomId) {
		this.dateClassAtomId = dateClassAtomId;
	}


	public String getAtomName(long id)
			throws InvalidDateAtomException, MissingDateAtomException {
		try{
			Node node = graphDb.getNodeById(id);
			return getAtom(node).getName();
		}
		catch(NotFoundException e){
			throw new MissingDateAtomException(null, id, "date with given id could not be found", e);
		}
	}

	public DateAtom getAtomByName(String name)
			throws InvalidAtomException, MissingAtomException {
		this.numStore.isNameValid(name);
		Node node = nodeIndex.get(NAME_FIELD, name).getSingle();
		if(null == node){
			throw new MissingDateAtomException(-1, name, "date node does not exist");
		}
		Iterator<Relationship> relationships = node.getRelationships(DateBonds.date_val, Direction.INCOMING).iterator();
		if(!relationships.hasNext()){
			throw new MissingDateAtomException(-1, name, "date node does not exist");
		}
		return getAtom(relationships.next().getOtherNode(node));
	}

	//TODO make sure value node is connected to only one date node
	public DateAtom createAtom(String name) throws InvalidAtomException, MissingAtomException {
		NumberAtom numNode = this.numStore.createAtom(name);
		Transaction tx = graphDb.beginTx();
		Node dateClassNode = graphDb.getNodeById(dateClassAtomId);
		try {
			Node dateObjNode = null;
			Node valNode = graphDb.getNodeById(numNode.getId());
			Iterator<Relationship> iterator = valNode.getRelationships(DateBonds.date_val, Direction.INCOMING).iterator();
			if(iterator.hasNext()){
				dateObjNode = iterator.next().getOtherNode(valNode);
			}
			else{
				tx.acquireWriteLock(valNode);
				dateObjNode = graphDb.createNode();
				dateObjNode.setProperty(TYPE_FIELD, AtomType.date.name());
				dateObjNode.createRelationshipTo(valNode, DateBonds.date_val);
				dateObjNode.createRelationshipTo(dateClassNode, Bonds.type);
			}
			tx.success();
			return new DateAtom(dateObjNode.getId(), name);
		}
		finally{
			tx.finish();
		}
	}

	@Override
	protected MissingDateAtomException getMissingAtomException(
			long id, String name, String errMessage, Throwable cause) {
		return new MissingDateAtomException(name, id, errMessage, cause);
	}

	@Override
	protected InvalidDateAtomException getInvalidAtomException(
			long id, String name, String errMessage, Throwable cause) {
		return new InvalidDateAtomException(name, id, errMessage, cause);
	}

	@Override
	protected DateAtom getAtom(Node node)
			throws InvalidDateAtomException{
		Iterator<Relationship> relationships = node.getRelationships(DateBonds.date_val, Direction.OUTGOING).iterator();
		if(!relationships.hasNext()){
			throw new InvalidDateAtomException(null, node.getId(), "this node does not have date_val link");
		}
		Node valNode = relationships.next().getOtherNode(node);
		return new DateAtom(node.getId(), String.valueOf(valNode.getProperty(NAME_FIELD)));
	}
}
