package com.choicemaker.cms.beans;

import java.util.List;

import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;
import com.choicemaker.cms.api.MatchPairInfo;
import com.choicemaker.cms.api.PERSISTENCE_SCHEME;

public class MatchPairInfoBean implements MatchPairInfo {

	private final int holdCount;
	private final int matchCount;
	private final int pairCount;
	private final List<String> pairFileURIs;
	private final PERSISTENCE_SCHEME persistenceScheme;
	private final RECORD_ID_TYPE recordIdType;

	private final int differCount;

	public MatchPairInfoBean(int differCount, int holdCount, int matchCount,
			int pairCount, List<String> pairFileURIs,
			PERSISTENCE_SCHEME persistenceScheme, RECORD_ID_TYPE recordIdType) {
		super();
		this.differCount = differCount;
		this.holdCount = holdCount;
		this.matchCount = matchCount;
		this.pairCount = pairCount;
		this.pairFileURIs = pairFileURIs;
		this.persistenceScheme = persistenceScheme;
		this.recordIdType = recordIdType;
	}

	// @Override
	public int getDifferCount() {
		return differCount;
	}

	// @Override
	public int getHoldCount() {
		return holdCount;
	}

	// @Override
	public int getMatchCount() {
		return matchCount;
	}

	// @Override
	public int getPairCount() {
		return pairCount;
	}

	// @Override
	public List<String> getPairFileURIs() {
		return pairFileURIs;
	}

	// @Override
	public PERSISTENCE_SCHEME getPersistenceScheme() {
		return persistenceScheme;
	}

	// @Override
	public RECORD_ID_TYPE getRecordIdType() {
		return recordIdType;
	}

}
