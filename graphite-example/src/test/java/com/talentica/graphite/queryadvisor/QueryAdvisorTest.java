package com.talentica.graphite.queryadvisor;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.talentica.graphite.atom.AndAtom;
import com.talentica.graphite.atom.Atom;
import com.talentica.graphite.atom.ClassAtom;
import com.talentica.graphite.atom.ObjectAtom;
import com.talentica.graphite.atom.PropertyAtom;
import com.talentica.graphite.atom.StringAtom;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.search.AtomSearcher;
import com.talentica.graphite.search.GraphiteTest;
import com.talentica.graphite.search.exception.InvalidLinkException;
import com.talentica.graphite.search.query.chain.Chain;

public class QueryAdvisorTest extends GraphiteTest{
	private QueryAdvisor queryAdvisor;

	@Before
	public void setup() throws MissingAtomException, InvalidAtomException{
		AtomSearcher atomSearcher = new AtomSearcher(storeResources);
		queryAdvisor = new QueryAdvisor(atomSearcher, storeResources);
	}
	@Test
	public void testGetNextLevelChainsForNonExpandableDivergingChain() throws InvalidLinkException, MissingAtomException, InvalidAtomException{
		Chain chain = new Chain(storeResources);
		ObjectAtom sp = objStore.getAtomByName("sushant pradhan");
		ObjectAtom sr = objStore.getAtomByName("sushant rajput");
		PropertyAtom friend = propStore.getAtomByName("friend");
		ClassAtom candidate = classStore.getAtomByName("candidate");
		PropertyAtom skill = propStore.getAtomByName("skill");
		StringAtom java = strStore.getAtomByName("java");
		chain.addLink(sp);
		assertNotSame("", chain.getDBQueryTemplate().getQueryString());
		chain.addLink(friend);
		assertNotSame("", chain.getDBQueryTemplate().getQueryString());
		chain.addLink(candidate);
		assertSame("", chain.getDBQueryTemplate().getQueryString());
		chain.addLink(skill);
		assertSame("", chain.getDBQueryTemplate().getQueryString());
		chain.addLink(java);
		assertNotSame("", chain.getDBQueryTemplate().getQueryString());
		chain.addLink(AndAtom.AND);
		assertSame("", chain.getDBQueryTemplate().getQueryString());
		chain.addLink(friend);
		assertSame("", chain.getDBQueryTemplate().getQueryString());
		chain.addLink(sr);
		assertNotSame("", chain.getDBQueryTemplate().getQueryString());
		chain.addLink(AndAtom.AND);
		assertSame("", chain.getDBQueryTemplate().getQueryString());
		chain.addLink(skill);
		assertSame("", chain.getDBQueryTemplate().getQueryString());
		chain.addLink(java);
		assertNotSame("", chain.getDBQueryTemplate().getQueryString());
		Chain qChain = new Chain(storeResources);
		qChain.addLink(sp);
		qChain.addLink(friend);
		qChain.addLink(candidate);
		qChain.addLink(skill);
		qChain.addLink(java);
		System.out.println(qChain.getQueryPhrase());
		List<Chain> nextLevelChains = this.queryAdvisor.getNextLevelChains(qChain);
		for(Chain chain1: nextLevelChains){
			System.out.println("	"+chain1.getQueryPhrase());
			System.out.println("	  " + chain1.getDBQueryTemplate().getQueryString());

			List<Chain> level1Chains = this.queryAdvisor.getNextLevelChains(chain1);
			for(Chain chain2: level1Chains){
				System.out.println("		"+chain2.getQueryPhrase());
				System.out.println("  		  " + chain2.getDBQueryTemplate().getQueryString());

				List<Chain> level2Chains = this.queryAdvisor.getNextLevelChains(chain2);
				for(Chain chain3: level2Chains){
					System.out.println("			"+chain3.getQueryPhrase());
					System.out.println("			  " + chain3.getDBQueryTemplate().getQueryString());

					List<Chain> level3Chains = this.queryAdvisor.getNextLevelChains(chain3);
					for(Chain chain4: level3Chains){
						System.out.println("				"+chain4.getQueryPhrase());
						System.out.println("			  	  " + chain4.getDBQueryTemplate().getQueryString());
					}
				}
			}

		}
	}

	@Test
	public void test1GetNextLevelChainsForNonExpandableConvergingChain() throws InvalidLinkException, MissingAtomException, InvalidAtomException{
		Chain chain = new Chain(storeResources);;
		ClassAtom candidate = classStore.getAtomByName("candidate");
		PropertyAtom skill = propStore.getAtomByName("skill");
		PropertyAtom friend = propStore.getAtomByName("friend");
		StringAtom java = strStore.getAtomByName("java");
		chain.addLink(candidate);
		chain.addLink(friend);
		chain.addLink(candidate);
		chain.addLink(skill);
		chain.addLink(java);
		chain.addLink(AndAtom.AND);
		chain.addLink(skill);
		chain.addLink(java);
		System.out.println(chain.getQueryPhrase());
		System.out.println(chain.getDBQueryTemplate().getQueryString());
	}

	@Test
	public void testGetNextLevelChainsForNonExpandableConvergingChain() throws InvalidLinkException, MissingAtomException, InvalidAtomException{
		Chain chain = new Chain(storeResources);;
		ClassAtom candidate = classStore.getAtomByName("candidate");
		ObjectAtom sr = objStore.getAtomByName("sushant rajput");
		PropertyAtom skill = propStore.getAtomByName("skill");
		PropertyAtom friend = propStore.getAtomByName("friend");
		StringAtom java = strStore.getAtomByName("java");
		chain.addLink(candidate);
		assertNotSame("", chain.getDBQueryTemplate().getQueryString());
		chain.addLink(skill);
		assertSame("", chain.getDBQueryTemplate().getQueryString());
		chain.addLink(java);
		assertNotSame("", chain.getDBQueryTemplate().getQueryString());
		chain.addLink(AndAtom.AND);
		assertSame("", chain.getDBQueryTemplate().getQueryString());
		chain.addLink(friend);
		assertSame("", chain.getDBQueryTemplate().getQueryString());
		chain.addLink(sr);
		assertNotSame("", chain.getDBQueryTemplate().getQueryString());
		System.out.println(chain.getDBQueryTemplate().getQueryString());
		List<Chain> nextLevelChains = this.queryAdvisor.getNextLevelChains(chain);
		for(Chain chain1: nextLevelChains){
			System.out.println(chain1.getQueryPhrase());
			System.out.println("  " + chain1.getDBQueryTemplate().getQueryString());
			List<Chain> level1Chains = this.queryAdvisor.getNextLevelChains(chain1);
			for(Chain chain2: level1Chains){
				System.out.println("	" + chain2.getQueryPhrase());
				System.out.println("  	  " + chain2.getDBQueryTemplate().getQueryString());
				List<Chain> level2Chains = this.queryAdvisor.getNextLevelChains(chain2);
				for(Chain chain3: level2Chains){
					System.out.println("		" + chain3.getQueryPhrase());
					System.out.println("  	  	  " + chain3.getDBQueryTemplate().getQueryString());
				}
			}
		}
	}
	@Test
	public void testGetNextLinksForObjectAtom() throws InvalidAtomException, MissingAtomException, InvalidLinkException{
		ObjectAtom objAtom = objStore.getAtomByName("nadeem khan");
		Set<Atom> nextLinks = this.queryAdvisor.getNextLinks(objAtom);
		System.out.println(nextLinks);
	}

	@Test
	public void testGetChainsForClass() throws InvalidAtomException, MissingAtomException, InvalidLinkException{
		List<Chain> chains = this.queryAdvisor.getChains("can");
		System.out.println(chains);
	}
	@Test
	public void testGetChainsForProperty() throws InvalidAtomException, MissingAtomException, InvalidLinkException{
		List<Chain> chains = this.queryAdvisor.getChains("skill");
		System.out.println(chains);
	}
	@Test
	public void testGetChainsForObject() throws InvalidAtomException, MissingAtomException, InvalidLinkException{
		List<Chain> chains = this.queryAdvisor.getChains("sushant");
		System.out.println(chains);
	}
	@Test
	public void testGetNextLinksForClassAtom() throws MissingAtomException, InvalidAtomException, InvalidLinkException{
		ClassAtom clazz = classStore.getAtomByName("candidate");
		Chain queryChain = new Chain(storeResources);;
		queryChain.addLink(clazz);
		System.out.println(this.queryAdvisor.getNextLinks(queryChain.getTopLink()));
	}

	@Test
	public void testGetNextLinksForPropertyAtom() throws MissingAtomException, InvalidAtomException, InvalidLinkException{
		PropertyAtom prop = propStore.getAtomByName("location");
		ClassAtom clazz = classStore.getAtomByName("candidate");
		Chain queryChain = new Chain(storeResources);;
		System.out.println(queryChain);
		queryChain.addLink(clazz);
		queryChain.addLink(prop);
		System.out.println(queryChain.getQueryPhrase());
		Set<? extends Atom> nextLinks = this.queryAdvisor.getNextLinks(queryChain.getTopLink());
		queryChain.addLink(nextLinks.iterator().next());
		System.out.println(queryChain.getQueryPhrase());

	}

	@Test
	public void testGetChainTimeTest() throws MissingAtomException, InvalidAtomException, InvalidLinkException{
		for(int i=0;i<10;i++){
			long startTime = System.currentTimeMillis();
			List<Chain> level0Chains = this.queryAdvisor.getChains("skill");
			for(Chain level0Chain : level0Chains){
				List<Chain> level1Chains = this.queryAdvisor.getNextLevelChains(level0Chain);
				for(Chain level1Chain : level1Chains){
					List<Chain> level2Chains = this.queryAdvisor.getNextLevelChains(level1Chain);
					for(Chain level2Chain : level2Chains){
						this.queryAdvisor.getNextLevelChains(level2Chain);
					}
				}
			}
			long timetaken = System.currentTimeMillis() - startTime;
			System.out.println("################ time taken: " + timetaken + "################");
		}
	}

	@Test
	public void testGetChainNaturalLanguageQuery() throws MissingAtomException, InvalidAtomException, InvalidLinkException{
		String tab = "	";
		long startTime = System.currentTimeMillis();
		List<Chain> level0Chains = this.queryAdvisor.getChains("skill");
		for(Chain level0Chain : level0Chains){
			System.out.println(level0Chain.getQueryPhrase());
			List<Chain> level1Chains = this.queryAdvisor.getNextLevelChains(level0Chain);
			for(Chain level1Chain : level1Chains){
				System.out.println(tab.concat(level1Chain.getQueryPhrase()));
				List<Chain> level2Chains = this.queryAdvisor.getNextLevelChains(level1Chain);
				for(Chain level2Chain : level2Chains){
					System.out.println(tab.concat(tab).concat(level2Chain.getQueryPhrase()));
					List<Chain> level3Chains = this.queryAdvisor.getNextLevelChains(level2Chain);
					for(Chain level3Chain : level3Chains){
						System.out.println(tab.concat(tab).concat(tab).concat(level3Chain.getQueryPhrase()));
					}
				}
			}
		}
		long timetaken = System.currentTimeMillis() - startTime;
		System.out.println("################ time taken: " + timetaken + "################");
	}

	@Test
	public void testGetDataBaseQuery() throws MissingAtomException, InvalidAtomException, InvalidLinkException{
		String tab = "	";
		long startTime = System.currentTimeMillis();
		List<Chain> level0Chains = this.queryAdvisor.getChains("candidate");
		for(Chain level0Chain : level0Chains){
			System.out.println(level0Chain.getQueryPhrase() + "|" + level0Chain.getDBQueryTemplate().getQueryString());
			List<Chain> level1Chains = this.queryAdvisor.getNextLevelChains(level0Chain);
			for(Chain level1Chain : level1Chains){
				System.out.println(tab.concat(level1Chain.getQueryPhrase()) + "|" + level1Chain.getDBQueryTemplate().getQueryString());
				List<Chain> level2Chains = this.queryAdvisor.getNextLevelChains(level1Chain);
				for(Chain level2Chain : level2Chains){
					System.out.println(tab.concat(tab).concat(level2Chain.getQueryPhrase()) + "|" + level2Chain.getDBQueryTemplate().getQueryString());
					List<Chain> level3Chains = this.queryAdvisor.getNextLevelChains(level2Chain);
					for(Chain level3Chain : level3Chains){
						System.out.println(tab.concat(tab).concat(tab).concat(level3Chain.getQueryPhrase()) + "|" + level3Chain.getDBQueryTemplate().getQueryString());
					}
				}
			}
		}
		long timetaken = System.currentTimeMillis() - startTime;
		System.out.println("################ time taken: " + timetaken + "################");
	}
}

