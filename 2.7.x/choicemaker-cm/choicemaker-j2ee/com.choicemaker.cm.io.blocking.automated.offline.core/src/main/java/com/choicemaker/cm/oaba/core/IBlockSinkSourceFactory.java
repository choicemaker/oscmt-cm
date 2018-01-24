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
 * This object handles creating IBlockSink and IBlockSource.
 * 
 * @author pcheung
 *
 */
public interface IBlockSinkSourceFactory {

	/** Gets the next IOverSizedSink in the sequence. */
	public IBlockSink getNextSink() throws BlockingException;

	/** Gets the next IOverSizedSource in the sequence. */
	public IBlockSource getNextSource() throws BlockingException;

	/** Gets the number of sequence sinks created. */
	public int getNumSink();

	/** Gets the number of sequence sources created. */
	public int getNumSource();

	/** Creates an IOverSizedSource for an existing IOversizedSink. */
	public IBlockSource getSource(IBlockSink sink) throws BlockingException;

	/** Creates an IOverSizedSource for an existing IOversizedSink. */
	public IBlockSink getSink(IBlockSource source) throws BlockingException;

	public void removeSink(IBlockSink sink) throws BlockingException;

	public void removeSource(IBlockSource source) throws BlockingException;

}
