package com.talentica.graphite.atom;


public class AndAtom implements ConjuctionAtom{
	public static final AndAtom AND = new AndAtom();
	
	private AndAtom(){
		
	}
	@Override
	public long getId() {
		return -786;
	}

	@Override
	public String getName() {
		return "and";
	}

	@Override
	public AtomType getAtomType() {
		return AtomType.conjuction;
	}

}
