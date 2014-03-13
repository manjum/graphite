package com.talentica.graphite.store;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;

import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;

/**
 * @author sush
 *
 */
public class StoreResources {
	private StringAtomStore strStore;
	private DateAtomStore dateStore;
	private NumberAtomStore numStore;
	private PhraseAtomStore phraseStore;
	private ClassAtomStore classStore;
	private ObjectAtomStore objStore;
	private PropertyAtomStore propStore;
	protected final String dbPath; 
	protected final String dbPropFile;
	protected GraphDatabaseService graphDb;
	protected ExecutionEngine engine;
	protected Index<Node> nodeIndex;



	public StoreResources(String dbPath, String dbPropFile) {
		this.dbPath = dbPath;
		this.dbPropFile = dbPropFile;
	}

	public void init() throws MissingAtomException, InvalidAtomException, IOException{
		Properties properties = new Properties();
		Map<String, String> config = new HashMap<String, String>();
		if(null != this.dbPropFile){
			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(this.dbPropFile));
			for (final String name: properties.stringPropertyNames())
			    config.put(name, properties.getProperty(name));
		}
		this.graphDb = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder( this.dbPath ).
				setConfig(config).newGraphDatabase();
		this.engine = new ExecutionEngine(graphDb);
		this.nodeIndex = graphDb.index().forNodes(Store.ATOM_INDEX_NAME);
		createStores();
		initStores();
	}

	protected void createStores() throws MissingAtomException,
	InvalidAtomException {
		this.strStore = new StringAtomStore(this);
		this.phraseStore = new PhraseAtomStore(this);
		this.classStore = new ClassAtomStore(this);
		this.propStore = new PropertyAtomStore(this);
		this.objStore = new ObjectAtomStore(this);
		this.numStore = new NumberAtomStore(this);
		this.dateStore = new DateAtomStore(this);
	}

	protected void initStores() throws InvalidAtomException, MissingAtomException{
		this.strStore.init();
		this.phraseStore.init();
		this.classStore.init();
		this.propStore.init();
		this.objStore.init();
		this.numStore.init();
		this.dateStore.init();
	}

	public GraphDatabaseService getGraphDb() {
		return graphDb;
	}

	public ExecutionEngine getEngine() {
		return engine;
	}

	public Index<Node> getNodeIndex() {
		return nodeIndex;
	}

	public void destroy(){
		Transaction tx = this.graphDb.beginTx();
		try{
			this.nodeIndex.delete();
			tx.success();
		}
		finally{
			tx.finish();
		}
		this.graphDb.shutdown();
	}

	@SuppressWarnings("rawtypes")
	public AtomStore getAtomStore(AtomType atomType){
		switch (atomType) {
		case date:
			return this.dateStore;
		case number:
			return this.numStore;
		case string:
			return this.strStore;
		case phrase:
			return this.phraseStore;
		case domain_class:
			return this.classStore;
		case domain_obj:
			return this.objStore;
		case domain_prop:
			return this.propStore;
		default:
			return null;
		}
	}
}