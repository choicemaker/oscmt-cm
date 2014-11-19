package com.choicemaker.cm.io.blocking.automated.offline.server.impl;

import java.util.HashMap;
import java.util.Map;

import com.choicemaker.cm.io.blocking.automated.offline.core.OabaProcessing.OabaEvent;

import static com.choicemaker.cm.io.blocking.automated.offline.core.OabaProcessing.*;

class OabaProcessingUtil {

	private static Map<Integer, OabaEvent> mapOabaIdEvent = new HashMap<>();

	static {
		mapOabaIdEvent.put(EVT_INIT, OabaEvent.INIT);
		mapOabaIdEvent.put(EVT_CREATE_REC_VAL, OabaEvent.CREATE_REC_VAL);
		mapOabaIdEvent.put(EVT_DONE_REC_VAL, OabaEvent.DONE_REC_VAL);
		mapOabaIdEvent.put(EVT_BLOCK_BY_ONE_COLUMN,
				OabaEvent.BLOCK_BY_ONE_COLUMN);
		mapOabaIdEvent.put(EVT_DONE_BLOCK_BY_ONE_COLUMN,
				OabaEvent.DONE_BLOCK_BY_ONE_COLUMN);
		mapOabaIdEvent
				.put(EVT_OVERSIZED_TRIMMING, OabaEvent.OVERSIZED_TRIMMING);
		mapOabaIdEvent.put(EVT_DONE_OVERSIZED_TRIMMING,
				OabaEvent.DONE_OVERSIZED_TRIMMING);
		mapOabaIdEvent.put(EVT_DEDUP_BLOCKS, OabaEvent.DEDUP_BLOCKS);
		mapOabaIdEvent.put(EVT_DONE_DEDUP_BLOCKS, OabaEvent.DONE_DEDUP_BLOCKS);
		mapOabaIdEvent.put(EVT_DEDUP_OVERSIZED_EXACT,
				OabaEvent.DEDUP_OVERSIZED_EXACT);
		mapOabaIdEvent.put(EVT_DONE_DEDUP_OVERSIZED_EXACT,
				OabaEvent.DONE_DEDUP_OVERSIZED_EXACT);
		mapOabaIdEvent.put(EVT_DEDUP_OVERSIZED, OabaEvent.DEDUP_OVERSIZED);

		mapOabaIdEvent.put(EVT_DONE_DEDUP_OVERSIZED,
				OabaEvent.DONE_DEDUP_OVERSIZED);

		mapOabaIdEvent.put(EVT_DONE_REVERSE_TRANSLATE_BLOCK,
				OabaEvent.DONE_REVERSE_TRANSLATE_BLOCK);
		mapOabaIdEvent.put(EVT_DONE_REVERSE_TRANSLATE_OVERSIZED,
				OabaEvent.DONE_REVERSE_TRANSLATE_OVERSIZED);

		mapOabaIdEvent.put(EVT_CREATE_CHUNK_IDS, OabaEvent.CREATE_CHUNK_IDS);
		mapOabaIdEvent.put(EVT_CREATE_CHUNK_OVERSIZED_IDS,
				OabaEvent.CREATE_CHUNK_OVERSIZED_IDS);
		mapOabaIdEvent.put(EVT_DONE_CREATE_CHUNK_IDS,
				OabaEvent.DONE_CREATE_CHUNK_IDS);
		mapOabaIdEvent.put(EVT_DONE_CREATE_CHUNK_DATA,
				OabaEvent.DONE_CREATE_CHUNK_DATA);

		mapOabaIdEvent.put(EVT_ALLOCATE_CHUNKS, OabaEvent.ALLOCATE_CHUNKS);
		mapOabaIdEvent.put(EVT_DONE_ALLOCATE_CHUNKS,
				OabaEvent.DONE_ALLOCATE_CHUNKS);
		mapOabaIdEvent.put(EVT_MATCHING_DATA, OabaEvent.MATCHING_DATA);
		mapOabaIdEvent
				.put(EVT_DONE_MATCHING_DATA, OabaEvent.DONE_MATCHING_DATA);

		mapOabaIdEvent.put(EVT_OUTPUT_DEDUP_MATCHES,
				OabaEvent.OUTPUT_DEDUP_MATCHES);
		mapOabaIdEvent.put(EVT_MERGE_DEDUP_MATCHES,
				OabaEvent.MERGE_DEDUP_MATCHES);
		mapOabaIdEvent
				.put(EVT_DONE_DEDUP_MATCHES, OabaEvent.DONE_DEDUP_MATCHES);

		mapOabaIdEvent.put(EVT_DONE_OABA, OabaEvent.DONE_OABA);

		// Check that there were no duplicate event ids
		assert mapOabaIdEvent.keySet().size() == OabaEvent.values().length;
	}

	private static Map<Integer, TransEvent> mapTransIdEvent = new HashMap<>();

	static {
		mapTransIdEvent.put(EVT_INIT, TransEvent.INIT);
		mapTransIdEvent.put(EVT_CREATE_REC_VAL, TransEvent.CREATE_REC_VAL);
		mapTransIdEvent.put(EVT_DONE_REC_VAL, TransEvent.DONE_REC_VAL);
		mapTransIdEvent.put(EVT_BLOCK_BY_ONE_COLUMN,
				TransEvent.BLOCK_BY_ONE_COLUMN);
		mapTransIdEvent.put(EVT_DONE_BLOCK_BY_ONE_COLUMN,
				TransEvent.DONE_BLOCK_BY_ONE_COLUMN);
		mapTransIdEvent.put(EVT_OVERSIZED_TRIMMING,
				TransEvent.OVERSIZED_TRIMMING);
		mapTransIdEvent.put(EVT_DONE_OVERSIZED_TRIMMING,
				TransEvent.DONE_OVERSIZED_TRIMMING);
		mapTransIdEvent.put(EVT_DEDUP_BLOCKS, TransEvent.DEDUP_BLOCKS);
		mapTransIdEvent
				.put(EVT_DONE_DEDUP_BLOCKS, TransEvent.DONE_DEDUP_BLOCKS);
		mapTransIdEvent.put(EVT_DEDUP_OVERSIZED_EXACT,
				TransEvent.DEDUP_OVERSIZED_EXACT);
		mapTransIdEvent.put(EVT_DONE_DEDUP_OVERSIZED_EXACT,
				TransEvent.DONE_DEDUP_OVERSIZED_EXACT);
		mapTransIdEvent.put(EVT_DEDUP_OVERSIZED, TransEvent.DEDUP_OVERSIZED);
		mapTransIdEvent.put(EVT_DONE_TRANS_DEDUP_OVERSIZED,
				TransEvent.DONE_TRANS_DEDUP_OVERSIZED);
		mapTransIdEvent.put(EVT_DONE_REVERSE_TRANSLATE_BLOCK,
				TransEvent.DONE_REVERSE_TRANSLATE_BLOCK);
		mapTransIdEvent.put(EVT_DONE_REVERSE_TRANSLATE_OVERSIZED,
				TransEvent.DONE_REVERSE_TRANSLATE_OVERSIZED);

		mapTransIdEvent.put(EVT_CREATE_CHUNK_IDS, TransEvent.CREATE_CHUNK_IDS);
		mapTransIdEvent.put(EVT_CREATE_CHUNK_OVERSIZED_IDS,
				TransEvent.CREATE_CHUNK_OVERSIZED_IDS);
		mapTransIdEvent.put(EVT_DONE_CREATE_CHUNK_IDS,
				TransEvent.DONE_CREATE_CHUNK_IDS);
		mapTransIdEvent.put(EVT_DONE_CREATE_CHUNK_DATA,
				TransEvent.DONE_CREATE_CHUNK_DATA);

		mapTransIdEvent.put(EVT_ALLOCATE_CHUNKS, TransEvent.ALLOCATE_CHUNKS);
		mapTransIdEvent.put(EVT_DONE_ALLOCATE_CHUNKS,
				TransEvent.DONE_ALLOCATE_CHUNKS);
		mapTransIdEvent.put(EVT_MATCHING_DATA, TransEvent.MATCHING_DATA);
		mapTransIdEvent.put(EVT_DONE_MATCHING_DATA,
				TransEvent.DONE_MATCHING_DATA);
		mapTransIdEvent.put(EVT_OUTPUT_DEDUP_MATCHES,
				TransEvent.OUTPUT_DEDUP_MATCHES);
		mapTransIdEvent.put(EVT_MERGE_DEDUP_MATCHES,
				TransEvent.MERGE_DEDUP_MATCHES);
		mapTransIdEvent.put(EVT_DONE_DEDUP_MATCHES,
				TransEvent.DONE_DEDUP_MATCHES);
		mapTransIdEvent.put(EVT_DONE_TRANSANALYSIS,
				TransEvent.DONE_TRANSANALYSIS);

		// Check that there were no duplicate event ids
		assert mapTransIdEvent.keySet().size() == TransEvent.values().length;
	}

	public static TransEvent getTransEvent(int evtId) {
		return mapTransIdEvent.get(evtId);
	}

	public static OabaEvent getOabaEvent(int evtId) {
		return mapOabaIdEvent.get(evtId);
	}

	private OabaProcessingUtil() {
	}

}