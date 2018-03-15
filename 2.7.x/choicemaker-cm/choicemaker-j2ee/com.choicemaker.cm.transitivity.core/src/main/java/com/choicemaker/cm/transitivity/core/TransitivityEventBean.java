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

public class TransitivityEventBean extends ProcessingEventBean implements
		ProcessingEvent, TransitivityProcessingConstants {

	private static final long serialVersionUID = 271L;

	public TransitivityEventBean(String name, int id, float estimate) {
		super(name, id, estimate);
	}

	public TransitivityEventBean(TransitivityEvent event) {
		super(event.name(), event.getEventId(), event.getPercentComplete());
	}

	public static final TransitivityEventBean INIT_TRANSANALYSIS =
		new TransitivityEventBean(TransitivityEvent.INIT);

	public static final TransitivityEventBean CREATE_REC_VAL =
		new TransitivityEventBean(TransitivityEvent.CREATE_REC_VAL);

	public static final TransitivityEventBean DONE_REC_VAL =
		new TransitivityEventBean(TransitivityEvent.DONE_REC_VAL);

	public static final TransitivityEventBean BLOCK_BY_ONE_COLUMN =
		new TransitivityEventBean(TransitivityEvent.BLOCK_BY_ONE_COLUMN);

	public static final TransitivityEventBean DONE_BLOCK_BY_ONE_COLUMN =
		new TransitivityEventBean(
				TransitivityEvent.DONE_BLOCK_BY_ONE_COLUMN);

	public static final TransitivityEventBean OVERSIZED_TRIMMING =
		new TransitivityEventBean(TransitivityEvent.OVERSIZED_TRIMMING);

	public static final TransitivityEventBean DONE_OVERSIZED_TRIMMING =
		new TransitivityEventBean(
				TransitivityEvent.DONE_OVERSIZED_TRIMMING);

	public static final TransitivityEventBean DEDUP_BLOCKS =
		new TransitivityEventBean(TransitivityEvent.DEDUP_BLOCKS);

	public static final TransitivityEventBean DONE_DEDUP_BLOCKS =
		new TransitivityEventBean(TransitivityEvent.DONE_DEDUP_BLOCKS);

	public static final TransitivityEventBean DEDUP_OVERSIZED_EXACT =
		new TransitivityEventBean(TransitivityEvent.DEDUP_OVERSIZED_EXACT);

	public static final TransitivityEventBean DONE_DEDUP_OVERSIZED_EXACT =
		new TransitivityEventBean(
				TransitivityEvent.DONE_DEDUP_OVERSIZED_EXACT);

	public static final TransitivityEventBean DEDUP_OVERSIZED =
		new TransitivityEventBean(TransitivityEvent.DEDUP_OVERSIZED);

	public static final TransitivityEventBean DONE_TRANS_DEDUP_OVERSIZED =
		new TransitivityEventBean(
				TransitivityEvent.DONE_TRANS_DEDUP_OVERSIZED);

	public static final TransitivityEventBean DONE_REVERSE_TRANSLATE_BLOCK =
		new TransitivityEventBean(
				TransitivityEvent.DONE_REVERSE_TRANSLATE_BLOCK);

	public static final TransitivityEventBean DONE_REVERSE_TRANSLATE_OVERSIZED =
		new TransitivityEventBean(
				TransitivityEvent.DONE_REVERSE_TRANSLATE_OVERSIZED);

	public static final TransitivityEventBean CREATE_CHUNK_IDS =
		new TransitivityEventBean(TransitivityEvent.CREATE_CHUNK_IDS);

	public static final TransitivityEventBean CREATE_CHUNK_OVERSIZED_IDS =
		new TransitivityEventBean(
				TransitivityEvent.CREATE_CHUNK_OVERSIZED_IDS);

	public static final TransitivityEventBean DONE_CREATE_CHUNK_IDS =
		new TransitivityEventBean(TransitivityEvent.DONE_CREATE_CHUNK_IDS);

	public static final TransitivityEventBean DONE_CREATE_CHUNK_DATA =
		new TransitivityEventBean(
				TransitivityEvent.DONE_CREATE_CHUNK_DATA);

	public static final TransitivityEventBean ALLOCATE_CHUNKS =
		new TransitivityEventBean(TransitivityEvent.ALLOCATE_CHUNKS);

	public static final TransitivityEventBean DONE_ALLOCATE_CHUNKS =
		new TransitivityEventBean(TransitivityEvent.DONE_ALLOCATE_CHUNKS);

	public static final TransitivityEventBean MATCHING_DATA =
		new TransitivityEventBean(TransitivityEvent.MATCHING_DATA);

	public static final TransitivityEventBean DONE_MATCHING_DATA =
		new TransitivityEventBean(TransitivityEvent.DONE_MATCHING_DATA);

	public static final TransitivityEventBean OUTPUT_DEDUP_MATCHES =
		new TransitivityEventBean(TransitivityEvent.OUTPUT_DEDUP_MATCHES);

	public static final TransitivityEventBean MERGE_DEDUP_MATCHES =
		new TransitivityEventBean(TransitivityEvent.MERGE_DEDUP_MATCHES);

	public static final TransitivityEventBean DONE_DEDUP_MATCHES =
		new TransitivityEventBean(TransitivityEvent.DONE_DEDUP_MATCHES);

	public static final TransitivityEventBean DONE_TRANSITIVITY_PAIRWISE =
		new TransitivityEventBean(TransitivityEvent.DONE_TRANSITIVITY_PAIRWISE);

}
