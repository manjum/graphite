package com.talentica.graphite.search.query.chain;

public class EmptyQueryBuilder extends QueryBuilder{
	public static final EmptyQueryBuilder EQBUILDER = new EmptyQueryBuilder();
	private static final QueryTemplate EMPTY_TEMPLATE = new QueryTemplate();
	@Override
	String getQueryPhrase(Chain chain) {
		return "";
	}

	@Override
	QueryTemplate getDBQueryTemplate(Chain chain) {
		return EMPTY_TEMPLATE;
	}

	@Override
	String getConnectionQueryString(int level, long nodeId, Chain chain) {
		return getConnectionQueryTemplate(level, nodeId).getQueryString();
	}
}
