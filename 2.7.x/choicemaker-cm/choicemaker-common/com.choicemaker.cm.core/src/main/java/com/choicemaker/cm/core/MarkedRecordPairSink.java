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
 * Sink of marked record pairs.
 *
 * @author    Martin Buechi
 */
public interface MarkedRecordPairSink extends RecordPairSink {
	/**
	 * As <code>put</code>, but parameter type specialized to
	 * <code>MarkedRecordPair</code>.
	 *
	 * @param   r The marked record pair to be stored.
	 * @throws  Exception  if there is a problem retrieving the data.
	 */
	void putMarkedRecordPair(ImmutableMarkedRecordPair r) throws Exception;
}
