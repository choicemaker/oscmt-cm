package com.choicemaker.cm.transitivity.api;

import java.util.List;

import com.choicemaker.cm.batch.api.BATCH_RESULTS_PERSISTENCE_SCHEME;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;

public interface TransitiveGroupInfo {

	List<String> getGroupFileURIs();
	
	int getHoldGroupCount();
	
	int getMergeGroupCount();
	
	BATCH_RESULTS_PERSISTENCE_SCHEME getPersistenceScheme();

	RECORD_ID_TYPE getRecordIdType();

}
