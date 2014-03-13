package com.talentica.graphite.store;

import com.talentica.graphite.atom.Atom;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;

public interface Store<T extends Atom> {
	static final String NAME_FIELD = "name";
	static final String TYPE_FIELD = "type";
	static final String RANK_FIELD = "rank";
	static final String ATOM_INDEX_NAME = "atoms";
	String getAtomName(long id) throws InvalidAtomException, MissingAtomException;
	T getAtomById(long id) throws InvalidAtomException, MissingAtomException;
	T getAtomByName(String name) throws InvalidAtomException, MissingAtomException;
	T createAtom(String name) throws InvalidAtomException, MissingAtomException;
}
