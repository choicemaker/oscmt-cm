/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;

import java.io.IOException;
import java.io.Serializable;

/**
 * Source of marked record pairs.
 *
 * @author Martin Buechi
 */
public interface MarkedRecordPairSource<T extends Comparable<T> & Serializable>
		extends RecordPairSource<T> {
	/**
	 * As <code>getNext</code>, but return type specialized to
	 * <code>MarkedRecordPair</code>.
	 *
	 * @return The next marked record pair.
	 * @throws IOException
	 *             if there is a problem retrieving the data.
	 */
	MutableMarkedRecordPair<T> getNextMarkedRecordPair() throws IOException;
}
