package com.talentica.graphite.search.query.chain;

import junit.framework.Assert;

import org.junit.Test;

import com.talentica.graphite.atom.ClassAtom;
import com.talentica.graphite.atom.ObjectAtom;
import com.talentica.graphite.atom.PropertyAtom;
import com.talentica.graphite.atom.StringAtom;
import com.talentica.graphite.search.exception.InvalidLinkException;
import com.talentica.graphite.search.query.chain.Chain;
import com.talentica.graphite.search.query.chain.ClassState;
import com.talentica.graphite.search.query.chain.EmptyState;
import com.talentica.graphite.search.query.chain.ObjectState;
import com.talentica.graphite.search.query.chain.State;
import com.talentica.graphite.store.StoreResources;

public class TestEmptyState {
	private StoreResources storeResources;
	
	@Test
	public void testAddLinkClassAtom() throws InvalidLinkException{
		ClassAtom classAtom = new ClassAtom("test");
		Chain chain = new Chain(this.storeResources);
		State newState = EmptyState.EMPTY.addLink(classAtom, chain);
		Assert.assertEquals(ClassState.CLASS, newState);
	}
	
	@Test
	public void testAddLinkObjectAtom() throws InvalidLinkException{
		ClassAtom classAtom = new ClassAtom("test");
		ObjectAtom objAtom = new ObjectAtom("test", classAtom);
		Chain chain = new Chain(this.storeResources);
		State newState = EmptyState.EMPTY.addLink(objAtom, chain);
		Assert.assertEquals(ObjectState.OBJECT, newState);
	}
	
	@Test(expected = InvalidLinkException.class)
	public void testAddLinkPhraseAtom() throws InvalidLinkException{
		StringAtom strAtom = new StringAtom(-1, "sushant");
		Chain chain = new Chain(this.storeResources);
		EmptyState.EMPTY.addLink(strAtom, chain);
	}
	
	@Test(expected = InvalidLinkException.class)
	public void testAddLinkPropertyAtom() throws InvalidLinkException{
		Chain chain = new Chain(this.storeResources);
		PropertyAtom propAtom = new PropertyAtom("test");
		EmptyState.EMPTY.addLink(propAtom, chain);
	}
}
