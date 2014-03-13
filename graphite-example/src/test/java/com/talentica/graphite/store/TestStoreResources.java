package com.talentica.graphite.store;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.test.TestGraphDatabaseFactory;

import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;

public class TestStoreResources extends StoreResources{

	public TestStoreResources(String dbPath, String dbPropFile) {
		super(dbPath, dbPropFile);
	}
	
	public void init() throws InvalidAtomException, MissingAtomException{
		this.graphDb = new TestGraphDatabaseFactory().newImpermanentDatabase();
		this.engine = new ExecutionEngine(graphDb);
		this.nodeIndex = graphDb.index().forNodes(Store.ATOM_INDEX_NAME);
		super.createStores();
		super.initStores();
	}

}
