package com.talentica.graphite.search.query.chain;

import junit.framework.Assert;

import com.talentica.graphite.atom.ClassAtom;
import com.talentica.graphite.atom.ObjectAtom;
import com.talentica.graphite.atom.PropertyAtom;
import com.talentica.graphite.atom.StringAtom;
import com.talentica.graphite.store.StoreResources;

abstract class ChainTest {
	protected Chain chain;
	protected ClassAtom candidate;
	protected PropertyAtom skill;
	protected StringAtom java;
	protected PropertyAtom friend;
	protected ObjectAtom sushant;
	protected PropertyAtom location;
	protected StringAtom pune;
	protected StoreResources storeResources;

	public ChainTest() {
		this.candidate = new ClassAtom(1, "candidate");
		this.skill = new PropertyAtom("skill", 17);
		this.java = new StringAtom(98, "java");
		this.friend = new PropertyAtom("friend", 45);
		this.sushant = new ObjectAtom("sushant", candidate, 100);
		this.location = new PropertyAtom("location", 18);
		this.pune = new StringAtom(21, "pune");
	}

	protected void assertGenericConnectionQuery() {
		Assert.assertEquals("start n=node(200) match (n)-[r1]-(c1) where c1.type='domain_obj' return r1,c1;",
				chain.getConnectionQuery(1, 200));
		Assert.assertEquals("start n=node(200) match (n)-[r1]-(c1)-[r2]-(c2) where c1.type='domain_obj' and c2.type='domain_obj' return r1,c1,r2,c2;",
				chain.getConnectionQuery(2, 200));
		Assert.assertEquals("start n=node(200) match (n)-[r1]-(c1)-[r2]-(c2)-[r3]-(c3) where c1.type='domain_obj' and c2.type='domain_obj' and c3.type='domain_obj' return r1,c1,r2,c2,r3,c3;",
				chain.getConnectionQuery(3, 200));
	}
}
