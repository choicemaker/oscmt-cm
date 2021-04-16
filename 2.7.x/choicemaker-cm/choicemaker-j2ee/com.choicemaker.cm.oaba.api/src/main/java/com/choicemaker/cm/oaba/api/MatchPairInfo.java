package com.choicemaker.cm.oaba.api;

import java.util.List;

import com.choicemaker.cm.batch.api.BATCH_RESULTS_PERSISTENCE_SCHEME;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;

public interface MatchPairInfo {
	
	int getDifferCount();
	
	int getHoldCount();
	
	int getMatchCount();
	
	int getPairCount();
	
	List<String> getPairFileURIs();

	BATCH_RESULTS_PERSISTENCE_SCHEME getPersistenceScheme();
	
	RECORD_ID_TYPE getRecordIdType();

}
