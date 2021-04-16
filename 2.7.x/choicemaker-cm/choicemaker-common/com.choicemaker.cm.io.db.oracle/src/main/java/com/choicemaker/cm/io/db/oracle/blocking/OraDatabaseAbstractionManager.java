/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.db.oracle.blocking;

import javax.sql.DataSource;

import com.choicemaker.cm.core.DatabaseException;
import com.choicemaker.cm.io.db.base.DatabaseAbstraction;
import com.choicemaker.cm.io.db.base.DatabaseAbstractionManager;

/**
 * A hard-coded implementation of DatabaseAbstractionManager that handles only
 * Oracle DataSources.
 * <p>
 * FIXME replace with a plugin-based manager
 * 
 * @author rphall
 * @deprecated
 */
@Deprecated
public class OraDatabaseAbstractionManager
		implements DatabaseAbstractionManager {

	@Override
	public DatabaseAbstraction lookupDatabaseAbstraction(DataSource ds)
			throws DatabaseException {

		// Precondition
		if (ds == null) {
			throw new IllegalArgumentException("null data source");
		}

		// Default value for unknown data sources
		DatabaseAbstraction retVal = null;

		// Oracle DataSource implementations
		try {
			if (ds.isWrapperFor(Class.forName(
					"oracle.jdbc.pool.OracleConnectionPoolDataSource"))) {
				retVal = new OraDatabaseAbstraction();

			} else if (ds.isWrapperFor(
					Class.forName("oracle.jdbc.pool.OracleDataSource"))) {
				retVal = new OraDatabaseAbstraction();

			} else if (ds.isWrapperFor(
					Class.forName("oracle.jdbc.xa.OracleXADataSource"))) {
				retVal = new OraDatabaseAbstraction();

			} else if (ds.isWrapperFor(Class
					.forName("oracle.jdbc.xa.client.OracleXADataSource"))) {
				retVal = new OraDatabaseAbstraction();

			} else if (ds.isWrapperFor(
					Class.forName("oracle.ucp.jdbc.PoolDataSourceImpl"))) {
				retVal = new OraDatabaseAbstraction();
			}

			// Error if unknown
			else {
				assert retVal == null;
				String msg = "Unknown DataSource implementation: '"
						+ ds.getClass().getName() + "'";
				throw new DatabaseException(msg);
			}

		} catch (Exception e) {
			String msg = "Unable to lookup database abstraction for '"
					+ ds.getClass().getName() + "': " + e.toString();
			;
			throw new DatabaseException(msg, e);
		}

		// Postcondition
		assert retVal != null;

		return retVal;
	}

}
