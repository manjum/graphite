package com.talentica.graphite.search.query.chain;

import com.talentica.graphite.atom.Atom;
import com.talentica.graphite.atom.ConjuctionAtom;
import com.talentica.graphite.atom.PropertyAtom;

public class QueryTemplate {
	public static final String INCOMING = "<-[:%s]-";
	public static final String OUTGOING = "-[:%s]->";
	public static final String BOTH = "-[:%s]-";
			
	String startClause = "";
	String matchClause = "";
	String whereClause = "";
	String returnClause = "";

	QueryTemplate() {
	}

	public String getStartClause() {
		return startClause;
	}

	public String getReturnClause() {
		return returnClause;
	}

	void addStart(int pos, Atom atom){
		if( startClause.equals("") ){
			startClause = startClause.concat( String.format("obj%s=node(%s)", pos, atom.getId()) );
		}else{
			startClause = startClause.concat( String.format(",obj%s=node(%s)", pos, atom.getId()) );
		}
	}

	void appendStart(String startClause){
		this.startClause = this.startClause.concat(",").concat(startClause);
	}

	void addReturn(int pos){
		if( returnClause.equals("") ){
			returnClause = returnClause.concat( String.format("obj%s", pos ) );
		}else{
			returnClause = returnClause.concat( String.format(",obj%s", pos ) );
		}
	}

	void addConenctionReturn(int level){
		if( returnClause.equals("") ){
			returnClause = returnClause.concat( String.format("r%s,c%s", level, level) );
		}else{
			returnClause = returnClause.concat( String.format(",r%s,c%s", level, level) );
		}
	}

	void appendMatch(PropertyAtom propAtom, Direction direction){
		String format = BOTH;
		switch(direction){
		case incoming:
			format = INCOMING;
			break;
		case outgoing:
			format = OUTGOING;
			break;
		default:
			format = BOTH;
		}
		matchClause = matchClause.concat(String.format(format, propAtom.getName() ) );
	}
	
	void prependMatch(PropertyAtom propAtom, Direction direction){
		String format = BOTH;
		switch(direction){
		case incoming:
			format = INCOMING;
			break;
		case outgoing:
			format = OUTGOING;
			break;
		default:
			format = BOTH;
		}
		matchClause = String.format(format, propAtom.getName() ).concat(matchClause);
	}

	void appendMatch(ConjuctionAtom propAtom){
		matchClause = matchClause.concat(",");
	}

	void appendMatch(int pos){
		matchClause = matchClause.concat( String.format("(obj%s)", pos) );
	}
	
	void prependMatch(int pos){
		matchClause = String.format("(obj%s)", pos).concat(matchClause);
	}

	void appendConnectionMatch(int pos, boolean relation){
		if(relation){
			matchClause = matchClause.concat( String.format("-[r%s]-", pos) );
		}else{
			matchClause = matchClause.concat( String.format("(c%s)", pos) );
		}
	}

	void addWhere(int pos){
		if( whereClause.equals("") ){
			whereClause = whereClause.concat( String.format("c%d.type='domain_obj'", pos) );
		}else{
			whereClause = whereClause.concat( String.format(" and c%d.type='domain_obj'", pos) );
		}
		
	}
	void addConenctionStart(long nodeId){
		startClause = startClause.concat( String.format("n=node(%s)", nodeId) );
		matchClause = matchClause.concat("(n)");
	}

	public String getQueryString() {
		String str = "";
		if(!startClause.isEmpty()){
			str = str.concat("start ").concat(startClause);
		}
		if(!matchClause.isEmpty()){
			str = str.concat(" match ").concat(matchClause);
		}
		if(!whereClause.isEmpty()){
			str = str.concat(" where ").concat(whereClause);
		}
		if(!returnClause.isEmpty()){
			str = str.concat(" return ").concat(returnClause);
		}
		if(!str.isEmpty()){
			str = str.concat(";");
		}
		return str;
	}
}
