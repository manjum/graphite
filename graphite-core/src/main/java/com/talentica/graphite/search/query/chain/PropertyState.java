package com.talentica.graphite.search.query.chain;

import java.util.HashSet;
import java.util.Set;

import com.talentica.graphite.atom.Atom;
import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.atom.ClassAtom;
import com.talentica.graphite.atom.ObjectAtom;
import com.talentica.graphite.atom.PropertyAtom;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.search.exception.InvalidLinkException;
import com.talentica.graphite.store.PropertyAtomStore;

public class PropertyState implements State{
	public final static State PROP = new PropertyState();

	private PropertyState(){
	}

	@Override
	public State addLink(Atom atom, Chain chain) throws InvalidLinkException {
		State state = PROP;
		if(atom instanceof ClassAtom){
			state = ClassState.CLASS;
			chain.add(atom);
		}else if(atom instanceof PropertyAtom){
			throw new InvalidLinkException(String.format(ERROR_MESSAGE, atom.getAtomType().name()));
		}else{
			state = ObjectState.OBJECT;
			chain.add(atom);
		}
		return state;
	}

	@Override
	public boolean isComplete(Chain chain) {
		return chain.getLink(0) instanceof ObjectAtom && chain.getSize() == 2;
	}

	@Override
	public State removeLink(Chain chain) {
		chain.remove();
		if(chain.getSize() == 1){
			return ObjectState.OBJECT;
		}else{
			return ClassState.CLASS;
		}
	}

	@Override
	public Set<Atom> getNextLinks(Chain chain) throws MissingAtomException, InvalidAtomException {
		Set<Atom> nextLinks = new HashSet<Atom>();
		PropertyAtom prop = (PropertyAtom) chain.getTopLink();
		PropertyAtomStore propStore = (PropertyAtomStore) chain.getStoreResources().getAtomStore(AtomType.domain_prop);

		boolean isFirstAtomObject = chain.getLink(0) instanceof ObjectAtom;
		if(!isFirstAtomObject || chain.getSize() == 2){
			nextLinks.addAll( propStore.getPropertyValues(prop) );
		}

		ClassAtom range = propStore.getRange(prop);
		String atomName = range.getName();
		if(!AtomType.string.name().equals(atomName)
				&& !AtomType.phrase.name().equals(atomName)
				&& !AtomType.number.name().equals(atomName)
				&& !AtomType.date.name().equals(atomName)){
			nextLinks.add(range);
		}
		return nextLinks;
	}
}
