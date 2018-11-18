package com.choicemaker.cms.api;

import java.util.List;

import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;

public interface TransitiveGroupInfo {

	List<String> getGroupFileURIs();
	
	int getHoldGroupCount();
	
	int getMergeGroupCount();
	
	PERSISTENCE_SCHEME getPersistenceScheme();

	RECORD_ID_TYPE getRecordIdType();

}
