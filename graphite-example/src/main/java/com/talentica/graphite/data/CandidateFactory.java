package com.talentica.graphite.data;

import java.util.HashMap;
import java.util.Map;

import com.talentica.graphite.domain.Candidate;

//DO NOT tamper with this data, test cases have heavy dependency on this data.
public class CandidateFactory {
	private static final Map<String, Candidate> candidates = new HashMap<String, Candidate>(5);
	static{
		Candidate sp = new Candidate("sushant pradhan");
		sp.setLocation(PlaceFactory.getPlaces().get("pune"));
		sp.addSkill("java");
		sp.addSkill("python");
		candidates.put(sp.getName(), sp);

		Candidate sr = new Candidate("sushant rajput");
		sr.setLocation(PlaceFactory.getPlaces().get("pune"));
		sr.addSkill("java");
		sr.addSkill("ruby");
		candidates.put(sr.getName(), sr);

		Candidate nk = new Candidate("nadeem khan");
		candidates.put(nk.getName(), nk);
		nk.setLocation(PlaceFactory.getPlaces().get("bangalore"));
		nk.addSkill("sap");
		nk.addSkill("scala");

		Candidate rm = new Candidate("rahul mehra");
		candidates.put(rm.getName(), rm);
		rm.setLocation(PlaceFactory.getPlaces().get("noida"));
		rm.addSkill("hadoop");
		rm.addSkill("python");

		Candidate ng = new Candidate("nitin garg");
		candidates.put(ng.getName(), ng);
		ng.setLocation(PlaceFactory.getPlaces().get("noida"));
		ng.addSkill("javascript");
		ng.addSkill("cpp");

		sp.addFriend(sr);
		sp.addFriend(nk);
		nk.addFriend(rm);
		ng.addFriend(sp);

		Candidate vt = new Candidate("virat kohli");
		candidates.put(vt.getName(), vt);
		rm.addFriend(vt);

		Candidate ms = new Candidate("ms dhoni");
		candidates.put(ms.getName(), ms);

		Candidate sd = new Candidate("shikhar dhawan");
		candidates.put(sd.getName(), sd);

		Candidate zk = new Candidate("zaheer khan");
		candidates.put(zk.getName(), zk);

	}
	public static Map<String, Candidate> getCandidates() {
		return candidates;
	}
}
