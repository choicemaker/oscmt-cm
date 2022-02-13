/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.api;

import java.io.Serializable;

import com.choicemaker.cm.core.base.RECORD_SOURCE_ROLE;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;

public interface RecordIdTranslation<T extends Comparable<T>>
		extends Serializable, Comparable<RecordIdTranslation<T>> {

	/** Translated identifiers are non-negative */
	int INVALID_TRANSLATED_ID = -1;

	/** Persistence id */
	long getId();

	/** Job identifier */
	long getJobId();

	/** Translated id (job-specific) */
	int getTranslatedId();

	/** Record id */
	T getRecordId();

	RECORD_SOURCE_ROLE getRecordSourceRole();

	RECORD_ID_TYPE getRecordIdType();

}
