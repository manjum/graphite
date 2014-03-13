package com.talentica.graphite.search.query.chain;

import java.util.HashSet;
import java.util.Set;

import com.talentica.graphite.atom.AndAtom;
import com.talentica.graphite.atom.Atom;
import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.atom.ClassAtom;
import com.talentica.graphite.atom.ConjuctionAtom;
import com.talentica.graphite.atom.PropertyAtom;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.search.exception.InvalidLinkException;
import com.talentica.graphite.store.ClassAtomStore;
import com.talentica.graphite.store.ObjectAtomStore;

public class ObjectState implements State{
	public static final State OBJECT = new ObjectState();

	private ObjectState() {
	}

	@Override
	public State addLink(Atom atom, Chain chain) throws InvalidLinkException {
		State state = OBJECT;
		if(atom instanceof PropertyAtom){
			chain.add(atom);
			state = PropertyState.PROP;
		}else if (atom instanceof ConjuctionAtom){
			int classPos = chain.getSize()-3;
			if(classPos >= 0){
				ClassAtom classAtom = (ClassAtom) chain.getLink(classPos);
				chain.add(atom);
				chain.add(classAtom);
				state = ClassState.CLASS;
			}
		}else{
			throw new InvalidLinkException(String.format(ERROR_MESSAGE, atom.getAtomType().name()));
		}
		return state;
	}

	@Override
	public boolean isComplete(Chain chain) {
		return true;
	}

	@Override
	public State removeLink(Chain chain) {
		chain.remove();
		if(chain.getSize() == 0){
			return EmptyState.EMPTY;
		}else{
			return PropertyState.PROP;
		}
	}

	@Override
	public Set<Atom> getNextLinks(Chain chain) throws MissingAtomException,
	InvalidAtomException {
		Set<Atom> nextLinks = new HashSet<Atom>();
		if(chain.getSize() != 1){
			nextLinks.add(AndAtom.AND);
		}else{
			ObjectAtomStore objStore = (ObjectAtomStore) chain.getStoreResources().getAtomStore(AtomType.domain_obj);
			ClassAtomStore classStore = (ClassAtomStore) chain.getStoreResources().getAtomStore(AtomType.domain_class);
			Atom atom = chain.getTopLink();
			nextLinks.addAll(classStore.getProperties(objStore.getType(atom)));
		}
		return nextLinks;
	}
}
