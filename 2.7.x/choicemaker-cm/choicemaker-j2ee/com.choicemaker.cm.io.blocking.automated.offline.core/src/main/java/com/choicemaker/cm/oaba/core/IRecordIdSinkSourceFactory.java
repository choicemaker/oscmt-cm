/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.core;

import com.choicemaker.cm.core.BlockingException;

/**
 * This object handles creating IRecordIdSink and IRecordIdSource.
 * 
 * @author pcheung
 *
 */
@SuppressWarnings({ "rawtypes" })
public interface IRecordIdSinkSourceFactory {

	/** Gets the next IRecordIdSink in the sequence. */
	public IRecordIdSink getNextSink() throws BlockingException;

	/** Gets the next IRecordIdSource in the sequence. */
	public IRecordIdSource getNextSource() throws BlockingException;

	/** Gets the number of sequence sinks created. */
	public int getNumSink();

	/** Gets the number of sequence sources created. */
	public int getNumSource();

	/** Creates an IRecordIdSource for an existing IRecordIdSink. */
	public IRecordIdSource getSource(IRecordIdSink sink)
			throws BlockingException;

	/** Creates an IRecordIdSink for an existing IRecordIdSource. */
	public IRecordIdSink getSink(IRecordIdSource source)
			throws BlockingException;

	/**
	 * Removes this sink.
	 * 
	 * @param sink
	 * @throws BlockingException
	 */
	public void removeSink(IRecordIdSink sink) throws BlockingException;

	/**
	 * Removes this source.
	 * 
	 * @param source
	 * @throws BlockingException
	 */
	public void removeSource(IRecordIdSource source) throws BlockingException;

}
