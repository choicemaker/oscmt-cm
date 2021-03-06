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
 * @author pcheung
 *
 */
public interface IPairIDSinkSourceFactory {

	/** Gets the next IPairIDSink in the sequence. */
	public IPairIDSink getNextSink() throws BlockingException;

	/** Gets the next IPairIDSource in the sequence. */
	public IPairIDSource getNextSource() throws BlockingException;

	/** Gets the number of sequence sinks created. */
	public int getNumSink();

	/** Gets the number of sequence sources created. */
	public int getNumSource();

	/** Creates an IPairIDSource for an existing IPairIDSink. */
	public IPairIDSource getSource(IPairIDSink sink) throws BlockingException;

	/** Creates an IPairIDSink for an existing IPairIDSource. */
	public IPairIDSink getSink(IPairIDSource source) throws BlockingException;

	/**
	 * Removes this sink.
	 * 
	 * @param sink
	 * @throws BlockingException
	 */
	public void removeSink(IPairIDSink sink) throws BlockingException;

	/**
	 * Removes this source.
	 * 
	 * @param source
	 * @throws BlockingException
	 */
	public void removeSource(IPairIDSource source) throws BlockingException;

	public void move(IPairIDSink sink1, IPairIDSink sink2)
			throws BlockingException;

}
