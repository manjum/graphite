package com.talentica.graphite.queryadvisor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.atom.StringAtom;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.store.StoreResources;
import com.talentica.graphite.store.StringAtomStore;

public class WordCompletor {
	private final StoreResources storeResources;
	private final StringAtomStore strStore;

	public WordCompletor(StoreResources storeResources) {
		this.storeResources = storeResources;
		this.strStore = (StringAtomStore) this.storeResources.getAtomStore(AtomType.string);
	}

	public Set<String> getWordsBeginningWith(String rawString) throws InvalidAtomException, MissingAtomException{
		Set<StringAtom> stringAtoms = getStringAtomsBeginingWith(rawString);
		return extractNames(stringAtoms);
	}

	private Set<StringAtom> getStringAtomsBeginingWith(String rawString) {
		String regex = rawString.toLowerCase().trim().concat("*");
		return this.strStore.getStringAtoms(regex);
	}

	public Set<String> getWordsContaining(String rawString) throws InvalidAtomException, MissingAtomException{
		Set<StringAtom> stringAtoms = getStringAtomsContaining(rawString);
		return extractNames(stringAtoms);
	}

	public Set<StringAtom> getStringAtomsContaining(String rawString) {
		String regex = "*".concat(rawString.toLowerCase().trim()).concat("*");
		return this.strStore.getStringAtoms(regex);
	}

	public Set<String> getWordsEndingWith(String rawString) throws InvalidAtomException, MissingAtomException{
		Set<StringAtom> stringAtoms = getStringAtomsEndingWith(rawString);
		return extractNames(stringAtoms);
	}

	public Set<StringAtom> getStringAtomsEndingWith(String rawString) {
		String regex = "*".concat(rawString.toLowerCase().trim());
		return this.strStore.getStringAtoms(regex);
	}

	private Set<String> extractNames(Set<StringAtom> stringAtoms){
		Set<String> strings = Collections.emptySet();
		if(null != stringAtoms && stringAtoms.size() >0){
			strings = new HashSet<>(stringAtoms.size());
			for(StringAtom strAtom: stringAtoms){
				strings.add(strAtom.getName());
			}
		}
		return strings;
	}
}