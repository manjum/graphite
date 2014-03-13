package com.talentica.graphite.api.search;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.talentica.graphite.api.index.ObjectNode;
import com.talentica.graphite.atom.AndAtom;
import com.talentica.graphite.atom.ClassAtom;
import com.talentica.graphite.atom.ObjectAtom;
import com.talentica.graphite.atom.PropertyAtom;
import com.talentica.graphite.atom.StringAtom;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.search.Connections;
import com.talentica.graphite.search.GraphiteTest;
import com.talentica.graphite.search.exception.InvalidLinkException;
import com.talentica.graphite.search.query.chain.Chain;
import com.talentica.graphite.store.ObjectAtomStore;

public class ObjectSearcherTest extends GraphiteTest{
	private ObjectSearcher objSearcher;
	private QuerySuggestor querySuggestor;

	@Before
	public void setup() throws MissingAtomException, InvalidAtomException {
		this.objSearcher = new ObjectSearcher(storeResources);
		this.querySuggestor = new QuerySuggestor(storeResources);
	}

	@Test
	public void test1Search() throws MissingAtomException, InvalidAtomException, InvalidLinkException{
		ClassAtom candidate = classStore.getAtomByName("candidate");
		PropertyAtom skill = propStore.getAtomByName("skill");
		StringAtom java = strStore.getAtomByName("java");
		Chain queryChain = new Chain(storeResources);
		queryChain.addLink(candidate);
		queryChain.addLink(skill);
		queryChain.addLink(java);
		Query query = new Query("test", queryChain, null);
		List<ObjectNode> searchResult = this.objSearcher.search(query);
		Assert.assertEquals(2, searchResult.size());
		for(ObjectNode objNode: searchResult){
			boolean found = objNode.getName().equals("sushant pradhan") || objNode.getName().equals("sushant rajput"); 
			Assert.assertEquals(true, found);
		}
	}

	@Test
	public void testGetConnectionsWithFilterDivergingQuery() throws MissingAtomException, InvalidAtomException, InvalidLinkException{
		ObjectAtomStore objectAtomStore = objStore;
		ObjectAtom nk = objectAtomStore.getAtomByName("nadeem khan");
		testConnections(nk, getDivergingQuery(), false);
	}

	@Test
	public void test1GetConnectionsWithFilterDivergingQuery() throws MissingAtomException, InvalidAtomException, InvalidLinkException{
		ObjectAtomStore objectAtomStore = objStore;
		ObjectAtom nk = objectAtomStore.getAtomByName("rahul mehra");
		Chain dg = new Chain(storeResources);
		ObjectAtom sp = objStore.getAtomByName("sushant pradhan");
		PropertyAtom friend = propStore.getAtomByName("friend");
		dg.addLink(sp);
		dg.addLink(friend);
		Query q =  new Query("", dg, null);
		testConnections(nk, q, true);
	}

	@Test
	public void testGetConnectionsWithFilterQuery() throws MissingAtomException, InvalidAtomException, InvalidLinkException{
		ObjectAtomStore objectAtomStore = objStore;
		ObjectAtom nk = objectAtomStore.getAtomByName("nadeem khan");
		Query query = getSkillQuery();
		testConnections(nk, query, false);
	}

	@Test
	public void testGetConnectionsWithoutFilterQuery() throws MissingAtomException, InvalidAtomException, InvalidLinkException{
		ObjectAtomStore objectAtomStore = objStore;
		ObjectAtom nk = objectAtomStore.getAtomByName("nadeem khan");
		ObjectAtom sp = objStore.getAtomByName("sushant pradhan");
		Connections connections = objSearcher.getConnections(1, nk.getId(), null);
		Assert.assertEquals(connections.getConnections().size(), 4);
		Assert.assertEquals(true,connections.getConnections().contains(sp.getId()));
		Assert.assertTrue(3.0 == connections.getConnectionStrength(sp.getId()));
		Assert.assertTrue(1 == connections.getRoutes(sp.getId()).size());
		Assert.assertTrue(1 == connections.getRoutes(sp.getId()).iterator().next().getLevel());
	}


	@Test
	public void testsearch() throws MissingAtomException, InvalidAtomException, InvalidLinkException{
		System.out.println(storeResources.getGraphDb().hashCode());
		System.out.println(storeResources.getGraphDb().getNodeById(54));

		List<Query> queries = querySuggestor.getQuery("can");
		for(Query query: queries){
			List<Query> qs = query.expand();
			for(Query q1 : qs){
				System.out.println(q1.getHumanReadableQueryString() + "--" + q1.getQueryChain().getDBQueryTemplate().getQueryString());
				List<ObjectNode> results = objSearcher.search(q1);
				for(ObjectNode result: results){
					System.out.println("	" + result);
					//					try{
					//						System.out.println("skill = " + this.objSearcher.getValue(result, "skill"));
					//					}catch(MissingAtomException e){

					//					}
				}
			}
		}
	}

	@Test
	public void testAndQuery() throws MissingAtomException, InvalidAtomException, InvalidLinkException{
		System.out.println(this.objSearcher.search(getAndQuery()));
	}

	@Test
	public void testConnectionsWithAndQuery() throws MissingAtomException, InvalidAtomException, InvalidLinkException{
		ObjectAtomStore objectAtomStore = objStore;
		ObjectAtom nk = objectAtomStore.getAtomByName("nadeem khan");
		Query query = getAndQuery();
		testConnections(nk, query, false);
		System.out.println();
	}
	private Query getAndQuery() throws MissingAtomException, InvalidAtomException, InvalidLinkException{
		Chain chain = new Chain(storeResources);
		ClassAtom candidate = classStore.getAtomByName("candidate");
		ObjectAtom sr = objStore.getAtomByName("sushant rajput");
		PropertyAtom skill = propStore.getAtomByName("skill");
		PropertyAtom friend = propStore.getAtomByName("friend");
		StringAtom java = strStore.getAtomByName("java");
		chain.addLink(candidate);
		chain.addLink(skill);
		chain.addLink(java);
		chain.addLink(AndAtom.AND);
		chain.addLink(friend);
		chain.addLink(sr);
		return new Query("", chain, null);
	}

	private Query getSkillQuery() throws MissingAtomException, InvalidAtomException, InvalidLinkException{
		PropertyAtom skill = propStore.getAtomByName("skill");
		StringAtom java = strStore.getAtomByName("java");
		ClassAtom candidate = classStore.getAtomByName("candidate");
		Chain queryChain = new Chain(storeResources);
		queryChain.addLink(candidate);
		queryChain.addLink(skill);
		queryChain.addLink(java);
		return new Query("test", queryChain, null);
	}

	private Query getDivergingQuery() throws MissingAtomException, InvalidAtomException, InvalidLinkException{
		Chain dg = new Chain(storeResources);
		ObjectAtom sp = objStore.getAtomByName("sushant pradhan");
		dg.addLink(sp);
		return new Query("", dg, null);
	}

	private void testConnections(ObjectAtom obj, Query query, boolean print)
			throws MissingAtomException, InvalidAtomException {
		ObjectAtom sp = objStore.getAtomByName("sushant pradhan");
		Connections connections = objSearcher.getConnections(1, obj.getId(), query);
		if(!print){
			Assert.assertEquals(connections.getConnections().size(), 1);
			Assert.assertEquals(true,connections.getConnections().contains(sp.getId()));
			Assert.assertTrue(3.0 == connections.getConnectionStrength(sp.getId()));
			Assert.assertTrue(1 == connections.getRoutes(sp.getId()).size());
			Assert.assertTrue(1 == connections.getRoutes(sp.getId()).iterator().next().getLevel());
		}else{
			connections.print();
			connections.printConnectionStrength();
		}

		connections = objSearcher.getConnections(2, obj.getId(), query);
		connections.print();
		connections.printConnectionStrength();

		connections = objSearcher.getConnections(3, obj.getId(), query);
		connections.print();
		connections.printConnectionStrength();

		connections = objSearcher.getConnections(4, obj.getId(), query);
		connections.print();
		connections.printConnectionStrength();
	}
}