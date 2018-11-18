package com.choicemaker.cms.api;

/**
 * Indicates whether batch results are persisted as files, as database table
 * entries, both or neither.
 */
public enum PERSISTENCE_SCHEME {
	NONE, FILE, DATABASE, BOTH
}
