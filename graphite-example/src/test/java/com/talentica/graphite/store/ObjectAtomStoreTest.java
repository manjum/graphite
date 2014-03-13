package com.talentica.graphite.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.talentica.graphite.atom.Atom;
import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.atom.ClassAtom;
import com.talentica.graphite.atom.ObjectAtom;
import com.talentica.graphite.atom.PropertyAtom;
import com.talentica.graphite.bond.Bonds;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;

public class ObjectAtomStoreTest extends StoreTest{
	private static PropertyAtomStore propStore;
	private static ClassAtomStore classStore;
	private static ObjectAtomStore objStore;
	private static StringAtomStore strStore;
	private static NumberAtomStore numStore;

	@BeforeClass
	public static void setup() throws InvalidAtomException, MissingAtomException{
		numStore = (NumberAtomStore) storeResources.getAtomStore(AtomType.number);
		strStore = (StringAtomStore) storeResources.getAtomStore(AtomType.string);
		classStore = (ClassAtomStore) storeResources.getAtomStore(AtomType.domain_class);
		propStore = (PropertyAtomStore) storeResources.getAtomStore(AtomType.domain_prop);
		objStore = (ObjectAtomStore) storeResources.getAtomStore(AtomType.domain_obj);
	}

	@Test
	public void testCreateAtom() throws InvalidAtomException, MissingAtomException{
		ClassAtom intClass = classStore.createAtom(AtomType.number.name());
		ClassAtom classAtom = classStore.createAtom("candidate");
		classStore.setRank(1, classAtom);
		PropertyAtom propAtom = propStore.createAtom("age");
		Atom numAtom = numStore.createAtom("28");
		propStore.addRange(propAtom, intClass);
		propStore.addDomain(propAtom, classAtom);
		classStore.addProperty(classAtom, propAtom);


		ObjectAtom objAtom = objStore.createAtom("sushant", classAtom);
		validate(objAtom, classAtom);
		objStore.addProperty(objAtom, propAtom, numAtom);

		int nodeCount = getTotalNodeCount();
		//check for duplicacy
		objStore.createAtom("sushant", classAtom);
		assertEquals(nodeCount, getTotalNodeCount());

		nodeCount = getTotalNodeCount();
		ObjectAtom objAtom1 = objStore.createAtom("sushant pradhan", classAtom);
		validate(objAtom1, classAtom);
		assertEquals("sushant pradhan", objAtom1.getName());
		assertEquals(nodeCount+4, getTotalNodeCount());
		nodeCount = getTotalNodeCount();

		objStore.createAtom("sushant rajput", classAtom);
		assertEquals(nodeCount+3, getTotalNodeCount());

	}

	//	@Test(expected=InvalidAtomException.class)
	public void test2CreateAtom() throws InvalidAtomException, MissingAtomException{
		ClassAtom strClass = classStore.createAtom(AtomType.string.name());
		ClassAtom classAtom = classStore.createAtom("candidate");
		PropertyAtom propAtom = propStore.createAtom("education");
		Atom numAtom = strStore.createAtom("NITC");
		propStore.addRange(propAtom, strClass);
		//		propStore.addDomain(propAtom, classAtom);
		//		classStore.addProperty(classAtom, propAtom);

		ObjectAtom objAtom = objStore.createAtom("sushant", classAtom);
		validate(objAtom, classAtom);
		objStore.addProperty(objAtom, propAtom, numAtom);
	}

	//	@Test
	public void TestGetAtomByName() throws MissingAtomException, InvalidAtomException{
		ObjectAtom sushant = objStore.getAtomByName("sushant");
		assertEquals("sushant", sushant.getName());

		ObjectAtom sushantpradhan = objStore.getAtomByName("sushant pradhan");
		assertEquals("sushant pradhan", sushantpradhan.getName());
	}

	private void validate(ObjectAtom objAtom, ClassAtom classAtom){
		Node node = storeResources.getGraphDb().getNodeById(objAtom.getId());
		Node classNode = storeResources.getGraphDb().getNodeById(classAtom.getId());
		assertTrue(node.hasRelationship(Direction.OUTGOING, Bonds.type));
		assertTrue(classNode.hasRelationship(Direction.INCOMING, Bonds.type));
		Iterator<Relationship> iterator = node.getRelationships(Direction.OUTGOING, Bonds.type).iterator();
		assertEquals(iterator.next().getOtherNode(node), classNode);
		assertTrue(!iterator.hasNext());
	}
}
