package com.talentica.graphite.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.talentica.graphite.atom.ObjectAtom;


public class Connection {
	private final ObjectAtom connection;
	private final List<Route> routes = new ArrayList<Route>(5);

	Connection(ObjectAtom connection) {
		this.connection = connection;
	}

	void addRoute(Route route){
		routes.add(route);
	}


	/**
	 * @return - connection strength for a given connection id, 0 if invalid connection
	 */
	public float getConnectionStrength(){
		float strength = 0;
		for(Route route: routes){
			strength += route.getStrength()/route.getLevel();
		}
		return strength;
	}
	
	/**
	 * @return - all levels of this connection
	 */
	public List<Integer> getConnectionLevels(){
		List<Integer> connectionLevels = Collections.emptyList();
		if(!this.routes.isEmpty()){
			connectionLevels = new ArrayList<>(3);
		}
		for(Route route: this.routes){
			connectionLevels.add(route.getLevel());
		}
		return connectionLevels;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (int) (connection.getId() ^ (connection.getId() >>> 32));
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
		Connection other = (Connection) obj;
		if (connection.getId() != other.connection.getId())
			return false;
		return true;
	}
}
