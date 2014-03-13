package com.talentica.graphite.search;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.atom.ClassAtom;
import com.talentica.graphite.atom.ObjectAtom;
import com.talentica.graphite.atom.PropertyAtom;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.queryadvisor.PhraseCompletor;
import com.talentica.graphite.queryadvisor.WordCompletor;
import com.talentica.graphite.store.ClassAtomStore;
import com.talentica.graphite.store.ObjectAtomStore;
import com.talentica.graphite.store.PropertyAtomStore;
import com.talentica.graphite.store.StoreResources;

public class AtomSearcher {
	private final WordCompletor wordCompletor;
	private final PhraseCompletor phraseCompletor;
	private final ClassAtom stringClassAtom;
	private final ClassAtom numClassAtom;
	private final ObjectAtomStore objStore;
	private final PropertyAtomStore propStore;
	private final ClassAtomStore classStore;

	public AtomSearcher(StoreResources storeResources) throws InvalidAtomException, MissingAtomException {
		this.wordCompletor = new WordCompletor(storeResources);
		this.phraseCompletor = new PhraseCompletor(storeResources);
		this.classStore = (ClassAtomStore) storeResources.getAtomStore(AtomType.domain_class);
		this.stringClassAtom = this.classStore.getAtomByName(AtomType.string.name());
		this.numClassAtom = this.classStore.getAtomByName(AtomType.number.name());
		this.objStore = (ObjectAtomStore) storeResources.getAtomStore(AtomType.domain_obj);
		this.propStore = (PropertyAtomStore) storeResources.getAtomStore(AtomType.domain_prop);
	}

	public Set<ObjectAtom> getObjectAtoms(String str) throws InvalidAtomException, MissingAtomException {
		Set<ObjectAtom> objAtoms = Collections.emptySet();
		Set<String> names = this.wordCompletor.getWordsContaining(str);
		names.addAll(this.phraseCompletor.getPhrasesContaining(str));
		if(names.size()>0){
			objAtoms = new HashSet<ObjectAtom>(names.size());
		}
		for(String name: names){
			try{
				ObjectAtom objAtom = this.objStore.getAtomByName(name);
				objAtoms.add(objAtom);
			}catch(MissingAtomException e){
				//no worries this string might be name of some other atom.
			}catch(InvalidAtomException e){
				//no worries this string might be name of some other atom.
			}
		}
		return objAtoms;
	}

	public Set<PropertyAtom> getPropertyAtoms(String str) throws InvalidAtomException, MissingAtomException {
		Set<PropertyAtom> propAtoms = Collections.emptySet();
		Set<String> names = this.wordCompletor.getWordsContaining(str);
		names.addAll(this.phraseCompletor.getPhrasesContaining(str));
		if(names.size()>0){
			propAtoms = new HashSet<PropertyAtom>(names.size());
		}
		for(String name: names){
			try{
				PropertyAtom propAtom = this.propStore.getAtomByName(name);
				propAtoms.add(propAtom);
			}catch(MissingAtomException e){
				//no worries this string might be name of some other atom.
			}catch(InvalidAtomException e){
				//no worries this string might be name of some other atom.
			}
		}
		return propAtoms;
	}

	public Set<ClassAtom> getClassAtoms(String str) throws InvalidAtomException, MissingAtomException{
		Set<ClassAtom> classAtoms = Collections.emptySet();
		Set<String> names = this.wordCompletor.getWordsContaining(str);
		names.addAll(this.phraseCompletor.getPhrasesContaining(str));
		if(names.size()>0){
			classAtoms = new HashSet<ClassAtom>(names.size());
		}
		for(String name: names){
			try{
				ClassAtom classAtom = this.classStore.getAtomByName(name);
				classAtoms.add(classAtom);
			}catch(MissingAtomException e){
				//no worries this string might be name of some other atom.
			}
			catch(InvalidAtomException e){
				//no worries this string might be name of some other atom.
			}
		}
		return classAtoms;
	}

	public Set<ClassAtom> getRelatedClassAtoms(ClassAtom classAtom) throws MissingAtomException, InvalidAtomException{
		Set<ClassAtom> classAtoms = Collections.emptySet();
		Set<ClassAtom> ranges = this.classStore.getRanges(classAtom);
		Set<ClassAtom> domains = this.classStore.getDomains(classAtom);
		if(ranges.size() + domains.size() > 0){
			classAtoms = new HashSet<ClassAtom>();
		}
		classAtoms.addAll(ranges);
		classAtoms.addAll(domains);
		classAtoms.remove(classAtom);
		classAtoms.remove(stringClassAtom);
		classAtoms.remove(numClassAtom);
		return classAtoms;
	}

}
