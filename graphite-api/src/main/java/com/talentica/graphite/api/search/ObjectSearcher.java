package com.talentica.graphite.api.search;

import java.util.ArrayList;
import java.util.List;

import com.talentica.graphite.api.index.ObjectNode;
import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.search.Connections;
import com.talentica.graphite.search.QueryExecutor;
import com.talentica.graphite.search.query.chain.Chain;
import com.talentica.graphite.store.ObjectAtomStore;
import com.talentica.graphite.store.StoreResources;

public class ObjectSearcher {
	private final QueryExecutor queryExecutor;
	private final StoreResources storeResources;
	private final ObjectAtomStore objStore;

	public ObjectSearcher(StoreResources storeResources) throws MissingAtomException, InvalidAtomException {
		this.storeResources = storeResources;
		this.queryExecutor = new QueryExecutor(storeResources);
		this.objStore = (ObjectAtomStore) storeResources.getAtomStore(AtomType.domain_obj);
	}

	/**
	 * @param query - query that should be satisfied by this query
	 * @return - list of object Nodes that satisfy a given query.
	 */
	public List<ObjectNode> search(Query query) {
		List<Long> atomIds = this.queryExecutor.executeQuery(query.getQueryChain());
		List<ObjectNode> objNodes = new ArrayList<ObjectNode>(atomIds.size());
		for(Long id: atomIds){
			ObjectNode objNode;
			try {
				objNode = new ObjectNode(this.objStore.getAtomName(id));
				objNode.setId(id);
				objNodes.add(objNode);
			} catch (Exception e) {
				// Id's were returned by the system itself, hence fetching them should never fail. 
				System.err.println("This error should never occur");
			}
		}
		return objNodes;
	}

	/**
	 * @param level - level up to which connections are to be searched
	 * @param nodeId - node id for whose connections are to be found
	 * @param query - query which should be satisfied by the connections. pass null for all connections
	 * @return - all connections for the given node up to a given level.
	 * @throws InvalidAtomException 
	 * @throws MissingAtomException 
	 */
	public Connections getConnections(int level, Long nodeId, Query query) throws MissingAtomException, InvalidAtomException{
		Chain queryChain = null;
		if(null == query){
			queryChain = new Chain(this.storeResources);
		}else{
			queryChain = query.getQueryChain();
		}
		return this.queryExecutor.getConnections(level, nodeId, queryChain);
	}
}
