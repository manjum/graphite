package com.talentica.graphite.domain;

import com.talentica.graphite.api.index.ClassNode;
import com.talentica.graphite.api.index.ObjectNode;
import com.talentica.graphite.api.index.PropertyNode;

@ClassNode
public class Qualification extends ObjectNode{

	public Qualification(String name) {
		super(name);
	}

	@PropertyNode
	private Candidate owner;

	@PropertyNode
	private String degree;

	@PropertyNode
	private Duration duration;

	@PropertyNode
	private Institute school;

	public Candidate getOwner() {
		return owner;
	}

	public void setOwner(Candidate owner) {
		this.owner = owner;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public Institute getSchool() {
		return school;
	}

	public void setSchool(Institute school) {
		this.school = school;
	}
}
