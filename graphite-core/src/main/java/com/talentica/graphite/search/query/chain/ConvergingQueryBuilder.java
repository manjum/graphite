package com.talentica.graphite.search.query.chain;

import com.talentica.graphite.atom.Atom;
import com.talentica.graphite.atom.ClassAtom;
import com.talentica.graphite.atom.ConjuctionAtom;
import com.talentica.graphite.atom.PropertyAtom;


public class ConvergingQueryBuilder extends QueryBuilder{
	static final ConvergingQueryBuilder CQBUILDER = new ConvergingQueryBuilder();

	@Override
	String getQueryPhrase(Chain chain) {
		String query = "";
		Atom firstLink = chain.getLink(0);
		query = query.concat(firstLink.getName());
		for(int i=1; i<chain.getSize();i++){
			Atom link = chain.getLink(i);
			if(link instanceof PropertyAtom){
				query = query.concat(String.format(" whose %s is", link.getName()));
			}
			else if(link instanceof ClassAtom){
				query = query.concat( String.format( " a %s", link.getName() ) );
			}else if(link instanceof ConjuctionAtom){
				query = query.concat( String.format( " %s", link.getName() ) );
				i++;
			}
			else{
				query = query.concat( String.format( " %s", link.getName() ) );
			}
		}
		return query;
	}

	@Override
	QueryTemplate getDBQueryTemplate(Chain chain) {
		QueryTemplate qTemplate = getDBTemplate(chain, 0);
		return qTemplate;
	}
	
	@Override
	String getConnectionQueryString(int level, long nodeId, Chain chain){
		QueryTemplate qTemplate = getConnectionQueryTemplate(level, nodeId);
		if(chain != null && chain.getChainState().isComplete(chain)){
			QueryTemplate chainTemplate = QueryBuilder.getQueryPhraseBuilder(chain.getChainType()).getDBQueryTemplate(chain);
			String returnClause = chainTemplate.returnClause;
			String matchClause = chainTemplate.matchClause.replace(returnClause, "c"+level);
			qTemplate.appendStart(chainTemplate.startClause);
			qTemplate.matchClause = qTemplate.matchClause.concat(",").concat(matchClause);
		}
		return qTemplate.getQueryString();
	}
}
