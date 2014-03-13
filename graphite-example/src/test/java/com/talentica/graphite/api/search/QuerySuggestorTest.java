package com.talentica.graphite.api.search;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.search.GraphiteTest;
import com.talentica.graphite.search.exception.InvalidLinkException;


public class QuerySuggestorTest extends GraphiteTest{
	private QuerySuggestor querySuggestor;

	@Before
	public void setup() throws MissingAtomException, InvalidAtomException {
		this.querySuggestor = new QuerySuggestor(storeResources);
	}

	@Test
	public void testGetQuery() throws MissingAtomException, InvalidAtomException, InvalidLinkException{
		List<Query> queries = querySuggestor.getQuery("can");
		for(Query query: queries){
			System.out.println(query.getHumanReadableQueryString());
		}
	}

	@Test
	public void testGetQueryWithPropertyName() throws MissingAtomException, InvalidAtomException, InvalidLinkException{
		List<Query> queries = querySuggestor.getQuery("skill");
		for(Query query: queries){
			System.out.println(query.getHumanReadableQueryString());
		}
	}

	@Test
	public void testExpandQuery() throws InvalidAtomException, MissingAtomException, InvalidLinkException{
		System.out.println("##########");
		List<Query> queries1 = querySuggestor.getQuery("workedat");
		for(Query query1: queries1){
			System.out.println(query1.getHumanReadableQueryString() + query1.getQueryChain().getDBQueryTemplate().getQueryString());
			List<Query> queries2 = query1.expand();
			for(Query query2: queries2){
				System.out.println("	" + query2.getHumanReadableQueryString());
			}
		}
	}

	@Test
	public void testContractQuery() throws InvalidAtomException, MissingAtomException, InvalidLinkException{
		System.out.println("##########");
		List<Query> queries = querySuggestor.getQuery("can").get(0).expand();
		for(Query query: queries){
			query.contract();
			System.out.println(query.getHumanReadableQueryString());
		}
	}
}
