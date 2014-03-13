package com.talentica.graphite.domain;

import com.talentica.graphite.api.index.ClassNode;
import com.talentica.graphite.api.index.ObjectNode;
import com.talentica.graphite.api.index.PropertyNode;

@ClassNode
public class WorkEx extends ObjectNode{

	public WorkEx(String name) {
		super(name);
	}
	@PropertyNode
	private String designation;

	@PropertyNode
	private Company employer;

	@PropertyNode
	private Candidate owner;

	@PropertyNode
	private Duration duration;

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public Company getEmployer() {
		return employer;
	}

	public void setEmployer(Company employer) {
		this.employer = employer;
	}

	public Candidate getOwner() {
		return owner;
	}

	public void setOwner(Candidate owner) {
		this.owner = owner;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}
}
