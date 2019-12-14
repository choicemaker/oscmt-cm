package com.choicemaker.cm.oaba.core;

/**
 * Specifies how a group of query records are to be matched.
 * <ul>
 * <li>Single record matching (SRM): each record from the group is matched one
 * at a time against reference records.</li>
 * <li>Batch record matching (BRM): the entire group is matched collectively
 * against reference records.</li>
 * </ul>
 * Both modes of operation should yield the same results. For small groups of
 * records, typically less than a few thousand, single record matching is more
 * efficient than batch record matching.
 * 
 * @author rphall
 *
 */
public enum RecordMatchingMode {
	/** Single-record matching mode */
	SRM,
	/** Batch-record matching mode */
	BRM
}
