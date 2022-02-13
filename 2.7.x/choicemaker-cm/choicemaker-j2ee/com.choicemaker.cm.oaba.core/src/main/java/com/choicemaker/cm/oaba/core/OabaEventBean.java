/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.core;

import com.choicemaker.cm.args.ProcessingEvent;
import com.choicemaker.cm.args.ProcessingEventBean;

public class OabaEventBean extends ProcessingEventBean implements
		ProcessingEvent, OabaProcessingConstants {

	private static final long serialVersionUID = 271L;

	public OabaEventBean(String name, int id, float estimate) {
		super(name, id, estimate);
	}

	public OabaEventBean(OabaEvent event) {
		super(event.name(), event.getEventId(), event.getPercentComplete());
	}

	public static final OabaEventBean QUEUED =
			new OabaEventBean(OabaEvent.QUEUED);

	public static final OabaEventBean CREATE_REC_VAL =
		new OabaEventBean(OabaEvent.CREATE_REC_VAL);

	public static final OabaEventBean DONE_REC_VAL =
		new OabaEventBean(OabaEvent.DONE_REC_VAL);

	public static final OabaEventBean BLOCK_BY_ONE_COLUMN =
		new OabaEventBean(OabaEvent.BLOCK_BY_ONE_COLUMN);

	public static final OabaEventBean DONE_BLOCK_BY_ONE_COLUMN =
		new OabaEventBean(OabaEvent.DONE_BLOCK_BY_ONE_COLUMN);

	public static final OabaEventBean OVERSIZED_TRIMMING =
		new OabaEventBean(OabaEvent.OVERSIZED_TRIMMING);

	public static final OabaEventBean DONE_OVERSIZED_TRIMMING =
		new OabaEventBean(OabaEvent.DONE_OVERSIZED_TRIMMING);

	public static final OabaEventBean DEDUP_BLOCKS =
		new OabaEventBean(OabaEvent.DEDUP_BLOCKS);

	public static final OabaEventBean DONE_DEDUP_BLOCKS =
		new OabaEventBean(OabaEvent.DONE_DEDUP_BLOCKS);

	public static final OabaEventBean DEDUP_OVERSIZED_EXACT =
		new OabaEventBean(OabaEvent.DEDUP_OVERSIZED_EXACT);

	public static final OabaEventBean DONE_DEDUP_OVERSIZED_EXACT =
		new OabaEventBean(OabaEvent.DONE_DEDUP_OVERSIZED_EXACT);

	public static final OabaEventBean DEDUP_OVERSIZED =
		new OabaEventBean(OabaEvent.DEDUP_OVERSIZED);

	public static final OabaEventBean DONE_DEDUP_OVERSIZED =
		new OabaEventBean(OabaEvent.DONE_DEDUP_OVERSIZED);

	public static final OabaEventBean DONE_REVERSE_TRANSLATE_BLOCK =
		new OabaEventBean(OabaEvent.DONE_REVERSE_TRANSLATE_BLOCK);

	public static final OabaEventBean DONE_REVERSE_TRANSLATE_OVERSIZED =
		new OabaEventBean(OabaEvent.DONE_REVERSE_TRANSLATE_OVERSIZED);

	public static final OabaEventBean CREATE_CHUNK_IDS =
		new OabaEventBean(OabaEvent.CREATE_CHUNK_IDS);

	public static final OabaEventBean CREATE_CHUNK_OVERSIZED_IDS =
		new OabaEventBean(OabaEvent.CREATE_CHUNK_OVERSIZED_IDS);

	public static final OabaEventBean DONE_CREATE_CHUNK_IDS =
		new OabaEventBean(OabaEvent.DONE_CREATE_CHUNK_IDS);

	public static final OabaEventBean DONE_CREATE_CHUNK_DATA =
		new OabaEventBean(OabaEvent.DONE_CREATE_CHUNK_DATA);

	public static final OabaEventBean ALLOCATE_CHUNKS =
		new OabaEventBean(OabaEvent.ALLOCATE_CHUNKS);

	public static final OabaEventBean DONE_ALLOCATE_CHUNKS =
		new OabaEventBean(OabaEvent.DONE_ALLOCATE_CHUNKS);

	public static final OabaEventBean MATCHING_DATA =
		new OabaEventBean(OabaEvent.MATCHING_DATA);

	public static final OabaEventBean DONE_MATCHING_CHUNKS =
			new OabaEventBean(OabaEvent.DONE_MATCHING_CHUNKS);

	public static final OabaEventBean DONE_MATCHING_DATA =
		new OabaEventBean(OabaEvent.DONE_MATCHING_DATA);

	public static final OabaEventBean OUTPUT_DEDUP_MATCHES =
		new OabaEventBean(OabaEvent.OUTPUT_DEDUP_MATCHES);

	public static final OabaEventBean MERGE_DEDUP_MATCHES =
		new OabaEventBean(OabaEvent.MERGE_DEDUP_MATCHES);

	public static final OabaEventBean DONE_DEDUP_MATCHES =
		new OabaEventBean(OabaEvent.DONE_DEDUP_MATCHES);

}
