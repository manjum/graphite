package com.talentica.graphite.api.search;

import java.util.ArrayList;
import java.util.List;

import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.queryadvisor.QueryAdvisor;
import com.talentica.graphite.search.exception.InvalidLinkException;
import com.talentica.graphite.search.query.chain.Chain;


public class Query {
	private final String keyword;
	private final Chain chain;
	private final QueryAdvisor queryAdvisor;

	Query(String keyword, Chain queryChain, QueryAdvisor queryAdvisor) {
		this.keyword = keyword;
		this.chain = queryChain;
		this.queryAdvisor = queryAdvisor;
	}

	public List<Query> expand() throws InvalidAtomException, MissingAtomException, InvalidLinkException{
		List<Chain> nextLevelChains = this.queryAdvisor.getNextLevelChains(this.chain);
		List<Query> queries = new ArrayList<Query>(nextLevelChains.size());
		for(Chain qChain: nextLevelChains){
			queries.add(new Query(this.keyword, qChain, this.queryAdvisor));
		}
		return queries;
	}
	
	public void contract() {
		this.chain.removeLink();
	}

	public String getHumanReadableQueryString() throws InvalidAtomException, MissingAtomException{
		return this.chain.getQueryPhrase();
	}
	
	Chain getQueryChain(){
		return this.chain;
	}
	
}
