//package com.talentica.graphite.store;
//
//import java.util.List;
//
//import org.junit.Test;
//
//import com.talentica.graphite.object.DomainClass;
//import com.talentica.graphite.object.DomainObject;
//import com.talentica.graphite.store.SessionStore;
//import com.talentica.graphite.store.exceptions.InvalidSearchObjectException;
//
//public class SessionStoreTest {
////	@Test
//	public void testGetNodes() throws InvalidSearchObjectException {
//		List<DomainObject> nodes = SessionStore.getDomainObjects('c');
//		System.out.println(nodes);
//		
//	}
////	@Test
//	public void testGetDomainClasses() throws InvalidSearchObjectException {
//		System.out.println(SessionStore.getDomainClasses('c'));
//	}
//	
//	@Test
//	public void testGetQueryPhraseNodes() throws InvalidSearchObjectException {
//		DomainClass domainClass = new DomainClass("candidate", 33); 
//		List<Long> queryPhraseNodes = SessionStore.getQueryPhraseNodes(domainClass);
//		System.out.println(queryPhraseNodes.isEmpty());
//		System.out.println(queryPhraseNodes);
//	}
//	
//
//}
