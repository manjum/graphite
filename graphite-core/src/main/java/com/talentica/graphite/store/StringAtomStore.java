package com.talentica.graphite.store;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.WildcardQuery;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.IndexHits;

import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.atom.StringAtom;
import com.talentica.graphite.bond.Bonds;
import com.talentica.graphite.bond.Position;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.InvalidStringAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.exceptions.MissingStringAtomException;


public class StringAtomStore extends AtomStore<StringAtom>{
	private long stringClassAtomId = -1;
	StringAtomStore(StoreResources storeResources) {
		super(storeResources);
		Transaction tx = storeResources.getGraphDb().beginTx();
		try{
			Node strNode = nodeFactory.getOrCreate(NAME_FIELD, AtomType.string.name());
			strNode.setProperty(TYPE_FIELD, AtomType.string.name());
			Iterator<Relationship> iterator = strNode.getRelationships(Direction.INCOMING, Bonds.name).iterator();
			if(!iterator.hasNext()){
				Node stringClassNode = graphDb.createNode();
				stringClassNode.setProperty(TYPE_FIELD, AtomType.domain_class.name());
				stringClassNode.createRelationshipTo(strNode, Bonds.name);
				this.stringClassAtomId = stringClassNode.getId();
			}
			else{
				this.stringClassAtomId = iterator.next().getOtherNode(strNode).getId();
			}
			tx.success();
		}
		finally{
			tx.finish();
		}
	}

	public long getStringClassAtomId() {
		return stringClassAtomId;
	}

	public void setStringClassAtomId(long stringClassAtomId) {
		this.stringClassAtomId = stringClassAtomId;
	}

	private String getName(String rawString){
		return rawString.toLowerCase().trim();
	}

	public String getAtomName(long id) throws InvalidStringAtomException, MissingStringAtomException {
		try{
			Node node = graphDb.getNodeById(id);
			return getAtom(node).getName();
		}
		catch(NotFoundException e){
			throw new MissingStringAtomException(null, id, "no string atom with the given id exists", e);
		}

	}

	public StringAtom getAtomByName(String name) throws MissingStringAtomException, InvalidStringAtomException {
		Node node = nodeIndex.get(NAME_FIELD, name).getSingle();
		if(null == node){
			throw new MissingStringAtomException(-1, name, "this string does not exist in store");
		}
		StringAtom strNode = getAtom(node);
		return strNode;

	}

	public StringAtom createAtomIndexed(String rawName) throws InvalidStringAtomException {
		String name = getName(rawName);
		Transaction tx = graphDb.beginTx();
		StringAtom strAtom = null;
		try
		{
			Node stringClassNode = graphDb.getNodeById(stringClassAtomId);
			Node node = nodeFactory.getOrCreate( NAME_FIELD, name);
			tx.acquireWriteLock(node);
			node.setProperty(TYPE_FIELD, AtomType.string.name());
			if(!node.hasRelationship(Direction.OUTGOING, Bonds.type)){
				node.createRelationshipTo(stringClassNode, Bonds.type);
			}
			tx.success();
			strAtom = new StringAtom(node.getId(), name);
		}
		finally{
			tx.finish();
		}
		return strAtom;
	}

	public Set<StringAtom> getStringAtoms(String regex){
		Set<StringAtom> atoms = Collections.emptySet();
		IndexHits<Node> hits = nodeIndex.query( new WildcardQuery( new Term( NAME_FIELD, regex ) ) );
		if(hits.hasNext()){
			atoms = new HashSet<>();
		}
		for ( Node node : hits )
		{
			atoms.add(new StringAtom(node.getId(), (String)node.getProperty(NAME_FIELD)));
		}
		return atoms;

	}

	public StringAtom createAtom(String rawName) throws InvalidStringAtomException {
		String name = getName(rawName);
		Transaction tx = graphDb.beginTx();
		StringAtom strAtom = null;
		try
		{
			Node stringClassNode = graphDb.getNodeById(stringClassAtomId);
			Node node = nodeFactory.getOrCreate( NAME_FIELD, name);
			tx.acquireWriteLock(node);
			node.setProperty(TYPE_FIELD, AtomType.string.name());
			if(!node.hasRelationship(Direction.OUTGOING, Bonds.type)){
				node.createRelationshipTo(stringClassNode, Bonds.type);
			}
			tx.success();
			strAtom = new StringAtom(node.getId(), name);
		}
		finally{
			tx.finish();
		}
		return strAtom;
	}

	@Override
	protected MissingAtomException getMissingAtomException(
			long id, String name, String errMessage, Throwable cause) {
		return new MissingStringAtomException(name, id, errMessage, cause);
	}

	@Override
	protected InvalidAtomException getInvalidAtomException(
			long id, String name, String errMessage, Throwable cause) {
		return new InvalidStringAtomException(name, id, errMessage, cause);
	}

	@Override
	protected StringAtom getAtom(Node node) throws InvalidStringAtomException {
		String type = (String)node.getProperty(TYPE_FIELD);
		StringAtom strNode = new StringAtom(node.getId(), (String)node.getProperty(NAME_FIELD));

		if(!type.equals(AtomType.string.name())){
			throw new InvalidStringAtomException(strNode, "this is not a string node"); 
		}
		return strNode;
	}

	public Set<Position> getPositions(StringAtom strAtom) throws MissingStringAtomException{
		Set<Position> positions = new HashSet<Position>();
		try{
			Node node = graphDb.getNodeById(strAtom.getId());
			for(int i=0; i<Position.cieling; i++){
				Position pos = Position.getPosLink(i);
				Iterator<Relationship> relationships = node.getRelationships(Direction.OUTGOING, pos).iterator();
				if(relationships.hasNext()){
					positions.add(pos);
				}
			}
		}catch(NotFoundException e)
		{
			throw new MissingStringAtomException(strAtom, "invalid id", e); 
		}
		return positions;
	}

	@Override
	void init() {
		return;
	}
}
