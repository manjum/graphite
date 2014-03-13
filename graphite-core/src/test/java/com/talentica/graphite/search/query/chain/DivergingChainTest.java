package com.talentica.graphite.search.query.chain;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.talentica.graphite.atom.AndAtom;
import com.talentica.graphite.search.exception.InvalidLinkException;

public class DivergingChainTest extends ChainTest{
	
	public DivergingChainTest() {
		super();
	}

	@Before
	public void setup(){
		this.chain = new Chain(this.storeResources);
	}

	@Test
	public void testGetQueryPhrase(){
		Assert.assertEquals("", chain.getQueryPhrase());
		Assert.assertEquals("", chain.getDBQueryTemplate().getQueryString());
		assertGenericConnectionQuery();
	}

	@Test
	public void test0GetQueryPhrase() throws InvalidLinkException{
		chain.addLink(this.sushant);
		Assert.assertEquals("sushant", chain.getQueryPhrase());
		Assert.assertEquals("start obj1=node(100) return obj1;", chain.getDBQueryTemplate().getQueryString());
		Assert.assertEquals("start n=node(200),c1=node(100) match (n)-[r1]-(c1) where c1.type='domain_obj' return r1,c1;", chain.getConnectionQuery(1, 200));
	}

	@Test
	public void test1GetQueryPhraseForDivergingChain() throws InvalidLinkException{
		chain.addLink(this.sushant);
		chain.addLink(this.skill);
		Assert.assertEquals("sushant's skill", chain.getQueryPhrase());
		Assert.assertEquals("start obj1=node(100) match (obj1)-[:skill]->(obj3) return obj3;", chain.getDBQueryTemplate().getQueryString());
		Assert.assertEquals("start n=node(200),obj1=node(100) match (n)-[r1]-(c1),(obj1)-[:skill]->(c1) where c1.type='domain_obj' return r1,c1;", chain.getConnectionQuery(1, 200));
	}

	@Test
	public void test2GetQueryPhraseForDivergingChain() throws InvalidLinkException{
		chain.addLink(this.sushant);
		chain.addLink(this.friend);
		chain.addLink(this.candidate);
		Assert.assertEquals("sushant's friend whose", chain.getQueryPhrase());
		chain.addLink(this.location);
		Assert.assertEquals("",chain.getDBQueryTemplate().getQueryString());
		Assert.assertEquals("sushant's friend whose location", chain.getQueryPhrase());
		chain.addLink(this.pune);
		Assert.assertEquals("sushant's friend whose location is pune", chain.getQueryPhrase());
		chain.addLink(AndAtom.AND);
		chain.addLink(this.skill);
		chain.addLink(this.java);
		Assert.assertEquals("sushant's friend whose location is pune and whose skill is java", chain.getQueryPhrase());
		Assert.assertEquals("start obj2=node(1),obj5=node(21),obj9=node(98),obj1=node(100) match (obj2)<-[:type]-(obj3)-[:location]->(obj5),(obj3)-[:skill]->(obj9),(obj1)-[:friend]->(obj3) return obj3;",
				chain.getDBQueryTemplate().getQueryString());
		Assert.assertEquals("start n=node(200),obj2=node(1),obj5=node(21),obj9=node(98),obj1=node(100) match (n)-[r1]-(c1),(obj2)<-[:type]-(c1)-[:location]->(obj5),(c1)-[:skill]->(obj9),(obj1)-[:friend]->(c1) where c1.type='domain_obj' return r1,c1;",
				chain.getConnectionQuery(1, 200));
	}
}
