/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.core;

import com.choicemaker.cm.args.ProcessingEventBean;
import com.choicemaker.cm.args.ProcessingEvent;

public class TransitivityProcessingEvent extends ProcessingEventBean implements
		ProcessingEvent, TransitivityProcessing {

	private static final long serialVersionUID = 271L;

	public TransitivityProcessingEvent(String name, int id, float estimate) {
		super(name, id, estimate);
	}

	public TransitivityProcessingEvent(TransitivityEvent event) {
		super(event.name(), event.getEventId(), event.getPercentComplete());
	}

	public static final TransitivityProcessingEvent INIT_TRANSANALYSIS =
		new TransitivityProcessingEvent(TransitivityEvent.INIT);

	public static final TransitivityProcessingEvent CREATE_REC_VAL =
		new TransitivityProcessingEvent(TransitivityEvent.CREATE_REC_VAL);

	public static final TransitivityProcessingEvent DONE_REC_VAL =
		new TransitivityProcessingEvent(TransitivityEvent.DONE_REC_VAL);

	public static final TransitivityProcessingEvent BLOCK_BY_ONE_COLUMN =
		new TransitivityProcessingEvent(TransitivityEvent.BLOCK_BY_ONE_COLUMN);

	public static final TransitivityProcessingEvent DONE_BLOCK_BY_ONE_COLUMN =
		new TransitivityProcessingEvent(
				TransitivityEvent.DONE_BLOCK_BY_ONE_COLUMN);

	public static final TransitivityProcessingEvent OVERSIZED_TRIMMING =
		new TransitivityProcessingEvent(TransitivityEvent.OVERSIZED_TRIMMING);

	public static final TransitivityProcessingEvent DONE_OVERSIZED_TRIMMING =
		new TransitivityProcessingEvent(
				TransitivityEvent.DONE_OVERSIZED_TRIMMING);

	public static final TransitivityProcessingEvent DEDUP_BLOCKS =
		new TransitivityProcessingEvent(TransitivityEvent.DEDUP_BLOCKS);

	public static final TransitivityProcessingEvent DONE_DEDUP_BLOCKS =
		new TransitivityProcessingEvent(TransitivityEvent.DONE_DEDUP_BLOCKS);

	public static final TransitivityProcessingEvent DEDUP_OVERSIZED_EXACT =
		new TransitivityProcessingEvent(TransitivityEvent.DEDUP_OVERSIZED_EXACT);

	public static final TransitivityProcessingEvent DONE_DEDUP_OVERSIZED_EXACT =
		new TransitivityProcessingEvent(
				TransitivityEvent.DONE_DEDUP_OVERSIZED_EXACT);

	public static final TransitivityProcessingEvent DEDUP_OVERSIZED =
		new TransitivityProcessingEvent(TransitivityEvent.DEDUP_OVERSIZED);

	public static final TransitivityProcessingEvent DONE_TRANS_DEDUP_OVERSIZED =
		new TransitivityProcessingEvent(
				TransitivityEvent.DONE_TRANS_DEDUP_OVERSIZED);

	public static final TransitivityProcessingEvent DONE_REVERSE_TRANSLATE_BLOCK =
		new TransitivityProcessingEvent(
				TransitivityEvent.DONE_REVERSE_TRANSLATE_BLOCK);

	public static final TransitivityProcessingEvent DONE_REVERSE_TRANSLATE_OVERSIZED =
		new TransitivityProcessingEvent(
				TransitivityEvent.DONE_REVERSE_TRANSLATE_OVERSIZED);

	public static final TransitivityProcessingEvent CREATE_CHUNK_IDS =
		new TransitivityProcessingEvent(TransitivityEvent.CREATE_CHUNK_IDS);

	public static final TransitivityProcessingEvent CREATE_CHUNK_OVERSIZED_IDS =
		new TransitivityProcessingEvent(
				TransitivityEvent.CREATE_CHUNK_OVERSIZED_IDS);

	public static final TransitivityProcessingEvent DONE_CREATE_CHUNK_IDS =
		new TransitivityProcessingEvent(TransitivityEvent.DONE_CREATE_CHUNK_IDS);

	public static final TransitivityProcessingEvent DONE_CREATE_CHUNK_DATA =
		new TransitivityProcessingEvent(
				TransitivityEvent.DONE_CREATE_CHUNK_DATA);

	public static final TransitivityProcessingEvent ALLOCATE_CHUNKS =
		new TransitivityProcessingEvent(TransitivityEvent.ALLOCATE_CHUNKS);

	public static final TransitivityProcessingEvent DONE_ALLOCATE_CHUNKS =
		new TransitivityProcessingEvent(TransitivityEvent.DONE_ALLOCATE_CHUNKS);

	public static final TransitivityProcessingEvent MATCHING_DATA =
		new TransitivityProcessingEvent(TransitivityEvent.MATCHING_DATA);

	public static final TransitivityProcessingEvent DONE_MATCHING_DATA =
		new TransitivityProcessingEvent(TransitivityEvent.DONE_MATCHING_DATA);

	public static final TransitivityProcessingEvent OUTPUT_DEDUP_MATCHES =
		new TransitivityProcessingEvent(TransitivityEvent.OUTPUT_DEDUP_MATCHES);

	public static final TransitivityProcessingEvent MERGE_DEDUP_MATCHES =
		new TransitivityProcessingEvent(TransitivityEvent.MERGE_DEDUP_MATCHES);

	public static final TransitivityProcessingEvent DONE_DEDUP_MATCHES =
		new TransitivityProcessingEvent(TransitivityEvent.DONE_DEDUP_MATCHES);

	public static final TransitivityProcessingEvent DONE_TRANSITIVITY_PAIRWISE =
		new TransitivityProcessingEvent(TransitivityEvent.DONE_TRANSITIVITY_PAIRWISE);

}
