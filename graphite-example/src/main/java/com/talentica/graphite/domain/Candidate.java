package com.talentica.graphite.domain;

import java.util.HashSet;
import java.util.Set;

import com.talentica.graphite.api.index.ClassNode;
import com.talentica.graphite.api.index.ObjectNode;
import com.talentica.graphite.api.index.PropertyNode;

@ClassNode
public class Candidate extends ObjectNode{

	public Candidate(String name) {
		super(name);
	}
	@PropertyNode(rank=2)
	private Set<Candidate> friend = new HashSet<>(5);

	@PropertyNode
	private Set<String> skill = new HashSet<>(5);

	@PropertyNode(rank=1)
	private Place location;

	public void addFriend(Candidate candidate){
		this.friend.add(candidate);
	}
	public Set<Candidate> getFriend() {
		return friend;
	}

	public void setFriend(Set<Candidate> friend) {
		this.friend = friend;
	}


	public void addSkill(String skill) {
		this.skill.add(skill);
	}

	public Place getLocation() {
		return location;
	}

	public void setLocation(Place location) {
		this.location = location;
	}



}
