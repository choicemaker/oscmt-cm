/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.db.base;

import java.sql.SQLException;

import com.choicemaker.cm.core.Record;

/**
 * Base interface for all generated database readers, which
 * read data from result sets and translate them into object graphs
 * of holder class instances.
 *
 * @author    Martin Buechi
 */
public interface DbReader {
	/**
	 * Returns the next entity.
	 *
	 * @return  The next entity.
	 * @throws  SQLException  if an exception occurs while reading from the result sets.
	 */
	Record getNext() throws java.sql.SQLException;

	/**
	 * Answers whether there are more entities to be retrieved.
	 *
	 * @return  whether there are more entities to be retrieved.
	 */
	boolean hasNext();

	/**
	 * Returns the number of cursors to be passed.
	 *
	 * @return  the number of cursors to be passed.
	 */
	int getNoCursors();

	DbView[] getViews();

	String getMasterId();

	String getName();
}
