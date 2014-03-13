package com.talentica.graphite.search.query.chain;

import java.util.HashSet;
import java.util.Set;

import com.talentica.graphite.atom.Atom;
import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.atom.ClassAtom;
import com.talentica.graphite.atom.PropertyAtom;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.search.exception.InvalidLinkException;
import com.talentica.graphite.store.ClassAtomStore;

public class ClassState implements State{
	public static final State CLASS = new ClassState();

	private ClassState(){}

	@Override
	public State addLink(Atom atom, Chain chain) throws InvalidLinkException {
		State state = CLASS;
		if(atom instanceof PropertyAtom){
			state = PropertyState.PROP;
			chain.add(atom);
		}else{
			throw new InvalidLinkException(String.format(ERROR_MESSAGE, atom.getAtomType().name()));
		}
		return state;
	}

	@Override
	public boolean isComplete(Chain chain) {
		return chain.getSize() == 1;
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
		ClassAtomStore classStore = (ClassAtomStore) chain.getStoreResources().getAtomStore(AtomType.domain_class);
		ClassAtom atom = (ClassAtom) chain.getTopLink();
		nextLinks.addAll(classStore.getProperties(atom));
		return nextLinks;
	}
}
