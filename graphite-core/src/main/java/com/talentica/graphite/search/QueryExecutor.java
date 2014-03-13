package com.talentica.graphite.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.helpers.collection.IteratorUtil;

import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.atom.ObjectAtom;
import com.talentica.graphite.atom.PropertyAtom;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.search.query.chain.Chain;
import com.talentica.graphite.search.query.chain.QueryTemplate;
import com.talentica.graphite.store.ObjectAtomStore;
import com.talentica.graphite.store.PropertyAtomStore;
import com.talentica.graphite.store.StoreResources;

public class QueryExecutor {
	private final static Logger LOGGER = Logger.getLogger(QueryExecutor.class);
	private final StoreResources storeResources;
	private final PropertyAtomStore propStore;
	private final ObjectAtomStore objStore;


	public QueryExecutor(StoreResources storeResources) throws MissingAtomException, InvalidAtomException {
		this.storeResources = storeResources;
		this.propStore = (PropertyAtomStore) storeResources.getAtomStore(AtomType.domain_prop);
		this.objStore = (ObjectAtomStore) storeResources.getAtomStore(AtomType.domain_obj);
	}

	public List<Long> executeQuery(Chain chain){
		List<Long> atomIds = new ArrayList<Long>();
		QueryTemplate qTemplate = chain.getDBQueryTemplate();
		String query = qTemplate.getQueryString();
		LOGGER.debug(query);
		if(null != query && !query.equals("")){
			ExecutionResult result = storeResources.getEngine().execute(query);
			String columnName = qTemplate.getReturnClause();
			Iterator<Node> n_column = result.columnAs( columnName );
			for(Node n: IteratorUtil.asIterable(n_column)){
				atomIds.add(n.getId());
			}
		}
		return atomIds;
	}

	public Connections getConnections(int level, Long nodeId, Chain chain) throws MissingAtomException, InvalidAtomException{
		Connections connections = new Connections(nodeId, this.storeResources);
		String queryStr = chain.getConnectionQuery(level, nodeId);
		LOGGER.debug(queryStr);
		ExecutionResult execResult = storeResources.getEngine().execute(queryStr);
		for ( Map<String, Object> row : execResult )
		{
			Iterator<Entry<String, Object>> iterator = row.entrySet().iterator();
			Map<String, Object> result = new HashMap<>(level*2);
			for(int i=1;i<=level*2;i++){
				Entry<String, Object> next = iterator.next();
				result.put(next.getKey(), next.getValue());
			}
			int c = 1;
			int r = 0;
			int i = 1;
			Route route = new Route(level);
			while(i<level){
				String rKey = String.format("r%s", i);
				String cKey = String.format("c%s", i);
				Relationship relationship = (Relationship) result.get(rKey);
				PropertyAtom propAtom = this.propStore.getAtomByName(relationship.getType().name());
				propAtom.setRank(this.propStore.getRank(propAtom));
				route.addHop(propAtom, r);
				Node node = (Node) result.get(cKey);
				ObjectAtom objAtom = this.objStore.getAtomById(node.getId());
				objAtom.setRank(this.objStore.getRank(objAtom));
				route.addHop(objAtom, c);
				r += 2;
				c += 2;
				i += 1;
			}
			String rKey = String.format("r%s", i);
			String cKey = String.format("c%s", i);
			Relationship relationship = (Relationship) result.get(rKey);
			PropertyAtom propAtom = this.propStore.getAtomByName(relationship.getType().name());
			propAtom.setRank(this.propStore.getRank(propAtom));
			route.addHop(propAtom, r);

			Node node = (Node) result.get(cKey);
			if(node.getId() != nodeId){
				connections.addRoute(node.getId(), route);
			}
		}
		return connections;
	}
}
