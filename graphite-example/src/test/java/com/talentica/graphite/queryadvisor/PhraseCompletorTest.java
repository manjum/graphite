package com.talentica.graphite.queryadvisor;

import static junit.framework.Assert.assertEquals;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.search.GraphiteTest;

public class PhraseCompletorTest extends GraphiteTest{
	private static final String TEST_STRING = "quick brown fox jumped over a lazy dog";
	private static PhraseCompletor underTest;
	
	
	@Before
	public void setup() throws MissingAtomException, InvalidAtomException{
		underTest = new PhraseCompletor(storeResources);
		phrStore.createAtom(TEST_STRING);
	}

	@Test
	public void test1GetPhrasesBeginningWith() throws InvalidAtomException, MissingAtomException  {
		Set<String> phrasesBeginningWith = underTest.getPhrasesBeginningWith("quick ");
		assertEquals(false, phrasesBeginningWith.isEmpty());
		assertEquals(1, phrasesBeginningWith.size());
		assertEquals(TEST_STRING, phrasesBeginningWith.iterator().next());
	}
	
	public void test2GetPhrasesBeginningWith() throws InvalidAtomException, MissingAtomException  {
		Set<String> phrasesBeginningWith = underTest.getPhrasesBeginningWith("brown");
		assertEquals(true, phrasesBeginningWith.isEmpty());
	}
	

	@Test
	public void test4GetPhrasesBeginningWith() throws InvalidAtomException, MissingAtomException  {
		Set<String> phrasesBeginningWith = underTest.getPhrasesBeginningWith("qu ");
		assertEquals(false, phrasesBeginningWith.isEmpty());
		assertEquals(1, phrasesBeginningWith.size());
		assertEquals(TEST_STRING, phrasesBeginningWith.iterator().next());
	}
	
	@Test
	public void test1GetPhrasesContaining() throws InvalidAtomException, MissingAtomException  {
		Set<String> phrasesContaining = underTest.getPhrasesContaining("quick");
		assertEquals(false, phrasesContaining.isEmpty());
		assertEquals(1, phrasesContaining.size());
		assertEquals(TEST_STRING, phrasesContaining.iterator().next());
	}
	@Test
	public void test2GetPhrasesContaining() throws InvalidAtomException, MissingAtomException  {
		Set<String> phrasesContaining = underTest.getPhrasesContaining("brown");
		assertEquals(false, phrasesContaining.isEmpty());
		assertEquals(1, phrasesContaining.size());
		assertEquals(TEST_STRING, phrasesContaining.iterator().next());
	}
	
	@Test
	public void test3GetPhrasesContaining() throws InvalidAtomException, MissingAtomException  {
		Set<String> phrasesContaining = underTest.getPhrasesContaining("dog");
		assertEquals(false, phrasesContaining.isEmpty());
		assertEquals(1, phrasesContaining.size());
		assertEquals(TEST_STRING, phrasesContaining.iterator().next());
	}
	
	@Test
	public void test4GetPhrasesContaining() throws InvalidAtomException, MissingAtomException  {
		Set<String> phrasesContaining = underTest.getPhrasesContaining("lazy");
		assertEquals(false, phrasesContaining.isEmpty());
		assertEquals(1, phrasesContaining.size());
		assertEquals(TEST_STRING, phrasesContaining.iterator().next());
	}
}
