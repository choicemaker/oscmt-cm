/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;

/**
 * Sink of record pairs.
 *
 * @author    Martin Buechi
 */
public interface RecordPairSink extends Sink {
	/**
	 * Stores a record to the sink.
	 *
	 * @param   r The record to be stored.
	 * @throws  Exception  if there is a problem retrieving the data.
	 */
	void put(ImmutableRecordPair r) throws Exception;
}
