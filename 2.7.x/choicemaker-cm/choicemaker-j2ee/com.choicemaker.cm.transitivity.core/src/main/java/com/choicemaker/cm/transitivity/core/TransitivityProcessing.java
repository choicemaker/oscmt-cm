/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.transitivity.core;

import static com.choicemaker.cm.io.blocking.automated.offline.core.OabaProcessing.EVT_DONE_DEDUP_OVERSIZED;
import static com.choicemaker.cm.io.blocking.automated.offline.core.OabaProcessing.EVT_DONE_OABA;
import static com.choicemaker.cm.io.blocking.automated.offline.core.OabaProcessing.PCT_DONE_DEDUP_OVERSIZED;
import static com.choicemaker.cm.io.blocking.automated.offline.core.OabaProcessing.PCT_DONE_OABA;

import com.choicemaker.cm.io.blocking.automated.offline.core.OabaProcessing;

/**
 * This interface the processing steps of the Offline Automated Blocking
 * Algorithm (OABA).
 */
public interface TransitivityProcessing {

	// -- Ordered events used by transitivity analysis

	int EVT_DONE_TRANS_DEDUP_OVERSIZED = EVT_DONE_DEDUP_OVERSIZED;
	int EVT_DONE_TRANSANALYSIS = EVT_DONE_OABA;

	// -- Estimates of the completion status of a job, 0 - 100 percent

	float PCT_DONE_TRANS_DEDUP_OVERSIZED = PCT_DONE_DEDUP_OVERSIZED;
	float PCT_DONE_TRANSANALYSIS = PCT_DONE_OABA;

	public static final char DELIMIT = OabaProcessing.DELIMIT;

}
