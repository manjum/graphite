package com.talentica.graphite.store;

import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.UniqueFactory;

import com.talentica.graphite.atom.Atom;
import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;

abstract class AtomStore<T extends Atom> implements Store<T>{

	protected final StoreResources storeResources;
	protected final GraphDatabaseService graphDb;
	protected final ExecutionEngine engine;
	protected final Index<Node> nodeIndex;
	protected final UniqueFactory<Node> nodeFactory;

	protected abstract MissingAtomException getMissingAtomException(long id, String name, String errMessage, Throwable cause);
	protected abstract InvalidAtomException getInvalidAtomException(long id, String name, String errMessage, Throwable cause);
	protected abstract T getAtom(Node node) throws InvalidAtomException, MissingAtomException;
	abstract void init() throws InvalidAtomException, MissingAtomException;

	protected AtomStore(StoreResources storeResources) {
		this.storeResources = storeResources;
		this.graphDb = storeResources.getGraphDb();
		this.engine = storeResources.getEngine();
		this.nodeIndex = storeResources.getNodeIndex();
		Transaction tx = graphDb.beginTx();
		try
		{
			UniqueFactory<Node> factory = new UniqueFactory.UniqueNodeFactory( graphDb, Store.ATOM_INDEX_NAME )
			{
				@Override
				protected void initialize( Node created, Map<String, Object> properties )
				{
					created.setProperty( Store.NAME_FIELD, properties.get( Store.NAME_FIELD ) );
				}
			};
			this.nodeFactory = factory;
			tx.success();
		}
		finally{
			tx.finish();
		}
	}

	public T getAtomById(long id) throws MissingAtomException, InvalidAtomException {
		try{
			Node node = graphDb.getNodeById(id);
			return this.getAtom(node);
		}catch (NotFoundException e) {
			throw getMissingAtomException(id, null, "node with the given id does not exist", e);
		}
	}
	protected Node getAtomNode(Atom atom) throws MissingAtomException{
		try{
			return graphDb.getNodeById(atom.getId());
		}catch(NotFoundException e){
			throw new MissingAtomException(atom, atom.getAtomType().name() + " atom does not exist");
		}
	}

	public Atom getAtom(long id) throws MissingAtomException, InvalidAtomException{
		Node node = this.graphDb.getNodeById(id);
		AtomType atomType = AtomType.valueOf((String)node.getProperty(TYPE_FIELD));
		return this.storeResources.getAtomStore(atomType).getAtom(node);
	}
}
