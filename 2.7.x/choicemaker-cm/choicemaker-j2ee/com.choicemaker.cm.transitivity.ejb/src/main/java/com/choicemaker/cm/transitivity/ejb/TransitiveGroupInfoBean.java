package com.choicemaker.cm.transitivity.ejb;

import java.util.List;

import com.choicemaker.cm.batch.api.BATCH_RESULTS_PERSISTENCE_SCHEME;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;
import com.choicemaker.cm.transitivity.api.TransitiveGroupInfo;

public class TransitiveGroupInfoBean implements TransitiveGroupInfo {

	public static final String PN_TRANSMATCH_PAIR_FILE = "TRANSMATCH_PAIR";
	public static final String PN_TRANSMATCH_GROUP_FILE = "TRANSMATCH_GROUP";

	private final List<String> groupFileURIs;
	private final int holdGroupCount;
	private final int mergeGroupCount;
	private final BATCH_RESULTS_PERSISTENCE_SCHEME persistenceScheme;
	private final RECORD_ID_TYPE recordIdType;

	public TransitiveGroupInfoBean(List<String> groupFileURIs,
			int holdGroupCount, int mergeGroupCount,
			BATCH_RESULTS_PERSISTENCE_SCHEME persistenceScheme,
			RECORD_ID_TYPE recordIdType) {
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
	public BATCH_RESULTS_PERSISTENCE_SCHEME getPersistenceScheme() {
		return persistenceScheme;
	}

	@Override
	public RECORD_ID_TYPE getRecordIdType() {
		return recordIdType;
	}

}
