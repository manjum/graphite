package com.talentica.graphite.store;

import java.util.Iterator;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.atom.SearchObject;
import com.talentica.graphite.bond.Bonds;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;

public abstract class SearchObjectStore<T extends SearchObject> extends AtomStore<T>{
	protected abstract T getSearchObject(long id, String name) throws InvalidAtomException;
	protected abstract String getAtomType();
	protected StringAtomStore strStore;
	protected PhraseAtomStore phraseStore;

	protected SearchObjectStore(StoreResources storeResources) throws MissingAtomException, InvalidAtomException {
		super(storeResources);
	}

	void init() throws InvalidAtomException, MissingAtomException{
		this.strStore = (StringAtomStore) this.storeResources.getAtomStore(AtomType.string);
		this.phraseStore = (PhraseAtomStore) this.storeResources.getAtomStore(AtomType.phrase);
	}

	public String getAtomName(long id) throws InvalidAtomException,
	MissingAtomException {
		try{
			Node node = graphDb.getNodeById(id);
			Iterator<Relationship> iterator = node.getRelationships(Bonds.name, Direction.OUTGOING).iterator();
			if(iterator.hasNext()){
				Node nameNode = iterator.next().getOtherNode(node);
				if(nameNode.getProperty(TYPE_FIELD).equals(AtomType.phrase.name())){
					return this.phraseStore.getAtomName(nameNode.getId());
				}
				else if(nameNode.getProperty(TYPE_FIELD).equals(AtomType.string.name())){
					return this.strStore.getAtomName(nameNode.getId());
				}
				else{
					return null;
				}
			}else{
				return (String) node.getProperty(NAME_FIELD);
			}
		}catch(NotFoundException e){
			throw getMissingAtomException(id, null, null, e);
		}
	}

	public T getAtomByName(String rawName) throws MissingAtomException, InvalidAtomException  {
		String name = rawName.toLowerCase();
		Node nameNode = nodeIndex.get(NAME_FIELD, name).getSingle();
		if(null == nameNode || !nameNode.hasRelationship(Direction.INCOMING, Bonds.name)){
			throw new MissingAtomException(-1, name, "");
		}
		Iterator<Relationship> iterator = nameNode.getRelationships(Bonds.name, Direction.INCOMING).iterator();
		while(iterator.hasNext()){
			long id = iterator.next().getOtherNode(nameNode).getId();
			try{
				return this.getSearchObject(id, name);
			}catch(InvalidAtomException e){
				//other atom type with same name might exist, proceed
			}
		}
		throw new MissingAtomException(-1, name, "");
	}

	public T createAtom(String rawName) throws InvalidAtomException, MissingAtomException {
		String name = rawName.toLowerCase();
		T atom = null;
		boolean createNewNode = false;
		try{
			atom = this.getAtomByName(name);
		}catch(MissingAtomException e){
			createNewNode = true;
		}
		if(createNewNode){
			Long nameNodeId;
			if(isPhrase(name)){
				nameNodeId = phraseStore.createAtom(name).getId();
			}else{
				nameNodeId = strStore.createAtom(name).getId();
			}
			Transaction tx = graphDb.beginTx();
			try{
				Node nameNode = graphDb.getNodeById(nameNodeId);
				Node node = graphDb.createNode();
				node.setProperty(TYPE_FIELD, this.getAtomType());
				tx.acquireWriteLock(nameNode);
				node.createRelationshipTo(nameNode, Bonds.name);
				tx.success();
				atom = this.getSearchObject(node.getId(), name);
			}
			finally{
				tx.finish();
			}
		}
		return atom;
	}

	@Override
	protected T getAtom(Node node) throws InvalidAtomException, MissingAtomException {
		if(!node.hasRelationship(Direction.OUTGOING, Bonds.name)){
			throw this.getInvalidAtomException(node.getId(), null, "", null);
		}
		String name = this.getAtomName(node.getId());
		return this.getSearchObject(node.getId(), name);
	}

	protected boolean isBonded(Node propNode, Node classNode, RelationshipType bond, Direction direction) {
		Iterator<Relationship> domainRelationships = propNode.getRelationships(bond, direction).iterator();
		boolean exists = false;
		while(domainRelationships.hasNext()){
			Node domainNode = domainRelationships.next().getOtherNode(propNode);
			if(domainNode.equals(classNode)){
				exists = true;
				break;
			}
		}
		return exists;
	}

	public void setRank(int i, T propAtom) throws MissingAtomException{
		Transaction tx = graphDb.beginTx();
		try{
			Node propNode = this.getAtomNode(propAtom);
			propNode.setProperty(RANK_FIELD, i);
			tx.success();
		}
		finally{
			tx.finish();
		}
	}

	public int getRank(T atom) throws MissingAtomException{
		Node propNode = this.getAtomNode(atom);
		return (int) propNode.getProperty(RANK_FIELD);
	}

	protected boolean isPhrase(String name){
		boolean isPhrase = false;
		if(null != name && name.length()>1){
			String[] tokens = name.trim().split("\\s+");
			if(tokens.length>1){
				isPhrase = true;
			}
		}
		return isPhrase;
	}

}
