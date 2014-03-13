package com.talentica.graphite.store;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Lock;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.atom.NumberAtom;
import com.talentica.graphite.atom.PhraseAtom;
import com.talentica.graphite.atom.StringAtom;
import com.talentica.graphite.bond.Bonds;
import com.talentica.graphite.bond.Position;
import com.talentica.graphite.bond.StringBonds;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.InvalidPhraseAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.exceptions.MissingPhraseAtomException;
import com.talentica.graphite.exceptions.MissingStringAtomException;


public class PhraseAtomStore extends AtomStore<PhraseAtom>{
	private StringAtomStore strStore;
	private NumberAtomStore numStore;
	public static final String SELECT_TEMPLATE = "n%s=node(%s)";
	public static final String NEXT_MATCH_TEMPLATE = "-[:next]->(n%s)";

	PhraseAtomStore(StoreResources storeResources) throws MissingAtomException, InvalidAtomException {
		super(storeResources);
	}

	@Override
	void init() throws InvalidAtomException, MissingAtomException {
		this.strStore = (StringAtomStore) storeResources.getAtomStore(AtomType.string);
		this.numStore = (NumberAtomStore) storeResources.getAtomStore(AtomType.number);
	}

	public long getStringClassAtomId(){
		return this.strStore.getStringClassAtomId();
	}

	public String getAtomName(long id) throws InvalidAtomException, MissingAtomException {
		String name = "";
		Node phraseNode = graphDb.getNodeById(id);
		String type = (String) phraseNode.getProperty(TYPE_FIELD);
		if(!AtomType.phrase.name().equals(type)){
			if(AtomType.string.name().equals(type)){
				return this.strStore.getAtomName(id);
			}
			throw new InvalidPhraseAtomException(null,id, "given node is not a phrase");
		}
		Long phraseLength = 0L;
		//get length of phrase
		Iterator<Relationship> lengthRelationships = phraseNode.getRelationships(StringBonds.length, Direction.OUTGOING).iterator();
		if(lengthRelationships.hasNext()){
			phraseLength = (Long) lengthRelationships.next().getOtherNode(phraseNode).getProperty(NAME_FIELD);
			if(lengthRelationships.hasNext()){
				throw new InvalidPhraseAtomException(null,id, "Phrase contains more than one length link"); 
			}
		}
		else{
			throw new InvalidPhraseAtomException(null,id, "Phrase does not contain a length link");
		}

		int pos = 0;
		while(pos < phraseLength){
			Iterator<Relationship> nextRelationships = phraseNode.getRelationships(Position.getPosLink(pos++), Direction.INCOMING).iterator();
			Node stringNode = nextRelationships.next().getOtherNode(phraseNode);
			name = name.concat((String)stringNode.getProperty(NAME_FIELD)).concat(" ");
		}
		return name.trim();
	}

	@Override
	protected MissingAtomException getMissingAtomException(
			long id, String name, String errMessage, Throwable cause) {
		return new MissingPhraseAtomException(name, id, errMessage, cause);
	}

	@Override
	protected InvalidAtomException getInvalidAtomException(
			long id, String name, String errMessage, Throwable cause) {
		return new InvalidPhraseAtomException(name, id, errMessage, cause);
	}

	@Override
	protected PhraseAtom getAtom(Node node) throws InvalidAtomException, MissingAtomException  {
		String name = this.getAtomName(node.getId());
		return new PhraseAtom(node.getId(), name);
	}

	public PhraseAtom getAtomByName(String name) throws InvalidAtomException, MissingAtomException  {
		String[] tokens = this.getTokens(name);
		boolean exists = true;
		Node[] tokenNodes = new Node[tokens.length];
		for(int i = 0; i < tokens.length; i++){
			Node tokenNode = nodeIndex.get(NAME_FIELD, tokens[i]).getSingle();
			if(null == tokenNode){
				exists = false;
				break;
			}
			tokenNodes[i] = tokenNode;
		}
		if(tokens.length == 1){
			return (PhraseAtom) this.strStore.createAtom(tokens[0]);
		}
		if(exists){
			ExecutionResult result = engine.execute(getQuery(tokenNodes));
			Iterator<Node> n_column = result.columnAs( "phr" );
			if(!n_column.hasNext()){
				throw new MissingPhraseAtomException(-1, name, "phrase with given name does not exist");
			}
			else{
				return this.getAtom(n_column.next());
			}
		}
		throw new MissingPhraseAtomException(-1, name, "phrase with given name does not exist");
	}

	public PhraseAtom createAtom(String name) throws InvalidAtomException, MissingAtomException {
		PhraseAtom phraseNode = null;
		try {
			return this.getAtomByName(name);
		} catch (MissingPhraseAtomException e) {
			//node does not exist, so proceed further and create it.
		} catch (MissingStringAtomException e) {
			//node does not exist, so proceed further and create it.
		} 

		String[] tokens = this.getTokens(name);
		List<StringAtom> tokenNodes = new ArrayList<StringAtom>(tokens.length);
		for(int i = 0; i < tokens.length; i++){
			tokenNodes.add(this.strStore.createAtom(tokens[i]));
		}

		Transaction tx = graphDb.beginTx();
		try
		{
			Node node = graphDb.createNode();
			node.setProperty(TYPE_FIELD, AtomType.phrase.name());
			Node startTokenNode = graphDb.getNodeById(tokenNodes.get(0).getId());
			startTokenNode.createRelationshipTo(node, Position.getPosLink(0));
			if(!node.hasRelationship(Direction.OUTGOING, Bonds.type)){
				node.createRelationshipTo(storeResources.getGraphDb().getNodeById(this.strStore.getStringClassAtomId()), Bonds.type);
			}
			for(int i = 1; i < tokenNodes.size(); i++){
				//acquire locks so as to maintain uniqueness.
				Lock startTokenLock = tx.acquireWriteLock(startTokenNode);
				Node nextTokenNode = graphDb.getNodeById(tokenNodes.get(i).getId());
				tx.acquireWriteLock(nextTokenNode);
				nextTokenNode.createRelationshipTo(node, Position.getPosLink(i));
				boolean linkExists = false;
				for(Relationship rel : startTokenNode.getRelationships(StringBonds.next)) {
					if(rel.getEndNode().equals(nextTokenNode) && rel.getStartNode().equals(startTokenNode)){
						linkExists = true;
						break;
					}
				}
				if(!linkExists){
					startTokenNode.createRelationshipTo(nextTokenNode, StringBonds.next);
				}
				startTokenLock.release();
				startTokenNode = nextTokenNode;
			}
			startTokenNode.createRelationshipTo(node, StringBonds.end);
			NumberAtom numNode = this.numStore.createAtom(String.valueOf(tokens.length));
			Node lengthValueNode = graphDb.getNodeById(numNode.getId());
			node.createRelationshipTo(lengthValueNode, StringBonds.length);
			tx.success();
			phraseNode = new PhraseAtom(node.getId(), name);
		}
		finally {
			tx.finish();
		}
		return phraseNode;
	}

	//TODO do not use cypher query
	private String getQuery(Node[] charNodes){
		String queryPrefix = "start";
		String query = "";
		query = query.concat(String.format(SELECT_TEMPLATE, 0, charNodes[0].getId()));
		String match = String.format("match (n%s)",0);
		for(int i=1;i< charNodes.length; i++){
			query = query.concat(",").concat(String.format(SELECT_TEMPLATE, i, charNodes[i].getId()));
			match = match.concat(String.format(NEXT_MATCH_TEMPLATE, i));
		}
		query = queryPrefix.concat(" ").concat(query).concat(" ").concat(match).concat("-[:end]->(phr)<-[:pos_0]-n0").concat(" ").concat("return phr;");
		return query;
	}
	private String[] getTokens(String name) throws InvalidPhraseAtomException{
		if(null != name && name.length()>0){
			String[] tokens = name.trim().split("\\s+");
			if(tokens.length <1){
				throw new InvalidPhraseAtomException(name, -1, "phrase name should have more than one string");
			}
			return tokens;
		}else{
			throw new InvalidPhraseAtomException(name, -1, "name can't be null or empty");
		}
	}
}
