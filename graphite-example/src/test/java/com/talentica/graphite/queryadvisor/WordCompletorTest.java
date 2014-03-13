package com.talentica.graphite.queryadvisor;

import static junit.framework.Assert.assertEquals;

import java.io.IOException;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.talentica.graphite.api.exception.GraphiteAnnotationParseException;
import com.talentica.graphite.api.exception.ObjectWriteException;
import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.search.GraphiteTest;
import com.talentica.graphite.store.PhraseAtomStore;
import com.talentica.graphite.store.StoreTest;
import com.talentica.graphite.store.TestStoreResources;

public class WordCompletorTest extends GraphiteTest{
	private static WordCompletor underTest;
	
	@BeforeClass
	public static void setupDB() throws IOException, MissingAtomException, InvalidAtomException, GraphiteAnnotationParseException, IllegalArgumentException, IllegalAccessException, ObjectWriteException {
		storeResources = new TestStoreResources(StoreTest.DB_PATH, StoreTest.DB_PROPERTYFILE_PATH);
		storeResources.init();
		phrStore = (PhraseAtomStore) storeResources.getAtomStore(AtomType.phrase);
	}
	
	@Before
	public void setup() throws MissingAtomException, InvalidAtomException{
		underTest = new WordCompletor(storeResources);
		phrStore.createAtom("quick brown fox jumped over a lazy dog");
	}

	@Test
	public void test1GetWordsBeginningWith() throws InvalidAtomException, MissingAtomException{
		Set<String> phrasesBeginningWith = underTest.getWordsBeginningWith("quick");
		assertEquals(false, phrasesBeginningWith.isEmpty());
		assertEquals(1, phrasesBeginningWith.size());
		assertEquals("quick", phrasesBeginningWith.iterator().next());
	}

	@Test
	public void test2GetWordsBeginningWith() throws InvalidAtomException, MissingAtomException {
		Set<String> phrasesBeginningWith = underTest.getWordsBeginningWith("qu ");
		assertEquals(false, phrasesBeginningWith.isEmpty());
		assertEquals(1, phrasesBeginningWith.size());
		assertEquals("quick", phrasesBeginningWith.iterator().next());
	}
	
	@Test
	public void test3GetWordsBeginningWith() throws InvalidAtomException, MissingAtomException {
		Set<String> phrasesBeginningWith = underTest.getWordsBeginningWith("j");
		assertEquals(false, phrasesBeginningWith.isEmpty());
		assertEquals(1, phrasesBeginningWith.size());
		assertEquals("jumped", phrasesBeginningWith.iterator().next());
	}
	
	@Test
	public void test4GetWordsBeginningWith() throws InvalidAtomException, MissingAtomException {
		Set<String> phrasesBeginningWith = underTest.getWordsBeginningWith("ck ");
		assertEquals(true, phrasesBeginningWith.isEmpty());
	}
	
	@Test
	public void test1GetWordsContaining() throws InvalidAtomException, MissingAtomException {
		Set<String> phrasesBeginningWith = underTest.getWordsContaining("ui ");
		assertEquals("quick", phrasesBeginningWith.iterator().next());
	}
	
	@Test
	public void test2GetWordsContaining() throws InvalidAtomException, MissingAtomException {
		Set<String> phrasesBeginningWith = underTest.getWordsContaining("qu ");
		assertEquals("quick", phrasesBeginningWith.iterator().next());
	}
	
	@Test
	public void test3GetWordsContaining() throws InvalidAtomException, MissingAtomException {
		Set<String> phrasesBeginningWith = underTest.getWordsContaining("ck ");
		assertEquals("quick", phrasesBeginningWith.iterator().next());
	}
	
	@Test
	public void test1GetWordsEndingWith() throws InvalidAtomException, MissingAtomException {
		Set<String> phrasesBeginningWith = underTest.getWordsEndingWith("ck ");
		assertEquals("quick", phrasesBeginningWith.iterator().next());
	}

}
