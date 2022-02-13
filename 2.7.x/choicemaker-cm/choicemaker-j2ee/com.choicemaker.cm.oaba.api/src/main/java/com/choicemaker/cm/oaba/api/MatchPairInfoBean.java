/*******************************************************************************
 * Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.cm.oaba.api;

import java.util.List;

import com.choicemaker.cm.batch.api.BATCH_RESULTS_PERSISTENCE_SCHEME;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;

public class MatchPairInfoBean implements MatchPairInfo {

	public static final String PN_OABA_MATCH_RESULT_FILE = "MATCH_PAIR";

	private final int holdCount;
	private final int matchCount;
	private final int pairCount;
	private final List<String> pairFileURIs;
	private final BATCH_RESULTS_PERSISTENCE_SCHEME persistenceScheme;
	private final RECORD_ID_TYPE recordIdType;

	private final int differCount;

	public MatchPairInfoBean(int differCount, int holdCount, int matchCount,
			int pairCount, List<String> pairFileURIs,
			BATCH_RESULTS_PERSISTENCE_SCHEME persistenceScheme,
			RECORD_ID_TYPE recordIdType) {
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
	@Override
	public int getDifferCount() {
		return differCount;
	}

	// @Override
	@Override
	public int getHoldCount() {
		return holdCount;
	}

	// @Override
	@Override
	public int getMatchCount() {
		return matchCount;
	}

	// @Override
	@Override
	public int getPairCount() {
		return pairCount;
	}

	// @Override
	@Override
	public List<String> getPairFileURIs() {
		return pairFileURIs;
	}

	// @Override
	@Override
	public BATCH_RESULTS_PERSISTENCE_SCHEME getPersistenceScheme() {
		return persistenceScheme;
	}

	// @Override
	@Override
	public RECORD_ID_TYPE getRecordIdType() {
		return recordIdType;
	}

}
