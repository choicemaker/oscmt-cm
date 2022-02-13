/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.cm.args;

public interface PersistableSqlRecordSource extends PersistableRecordSource {

	String TYPE = "SQL";

	/**
	 * Returns the fully qualified class name of an implementation of DbReader
	 *
	 * @return the FQCN of an implementation of
	 *         com.choicemaker.cm.io.db.base.DbReader,
	 *         com.choicemaker.cm.io.db.base.DbReaderSequential, or
	 *         com.choicemaker.cm.io.db.base.DbReaderParallel
	 */
	String getDatabaseReader();

	/**
	 * Returns the JNDI name of a data source
	 *
	 * @return a non-null, valid JDNI reference
	 */
	String getDataSource();

	/**
	 * Returns a SQL Select statement used to pull records from the data source.
	 * Must be valid SQL that defines an <code>ID</code> output column.
	 *
	 * <pre>
	 * SELECT &lt;some column&gt; AS ID from &lt;some table&gt;
	 *  &lt;optional WHERE clause&gt;
	 * </pre>
	 *
	 * @return a non-null, valid statement
	 */
	String getSqlSelectStatement();

	/**
	 * Returns the name of a ChoiceMaker model.
	 *
	 * @return a non-null, valid name
	 */
	String getModelId();

	/**
	 * Returns the name of a database configuration defined by the model
	 *
	 * @return non-null, valid name of some configuration defined by the model
	 */
	String getDatabaseConfiguration();

	/**
	 * Returns the plugin identifier for an implementation of the
	 * DatabaseAccessor interface
	 *
	 * @return may be null
	 */
	String getDatabaseAccessor();

}
