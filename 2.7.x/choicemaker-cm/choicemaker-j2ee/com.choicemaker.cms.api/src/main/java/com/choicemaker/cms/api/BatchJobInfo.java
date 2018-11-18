package com.choicemaker.cms.api;

import com.choicemaker.cm.batch.api.BatchJobStatus;

public interface BatchJobInfo {

	long getJobId();

	String getExternalId();
	
	String getDescription();
	
	BatchJobStatus getJobStatus();
		
}
