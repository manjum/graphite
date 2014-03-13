package com.talentica.graphite.queryadvisor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.atom.PhraseAtom;
import com.talentica.graphite.atom.StringAtom;
import com.talentica.graphite.bond.Position;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.store.PhraseAtomStore;
import com.talentica.graphite.store.StoreResources;
import com.talentica.graphite.store.StringAtomStore;

public class PhraseCompletor {
	private final StoreResources storeResources;
	private final StringAtomStore strStore;
	private final PhraseAtomStore phraseStore;
	public PhraseCompletor(StoreResources storeResources) {
		this.storeResources = storeResources;
		this.strStore = (StringAtomStore) this.storeResources.getAtomStore(AtomType.string);
		this.phraseStore = (PhraseAtomStore) this.storeResources.getAtomStore(AtomType.phrase);
	}

	public Set<String> getPhrasesBeginningWith(String rawString) throws InvalidAtomException, MissingAtomException{
		Set<PhraseAtom> phraseAtoms = getPhraseAtomsBeginingWith(rawString);
		return extractNames(phraseAtoms);
	}

	public Set<PhraseAtom> getPhraseAtomsBeginingWith(String rawString)
			throws MissingAtomException, InvalidAtomException {
		Set<PhraseAtom> phraseAtoms = Collections.emptySet();
		String regex = rawString.toLowerCase().trim().concat("*");
		Set<StringAtom> stringAtoms = this.strStore.getStringAtoms(regex);
		if(!stringAtoms.isEmpty()){
			phraseAtoms = new HashSet<>(stringAtoms.size());
			for(StringAtom strAtom: stringAtoms){
				Node strNode = this.storeResources.getGraphDb().getNodeById(strAtom.getId());
				Iterator<Relationship> iterator = strNode.getRelationships(Direction.OUTGOING,
						Position.getPosLink(0)).iterator();
				while(iterator.hasNext()){
					Node phraseNode = iterator.next().getOtherNode(strNode);
					phraseAtoms.add(this.phraseStore.getAtomById(phraseNode.getId()));
				}
			}
		}
		return phraseAtoms;
	}
	public Set<String> getPhrasesContaining(String rawString) throws InvalidAtomException, MissingAtomException{
		Set<PhraseAtom> phraseAtoms = getPhraseAtomsContaining(rawString);
		return extractNames(phraseAtoms);
	}

	public Set<PhraseAtom> getPhraseAtomsContaining(String rawString)
			throws MissingAtomException, InvalidAtomException {
		Set<PhraseAtom> phraseAtoms = Collections.emptySet();
		String regex = "*".concat(rawString.toLowerCase().trim()).concat("*");
		Set<StringAtom> stringAtoms = this.strStore.getStringAtoms(regex);
		if(!stringAtoms.isEmpty()){
			phraseAtoms = new HashSet<>(stringAtoms.size());
			for(StringAtom strAtom: stringAtoms){
				Node strNode = this.storeResources.getGraphDb().getNodeById(strAtom.getId());
				Iterator<Relationship> iterator = strNode.getRelationships(Direction.OUTGOING,
						Position.getPosLink(0),
						Position.getPosLink(1),
						Position.getPosLink(2),
						Position.getPosLink(3),
						Position.getPosLink(4),
						Position.getPosLink(5),
						Position.getPosLink(6),
						Position.getPosLink(7),
						Position.getPosLink(8)).iterator();
				while(iterator.hasNext()){
					Node phraseNode = iterator.next().getOtherNode(strNode);
					phraseAtoms.add(this.phraseStore.getAtomById(phraseNode.getId()));
				}
			}
		}
		return phraseAtoms;
	}

	private Set<String> extractNames(Set<PhraseAtom> phraseAtoms){
		Set<String> strings = Collections.emptySet();
		if(null != phraseAtoms && phraseAtoms.size() >0){
			strings = new HashSet<>(phraseAtoms.size());
			for(PhraseAtom phraseAtom: phraseAtoms){
				strings.add(phraseAtom.getName());
			}
		}
		return strings;
	}
}
