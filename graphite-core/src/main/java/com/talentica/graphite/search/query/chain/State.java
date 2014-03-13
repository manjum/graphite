package com.talentica.graphite.search.query.chain;

import java.util.Set;

import com.talentica.graphite.atom.Atom;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.search.exception.InvalidLinkException;

public interface State {
	static final String ERROR_MESSAGE = "%s can't be added at this state";
	/**
	 * @param atom - atom to be added to the chain
	 * @param chain - chain to which atom has to be added, throws NULLPointerException if null 
	 * @return - returns the current state of the chain after addition of atom
	 * @throws InvalidLinkException
	 */
	State addLink(Atom atom, Chain chain) throws InvalidLinkException;
	State removeLink(Chain chain);
	/**
	 * @param chain - chain whose complete status is to be computed, throws NULLPointerException if null
	 * @return - the complete status of the given chain. 
	 */
	boolean isComplete(Chain chain);


	Set<Atom> getNextLinks(Chain chain) throws MissingAtomException, InvalidAtomException;
}
