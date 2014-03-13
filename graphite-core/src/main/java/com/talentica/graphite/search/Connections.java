package com.talentica.graphite.search;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.atom.ObjectAtom;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.store.ObjectAtomStore;
import com.talentica.graphite.store.StoreResources;

public class Connections {
	private final long sourceNodeId;
	private final ObjectAtomStore objStore;
	private final Map<Long, Set<Route>> connectionRouteMap = new HashMap<Long, Set<Route>>();

	Connections(long sourceNodeId, StoreResources storeResources) throws MissingAtomException, InvalidAtomException {
		this.sourceNodeId = sourceNodeId;
		this.objStore = (ObjectAtomStore) storeResources.getAtomStore(AtomType.domain_obj);
	}

	public long getSourceNodeId() {
		return sourceNodeId;
	}

	/**
	 * @return - all connections of sourceNodeId
	 */
	public Set<Long> getConnections(){
		return connectionRouteMap.keySet();
	}
	/**
	 * @param connectionId - node id of the connection whose strength is to be found
	 * @return - returns strength of the given connection, zero if connection does not exist.
	 * strength of a connection is sum total of all strengths of it's routes. 
	 * @throws InvalidAtomException 
	 * @throws MissingAtomException 
	 */
	public float getConnectionStrength(long connectionId) throws MissingAtomException, InvalidAtomException{
		float strength = 0;
		Set<Route> connectionRoutes = connectionRouteMap.get(connectionId);
		for(Route route: connectionRoutes){
			strength += route.getStrength();
		}
		ObjectAtom objAtom = this.objStore.getAtomById(connectionId);
		strength += this.objStore.getRank(objAtom);
		return strength;
	}

	public String getConnectionName(long connectionId) throws InvalidAtomException, MissingAtomException{
		return this.objStore.getAtomName(connectionId);
	}

	void addRoute(long connectionId, Route route){
		Set<Route> connectionRoutes = connectionRouteMap.get(connectionId);
		if(null == connectionRoutes){
			connectionRoutes = new HashSet<Route>(2);
		}
		connectionRoutes.add(route);
		this.connectionRouteMap.put(connectionId, connectionRoutes);
	}

	public void print() throws InvalidAtomException, MissingAtomException{
		for(Long connectionId: connectionRouteMap.keySet()){
			String conectionRoute = this.objStore.getAtomName(sourceNodeId).concat("--");
			for(Route route: connectionRouteMap.get(connectionId)){
				System.out.print(conectionRoute.concat(route.toString()).concat(this.objStore.getAtomName(connectionId)));
				System.out.println(" ," + route.getStrength());
			}
		}
	}
	public void printConnectionStrength() throws InvalidAtomException, MissingAtomException{
		for(Long connectionId: connectionRouteMap.keySet()){
			String connectionStrength = this.objStore.getAtomName(connectionId).concat(": ");
			System.out.println(connectionStrength + this.getConnectionStrength(connectionId));
		}
	}

	public Set<Route> getRoutes(long connectionId){
		Set<Route> routes = Collections.emptySet();
		Set<Route> connectionRoutes = connectionRouteMap.get(connectionId);
		if(null != connectionRoutes){
			routes = connectionRoutes;
		}
		return routes;
	}
}
