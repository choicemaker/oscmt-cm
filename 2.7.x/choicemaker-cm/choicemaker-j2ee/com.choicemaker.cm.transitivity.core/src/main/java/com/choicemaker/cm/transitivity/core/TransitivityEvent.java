/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.core;

import static com.choicemaker.cm.args.BatchProcessingConstants.EVT_INIT;
import static com.choicemaker.cm.args.BatchProcessingConstants.PCT_INIT;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.EVT_ALLOCATE_CHUNKS;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.EVT_BLOCK_BY_ONE_COLUMN;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.EVT_CREATE_CHUNK_IDS;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.EVT_CREATE_CHUNK_OVERSIZED_IDS;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.EVT_CREATE_REC_VAL;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.EVT_DEDUP_BLOCKS;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.EVT_DEDUP_OVERSIZED;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.EVT_DEDUP_OVERSIZED_EXACT;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.EVT_DONE_ALLOCATE_CHUNKS;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.EVT_DONE_BLOCK_BY_ONE_COLUMN;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.EVT_DONE_CREATE_CHUNK_DATA;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.EVT_DONE_CREATE_CHUNK_IDS;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.EVT_DONE_DEDUP_BLOCKS;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.EVT_DONE_DEDUP_MATCHES;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.EVT_DONE_DEDUP_OVERSIZED_EXACT;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.EVT_DONE_MATCHING_DATA;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.EVT_DONE_OVERSIZED_TRIMMING;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.EVT_DONE_REC_VAL;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.EVT_DONE_REVERSE_TRANSLATE_BLOCK;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.EVT_DONE_REVERSE_TRANSLATE_OVERSIZED;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.EVT_MATCHING_DATA;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.EVT_MERGE_DEDUP_MATCHES;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.EVT_OUTPUT_DEDUP_MATCHES;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.EVT_OVERSIZED_TRIMMING;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.PCT_ALLOCATE_CHUNKS;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.PCT_BLOCK_BY_ONE_COLUMN;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.PCT_CREATE_CHUNK_IDS;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.PCT_CREATE_CHUNK_OVERSIZED_IDS;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.PCT_CREATE_REC_VAL;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.PCT_DEDUP_BLOCKS;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.PCT_DEDUP_OVERSIZED;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.PCT_DEDUP_OVERSIZED_EXACT;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.PCT_DONE_ALLOCATE_CHUNKS;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.PCT_DONE_BLOCK_BY_ONE_COLUMN;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.PCT_DONE_CREATE_CHUNK_DATA;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.PCT_DONE_CREATE_CHUNK_IDS;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.PCT_DONE_DEDUP_BLOCKS;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.PCT_DONE_DEDUP_MATCHES;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.PCT_DONE_DEDUP_OVERSIZED_EXACT;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.PCT_DONE_MATCHING_DATA;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.PCT_DONE_OVERSIZED_TRIMMING;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.PCT_DONE_REC_VAL;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.PCT_DONE_REVERSE_TRANSLATE_BLOCK;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.PCT_DONE_REVERSE_TRANSLATE_OVERSIZED;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.PCT_MATCHING_DATA;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.PCT_MERGE_DEDUP_MATCHES;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.PCT_OUTPUT_DEDUP_MATCHES;
import static com.choicemaker.cm.oaba.core.OabaProcessingConstants.PCT_OVERSIZED_TRIMMING;

import com.choicemaker.cm.args.BatchProcessingConstants;

/**
 * Enumeration of transitivity events and completion estimates
 * 
 * @author rphall
 */
enum TransitivityEvent {

	QUEUED(EVT_INIT, PCT_INIT), CREATE_REC_VAL(EVT_CREATE_REC_VAL,
			PCT_CREATE_REC_VAL), DONE_REC_VAL(EVT_DONE_REC_VAL,
			PCT_DONE_REC_VAL), BLOCK_BY_ONE_COLUMN(EVT_BLOCK_BY_ONE_COLUMN,
			PCT_BLOCK_BY_ONE_COLUMN), DONE_BLOCK_BY_ONE_COLUMN(
			EVT_DONE_BLOCK_BY_ONE_COLUMN, PCT_DONE_BLOCK_BY_ONE_COLUMN),
	OVERSIZED_TRIMMING(EVT_OVERSIZED_TRIMMING, PCT_OVERSIZED_TRIMMING),
	DONE_OVERSIZED_TRIMMING(EVT_DONE_OVERSIZED_TRIMMING,
			PCT_DONE_OVERSIZED_TRIMMING), DEDUP_BLOCKS(EVT_DEDUP_BLOCKS,
			PCT_DEDUP_BLOCKS), DONE_DEDUP_BLOCKS(EVT_DONE_DEDUP_BLOCKS,
			PCT_DONE_DEDUP_BLOCKS), DEDUP_OVERSIZED_EXACT(
			EVT_DEDUP_OVERSIZED_EXACT, PCT_DEDUP_OVERSIZED_EXACT),
	DONE_DEDUP_OVERSIZED_EXACT(EVT_DONE_DEDUP_OVERSIZED_EXACT,
			PCT_DONE_DEDUP_OVERSIZED_EXACT), DEDUP_OVERSIZED(
			EVT_DEDUP_OVERSIZED, PCT_DEDUP_OVERSIZED),

	DONE_TRANS_DEDUP_OVERSIZED(
			TransitivityProcessingConstants.EVT_DONE_TRANS_DEDUP_OVERSIZED,
			TransitivityProcessingConstants.PCT_DONE_TRANS_DEDUP_OVERSIZED),

	DONE_REVERSE_TRANSLATE_BLOCK(EVT_DONE_REVERSE_TRANSLATE_BLOCK,
			PCT_DONE_REVERSE_TRANSLATE_BLOCK),
	DONE_REVERSE_TRANSLATE_OVERSIZED(EVT_DONE_REVERSE_TRANSLATE_OVERSIZED,
			PCT_DONE_REVERSE_TRANSLATE_OVERSIZED),

	CREATE_CHUNK_IDS(EVT_CREATE_CHUNK_IDS, PCT_CREATE_CHUNK_IDS),
	CREATE_CHUNK_OVERSIZED_IDS(EVT_CREATE_CHUNK_OVERSIZED_IDS,
			PCT_CREATE_CHUNK_OVERSIZED_IDS), DONE_CREATE_CHUNK_IDS(
			EVT_DONE_CREATE_CHUNK_IDS, PCT_DONE_CREATE_CHUNK_IDS),
	DONE_CREATE_CHUNK_DATA(EVT_DONE_CREATE_CHUNK_DATA,
			PCT_DONE_CREATE_CHUNK_DATA),

	ALLOCATE_CHUNKS(EVT_ALLOCATE_CHUNKS, PCT_ALLOCATE_CHUNKS),
	DONE_ALLOCATE_CHUNKS(EVT_DONE_ALLOCATE_CHUNKS, PCT_DONE_ALLOCATE_CHUNKS),
	MATCHING_DATA(EVT_MATCHING_DATA, PCT_MATCHING_DATA), DONE_MATCHING_DATA(
			EVT_DONE_MATCHING_DATA, PCT_DONE_MATCHING_DATA),

	OUTPUT_DEDUP_MATCHES(EVT_OUTPUT_DEDUP_MATCHES, PCT_OUTPUT_DEDUP_MATCHES),
	MERGE_DEDUP_MATCHES(EVT_MERGE_DEDUP_MATCHES, PCT_MERGE_DEDUP_MATCHES),
	DONE_DEDUP_MATCHES(EVT_DONE_DEDUP_MATCHES, PCT_DONE_DEDUP_MATCHES),

	DONE_TRANSITIVITY_PAIRWISE(
			TransitivityProcessingConstants.EVT_TRANSITIVITY_PAIRWISE,
			TransitivityProcessingConstants.PCT_TRANSITIVITY_PAIRWISE);

	private final int eventId;
	private final float percentComplete;

	TransitivityEvent(int evtId, float pct) {
		if (pct < BatchProcessingConstants.MINIMUM_FRACTION_COMPLETE
				|| pct > BatchProcessingConstants.MAXIMUM_FRACTION_COMPLETE) {
			throw new IllegalArgumentException("invalid percentage: " + pct);
		}
		this.eventId = evtId;
		this.percentComplete = pct;
	}

	public int getEventId() {
		return eventId;
	}

	public float getPercentComplete() {
		return percentComplete;
	}

	public String getEventName() {
		return name();
	}

}