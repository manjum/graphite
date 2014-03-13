package com.talentica.graphite.search.query.chain;

import java.util.Collections;
import java.util.Set;

import com.talentica.graphite.atom.Atom;
import com.talentica.graphite.atom.ClassAtom;
import com.talentica.graphite.atom.ObjectAtom;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.search.exception.InvalidLinkException;

public class EmptyState implements State{
	public static final State EMPTY = new EmptyState();

	@Override
	public State addLink(Atom atom, Chain chain) throws InvalidLinkException {
		State chainState = EMPTY;
		if(atom instanceof ClassAtom){
			chainState = ClassState.CLASS;
			ChainType converging = ChainType.converging;
			chain.setChainType(converging);
			chain.add(atom);
		}else if(atom instanceof ObjectAtom){
			ChainType diverging = ChainType.diverging;
			chain.setChainType(diverging);
			chainState = ObjectState.OBJECT;
			chain.add(atom);
		}else{
			throw new InvalidLinkException(String.format(ERROR_MESSAGE, atom.getAtomType().name()));
		}
		return chainState;
	}

	@Override
	public boolean isComplete(Chain chain) {
		return false;
	}

	@Override
	public State removeLink(Chain chain) {
		return EMPTY;
	}

	@Override
	public Set<Atom> getNextLinks(Chain chain) throws MissingAtomException,
			InvalidAtomException {
		return Collections.emptySet();
	}
}
