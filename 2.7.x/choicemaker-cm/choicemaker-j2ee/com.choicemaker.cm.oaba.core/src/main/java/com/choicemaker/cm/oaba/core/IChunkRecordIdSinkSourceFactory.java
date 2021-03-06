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
 * This object handles creating IChunkRecordIdSink and IChunkRecordIdSource.
 * 
 * @author pcheung
 *
 */
public interface IChunkRecordIdSinkSourceFactory {

	/** Gets the next IChunkRecordIdSink in the sequence. */
	public IChunkRecordIdSink getNextSink() throws BlockingException;

	/** Gets the next IChunkRecordIdSource in the sequence. */
	public IChunkRecordIdSource getNextSource() throws BlockingException;

	/** Gets the number of sequence sinks created. */
	public int getNumSink();

	/** Gets the number of sequence sources created. */
	public int getNumSource();

	/** Creates an IChunkRecordIdSource for an existing IChunkRecordIdSink. */
	public IChunkRecordIdSource getSource(IChunkRecordIdSink sink)
			throws BlockingException;

	/** Creates an IChunkRecordIdSink for an existing IChunkRecordIdSource. */
	public IChunkRecordIdSink getSink(IChunkRecordIdSource source)
			throws BlockingException;

	/** Creates a set from a sink */
	IChunkRecordIndexSet getChunkRecordIndexSet(IChunkRecordIdSink sink)
			throws BlockingException;

	/** Creates a set from a source */
	IChunkRecordIndexSet getChunkRecordIndexSet(IChunkRecordIdSource source)
			throws BlockingException;

	/**
	 * Removes a sink.
	 * 
	 * @param sink
	 * @throws BlockingException
	 */
	public void removeSink(IChunkRecordIdSink sink) throws BlockingException;

	/**
	 * Removes a source.
	 * 
	 * @param source
	 * @throws BlockingException
	 */
	public void removeSource(IChunkRecordIdSource source)
			throws BlockingException;

}
