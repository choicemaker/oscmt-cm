package com.choicemaker.cms.beans;

import java.util.List;

import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;
import com.choicemaker.cms.api.PERSISTENCE_SCHEME;
import com.choicemaker.cms.api.TransitiveGroupInfo;

public class TransitiveGroupInfoBean implements TransitiveGroupInfo {

	private final List<String> groupFileURIs;
	private final int holdGroupCount;
	private final int mergeGroupCount;
	private final PERSISTENCE_SCHEME persistenceScheme;
	private final RECORD_ID_TYPE recordIdType;
	
	public TransitiveGroupInfoBean(List<String> groupFileURIs,
			int holdGroupCount, int mergeGroupCount,
			PERSISTENCE_SCHEME persistenceScheme, RECORD_ID_TYPE recordIdType) {
		super();
		this.groupFileURIs = groupFileURIs;
		this.holdGroupCount = holdGroupCount;
		this.mergeGroupCount = mergeGroupCount;
		this.persistenceScheme = persistenceScheme;
		this.recordIdType = recordIdType;
	}

	@Override
	public List<String> getGroupFileURIs() {
		return groupFileURIs;
	}

	@Override
	public int getHoldGroupCount() {
		return holdGroupCount;
	}

	@Override
	public int getMergeGroupCount() {
		return mergeGroupCount;
	}

	@Override
	public PERSISTENCE_SCHEME getPersistenceScheme() {
		return persistenceScheme;
	}

	@Override
	public RECORD_ID_TYPE getRecordIdType() {
		return recordIdType;
	}

}
