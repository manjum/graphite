package com.talentica.graphite.store;

public class QueryFactory {
	public static final String TYPE_FIELD = "type";
	public static final String GET_TYPE_QUERY = "start n=node:node_auto_index(name='%s') match (n)-[:type]->(type) return " + TYPE_FIELD;
	public static final String GET_SO_QUERY = "start name=node:node_auto_index(name='%s') match (so)-[:name]->(name) return so";
}
