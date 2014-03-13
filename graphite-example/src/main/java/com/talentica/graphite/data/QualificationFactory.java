package com.talentica.graphite.data;

import java.util.ArrayList;
import java.util.List;

import com.talentica.graphite.domain.Candidate;
import com.talentica.graphite.domain.Qualification;

public class QualificationFactory {
	private static final List<Qualification> qualifications = new ArrayList<>(5);
	static{

		Candidate ms = CandidateFactory.getCandidates().get("ms dhoni");
		Qualification qms = new Qualification("ms's qualification");
		qms.setSchool(InstituteFactory.getInstitutes().get("nitc"));
		qms.setDuration(DurationFactory.getDuration(6, 2006, 4, 2010));
		qms.setDegree("B.Tech");
		qms.setOwner(ms);
		qualifications.add(qms);

		Qualification q4ms = new Qualification("ms's qualification1");
		q4ms.setSchool(InstituteFactory.getInstitutes().get("iitk"));
		q4ms.setDuration(DurationFactory.getDuration(6, 2010, 4, 2012));
		q4ms.setDegree("M.Tech");
		q4ms.setOwner(ms);
		qualifications.add(q4ms);

		Candidate zk = CandidateFactory.getCandidates().get("zaheer khan");
		Qualification qzk = new Qualification("zk's qualification");
		qzk.setSchool(InstituteFactory.getInstitutes().get("nitc"));
		qzk.setDuration(DurationFactory.getDuration(5, 2006, 4, 2010));
		qzk.setDegree("B.Tech");
		qzk.setOwner(zk);
		qualifications.add(qzk);

		Candidate sd = CandidateFactory.getCandidates().get("shikhar dhawan");
		Qualification qsd = new Qualification("sd's qualification");
		qsd.setSchool(InstituteFactory.getInstitutes().get("iitk"));
		qsd.setDuration(DurationFactory.getDuration(4, 2006, 4, 2010));
		qsd.setDegree("B.Tech");
		qsd.setOwner(sd);
		qualifications.add(qsd);
	}

	public static List<Qualification> getQualifications() {
		return qualifications;
	}
}
