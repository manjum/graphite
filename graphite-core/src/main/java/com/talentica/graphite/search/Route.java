package com.talentica.graphite.search;

import com.talentica.graphite.atom.Atom;
import com.talentica.graphite.atom.SearchObject;

public class Route {
	private final SearchObject[] path;
	Route(int level){
		if(level == 1){
			path = new SearchObject[level];
		}else{
			int size = (level*2)-1;
			path = new SearchObject[size];
		}
	}

	/**
	 * @return - returns the strength of the route. It is sum total of strength of the all hops in the route.
	 * It is inversely proportional to the length of the path.
	 */
	public float getStrength(){
		float strength = 0;
		for(SearchObject so: path){
			strength += so.getRank();
		}
		return strength/path.length/path.length;
	}


	/**
	 * @return - returns the number of hops required to reach the destination
	 */
	public int getLevel(){
		return this.path.length;
	}

	void addHop(SearchObject hop, int pos){
		path[pos] = hop;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Route other = (Route) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String str = "--";
		for(Atom atom: path){
			str = str.concat(atom.getName()).concat("--");
		}
		return str;
	}

}
