package com.talentica.graphite.search.query.chain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.talentica.graphite.atom.Atom;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.search.exception.InvalidLinkException;
import com.talentica.graphite.store.StoreResources;

/**
 * @author sush
 *
 */
public class Chain {
	private State chainState;
	private ChainType chainType;
	private final StoreResources storeResources;
	private final Stack<Atom> queryChain = new Stack<Atom>();

	public Chain(StoreResources storeResources){
		this.chainState = EmptyState.EMPTY;
		this.storeResources = storeResources;
	}

	public Chain getCopy(){
		Chain copy = new Chain(this.storeResources);
		copy.chainState = this.chainState;
		copy.chainType = this.chainType;
		copy.queryChain.addAll(this.queryChain);
		return copy;
	}

	public void addLink(Atom link) throws InvalidLinkException{
		this.chainState = chainState.addLink(link, this);
	}

	public void removeLink() {
		State newState = chainState.removeLink(this);
		this.chainState = newState;
	}

	public String getQueryPhrase(){
		if(this.chainState.equals(EmptyState.EMPTY)){
			return "";
		}
		return QueryBuilder.getQueryPhraseBuilder(this.chainType).getQueryPhrase(this);
	}

	public List<Chain> getNextLevelChains() throws MissingAtomException, InvalidAtomException{
		List<Chain> chains = Collections.emptyList();
		Set<Atom> nextLinks = this.chainState.getNextLinks(this);
		if(nextLinks.size()>0){
			chains = new ArrayList<Chain>();
		}
		for(Atom link: nextLinks){
			try{
				Chain nextLevelChain = this.getCopy();
				nextLevelChain.addLink(link);
				chains.add(nextLevelChain);
			}catch(InvalidLinkException e){
				//no worries if the link can't be added to the chain
			}
		}
		return chains;
	}
	public QueryTemplate getDBQueryTemplate(){
		if(!this.chainState.isComplete(this)){
			return new QueryTemplate();
		}
		return QueryBuilder.getQueryPhraseBuilder(this.chainType).getDBQueryTemplate(this);
	}

	public String getConnectionQuery(int level, long nodeId){
		if(this.chainState.isComplete(this)){
			return QueryBuilder.getQueryPhraseBuilder(this.chainType).getConnectionQueryString(level, nodeId, this);
		}else{
			return QueryBuilder.getQueryPhraseBuilder(ChainType.generic).getConnectionQueryString(level, nodeId, this);
		}
	}

	public boolean isComplete(){
		return this.chainState.isComplete(this);
	}

	public Atom getTopLink(){
		return this.queryChain.peek();
	}
	void add(Atom atom){
		queryChain.add(atom);
	}

	Atom remove(){
		return queryChain.pop();
	}

	State getChainState() {
		return chainState;
	}

	ChainType getChainType() {
		return chainType;
	}

	void setChainType(ChainType chainType) {
		this.chainType = chainType;
	}

	Stack<Atom> getQueryChain() {
		return queryChain;
	}

	int getSize(){
		return this.queryChain.size();
	}

	public Atom getLink(int index){
		return this.queryChain.get(index);
	}

	StoreResources getStoreResources() {
		return storeResources;
	}


}
