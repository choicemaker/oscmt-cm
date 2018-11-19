package com.choicemaker.cm.batch.api;

public interface BatchJobInfo {

	long getJobId();

	String getExternalId();
	
	String getDescription();
	
	BatchJobStatus getJobStatus();
		
}
