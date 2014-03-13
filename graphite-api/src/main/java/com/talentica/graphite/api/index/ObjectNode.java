package com.talentica.graphite.api.index;

public class ObjectNode {
	private final String name;
	private long id;
	private int rank = 1;
	
	public ObjectNode(String name) {
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}

	public long getId(){
		return this.id;
	}

	public void setId(long id){
		this.id = id;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	@Override
	public String toString() {
		return "ObjectNode [name=" + name + ", id=" + id + "]";
	}
}
