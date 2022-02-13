/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;

import java.io.IOException;
import java.io.Serializable;

/**
 * Source of records.
 *
 * @author    Martin Buechi
 */

public interface RecordSource<T extends Comparable<T> & Serializable> extends Source {
	/**
	 * Returns the next record.
	 *
	 * @return  The next record.
	 * @throws  IOException  if there is a problem retrieving the data.
	 */
	Record<T> getNext() throws IOException;
}
