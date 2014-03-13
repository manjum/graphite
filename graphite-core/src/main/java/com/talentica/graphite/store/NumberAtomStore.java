package com.talentica.graphite.store;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Transaction;

import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.atom.ClassAtom;
import com.talentica.graphite.atom.NumberAtom;
import com.talentica.graphite.bond.Bonds;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.InvalidNumberAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.exceptions.MissingNumberAtomException;


public class NumberAtomStore extends AtomStore<NumberAtom>{
	private long numberClassNodeId;

	NumberAtomStore(StoreResources storeResources) throws InvalidAtomException, MissingAtomException {
		super(storeResources);
	}

	@Override
	void init() throws InvalidAtomException, MissingAtomException {
		ClassAtomStore classStore = (ClassAtomStore) this.storeResources.getAtomStore(AtomType.domain_class);
		ClassAtom numberClassAtom = classStore.createAtom(AtomType.number.name());
		this.numberClassNodeId = numberClassAtom.getId();
	}

	public long getNumberClassNodeId() {
		return numberClassNodeId;
	}

	public String getAtomName(long id)
			throws InvalidNumberAtomException, MissingNumberAtomException {
		try{
			Node node = graphDb.getNodeById(id);
			return getAtom(node).getName();
		}catch(NotFoundException e){
			throw new MissingNumberAtomException(null, id, "Sorry, I could not find an atom with this id", e);
		}
	}

	public NumberAtom getAtomByName(String name)
			throws InvalidNumberAtomException, MissingNumberAtomException {
		isNameValid(name);
		Node node = nodeIndex.get(NAME_FIELD, name).getSingle();
		if(null == node){
			throw new MissingNumberAtomException(-1, name, "");
		}
		return this.getAtom(node);
	}

	public NumberAtom createAtom(String name)
			throws InvalidAtomException {
		isNameValid(name);
		Transaction tx = this.storeResources.graphDb.beginTx();

		Node numClassNode = this.storeResources.graphDb.getNodeById(numberClassNodeId);
		try{
			Node node = nodeFactory.getOrCreate(NAME_FIELD, Long.valueOf(name));
			tx.acquireWriteLock(node);
			if(!node.hasRelationship(Direction.OUTGOING, Bonds.type)){
				node.createRelationshipTo(numClassNode, Bonds.type);
			}
			tx.success();
			return this.getAtom(node);
		}finally{
			tx.finish();
		}
	}

	@Override
	protected MissingNumberAtomException getMissingAtomException(
			long id, String name, String errMessage, Throwable cause) {
		return new MissingNumberAtomException(name, id, errMessage, cause);
	}

	@Override
	protected InvalidNumberAtomException getInvalidAtomException(
			long id, String name, String errMessage, Throwable cause) {
		return new InvalidNumberAtomException(name, id, errMessage, cause);
	}

	@Override
	protected NumberAtom getAtom(Node node)
			throws InvalidNumberAtomException {
		try{
			return new NumberAtom(node.getId(), String.valueOf(node.getProperty(NAME_FIELD)));
		}catch(NotFoundException e){
			throw new InvalidNumberAtomException(null, node.getId(), "could not fetch name property", e);
		}
	}
	public void isNameValid(String name) throws InvalidNumberAtomException{
		try{
			Long.valueOf(name);
		}catch(NumberFormatException e){
			throw new InvalidNumberAtomException(name, -1, "number atom can have only integer names");
		}
	}
}
