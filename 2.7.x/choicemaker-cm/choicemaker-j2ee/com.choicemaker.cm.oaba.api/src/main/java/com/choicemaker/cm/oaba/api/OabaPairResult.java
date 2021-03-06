/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.api;

import java.io.Serializable;

import com.choicemaker.client.api.Decision;
import com.choicemaker.cm.core.base.RECORD_SOURCE_ROLE;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;

public interface OabaPairResult<T extends Comparable<T>> extends Serializable {

	/** Default id value for non-persistent pair results */
	long INVALID_ID = 0;

	long getId();

	long getJobId();

	RECORD_ID_TYPE getRecordIdType();

	T getRecord1Id();

	T getRecord2Id();

	RECORD_SOURCE_ROLE getRecord2Source();

	float getProbability();

	Decision getDecision();

	String[] getNotes();

	String getNotesAsDelimitedString();

	String getPairSHA1();

	// /** TransitivityPairResult */
	// String getEquivalenceClassSHA1();

	String exportToString();

}