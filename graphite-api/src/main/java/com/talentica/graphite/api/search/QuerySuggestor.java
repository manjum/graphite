package com.talentica.graphite.api.search;

import java.util.ArrayList;
import java.util.List;

import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.queryadvisor.QueryAdvisor;
import com.talentica.graphite.search.AtomSearcher;
import com.talentica.graphite.search.exception.InvalidLinkException;
import com.talentica.graphite.search.query.chain.Chain;
import com.talentica.graphite.store.StoreResources;

public class QuerySuggestor {
	private final QueryAdvisor queryAdvisor;

	public QuerySuggestor(StoreResources storeResources) throws MissingAtomException, InvalidAtomException {
		AtomSearcher classAtomSearcher = new AtomSearcher(storeResources);
		this.queryAdvisor = new QueryAdvisor(classAtomSearcher, storeResources);
	}

	public List<Query> getQuery(String keyword) throws InvalidAtomException, MissingAtomException, InvalidLinkException{
		List<Chain> chains = queryAdvisor.getChains(keyword);
		List<Query> queries = new ArrayList<Query>(chains.size());
		for(Chain chain : chains){
			Query query = new Query(keyword, chain, this.queryAdvisor);
			queries.add(query);
		}
		return queries;
	}
}
