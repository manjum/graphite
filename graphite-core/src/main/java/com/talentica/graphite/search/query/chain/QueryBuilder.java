package com.talentica.graphite.search.query.chain;

import com.talentica.graphite.atom.AndAtom;
import com.talentica.graphite.atom.Atom;
import com.talentica.graphite.atom.ClassAtom;
import com.talentica.graphite.atom.ConjuctionAtom;
import com.talentica.graphite.atom.PropertyAtom;


public abstract class QueryBuilder {
	abstract String getQueryPhrase(Chain chain);
	abstract QueryTemplate getDBQueryTemplate(Chain chain);
	abstract String getConnectionQueryString(int level, long nodeId, Chain chain);
	
	public static QueryBuilder getQueryPhraseBuilder(ChainType chainType){
		switch (chainType) {
		case diverging:
			return DivergingQueryBuilder.DQBUILDER;
		case converging:
			return ConvergingQueryBuilder.CQBUILDER;
		default:
			return EmptyQueryBuilder.EQBUILDER;
		}
	}

	protected QueryTemplate getDBTemplate(Chain chain, int pos) {
		QueryTemplate qTemplate = new QueryTemplate();
		qTemplate.addStart(pos, chain.getLink(pos));
		qTemplate.addReturn(pos+1);

		int i=pos;
		int classPos = pos+1;
		for(;i<chain.getSize();i++){
			
			Atom link = chain.getLink(i);
			if(link instanceof PropertyAtom){
				PropertyAtom prop = (PropertyAtom) link;
				qTemplate.appendMatch(prop, Direction.outgoing);
			}
			else if(link instanceof ClassAtom){
				qTemplate.appendMatch(i+1);
				classPos = i+1;
			}else if(link instanceof ConjuctionAtom){
				AndAtom con = (AndAtom) link;
				qTemplate.appendMatch(con);
				qTemplate.appendMatch(classPos);
				i++;
			}
			else{
				qTemplate.addStart(i+1, chain.getLink(i));
				qTemplate.appendMatch(i+1);
			}
		}
		qTemplate.prependMatch(new PropertyAtom("type"), Direction.incoming);
		qTemplate.prependMatch(pos);
		return qTemplate;
	}
	
	protected QueryTemplate getConnectionQueryTemplate(int level, long nodeId) {
		QueryTemplate qTemplate = new QueryTemplate();
		qTemplate.addConenctionStart(nodeId);
		
		for(int i=1;i<=level;i++){
			qTemplate.appendConnectionMatch(i,true);
			qTemplate.appendConnectionMatch(i,false);
			qTemplate.addConenctionReturn(i);
			qTemplate.addWhere(i);
		}
		return qTemplate;
	}
	public static void main(String[] args) {
//		System.out.println(getConnectionQueryString(1, 123L, null));
	}
}

