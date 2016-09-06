/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated;

import com.choicemaker.cm.core.Record;

public interface IBlockingConfiguration {

	/**
	 * A unique identifier for a a blocking configuration. When ABA statistics
	 * are stored in a database, they are stored per blocking configuration,
	 * where blocking configurations are identified by their ids. Currently,
	 * the id of a blocking configuration is generated automatically from the
	 * schema of a model and consists of four parts, separated by colons:
	 * <ol>
	 * <li>the name of the model schema</li>
	 * <li>the literal character <code>b</coded> (for <em>blocking</em>)</li>
	 * <li>the name of a blocking configuration defined by the model schema</li>
	 * <li>the name of a database configuration defined by the model schema</li>
	 * </ol>
	 * As an example
	 *
	 * <pre>
	 * MciRecords:b:batch:patient
	 * </pre>
	 *
	 * In principle, completely unrelated models could use the same names for
	 * model schema, blocking configuration and database configuration, so there
	 * could be id collisions among ABA statistics persisted to a database. In
	 * practice, this has yet to happen.
	 *
	 * @return a &quot;unique&quot; id that unambiguously identifies a
	 *         blocking configuration.
	 */
	String getBlockingConfiguationId();

	IDbTable[] getDbTables();

	IDbField[] getDbFields();

	IBlockingField[] getBlockingFields();

	IBlockingValue[] createBlockingValues(Record q);

	/**
	 * If two implementations return the same id, they should be evaluated as
	 * equal.
	 * <p>
	 * Equality should not be based in part or in whole on the implementing
	 * class, since the id alone is used for identifying distinct ABA
	 * statistics in a database.
	 * <p>
	 * Equality should not be based on the values returned by the other
	 * accessors, particularly <code>getBlockingFields()</code>. Because of the
	 * way that BlockingConfiguration code is currently generated, the objects
	 * returned by a particular type of accessor, say
	 * <code>getBlockingFields()</code>, will be the identical for all instances
	 * of a class.
	 *
	 * @param ibc
	 *            possibly null
	 * @return false if the argument <code>ibc</code> is null, otherwise true if
	 *         the id of the specified configuration matches that of the
	 *         evaluating configuration.
	 */
	boolean equals(IBlockingConfiguration ibc);

	/**
	 * Implementations must override <code>equals(Object)()</code> to provide an
	 * method consistent with <code>equals(IBlockingConfiguration)</code> and
	 * <code>hashCode()</code>
	 *
	 * @param o
	 *            possibly null
	 * @return false if the argument <code>o</code> is null or not an
	 *         implementation of <code>IBlockingConfiguration</code>, otherwise
	 *         return the value computed by
	 *         <code>equals(IBlockingConfiguration)</code>.
	 */
	boolean equals(Object o);

	/**
	 * Implementations must override <code>hashCode()</code> to provide an
	 * method consistent with <code>equals(Object)</code> and
	 * <code>equals(IBlockingConfiguration)</code>
	 */
	int hashCode();

}
