package com.talentica.graphite.store;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.atom.PhraseAtom;
import com.talentica.graphite.bond.Bonds;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;

public class PhraseStoreTest extends StoreTest{
	private static PhraseAtomStore phrStore;
	private final static String testPhrase = "quick brown fox jumped over the lazy dog";
	private PhraseAtom phrAtom;
	
	@BeforeClass
	public static void setupDb() throws MissingAtomException, InvalidAtomException{
		phrStore = (PhraseAtomStore) storeResources.getAtomStore(AtomType.phrase);
	}
	
	@Before
	public void setup() throws InvalidAtomException, MissingAtomException{
		this.phrAtom = phrStore.createAtom(testPhrase);
	}
	@Test
	public void testCreateAtom() throws InvalidAtomException, MissingAtomException{
		PhraseAtom atom = phrStore.createAtom(testPhrase);
		validateAtom(atom);
		int nodeCount = getTotalNodeCount();
		
		atom = phrStore.createAtom(testPhrase);
		assertEquals(nodeCount, getTotalNodeCount());
		
		Node strNode = storeResources.getGraphDb().getNodeById(phrStore.getStringClassAtomId());
		Node expectedStrNode = storeResources.getGraphDb().getNodeById(this.phrAtom.getId());
		Iterator<Relationship> relationships = expectedStrNode.getRelationships(Direction.OUTGOING, Bonds.type).iterator();
		assertTrue(relationships.hasNext());
		assertEquals(strNode, relationships.next().getOtherNode(expectedStrNode));
		assertTrue(!relationships.hasNext());
	}
	
	@Test
	public void test2CreateAtom() throws InvalidAtomException, MissingAtomException{
		int nodeCount = getTotalNodeCount();
		phrStore.createAtom("quick");
		assertEquals(nodeCount, getTotalNodeCount());
		
	}
	
	@Test
	public void testGetAtomName() throws InvalidAtomException, MissingAtomException  {
		String name = phrStore.getAtomName(phrStore.createAtom(testPhrase).getId());
		assertEquals(testPhrase, name);
	}
	private void validateAtom(PhraseAtom atom){
		assertEquals(testPhrase, atom.getName());
		assertEquals(AtomType.phrase, atom.getAtomType());
	}
}
