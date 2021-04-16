package com.choicemaker.cm.batch.api;

/**
 * Indicates whether batch results are persisted as files, as database table
 * entries, as both or as neither.
 */
public enum BATCH_RESULTS_PERSISTENCE_SCHEME {
	NONE, FILE, DATABASE, BOTH
}
