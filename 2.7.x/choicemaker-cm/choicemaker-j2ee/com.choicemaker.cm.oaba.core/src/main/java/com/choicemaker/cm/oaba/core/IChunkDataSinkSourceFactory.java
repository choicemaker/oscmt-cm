/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.core;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.RecordSink;
import com.choicemaker.cm.core.RecordSource;

/**
 * This Object handles creating RecordSink to put chunk data.
 * 
 * @author pcheung
 *
 */
public interface IChunkDataSinkSourceFactory {

	/** Gets the next record sink. */
	public RecordSink getNextSink() throws BlockingException;

	/** Gets the number of sequence sinks created. */
	public int getNumSink();

	/**
	 * Gets the next record source. This only returns a source from a previously
	 * created sink.
	 */
	public RecordSource getNextSource() throws BlockingException;

	/** Gets the number of sequence source created. */
	public int getNumSource();

	/** Removes the record sinks in memory. */
	public void removeAllSinks() throws BlockingException;

	/**
	 * This removes records sinks from 1 to numChunks
	 * 
	 * @param numChunks
	 * @throws BlockingException
	 */
	public void removeAllSinks(int numChunks) throws BlockingException;
}
