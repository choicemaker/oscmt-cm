/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.db.base;

import javax.sql.DataSource;

import com.choicemaker.cm.core.DatabaseException;

public interface DatabaseAbstractionManager {

	/**
	 * Looks up the appropriate implementation of the DatabaseAbstraction
	 * interface for the specified DataSource.
	 * 
	 * @param ds
	 *            a non-null data source
	 * @return an appropriate, non-null DatabaseAbstraction for the DataSource, if such
	 *         an abstraction has been registered.
	 * @throws DatabaseException if an appropriate abstraction can not be found.
	 */
	DatabaseAbstraction lookupDatabaseAbstraction(DataSource ds)
			throws DatabaseException;

}
