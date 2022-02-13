/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.db.base;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.choicemaker.cm.core.Record;

/**
 * Base interface for all generated database readers, which
 * read data from result sets and translate them into object graphs
 * of holder class instances.
 *
 * @author    Martin Buechi
 */
public interface DbReaderSequential<T extends Comparable<T>> extends DbReader<T> {
	/**
	 * Opens a database reader in preparation for reading. It is automatically
	 * closed upon retrieval of the last entity.
	 *
	 * @param   rs  The result sets to be read from. <code>rs.length</code> must be
	 *            <code>getNoCursors()</code>.
	 * @throws  SQLException  if an exception occurs while reading from the result sets.
	 */
	void open(ResultSet rs, Statement stmt) throws java.sql.SQLException;

	/**
	 * Returns the next entity.
	 *
	 * @return  The next entity.
	 */
	@Override
	Record<T> getNext();

	String getMasterIdType();
}
