package com.talentica.graphite.search.query.chain;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.talentica.graphite.atom.AndAtom;
import com.talentica.graphite.search.exception.InvalidLinkException;

public class ConvergingChainTest extends ChainTest{

	public ConvergingChainTest() {
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
		chain.addLink(candidate);
		Assert.assertEquals("candidate", chain.getQueryPhrase());
		Assert.assertEquals("start obj0=node(1) match (obj0)<-[:type]-(obj1) return obj1;", chain.getDBQueryTemplate().getQueryString());
		Assert.assertEquals("start n=node(200),obj0=node(1) match (n)-[r1]-(c1),(obj0)<-[:type]-(c1) where c1.type='domain_obj' return r1,c1;",
				chain.getConnectionQuery(1, 200));
		Assert.assertEquals("start n=node(200),obj0=node(1) match (n)-[r1]-(c1)-[r2]-(c2),(obj0)<-[:type]-(c2) where c1.type='domain_obj' and c2.type='domain_obj' return r1,c1,r2,c2;",
				chain.getConnectionQuery(2, 200));
		Assert.assertEquals("start n=node(200),obj0=node(1) match (n)-[r1]-(c1)-[r2]-(c2)-[r3]-(c3),(obj0)<-[:type]-(c3) where c1.type='domain_obj' and c2.type='domain_obj' and c3.type='domain_obj' return r1,c1,r2,c2,r3,c3;",
				chain.getConnectionQuery(3, 200));
	}

	@Test
	public void test1GetQueryPhrase() throws InvalidLinkException{
		chain.addLink(candidate);
		chain.addLink(skill);
		Assert.assertEquals("candidate whose skill is", chain.getQueryPhrase());
		Assert.assertEquals("", chain.getDBQueryTemplate().getQueryString());
		assertGenericConnectionQuery();
	}

	@Test
	public void test2GetQueryPhrase() throws InvalidLinkException{
		chain.addLink(candidate);
		chain.addLink(skill);
		chain.addLink(java);
		Assert.assertEquals("candidate whose skill is java", chain.getQueryPhrase());
		Assert.assertEquals("start obj0=node(1),obj3=node(98) match (obj0)<-[:type]-(obj1)-[:skill]->(obj3) return obj1;", chain.getDBQueryTemplate().getQueryString());
		Assert.assertEquals("start n=node(200),obj0=node(1),obj3=node(98) match (n)-[r1]-(c1),(obj0)<-[:type]-(c1)-[:skill]->(obj3) where c1.type='domain_obj' return r1,c1;",
				chain.getConnectionQuery(1, 200));
		Assert.assertEquals("start n=node(200),obj0=node(1),obj3=node(98) match (n)-[r1]-(c1)-[r2]-(c2),(obj0)<-[:type]-(c2)-[:skill]->(obj3) where c1.type='domain_obj' and c2.type='domain_obj' return r1,c1,r2,c2;",
				chain.getConnectionQuery(2, 200));
		Assert.assertEquals("start n=node(200),obj0=node(1),obj3=node(98) match (n)-[r1]-(c1)-[r2]-(c2)-[r3]-(c3),(obj0)<-[:type]-(c3)-[:skill]->(obj3) where c1.type='domain_obj' and c2.type='domain_obj' and c3.type='domain_obj' return r1,c1,r2,c2,r3,c3;",
				chain.getConnectionQuery(3, 200));
	}

	@Test
	public void test3GetQueryPhrase() throws InvalidLinkException{
		chain.addLink(candidate);
		chain.addLink(skill);
		chain.addLink(java);
		chain.addLink(AndAtom.AND);
		chain.addLink(friend);
		Assert.assertEquals("candidate whose skill is java and whose friend is", chain.getQueryPhrase());
		chain.addLink(sushant);
		Assert.assertEquals("candidate whose skill is java and whose friend is sushant", chain.getQueryPhrase());
		Assert.assertEquals("start obj0=node(1),obj3=node(98),obj7=node(100) match (obj0)<-[:type]-(obj1)-[:skill]->(obj3),(obj1)-[:friend]->(obj7) return obj1;",
				chain.getDBQueryTemplate().getQueryString());

		Assert.assertEquals("start n=node(200),obj0=node(1),obj3=node(98),obj7=node(100) match (n)-[r1]-(c1),(obj0)<-[:type]-(c1)-[:skill]->(obj3),(c1)-[:friend]->(obj7) where c1.type='domain_obj' return r1,c1;",
				chain.getConnectionQuery(1, 200));
		Assert.assertEquals("start n=node(200),obj0=node(1),obj3=node(98),obj7=node(100) match (n)-[r1]-(c1)-[r2]-(c2),(obj0)<-[:type]-(c2)-[:skill]->(obj3),(c2)-[:friend]->(obj7) where c1.type='domain_obj' and c2.type='domain_obj' return r1,c1,r2,c2;",
				chain.getConnectionQuery(2, 200));
	}

	@Test
	public void test4GetQueryPhrase() throws InvalidLinkException{
		chain.addLink(candidate);
		chain.addLink(friend);
		chain.addLink(candidate);
		chain.addLink(skill);
		chain.addLink(java);
		chain.addLink(AndAtom.AND);
		chain.addLink(friend);
		chain.addLink(sushant);
		Assert.assertEquals("candidate whose friend is a candidate whose skill is java and whose friend is sushant",chain.getQueryPhrase());
		Assert.assertEquals("start obj0=node(1),obj5=node(98),obj9=node(100) match (obj0)<-[:type]-(obj1)-[:friend]->(obj3)-[:skill]->(obj5),(obj3)-[:friend]->(obj9) return obj1;",
				chain.getDBQueryTemplate().getQueryString());
		Assert.assertEquals("start n=node(200),obj0=node(1),obj5=node(98),obj9=node(100) match (n)-[r1]-(c1),(obj0)<-[:type]-(c1)-[:friend]->(obj3)-[:skill]->(obj5),(obj3)-[:friend]->(obj9) where c1.type='domain_obj' return r1,c1;",
				chain.getConnectionQuery(1, 200));
	}
}
