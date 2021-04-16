/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;
import java.io.IOException;

/**
 * Base interface of record-related sources.
 *
 * @author    Martin Buechi
 */

public interface Sink extends AutoCloseable {
	/**
	 * Opens the sink for writing data.
	 *
	 * A sink must be closed before it can be re-opened.
	 * Reopening the sink will re-start the retrieval from the beginning.
	 * There is no guarantee that the same data will be returned or that
	 * the order is the same, but in most cases these two properties hold.
	 *
	 * @throws  IOException  if there is a problem opening the sink.
	 */
	void open() throws IOException;

	/**
	 * Closes the data sink.
	 * Every data sinks that is opened must eventually be closed again explicitly.
	 *
	 * @throws  Exception  if there is a problem closing the sink.
	 */
	@Override
	void close() throws Exception;

	/**
	 * Flushes the data sink.
	 * If the sink is backed by persistent storage, this method forces the sink
	 * to write any cached data to the storage. Otherwise this method does nothing.
	 *
	 * @throws  Exception  if there is a problem writing the data.
	 */
	void flush() throws Exception;

	String getName();

	void setName(String name);

	ImmutableProbabilityModel getModel();

	void setModel(ImmutableProbabilityModel m);
}
