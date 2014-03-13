package com.talentica.graphite.search;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.talentica.graphite.atom.ClassAtom;
import com.talentica.graphite.atom.ObjectAtom;
import com.talentica.graphite.atom.PropertyAtom;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;

public class AtomSearcherTest extends GraphiteTest{
	private AtomSearcher atomSearcher;

	@Before
	public void setup() throws MissingAtomException, InvalidAtomException{
		atomSearcher = new AtomSearcher(storeResources);
	}

	@Test
	public void testGetClassAtoms() throws InvalidAtomException, MissingAtomException{
		Set<ClassAtom> classAtoms = this.atomSearcher.getClassAtoms("can");
		System.out.println(classAtoms);
		System.out.println(this.atomSearcher.getRelatedClassAtoms(classAtoms.iterator().next()));
	}

	@Test
	public void testGetObjectAtoms() throws InvalidAtomException, MissingAtomException{
		Set<ObjectAtom> objAtoms = this.atomSearcher.getObjectAtoms("sushant");
		System.out.println(objAtoms);

	}

	@Test
	public void testGetPropertyAtoms() throws InvalidAtomException, MissingAtomException{
		Set<PropertyAtom> propAtoms = this.atomSearcher.getPropertyAtoms("skill");
		System.out.println(propAtoms);

	}
}
