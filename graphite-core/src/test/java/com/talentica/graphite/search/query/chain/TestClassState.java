package com.talentica.graphite.search.query.chain;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.talentica.graphite.atom.ClassAtom;
import com.talentica.graphite.atom.ObjectAtom;
import com.talentica.graphite.atom.PropertyAtom;
import com.talentica.graphite.atom.StringAtom;
import com.talentica.graphite.search.exception.InvalidLinkException;
import com.talentica.graphite.search.query.chain.Chain;
import com.talentica.graphite.search.query.chain.ClassState;
import com.talentica.graphite.search.query.chain.ObjectState;
import com.talentica.graphite.search.query.chain.State;
import com.talentica.graphite.store.StoreResources;

public class TestClassState {
	private Chain chain;
	private StoreResources storeResources;

	@Before
	public void setup(){
		this.chain = new Chain(this.storeResources);
		ClassAtom classAtom = new ClassAtom("test");
		chain.add(classAtom);
	}

	@Test
	public void testAddLinkPropertyAtom() throws InvalidLinkException{
		PropertyAtom propAtom = new PropertyAtom("test");
		ClassState.CLASS.addLink(propAtom, this.chain);
	}

	@Test(expected = InvalidLinkException.class)
	public void testAddLinkClassAtom() throws InvalidLinkException{
		ClassAtom classAtom = new ClassAtom("test");
		State newState = ClassState.CLASS.addLink(classAtom, this.chain);
		Assert.assertEquals(ClassState.CLASS, newState);
	}

	@Test(expected = InvalidLinkException.class)
	public void testAddLinkObjectAtom() throws InvalidLinkException{
		ClassAtom classAtom = new ClassAtom("test");
		ObjectAtom objAtom = new ObjectAtom("test", classAtom);
		State newState = ClassState.CLASS.addLink(objAtom, this.chain);
		Assert.assertEquals(ObjectState.OBJECT, newState);
	}

	@Test(expected = InvalidLinkException.class)
	public void testAddLinkPhraseAtom() throws InvalidLinkException{
		StringAtom strAtom = new StringAtom(-1, "sushant");
		ClassState.CLASS.addLink(strAtom, this.chain);
	}
	
	@Test
	public void testIsCompleteWithOneAtom(){
		boolean isComplete = ClassState.CLASS.isComplete(this.chain);
		Assert.assertEquals(true, isComplete);
	}
	
	@Test
	public void testIsCompleteWithMoreThanOneAtom(){
		PropertyAtom propAtom = new PropertyAtom("test");
		this.chain.add(propAtom);
		boolean isComplete = ClassState.CLASS.isComplete(this.chain);
		Assert.assertEquals(false, isComplete);
	}
}
