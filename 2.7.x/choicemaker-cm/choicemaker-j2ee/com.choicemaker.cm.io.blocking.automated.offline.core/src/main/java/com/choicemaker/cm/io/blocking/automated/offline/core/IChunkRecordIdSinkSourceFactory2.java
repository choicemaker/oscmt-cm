/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.offline.core;

import com.choicemaker.cm.core.BlockingException;

/**
 * An extension of IChunkRecordIdSinkSourceFactory that adds
 * methods for creating IChunkRecordIndexSet instances.
 * @author rphall
 * @version $Revision$ $Date$
 */
public interface IChunkRecordIdSinkSourceFactory2
	extends IChunkRecordIdSinkSourceFactory {

	/** Creates a set from a sink with diagnostics optionally enabled */
	IChunkRecordIndexSet getChunkRecordIndexSet(
		IChunkRecordIdSink sink,
		boolean isDebugEnabled)
		throws BlockingException;

	/** Creates a set from a source with diagnostics optionally enabled */
	IChunkRecordIndexSet getChunkRecordIndexSet(
		IChunkRecordIdSource source,
		boolean isDebugEnabled)
		throws BlockingException;

}
