/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.core;

import com.choicemaker.cm.oaba.core.OabaProcessingConstants;

/**
 * This interface defines event ids and completion estimates used in
 * transitivity processing.
 */
public interface TransitivityProcessingConstants
		extends OabaProcessingConstants {

	// -- Ordered events

	int EVT_DONE_TRANS_DEDUP_OVERSIZED = EVT_DONE_DEDUP_OVERSIZED;
	int EVT_TRANSITIVITY_PAIRWISE = 255;

	// -- Completion estimates

	float PCT_DONE_TRANS_DEDUP_OVERSIZED = PCT_DONE_DEDUP_OVERSIZED;
	float PCT_TRANSITIVITY_PAIRWISE = 0.99f;

}
