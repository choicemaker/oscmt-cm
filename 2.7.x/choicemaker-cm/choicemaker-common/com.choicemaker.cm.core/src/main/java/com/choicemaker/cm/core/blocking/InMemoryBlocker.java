/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.core.blocking;

import java.util.List;

import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.RecordSource;

/**
 * Description
 * 
 * @author  Martin Buechi
 */
public interface InMemoryBlocker {
	/**
	 * Method init.
	 * @param records
	 */
	void init(List records);
	
	/**
	 * The blocker should destroy memory structures used in 
	 * blocking, freeing up memory...
	 */
	void clear();
	
	/**
	 * Method block.
	 * @param q
	 * @return List
	 */
	RecordSource block(Record q);

	RecordSource block(Record q, int start);
}
