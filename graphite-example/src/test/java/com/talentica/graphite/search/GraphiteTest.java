package com.talentica.graphite.search;

import java.io.File;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.neo4j.kernel.impl.util.FileUtils;

import com.talentica.graphite.api.exception.GraphiteAnnotationParseException;
import com.talentica.graphite.api.exception.ObjectWriteException;
import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.data.DataPopulator;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.store.ClassAtomStore;
import com.talentica.graphite.store.ObjectAtomStore;
import com.talentica.graphite.store.PhraseAtomStore;
import com.talentica.graphite.store.PropertyAtomStore;
import com.talentica.graphite.store.StoreResources;
import com.talentica.graphite.store.StoreTest;
import com.talentica.graphite.store.StringAtomStore;
import com.talentica.graphite.store.TestStoreResources;

public class GraphiteTest {
	protected static final String DB_PATH = "/TalenticaWorkspace/Tlabs/graphite_data/testdata";
	protected static final String DB_PROPERTYFILE_PATH = "/TalenticaWorkspace/Tlabs/graphite_data/conf/neo4j.properties";
	protected static StoreResources storeResources;
	protected static PropertyAtomStore propStore;
	protected static ClassAtomStore classStore;
	protected static StringAtomStore strStore;
	protected static ObjectAtomStore objStore;
	protected static PhraseAtomStore phrStore;

	@BeforeClass
	public static void setupDB() throws IOException, MissingAtomException, InvalidAtomException, GraphiteAnnotationParseException, IllegalArgumentException, IllegalAccessException, ObjectWriteException {
		File file = new File(DB_PATH);
		FileUtils.deleteRecursively(file);
		storeResources = new TestStoreResources(StoreTest.DB_PATH, StoreTest.DB_PROPERTYFILE_PATH);
		storeResources.init();

		classStore = (ClassAtomStore) storeResources.getAtomStore(AtomType.domain_class);
		objStore = (ObjectAtomStore) storeResources.getAtomStore(AtomType.domain_obj);
		propStore = (PropertyAtomStore) storeResources.getAtomStore(AtomType.domain_prop);
		strStore = (StringAtomStore) storeResources.getAtomStore(AtomType.string);
		phrStore = (PhraseAtomStore) storeResources.getAtomStore(AtomType.phrase);
		populateTestData();
	}

	@AfterClass
	public static void clean() throws IOException {
		storeResources.destroy();
	}

	private static void populateTestData() throws MissingAtomException, InvalidAtomException, GraphiteAnnotationParseException, IllegalArgumentException, IllegalAccessException, ObjectWriteException{
		DataPopulator dataPopulator = new DataPopulator(storeResources);
		dataPopulator.populateData();
	}
}
