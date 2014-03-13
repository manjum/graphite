package com.talentica.graphite.bond;

import org.neo4j.graphdb.RelationshipType;


public class Position implements RelationshipType{
	public static int cieling = 15;
	private static Position POS_LINKS[] = new Position[Position.cieling];
	
	private final int pos;
	private final String name;
	
	private Position(String name, int pos) {
		this.name = name;
		this.pos = pos;
	}
	public int getPos(){
		return this.pos;
	}
	static{
		for(int i=0; i < Position.cieling; i++){
			Position posLink = new Position("pos_"+i, i);
			POS_LINKS[i] = posLink;
		}
	}
	public static Position getPosLink(int i){
		return POS_LINKS[i];
	}
	public static Position getNextPosition(Position position){
		return POS_LINKS[position.getPos()+1];
	}
	public static Position getPreviousPosition(Position position){
		return POS_LINKS[position.getPos()-1];
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + pos;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		if (pos != other.pos)
			return false;
		return true;
	}
	public String name() {
		return this.name;
	}
}
