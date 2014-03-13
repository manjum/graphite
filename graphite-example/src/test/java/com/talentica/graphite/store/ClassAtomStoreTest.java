package com.talentica.graphite.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;

import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.atom.ClassAtom;
import com.talentica.graphite.atom.PropertyAtom;
import com.talentica.graphite.bond.Bonds;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;

public class ClassAtomStoreTest extends StoreTest{
	private static ClassAtomStore classStore;
	private static PropertyAtomStore propStore;

	@BeforeClass
	public static void setupDb() throws MissingAtomException, InvalidAtomException{
		classStore = (ClassAtomStore) storeResources.getAtomStore(AtomType.domain_class);
		propStore = (PropertyAtomStore) storeResources.getAtomStore(AtomType.domain_prop);
	}

	@Before
	public void init() throws InvalidAtomException, MissingAtomException{
		classStore.createAtom("candidate");
	}
	@Test
	public void testCreateAtom() throws InvalidAtomException, MissingAtomException{
		ClassAtom strClass = classStore.createAtom("string");
		validate(strClass);
		ClassAtom intClass = classStore.createAtom("integer");
		//create a candidate class
		ClassAtom candidateClass = classStore.createAtom("candidate");
		//add property to this class
		PropertyAtom prop = propStore.createAtom("age");
		propStore.addRange(prop, intClass);
		classStore.addProperty(candidateClass, prop);
		validate(candidateClass);


		//check for duplicacy
		int nodeCount = getTotalNodeCount();
		candidateClass = classStore.createAtom("Candidate");
		assertEquals(nodeCount, getTotalNodeCount());
		assertTrue(!candidateClass.getProperties().isEmpty());
		assertTrue(candidateClass.getProperties().contains(prop));
	}

	@Test
	public void testGetAtomName() throws MissingAtomException, InvalidAtomException{
		ClassAtom strClass = classStore.getAtomByName("string");
		validate(strClass);
		assertEquals("string", strClass.getName());
	}
	@Test

	public void testGetAtomById() throws MissingAtomException, InvalidAtomException{
		ClassAtom expected = classStore.getAtomByName("string");
		ClassAtom actual = classStore.getAtomById(expected.getId());
		assertEquals(expected, actual);
	}
	@Test
	public void TestGetAtomByName() throws MissingAtomException, InvalidAtomException{
		ClassAtom classAtom = classStore.getAtomByName("candidate");
		validate(classAtom);
		assertEquals("candidate", classAtom.getName());

	}

	private void validate(ClassAtom atom){
		Node node = storeResources.getGraphDb().getNodeById(atom.getId());
		assertTrue(node.hasRelationship(Direction.OUTGOING, Bonds.name));
		assertTrue(node.hasProperty(Store.TYPE_FIELD));
		assertEquals(AtomType.domain_class.name(), node.getProperty(Store.TYPE_FIELD));
	}

}
