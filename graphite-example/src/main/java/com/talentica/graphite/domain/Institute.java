package com.talentica.graphite.domain;

import com.talentica.graphite.api.index.ClassNode;
import com.talentica.graphite.api.index.ObjectNode;
import com.talentica.graphite.api.index.PropertyNode;

@ClassNode
public class Institute extends ObjectNode {

	public Institute(String name) {
		super(name);
	}

	@PropertyNode
	private Place location;

	public Place getLocation() {
		return location;
	}

	public void setLocation(Place location) {
		this.location = location;
	}
}
