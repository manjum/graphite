package com.talentica.graphite.data;

import java.util.HashMap;
import java.util.Map;

import com.talentica.graphite.domain.Company;

public class EmployerFactory {
	private static final Map<String, Company> employers = new HashMap<String, Company>(5);
	static{
		Company talentica = new Company("talentica");
		talentica.setLocation(PlaceFactory.getPlaces().get("pune"));
		employers.put(talentica.getName(), talentica);
		
		Company adobe = new Company("adobe");
		adobe.setLocation(PlaceFactory.getPlaces().get("noida"));
		employers.put(adobe.getName(), adobe);
		
		Company cisco = new Company("cisco");
		cisco.setLocation(PlaceFactory.getPlaces().get("bangalore"));
		employers.put(cisco.getName(), cisco);
		
		Company tcs = new Company("tcs");
		tcs.setLocation(PlaceFactory.getPlaces().get("pune"));
		employers.put(tcs.getName(), tcs);
		
		Company infosys = new Company("infosys");
		infosys.setLocation(PlaceFactory.getPlaces().get("mumbai"));
		employers.put(infosys.getName(), infosys);
		
	}
	public static Map<String, Company> getEmployers() {
		return employers;
	}
	

}
