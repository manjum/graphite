package com.talentica.graphite.search.query.chain;

import com.talentica.graphite.atom.AndAtom;
import com.talentica.graphite.atom.Atom;
import com.talentica.graphite.atom.ClassAtom;
import com.talentica.graphite.atom.ConjuctionAtom;
import com.talentica.graphite.atom.PropertyAtom;

public class DivergingQueryBuilder extends QueryBuilder{
	static final DivergingQueryBuilder DQBUILDER = new DivergingQueryBuilder();

	@Override
	String getQueryPhrase(Chain chain) {
		String query = "";
		if(chain.getSize() == 1){
			query = chain.getLink(0).getName();
		}
		else{
			query = chain.getLink(0).getName().concat("'s");
			for(int i=1; i< chain.getSize(); i++){
				Atom link = chain.getLink(i);
				if(link instanceof PropertyAtom || link instanceof ConjuctionAtom){
					query = query.concat(String.format(" %s", link.getName()));
				}
				else if(link instanceof ClassAtom){
					query = query.concat(String.format(" whose", link.getName()));
				}
				else{
					query = query.concat(String.format(" is %s", link.getName()));
				}
			}
		}
		return query;
	}

	@Override
	QueryTemplate getDBQueryTemplate(Chain chain) {
		QueryTemplate qTemplate = new QueryTemplate();
		if(chain.getSize() == 1){
			qTemplate.addStart(1, chain.getLink(0));
			qTemplate.addReturn(1);
		}
		else if(chain.getSize() == 2){
			qTemplate.addStart(1, chain.getLink(0));
			qTemplate.appendMatch(1);
			PropertyAtom prop = (PropertyAtom) chain.getLink(1);
			qTemplate.appendMatch(prop, Direction.outgoing);
			qTemplate.appendMatch(3);
			qTemplate.addReturn(3);
		}
		else{
			PropertyAtom prop = (PropertyAtom) chain.getLink(1);
			qTemplate = this.getDBTemplate(chain, 2);
			qTemplate.addStart(1, chain.getLink(0));
			qTemplate.appendMatch(AndAtom.AND);
			qTemplate.appendMatch(1);
			qTemplate.appendMatch(prop, Direction.outgoing);
			qTemplate.appendMatch(3);
		}
		return qTemplate;
	}

	@Override
	String getConnectionQueryString(int level, long nodeId, Chain chain){
		QueryTemplate connectionQueryTemplate = getConnectionQueryTemplate(level, nodeId);
		if(chain != null && chain.getChainState().isComplete(chain)){
			QueryTemplate chainTemplate = QueryBuilder.getQueryPhraseBuilder(chain.getChainType()).getDBQueryTemplate(chain);
			connectionQueryTemplate.appendStart(chainTemplate.startClause);
			String returnClause = chainTemplate.returnClause;
			if(chain.getSize() == 1){
				connectionQueryTemplate.startClause = connectionQueryTemplate.startClause.replace(returnClause, "c"+level);
			}
			else{
				String matchClause = chainTemplate.matchClause;
				connectionQueryTemplate.matchClause = connectionQueryTemplate.matchClause.concat(",").concat(matchClause);
				connectionQueryTemplate.matchClause = connectionQueryTemplate.matchClause.replace(returnClause, "c"+level);
			}
		}
		return connectionQueryTemplate.getQueryString();
	}
}
