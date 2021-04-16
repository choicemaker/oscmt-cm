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
 * Base interface for all generated database readers, which read data from
 * result sets and translate them into object graphs of holder class instances.
 *
 * @author Martin Buechi
 */
public interface DbReader<T extends Comparable<T>> {
	/**
	 * Returns the next entity.
	 *
	 * @return The next entity.
	 * @throws SQLException
	 *             if an exception occurs while reading from the result sets.
	 */
	Record<T> getNext() throws java.sql.SQLException;

	/**
	 * Answers whether there are more entities to be retrieved.
	 *
	 * @return whether there are more entities to be retrieved.
	 */
	boolean hasNext();

	/**
	 * Returns the number of cursors to be passed.
	 *
	 * @return the number of cursors to be passed.
	 */
	int getNoCursors();

	/**
	 * Returns an (ordered) array of DbView instances describing the database
	 * views used to pull data from the hierarchy of tables used to store a
	 * ChoiceMaker record.
	 * 
	 * @return a non-null, non-empty array of non-null DbView instances.
	 */
	DbView[] getViews();

	/**
	 * Returns the name of the database column that contains the primary key for
	 * top-level table in the hierarchy of tables used to store a ChoiceMaker
	 * record.
	 * 
	 * @return a non-null, non-empty String that represents a valid column name
	 */
	String getMasterId();

	/**
	 * Returns the semantic name of a reader, which is structured in three
	 * parts:
	 * <ol>
	 * <li>Simple ChoiceMaker schema name</li>
	 * <li>A flag (r) indicating type of objects retrieved (records)</li>
	 * <li>An name identifying the DBMS type (oracle, sqlserver or postgres)
	 * </li>
	 * </ol>
	 * 
	 * Example: <code>mcirecords:r:oracle</code>
	 * 
	 * @return a non-null, non-empty, structured String value
	 */
	String getName();
}
