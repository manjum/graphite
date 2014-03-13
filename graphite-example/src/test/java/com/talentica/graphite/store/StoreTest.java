package com.talentica.graphite.store;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.neo4j.graphdb.Node;
import org.neo4j.kernel.impl.util.FileUtils;
import org.neo4j.tooling.GlobalGraphOperations;

import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
public class StoreTest {
	public static final String DB_PATH = "/TalenticaWorkspace/Tlabs/graphite_data/testdata";
	public static final String DB_PROPERTYFILE_PATH = "/TalenticaWorkspace/TLabs/graphite-data/conf/neo4j.properties";
	protected static StoreResources storeResources;

	@BeforeClass
	public static void setupDB() throws IOException, MissingAtomException, InvalidAtomException {
		File file = new File(DB_PATH);
		FileUtils.deleteRecursively(file);
		storeResources = new TestStoreResources(StoreTest.DB_PATH, StoreTest.DB_PROPERTYFILE_PATH);
		storeResources.init();
	}

	@AfterClass
	public static void clean() throws IOException {
		storeResources.destroy();
	}

	protected int getTotalNodeCount(){
		Iterable<Node> allNodes = GlobalGraphOperations.at(storeResources.getGraphDb()).getAllNodes();
		int i=0;
		Iterator<Node> iterator = allNodes.iterator();
		while(iterator.hasNext()){
			i++;
			iterator.next();
		}
		return i;
	}

}
