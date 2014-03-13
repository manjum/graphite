package com.talentica.graphite.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.atom.NumberAtom;
import com.talentica.graphite.bond.Bonds;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.InvalidNumberAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.exceptions.MissingNumberAtomException;

public class NumberStoreTest extends StoreTest{
	private static NumberAtomStore numStore;
	private NumberAtom numAtom;
	private static final String testNum = "42";
	
	
	@Before
	public void setup() throws InvalidAtomException, MissingAtomException{
		numStore = (NumberAtomStore) storeResources.getAtomStore(AtomType.number);
		this.numAtom = numStore.createAtom(testNum);
	}
	
	@Test(expected = InvalidNumberAtomException.class)
	public void testCreateAtom() throws InvalidAtomException{
		numStore.createAtom("er");
	}
	
	@Test
	public void test2CreateAtom() throws InvalidAtomException{
		NumberAtom atom = numStore.createAtom(testNum);
		assertNotNull(atom);
		assertEquals(AtomType.number, atom.getAtomType());
		assertEquals(testNum, atom.getName());
		
		Node numNode = storeResources.getGraphDb().getNodeById(numStore.getNumberClassNodeId());
		Node expectedNode = storeResources.getGraphDb().getNodeById(this.numAtom.getId());
		Iterator<Relationship> relationships = expectedNode.getRelationships(Direction.OUTGOING, Bonds.type).iterator();
		assertTrue(relationships.hasNext());
		assertEquals(numNode, relationships.next().getOtherNode(expectedNode));
		assertTrue(!relationships.hasNext());
		
		int nodeCount = getTotalNodeCount();
		numStore.createAtom(testNum);
		assertEquals(nodeCount, getTotalNodeCount());
	}
	
	@Test(expected=InvalidNumberAtomException.class)
	public void test3CreateAtom() throws InvalidAtomException{
		numStore.createAtom("4c");
	}
	
	@Test
	public void testGetAtomByName() throws InvalidNumberAtomException, MissingNumberAtomException{
		NumberAtom atom = numStore.getAtomByName(testNum);
		assertEquals(testNum, atom.getName());
	}
	
	@Test(expected=InvalidNumberAtomException.class)
	public void test2GetAtomByName() throws InvalidNumberAtomException, MissingNumberAtomException{
		numStore.getAtomByName("4c");
	}
	
	@Test(expected=MissingNumberAtomException.class)
	public void test3GetAtomByName() throws InvalidNumberAtomException, MissingNumberAtomException{
		numStore.getAtomByName("54");
	}

	@Test
	public void testGetAtomName() throws InvalidNumberAtomException, MissingNumberAtomException{
		String atomName = numStore.getAtomName(this.numAtom.getId());
		assertEquals(testNum, atomName);
	}
	
	@Test(expected=MissingNumberAtomException.class)
	public void test3GetAtomName() throws InvalidNumberAtomException, MissingNumberAtomException{
		numStore.getAtomName(-1);
	}

	@Test
	public void testGetAtomById() throws MissingAtomException, InvalidAtomException{
		numStore.getAtomById(1);
	}
	
	@Test(expected=MissingNumberAtomException.class)
	public void test2GetAtomById() throws MissingAtomException, InvalidAtomException{
		numStore.getAtomById(-11);
	}
	
	@Test(expected=InvalidNumberAtomException.class)
	public void test3GetAtomById() throws MissingAtomException, InvalidAtomException{
		numStore.getAtomById(0);
	}
	
}
