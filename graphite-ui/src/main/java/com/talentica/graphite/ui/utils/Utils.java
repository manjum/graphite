package com.talentica.graphite.ui.utils;

import java.io.File;
import java.io.IOException;

import org.neo4j.kernel.impl.util.FileUtils;

import com.talentica.graphite.api.exception.GraphiteAnnotationParseException;
import com.talentica.graphite.api.exception.ObjectWriteException;
import com.talentica.graphite.api.index.ObjectNodeWriter;
import com.talentica.graphite.api.search.ObjectSearcher;
import com.talentica.graphite.api.search.QuerySuggestor;
import com.talentica.graphite.data.DataPopulator;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.queryadvisor.QueryAdvisor;
import com.talentica.graphite.search.AtomSearcher;
import com.talentica.graphite.store.StoreResources;

public class Utils {
	protected static final String DB_PATH = "/TalenticaWorkspace/Tlabs/graphite_data/talentpool";
	protected static final String DB_PROPERTYFILE_PATH = "neo4j.properties";
	private static StoreResources storeResources;
	private static QueryAdvisor queryAdvisor;
	private static QuerySuggestor qSuggestor;
	private static ObjectSearcher objSearcher;
	private static ObjectNodeWriter objNodeWriter;
	public static void init() throws MissingAtomException, InvalidAtomException, GraphiteAnnotationParseException, IOException, IllegalArgumentException, IllegalAccessException, ObjectWriteException{
		File file = new File(DB_PATH);
		FileUtils.deleteRecursively(file);
		storeResources = new StoreResources(DB_PATH, DB_PROPERTYFILE_PATH);
		storeResources.init();
		AtomSearcher atomSearcher = new AtomSearcher(storeResources);
		queryAdvisor = new QueryAdvisor(atomSearcher, storeResources);
		qSuggestor = new QuerySuggestor(storeResources);
		objSearcher = new ObjectSearcher(storeResources);
		objNodeWriter = new ObjectNodeWriter(storeResources);
		DataPopulator dataPopulator = new DataPopulator(storeResources);
		dataPopulator.populateData();
	}

	public static ObjectNodeWriter getObjNodeWriter() {
		return objNodeWriter;
	}

	public static void setObjNodeWriter(ObjectNodeWriter objNodeWriter) {
		Utils.objNodeWriter = objNodeWriter;
	}

	public static ObjectSearcher getObjSearcher() {
		return objSearcher;
	}

	public static void setObjSearcher(ObjectSearcher objSearcher) {
		Utils.objSearcher = objSearcher;
	}

	public static StoreResources getStoreResources(){
		return storeResources;
	}

	public static QueryAdvisor getQueryAdvisor() {
		return queryAdvisor;
	}

	public static QuerySuggestor getqSuggestor() {
		return qSuggestor;
	}

	public static void setqSuggestor(QuerySuggestor qSuggestor) {
		Utils.qSuggestor = qSuggestor;
	}

	public static void setQueryAdvisor(QueryAdvisor queryAdvisor) {
		Utils.queryAdvisor = queryAdvisor;
	}

	public static void main(String[] args) throws MissingAtomException, InvalidAtomException, GraphiteAnnotationParseException, IOException, IllegalArgumentException, IllegalAccessException, ObjectWriteException {
		Utils.init();
		System.out.println("complete");
	}
}
