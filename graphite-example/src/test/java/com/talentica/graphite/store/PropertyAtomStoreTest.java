package com.talentica.graphite.store;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.atom.PropertyAtom;
import com.talentica.graphite.bond.Bonds;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;

public class PropertyAtomStoreTest extends StoreTest{
	private static PropertyAtomStore propStore;
	private static ClassAtomStore classStore;
	private static StringAtomStore strStore;
	private PropertyAtom prop;
	private final static String testPropName = "age"; 

	@BeforeClass
	public static void setupDb() throws MissingAtomException, InvalidAtomException{
		strStore = (StringAtomStore) storeResources.getAtomStore(AtomType.string);
		classStore = (ClassAtomStore) storeResources.getAtomStore(AtomType.domain_class);
		propStore = (PropertyAtomStore) storeResources.getAtomStore(AtomType.domain_prop);
	}

	@Before
	public void setup() throws InvalidAtomException, MissingAtomException{
		this.prop = propStore.createAtom(testPropName);
	}
	@Test
	public void testCreateAtom() throws InvalidAtomException, MissingAtomException{
		PropertyAtom prop = propStore.createAtom(testPropName);
		validate(prop);

		//check for duplicacy
		int nodeCount = getTotalNodeCount();
		prop = propStore.createAtom("Age");
		assertEquals(nodeCount, getTotalNodeCount());
	}

	@Test
	public void testGetAtomName(){

	}
	@Test

	public void testGetAtomById(){

	}
	@Test
	public void TestGetDomian() throws MissingAtomException, InvalidAtomException{
		PropertyAtom prop = propStore.getAtomById(this.prop.getId());
		Node propNode = storeResources.getGraphDb().getNodeById(prop.getId());
		Node strClassNode = storeResources.getGraphDb().getNodeById(strStore.getStringClassAtomId());
		assertTrue(!propNode.hasRelationship(Direction.OUTGOING, Bonds.domain));
		propStore.addDomain(prop, classStore.getAtomByName(AtomType.string.name()));
		assertTrue(propNode.hasRelationship(Direction.OUTGOING, Bonds.domain));
		assertTrue(!propNode.hasRelationship(Direction.OUTGOING, Bonds.range));
		Iterator<Relationship> iterator = propNode.getRelationships(Direction.OUTGOING, Bonds.domain).iterator();
		assertEquals(strClassNode, iterator.next().getOtherNode(propNode));
	}

	@Test
	public void TestGetRange() throws MissingAtomException, InvalidAtomException{
		PropertyAtom prop = propStore.getAtomById(this.prop.getId());
		Node propNode = storeResources.getGraphDb().getNodeById(prop.getId());
		Node strClassNode = storeResources.getGraphDb().getNodeById(strStore.getStringClassAtomId());
		assertTrue(!propNode.hasRelationship(Direction.OUTGOING, Bonds.range));
		propStore.addRange(prop, classStore.getAtomByName(AtomType.string.name()));
		assertTrue(propNode.hasRelationship(Direction.OUTGOING, Bonds.range));
		Iterator<Relationship> iterator = propNode.getRelationships(Direction.OUTGOING, Bonds.range).iterator();
		assertEquals(strClassNode, iterator.next().getOtherNode(propNode));

	}

	private void validate(PropertyAtom prop){
		Node node = storeResources.getGraphDb().getNodeById(prop.getId());
		assertTrue(node.hasRelationship(Direction.OUTGOING, Bonds.name));
		assertTrue(node.hasProperty(Store.TYPE_FIELD));
		assertEquals(AtomType.domain_prop.name(), (String)node.getProperty(Store.TYPE_FIELD));
		assertEquals(testPropName, prop.getName());
		assertEquals(this.prop.getId(), prop.getId());
	}

}
