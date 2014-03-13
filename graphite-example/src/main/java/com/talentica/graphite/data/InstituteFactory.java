package com.talentica.graphite.data;

import java.util.HashMap;
import java.util.Map;

import com.talentica.graphite.domain.Institute;
import com.talentica.graphite.domain.Place;

public class InstituteFactory {
	private static final Map<String, Institute> institutes = new HashMap<String, Institute>(5);
	static{
		Institute nitc = new Institute("nitc");
		nitc.setLocation(new Place("calicut"));
		institutes.put(nitc.getName(), nitc);

		Institute nitk = new Institute("nitk");
		nitk.setLocation(new Place("kurukshetra"));
		institutes.put(nitk.getName(), nitk);

		Institute iitb = new Institute("iitb");
		iitb.setLocation(new Place("bombay"));
		iitb.setRank(2);
		institutes.put(iitb.getName(), iitb);

		Institute iitc = new Institute("iitc");
		iitc.setLocation(new Place("chennai"));
		iitc.setRank(2);
		institutes.put(iitc.getName(), iitc);

		Institute iitk = new Institute("iitk");
		iitk.setLocation(new Place("kharagpur"));
		institutes.put(iitk.getName(), iitk);
		iitk.setRank(2);
	}

	public static Map<String, Institute> getInstitutes() {
		return institutes;
	}

}
