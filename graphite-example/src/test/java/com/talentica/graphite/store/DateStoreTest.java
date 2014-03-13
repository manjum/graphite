package com.talentica.graphite.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.atom.DateAtom;
import com.talentica.graphite.bond.Bonds;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.InvalidDateAtomException;
import com.talentica.graphite.exceptions.InvalidNumberAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.exceptions.MissingDateAtomException;

public class DateStoreTest extends StoreTest{
	private static DateAtomStore dateStore;
	private final String testDate = "12";
	private DateAtom dateAtom;
	
	@BeforeClass
	public static void setupDb() throws MissingAtomException, InvalidAtomException{
		dateStore = (DateAtomStore) storeResources.getAtomStore(AtomType.date);
	}
	
	@Before
	public void setup() throws InvalidAtomException, MissingAtomException{
		this.dateAtom = dateStore.createAtom(testDate);
	}
	
	@Test
	public void testCreateAtom() throws InvalidAtomException, MissingAtomException{
		DateAtom atom = dateStore.createAtom(testDate);
		validate(atom);
		int nodeCount = getTotalNodeCount();
		
		atom = dateStore.createAtom(testDate);
		assertEquals(nodeCount, getTotalNodeCount());
		
		Node numNode = storeResources.getGraphDb().getNodeById(dateStore.getDateClassAtomId());
		Node expectedNode = storeResources.getGraphDb().getNodeById(this.dateAtom.getId());
		Iterator<Relationship> relationships = expectedNode.getRelationships(Direction.OUTGOING, Bonds.type).iterator();
		assertTrue(relationships.hasNext());
		assertEquals(numNode, relationships.next().getOtherNode(expectedNode));
		assertTrue(!relationships.hasNext());
	}
	
	@Test(expected=InvalidNumberAtomException.class)
	public void test2CreateAtom() throws InvalidAtomException, MissingAtomException{
		dateStore.createAtom("12c");
	}

	@Test
	public void testGetAtomName() throws InvalidNumberAtomException, InvalidDateAtomException, MissingDateAtomException{
		assertEquals(testDate, dateStore.getAtomName(this.dateAtom.getId()));
	}
	
	@Test(expected=InvalidDateAtomException.class)
	public void test1GetAtomName() throws InvalidNumberAtomException, InvalidDateAtomException, MissingDateAtomException{
		assertEquals(testDate, dateStore.getAtomName(1));
	}
	
	@Test(expected=MissingDateAtomException.class)
	public void test2GetAtomName() throws InvalidNumberAtomException, InvalidDateAtomException, MissingDateAtomException{
		assertEquals(testDate, dateStore.getAtomName(300));
	}
	
	@Test
	public void testGetAtomById() throws MissingAtomException, InvalidAtomException{
		DateAtom atom = dateStore.getAtomById(this.dateAtom.getId());
		validate(atom);
	}

	private void validate(DateAtom atom) {
		assertEquals(testDate, atom.getName());
		assertEquals(AtomType.date, atom.getAtomType());
		assertEquals(this.dateAtom.getId(), atom.getId());
	}
	
	@Test(expected=InvalidAtomException.class)
	public void test2GetAtomById() throws MissingAtomException, InvalidAtomException{
		dateStore.getAtomById(1);
	}
	@Test(expected=MissingAtomException.class)
	public void test3GetAtomById() throws MissingAtomException, InvalidAtomException{
		dateStore.getAtomById(-4);
	}
	
	@Test
	public void testGetAtomByName() throws InvalidAtomException, MissingAtomException{
		DateAtom atom = dateStore.getAtomByName(testDate);
		validate(atom);
	}
	
	@Test(expected=InvalidNumberAtomException.class)
	public void test2GetAtomByName() throws InvalidAtomException, MissingAtomException{
		DateAtom atom = dateStore.getAtomByName("12c");
		validate(atom);
	}
}
