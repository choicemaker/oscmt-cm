/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated;

import java.util.Map;

/**
 * Represents counts of values for a given column with a table. For example,
 * suppose a table T consists of two columns K and C, where numeric column K is
 * the primary key for the table and column C contains varchar values. If the
 * table contains three rows with the values {1, "A"}, {2, "A"}, {3, "B"}, and
 * {4, "D"} then a IFieldValueCounts instance {@code fvc} for column C would
 * return the following results.
 * 
 * <pre>
 * fvc.getColumn() ==> "C"
 * fvc.getTableSize() ==> 3
 * fvc.getView() ==> "T"
 * fvc.getUniqueId() ==> "K"
 * fvc.getValueCountSize() ==> 2
 * fvc.getCountForValue("A") ==> 2
 * fvc.getCountForValue("B") ==> 1
 * fvc.getCountForValue("D") ==> 1
 * </pre>
 * 
 * The {@link #getDefaultCount() default count} is the count returned for values
 * for which an explicit count is not stored. This is an optimization that makes
 * it unnecessary to store counts for values that occur infrequently. In the toy
 * example above, the default count could be set to 1, so that counts for the
 * values B and C would not have to be stored explicitly.
 * 
 */
public interface IFieldValueCounts {

	/** Returns the name of the column for which this statistic applies */
	String getColumn();

	/** Returns the number of times a value appears within a column */
	Integer getCountForValue(String value);

	/**
	 * Returns a default count of the number of times a value is presumed to
	 * occur within a column for values that occur infrequently.
	 */
	int getDefaultCount();

	/**
	 * Returns the number of rows in the table that contains the column to which
	 * this statistic applies
	 */
	int getTableSize();

	/**
	 * Returns the name of the primary key column for the table that contains
	 * the column to which this statistic applies.
	 */
	String getUniqueId();

	/** Returns the number of values for which counts are stored explicity */
	int getValueCountSize();

	/**
	 * Returns the name of the table that contains the column to which this
	 * statistic applies
	 */
	String getView();

	/**
	 * Returns a(n unmodifiable) map of values that have been explicitly mapped
	 * to counts. The map excludes any values that are only implicitly mapped to
	 * the default count.
	 */
	Map<String, Integer> getValueCountMap();

	void putAll(Map<String, Integer> m);

	void putValueCount(String value, Integer count);

}
