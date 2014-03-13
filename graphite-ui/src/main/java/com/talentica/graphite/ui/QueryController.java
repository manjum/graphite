package com.talentica.graphite.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.talentica.graphite.api.index.ObjectNode;
import com.talentica.graphite.api.search.ObjectSearcher;
import com.talentica.graphite.api.search.Query;
import com.talentica.graphite.api.search.QuerySuggestor;
import com.talentica.graphite.atom.AtomType;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.search.Connections;
import com.talentica.graphite.search.Route;
import com.talentica.graphite.search.exception.InvalidLinkException;
import com.talentica.graphite.store.ClassAtomStore;
import com.talentica.graphite.ui.utils.Utils;

@Controller
public class QueryController {
	private final Map<String, Query> queryMap = new HashMap<String, Query>();
	@RequestMapping(value="/", method = RequestMethod.GET)
	public String start(ModelMap model) throws MissingAtomException, InvalidAtomException {
		ClassAtomStore classStore = (ClassAtomStore) Utils.getStoreResources().getAtomStore(AtomType.domain_class);
		System.out.println(classStore.getAtomByName("duration"));
		return "index";
	}
	
	@RequestMapping(value="/query/{keyword}", method = RequestMethod.GET)
	public @ResponseBody String query(@PathVariable String keyword) throws InvalidAtomException, MissingAtomException, InvalidLinkException {
		QuerySuggestor qSuggestor  = Utils.getqSuggestor();
		List<Query> queries = qSuggestor.getQuery(keyword);
		JSONObject result = new JSONObject();
		JSONArray list = new JSONArray();
		for(Query query: queries){
			String humanReadableQueryString = query.getHumanReadableQueryString().trim();
			list.put(humanReadableQueryString);
			queryMap.put(humanReadableQueryString, query);
		}
		result.put("result", list);
		return result.toString();
	}
	@RequestMapping(value="/query/expand/{queryStr}", method = RequestMethod.GET)
	public @ResponseBody String expand(@PathVariable String queryStr) throws InvalidAtomException, MissingAtomException, InvalidLinkException {
		JSONObject result = new JSONObject();
		JSONArray list = new JSONArray();
		Query query = queryMap.get(queryStr);
		List<Query> queries = query.expand();
		for(Query expandedQuery: queries){
			String humanReadableQueryString = expandedQuery.getHumanReadableQueryString().trim();
			list.put(humanReadableQueryString);
			queryMap.put(humanReadableQueryString, expandedQuery);
		}
		result.put("result", list);
		return result.toString();
	}
	@RequestMapping(value="/query/contract/{queryStr}", method = RequestMethod.GET)
	public @ResponseBody String contract(@PathVariable String queryStr) throws InvalidAtomException, MissingAtomException, InvalidLinkException {
		JSONObject result = new JSONObject();
		Query query = queryMap.get(queryStr);
		query.contract();
		result.put("result", query.getHumanReadableQueryString());
		return result.toString();
	}
	
	@RequestMapping(value="/query/result/{queryStr}", method = RequestMethod.GET)
	public @ResponseBody String result(@PathVariable String queryStr) throws InvalidAtomException, MissingAtomException, InvalidLinkException {
		JSONObject result = new JSONObject();
		JSONArray list = new JSONArray();
		ObjectSearcher objSearcher = Utils.getObjSearcher();
		Query query = queryMap.get(queryStr);
		List<ObjectNode> r = objSearcher.search(query);
		for(ObjectNode objNode: r){
			JSONObject jNode = new JSONObject();
			jNode.put("id", objNode.getId());
			jNode.put("name", objNode.getName());
			list.put(jNode);
		}
		result.put("result", list);
		return result.toString();
	}
	@RequestMapping(value="/connections/{nodeId}/{level}/{queryStr}", method = RequestMethod.GET)
	public @ResponseBody String result(@PathVariable Long nodeId, @PathVariable Integer level, @PathVariable String queryStr) throws InvalidAtomException, MissingAtomException, InvalidLinkException {
		JSONObject result = new JSONObject();
		JSONArray list = new JSONArray();
		Connections connections = null;
		if(queryStr.equals("*")){
			connections = Utils.getObjSearcher().getConnections(level, nodeId, null);
		}else{
			Query query = this.queryMap.get(queryStr);
			connections = Utils.getObjSearcher().getConnections(level, nodeId, query);
		}
		if(null != connections){
			for(Long connectionId : connections.getConnections()){
				JSONObject connection = new JSONObject();
				JSONArray routes = new JSONArray();
				connection.put("id", connectionId);
				connection.put("name", connections.getConnectionName(connectionId));
				connection.put("strength", connections.getConnectionStrength(connectionId));
				for(Route route: connections.getRoutes(connectionId)){
					routes.put(route.toString());
				}
				connection.put("routes", routes);
				list.put(connection);
			}
		}
		result.put("result", list);
		return result.toString();
	}
}