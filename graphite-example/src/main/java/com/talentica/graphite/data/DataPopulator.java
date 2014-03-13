package com.talentica.graphite.data;

import com.talentica.graphite.api.exception.GraphiteAnnotationParseException;
import com.talentica.graphite.api.exception.ObjectWriteException;
import com.talentica.graphite.api.index.GraphiteAnnotationParser;
import com.talentica.graphite.api.index.ObjectNodeWriter;
import com.talentica.graphite.domain.Candidate;
import com.talentica.graphite.domain.Qualification;
import com.talentica.graphite.domain.WorkEx;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.store.StoreResources;

public class DataPopulator {
	private StoreResources storeResources;
	public DataPopulator(StoreResources storeResources) throws MissingAtomException, InvalidAtomException{
		this.storeResources = storeResources;
	}
	public void populateData() throws MissingAtomException, InvalidAtomException, IllegalArgumentException, IllegalAccessException, ObjectWriteException, GraphiteAnnotationParseException{
		GraphiteAnnotationParser gap = new GraphiteAnnotationParser("com.talentica.graphite.domain", storeResources);
		gap.parse();
		ObjectNodeWriter objNodeWriter = new ObjectNodeWriter(this.storeResources);
		for(Candidate candidate : CandidateFactory.getCandidates().values()){
			objNodeWriter.write(candidate);
		}
		for(WorkEx workEx: WorkExFactory.getWorkexs()){
			objNodeWriter.write(workEx);
		}
		for(Qualification qual : QualificationFactory.getQualifications()){
			objNodeWriter.write(qual);
		}
	}
}
