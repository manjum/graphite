package com.talentica.graphite.search.query.chain;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.talentica.graphite.atom.AndAtom;
import com.talentica.graphite.atom.ClassAtom;
import com.talentica.graphite.atom.ObjectAtom;
import com.talentica.graphite.atom.PropertyAtom;
import com.talentica.graphite.atom.StringAtom;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.search.exception.InvalidLinkException;
import com.talentica.graphite.search.query.chain.Chain;
import com.talentica.graphite.search.query.chain.ClassState;
import com.talentica.graphite.search.query.chain.ObjectState;
import com.talentica.graphite.search.query.chain.State;
import com.talentica.graphite.store.StoreResources;

public class TestObjectState {
	private StoreResources storeResources;
	private Chain chain;

	@Before
	public void setup() throws MissingAtomException, InvalidAtomException{
		chain = new Chain(storeResources);

		ClassAtom classAtom = new ClassAtom("test");
		ObjectAtom objAtom = new ObjectAtom("test", classAtom);
		chain.add(objAtom);
	}

	@Test
	public void testAddLinkPropertyAtom() throws InvalidLinkException{
		PropertyAtom propAtom = new PropertyAtom("test");
		ObjectState.OBJECT.addLink(propAtom, this.chain);
		Assert.assertEquals(2, chain.getSize());
	}

	@Test
	public void testAddLinkConjuctionAtomUnsuccesful() throws InvalidLinkException{
		State state = ObjectState.OBJECT.addLink(AndAtom.AND, this.chain);
		Assert.assertEquals(ObjectState.OBJECT, state);
		Assert.assertEquals(1, chain.getSize());
	}

	@Test
	public void testAddLinkConjuctionAtomSuccessful() throws InvalidLinkException{
		chain = new Chain(storeResources);
		ClassAtom classAtom = new ClassAtom("test");
		PropertyAtom prop = new PropertyAtom("tstProp"); 
		ObjectAtom objAtom = new ObjectAtom("test", classAtom);
		chain.add(classAtom);
		chain.add(prop);
		chain.add(objAtom);
		State state = ObjectState.OBJECT.addLink(AndAtom.AND, chain);
		Assert.assertEquals(ClassState.CLASS, state);
		Assert.assertEquals(5, chain.getSize());
	}

	@Test(expected = InvalidLinkException.class)
	public void testAddLinkClassAtom() throws InvalidLinkException{
		ClassAtom classAtom = new ClassAtom("test");
		ObjectState.OBJECT.addLink(classAtom, this.chain);
	}

	@Test(expected = InvalidLinkException.class)
	public void testAddLinkObjectAtom() throws InvalidLinkException{
		ClassAtom classAtom = new ClassAtom("test");
		ObjectAtom objAtom = new ObjectAtom("test", classAtom);
		ObjectState.OBJECT.addLink(objAtom, this.chain);
	}

	@Test(expected = InvalidLinkException.class)
	public void testAddLinkPhraseAtom() throws InvalidLinkException{
		StringAtom strAtom = new StringAtom(-1, "sushant");
		ObjectState.OBJECT.addLink(strAtom, this.chain);
	}

	@Test
	public void testIsCompleteWithOneAtom(){
		boolean isComplete = ObjectState.OBJECT.isComplete(this.chain);
		Assert.assertEquals(true, isComplete);
	}

	@Test
	public void testIsCompleteWithTwoAtoms(){
		PropertyAtom propAtom = new PropertyAtom("test");
		this.chain.add(propAtom);
		boolean isComplete = ObjectState.OBJECT.isComplete(this.chain);
		Assert.assertEquals(true, isComplete);
	}
}
