/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.core;

import com.choicemaker.cm.core.BlockingException;

/**
 * A set of internal indices for records.  Encapsulates the most
 * frequent use of IChunkRecordIdSource, which is to check if
 * a record index from some external stream (e.g. a database)
 * is contained within a set of target indices (e.g. a chunk data set).
 * 
 * @author rphall
 */
public interface IChunkRecordIndexSet {
	
	/**
	 * Checks if a record index is in this set.  A set must be opened
	 * before this operation is used.
	 * @throws BlockingException if this set has not been opened.
	 * @see #open()
	 */
	boolean containsRecordIndex(long recordIndex) throws BlockingException;
	
	/** Returns the IChunkRecordIdSource that backs this set */
	IChunkRecordIdSource getSource();

	/** True if the source backing this set exists */
	boolean exists ();
	
	/** Opens and initializes the source backing this set for reading. */
	void open () throws BlockingException;

	/** Closes the source file backing this set */
	void close () throws BlockingException;

	/** Gets the file name or other pertinent information of the source backing this set */
	String getInfo ();

	/** This method cleans up resources and removes the source backing this set */
	void remove () throws BlockingException;
	
	/** A flag indicating whether diagnostics are available */
	boolean isDebugEnabled();
	
	/**
	 * If diagnostics are enabled, returns a source representing all the indices
	 * that have been <strong><em>not</em></strong> checked via
	 * {@link #containsRecordIndex(long) containsRecordIndex}
	 * for containment in this set. Otherwise returns a depleted source.
	 */
	IChunkRecordIdSource getUncheckedIndices();

}

