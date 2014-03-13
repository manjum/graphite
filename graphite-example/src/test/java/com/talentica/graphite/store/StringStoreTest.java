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
import com.talentica.graphite.atom.StringAtom;
import com.talentica.graphite.bond.Bonds;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.InvalidStringAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.exceptions.MissingStringAtomException;

public class StringStoreTest extends StoreTest{
	private static StringAtomStore strStore;
	private final String testString = "candidate";
	private StringAtom atom;

	@BeforeClass
	public static void setupDb() throws InvalidStringAtomException, MissingAtomException{
		strStore = (StringAtomStore) storeResources.getAtomStore(AtomType.string);
	}

	@Before
	public void setup() throws InvalidStringAtomException{
		this.atom = strStore.createAtom(testString);
	}

	@Test
	public void testCreateAtom() throws InvalidStringAtomException {
		StringAtom atom = strStore.createAtom(testString);
		validate(atom);
		Node strNode = storeResources.getGraphDb().getNodeById(atom.getId());
		//				storeResources.getNodeIndex().get(Store.NAME_FIELD, AtomType.string.name()).getSingle();
		Node strClassNode = storeResources.getGraphDb().getNodeById(strStore.getStringClassAtomId());
		Iterator<Relationship> relationships = strNode.getRelationships(Direction.OUTGOING, Bonds.type).iterator();
		assertTrue(relationships.hasNext());
		assertEquals(strNode, relationships.next().getOtherNode(strClassNode));
		assertTrue(!relationships.hasNext());


		int nodeCount = getTotalNodeCount();

		this.atom = strStore.createAtom(testString);
		validate(this.atom);
		assertEquals(nodeCount, getTotalNodeCount());
	}


	@Test
	public void test2CreateAtom() throws InvalidStringAtomException {
		StringAtom atom = strStore.createAtom(testString);
		validate(atom);
		int nodeCount = getTotalNodeCount();

		atom = strStore.createAtom(testString);
		validate(atom);
		assertEquals(nodeCount, getTotalNodeCount());
	}

	@Test
	public void testgetAtomById() throws MissingAtomException, InvalidAtomException {
		StringAtom atom = strStore.getAtomById(this.atom.getId());
		validate(atom);
	}

	@Test(expected=MissingAtomException.class)
	public void test2getAtomById() throws MissingAtomException, InvalidAtomException {
		strStore.getAtomById(59);
	}

	@Test
	public void testgetAtomByName() throws MissingStringAtomException, InvalidStringAtomException {
		StringAtom atom = strStore.getAtomByName(testString);
		validate(atom);
		assertEquals(testString, atom.getName());
	}

	@Test(expected=MissingAtomException.class)
	public void test1getAtomByName() throws MissingStringAtomException, InvalidStringAtomException {
		strStore.getAtomByName("testk");
	}

	@Test
	public void testgetAtomName() throws InvalidStringAtomException, MissingStringAtomException {
		String name = strStore.getAtomName(this.atom.getId());
		assertEquals(testString, name);
	}

	@Test(expected=MissingAtomException.class)
	public void test2getAtomName() throws InvalidStringAtomException, MissingStringAtomException {
		strStore.getAtomName(100);
	}

	private void validate(StringAtom atom) {
		assertEquals(testString, this.atom.getName());
		assertEquals(AtomType.string, this.atom.getAtomType());
	}
}
