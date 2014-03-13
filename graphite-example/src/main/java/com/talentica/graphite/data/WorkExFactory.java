package com.talentica.graphite.data;

import java.util.ArrayList;
import java.util.List;

import com.talentica.graphite.domain.Candidate;
import com.talentica.graphite.domain.WorkEx;

public class WorkExFactory {
	private static final List<WorkEx> workexs = new ArrayList<>(5);
	static{
		
		Candidate sp = CandidateFactory.getCandidates().get("sushant pradhan");
		WorkEx sp1 = new WorkEx("sp's workex1");
		sp1.setDesignation("sr. engineer");
		sp1.setEmployer(EmployerFactory.getEmployers().get("talentica"));
		sp1.setDuration(DurationFactory.getDuration(4, 2011, 8, 2014));
		sp1.setOwner(sp);
		workexs.add(sp1);
		
		WorkEx sp2 = new WorkEx("sp's workex2");
		sp2.setDesignation("engineer");
		sp2.setEmployer(EmployerFactory.getEmployers().get("cisco"));
		sp2.setDuration(DurationFactory.getDuration(4, 2007, 8, 2011));
		sp2.setOwner(sp);
		workexs.add(sp2);
		
		Candidate sr = CandidateFactory.getCandidates().get("sushant rajput");
		WorkEx sr1 = new WorkEx("sr's workex");
		sr1.setDesignation("engineer");
		sr1.setEmployer(EmployerFactory.getEmployers().get("adobe"));
		sr1.setDuration(DurationFactory.getDuration(4, 2008, 8, 2011));
		sr1.setOwner(sr);
		workexs.add(sr1);
		
		Candidate nk = CandidateFactory.getCandidates().get("nadeem khan");
		WorkEx nk1 = new WorkEx("nk's workex");
		nk1.setDesignation("engineer");
		nk1.setEmployer(EmployerFactory.getEmployers().get("adobe"));
		nk1.setDuration(DurationFactory.getDuration(6, 2007, 9, 2011));
		nk1.setOwner(nk);
		workexs.add(nk1);
	}
	public static List<WorkEx> getWorkexs() {
		return workexs;
	}
}
