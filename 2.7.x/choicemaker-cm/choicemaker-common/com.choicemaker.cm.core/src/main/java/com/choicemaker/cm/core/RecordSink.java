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
 * Sink of records.
 *
 * @author    Martin Buechi
 */

public interface RecordSink extends Sink {
    /**
     * Stores a record to the sink.
     *
     * @param   r The record to be stored.
     * @throws  Exception  if there is a problem retrieving the data.
     */
    void put(Record r) throws Exception;
}
