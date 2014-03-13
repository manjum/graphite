package com.talentica.graphite.queryadvisor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.talentica.graphite.atom.Atom;
import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.atom.ClassAtom;
import com.talentica.graphite.atom.ObjectAtom;
import com.talentica.graphite.atom.PhraseAtom;
import com.talentica.graphite.atom.PropertyAtom;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.search.AtomSearcher;
import com.talentica.graphite.search.exception.InvalidLinkException;
import com.talentica.graphite.search.query.chain.Chain;
import com.talentica.graphite.store.ClassAtomStore;
import com.talentica.graphite.store.ObjectAtomStore;
import com.talentica.graphite.store.PropertyAtomStore;
import com.talentica.graphite.store.StoreResources;

public class QueryAdvisor {
	private final AtomSearcher atomSearcher;
	private final StoreResources storeResources;
	private final WordCompletor wordCompletor;
	private final PhraseCompletor phraseCompletor;
	private final ObjectAtomStore objStore;
	private final PropertyAtomStore propStore;
	private final ClassAtomStore classStore;

	public QueryAdvisor(AtomSearcher atomSearcher, StoreResources storeResources) throws MissingAtomException, InvalidAtomException {
		this.atomSearcher = atomSearcher;
		this.storeResources = storeResources;
		this.wordCompletor = new WordCompletor(storeResources);
		this.phraseCompletor = new PhraseCompletor(storeResources);
		this.classStore = (ClassAtomStore) storeResources.getAtomStore(AtomType.domain_class);
		this.objStore = (ObjectAtomStore) storeResources.getAtomStore(AtomType.domain_obj);
		this.propStore = (PropertyAtomStore) storeResources.getAtomStore(AtomType.domain_prop);
	}

	public Set<Atom> getNextLinks(Atom atom) throws MissingAtomException, InvalidAtomException{
		Set<Atom> nextLinks = new HashSet<Atom>();
		if(atom instanceof PropertyAtom){
			nextLinks.addAll(this.propStore.getPropertyValues((PropertyAtom) atom));
			ClassAtom range = this.propStore.getRange((PropertyAtom) atom);
			String atomName = range.getName();
			if(!AtomType.string.name().equals(atomName)
					&& !AtomType.phrase.name().equals(atomName)
					&& !AtomType.number.name().equals(atomName)
					&& !AtomType.date.name().equals(atomName)){
				nextLinks.add(range);
			}
		}
		else if (atom instanceof ClassAtom){
			nextLinks.addAll(this.classStore.getProperties((ClassAtom) atom));
		}
		else if(atom instanceof ObjectAtom){
			nextLinks.addAll(this.classStore.getProperties(this.objStore.getType(atom)));
		}
		return nextLinks;
	}
	public List<Chain> getChains(String str) throws InvalidAtomException, MissingAtomException, InvalidLinkException{
		List<Chain> chains = Collections.emptyList();
		Set<ObjectAtom> objAtoms = this.atomSearcher.getObjectAtoms(str);
		Set<ClassAtom> classAtoms = this.atomSearcher.getClassAtoms(str);
		Set<PropertyAtom> propAtoms = this.atomSearcher.getPropertyAtoms(str);
		int size = objAtoms.size() + propAtoms.size() + classAtoms.size();
		if(size > 0){
			chains = new ArrayList<Chain>(size);
			for(ObjectAtom objAtom : objAtoms){
				chains.addAll(this.getQueryChains(objAtom));
				//add value queries
				chains.addAll(this.getQueryChainsForValueAtom(objAtom));
			}
			for(ClassAtom classAtom: classAtoms){
				chains.addAll(this.getQueryChains(classAtom));
			}
			for(PropertyAtom propAtom: propAtoms){
				chains.addAll(this.getQueryChains(propAtom));
			}
		}else{
			//check for values
			Set<PhraseAtom> nameAtoms = new HashSet<>();
			nameAtoms.addAll(this.wordCompletor.getStringAtomsContaining(str));
			nameAtoms.addAll(this.phraseCompletor.getPhraseAtomsContaining(str));
			chains = new ArrayList<>();
			for(PhraseAtom valAtom: nameAtoms){
				chains.addAll(this.getQueryChainsForValueAtom(valAtom));
			}
		}
		return chains;
	}

	public List<Chain> getNextLevelChains(Chain chain) throws InvalidAtomException, MissingAtomException, InvalidLinkException{
		List<Chain> chains = chain.getNextLevelChains();
		return chains;
	}

	private List<Chain> getQueryChains(ObjectAtom objAtom) throws InvalidLinkException {
		List<Chain> chains = new ArrayList<Chain>(1);
		Chain chain = new Chain(this.storeResources);
		chain.addLink(objAtom);
		chains.add(chain);
		return chains;
	}

	private List<Chain> getQueryChains(ClassAtom classAtom) throws InvalidLinkException, MissingAtomException, InvalidAtomException {
		List<Chain> chains = Collections.emptyList();
		Set<ClassAtom> relatedClasses = this.atomSearcher.getRelatedClassAtoms(classAtom);
		chains = new ArrayList<>(relatedClasses.size()+1);
		Chain convergingChain = new Chain(this.storeResources);
		convergingChain.addLink(classAtom);
		chains.add(convergingChain);
		for(ClassAtom relatedClass : relatedClasses){
			Chain chain = new Chain(this.storeResources);
			chain.addLink(relatedClass);
			chains.add(chain);
		}
		return chains;
	}

	private List<Chain> getQueryChains(PropertyAtom propAtom) throws MissingAtomException, InvalidAtomException, InvalidLinkException {
		List<Chain> chains = Collections.emptyList();
		Set<ClassAtom> range = this.propStore.getDomain(propAtom);
		if(range.size() > 0){
			chains = new ArrayList<Chain>(range.size());
		}
		for(ClassAtom classAtom: range){
			Chain convergingChain = new Chain(this.storeResources);
			convergingChain.addLink(classAtom);
			convergingChain.addLink(propAtom);
			chains.add(convergingChain);
		}

		return chains;
	}
	private List<Chain> getQueryChainsForValueAtom(Atom valAtom) throws MissingAtomException, InvalidAtomException, InvalidLinkException {
		List<Chain> chains = Collections.emptyList();
		Set<PropertyAtom> propertyAtomsForValue = this.propStore.getPropertyAtomsForValue(valAtom);
		if(propertyAtomsForValue.size() == 0){
			return chains;
		}
		chains = new ArrayList<>(propertyAtomsForValue.size());
		for(PropertyAtom propAtom: propertyAtomsForValue){
			for(Chain convergingChain : this.getQueryChains(propAtom) ){
				convergingChain.addLink(valAtom);
				chains.add(convergingChain);
			}
		}
		return chains;
	}
}

