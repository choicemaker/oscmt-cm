/*******************************************************************************
 * Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
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
package com.choicemaker.cm.io.db.base;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.Sink;
import com.choicemaker.util.Precondition;

public abstract class AbstractParallelRecordSource implements RecordSource {

	private static Logger logger =
			Logger.getLogger(AbstractParallelRecordSource.class.getName());

	public static String createDataViewName() {
		String s = "CMT_DATAVIEW_" + UUID.randomUUID().toString();
		String retVal = s.replace('-', '_');
		return retVal;
	}

	public static String getOrderBy(DbView v) {
		Precondition.assertNonNullArgument(v);
		StringBuffer ob = new StringBuffer();
		for (int j = 0; j < v.orderBy.length; ++j) {
			if (j != 0)
				ob.append(",");
			ob.append(v.orderBy[j].name);
		}
		return ob.toString();
	}

	/**
	 * This method checks to make sure the idsQuery is valid. It must contain
	 * "as id". For example,
	 * 
	 * <pre>
	 * select distinct TAP_CORPORATE_ID as ID
	 *   from CORPORATE where primary_name like 'A%'
	 * </pre>
	 */
	protected static boolean isValidQuery(String s) {
		if (s == null || s.toUpperCase().indexOf(" AS ID ") == -1)
			return false;
		else
			return true;
	}

	private final String dbConfiguration;
	private final DbReaderParallel dbr;
	private final String fileName;
	private final String idsQuery;

	// Required to be mutable by RecordSource setXxx methods
	private Connection connection;
	private final String dataViewName;
	private DataSource ds;
	private String dsName;
	private ImmutableProbabilityModel model;

	// Required to be mutable by open/close methods
	private ResultSet[] results;

	public AbstractParallelRecordSource(String fileName,
			ImmutableProbabilityModel model, String dsName,
			String dbConfiguration, String idsQuery) {

		logger.fine("Constructor: " + fileName + " " + model + " " + dsName
				+ " " + dbConfiguration + " " + idsQuery);

		if (fileName == null) {
			logger.fine("Null file name for " + getSource());
		}
		if (dbConfiguration == null || dbConfiguration.trim().isEmpty()) {
			throw new IllegalArgumentException(
					"null or blank database configuration name");
		}
		if (!isValidQuery(idsQuery)) {
			throw new IllegalArgumentException(
					"idsQuery must contain ' AS ID '.");
		}

		// Don't use public modifiers here -- preconditions may not apply
		this.fileName = fileName;
		this.model = model;
		this.dsName = dsName;
		this.dbConfiguration = dbConfiguration;
		this.idsQuery = idsQuery;
		this.dataViewName = createDataViewName();
		
		DbAccessor accessor = (DbAccessor) model.getAccessor();
		this.dbr = accessor.getDbReaderParallel(getDbConfiguration());
	}

	@Override
	public void close() /* throws IOException */ {

		List<String> exceptionMessages = new ArrayList<>();
		closeOwnedResources(exceptionMessages);
		closeResultSets(exceptionMessages);
		closeConnection(exceptionMessages);

		// Log any exception messages as warnings
		if (!exceptionMessages.isEmpty()) {
			final int count = exceptionMessages.size();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			String msg0 = "Problem(s) closing %s: %d" + count;
			String msg = String.format(msg0, getSource(), count);
			pw.println(msg);
			for (int i = 0; i < count; i++) {
				msg = exceptionMessages.get(i);
				pw.println(msg);
			}
			msg = sw.toString();
			logger.warning(msg);
		}

	}

	protected void closeConnection(List<String> exceptionMessages) {
		
		Precondition.assertNonNullArgument(
				"Exception message list must be non-null", exceptionMessages);

		if (getConnection() != null) {
			try {
				dropView(getConnection());
			} catch (SQLException e) {
				String msg = "Problem dropping view:" + e.toString();
				exceptionMessages.add(msg);
			}

			try {
				getConnection().close();
			} catch (SQLException e) {
				String msg = "Problem dropping connection:" + e.toString();
				exceptionMessages.add(msg);
			}
			connection = null;
		}

		// Postcondition
		assert getConnection() == null;
	}

	protected abstract void closeOwnedResources(List<String> exceptionMessages);

	protected void closeResultSets(List<String> exceptionMessages) {

		if (getResultSets() != null) {
			int r = getResultSets().length;
			for (int i = 0; i < r; i++) {
				if (getResultSets()[i] != null) {
					try {
						getResultSets()[i].close();
					} catch (SQLException e) {
						String msg0 = "Problem closing result set [%d]: %s";
						String msg = String.format(msg0, i, e.toString());
						exceptionMessages.add(msg);
					}
				}
				getResultSets()[i] = null;
			}
			setResultSets(null);
		}

		// Postcondition
		assert getResultSets() == null;
	}

	protected abstract void createView(Connection conn) throws SQLException;

	protected abstract void dropView(Connection conn) throws SQLException;

	@Override
	protected void finalize() {
		close();
	}

	protected Connection getConnection() {
		return connection;
	}

	protected DbReaderParallel getDatabaseReader() {
		return dbr;
	}

	public DataSource getDataSource() {
		if (ds == null) {
			final String name = getDataSourceName();
			String msg = String.format("Looking up JDBC datasource '%s'", name);
			logger.fine(msg);
			ds = DataSources.getDataSource(name);
			if (ds == null) {
				msg = String.format("No datasource registered for name '%s'", name);
				logger.warning(msg);
			}
		}
		if (ds == null) {
			throw new IllegalStateException("null data source");
		}
		return ds;
	}

	public String getDataSourceName() {
		if (dsName == null) {
			throw new IllegalStateException("null dsName");
		}
		return dsName;
	}

	public String getDataView() {
		return dataViewName;
	}

	public String getDbConfiguration() {
		assert dbConfiguration != null;
		return dbConfiguration;
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	public String getIdsQuery() {
		assert isValidQuery(this.idsQuery);
		return idsQuery;
	}

	@Override
	public ImmutableProbabilityModel getModel() {
		if (model == null) {
			throw new IllegalStateException("null model");
		}
		return model;
	}

	@Override
	public String getName() {
		return "SQL Server Parallel Record Source";
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Record getNext() throws IOException {
		Record r = null;
		try {
			r = getDatabaseReader().getNext();
		} catch (SQLException e) {
			throw new IOException(e.toString());
		}
		return r;
	}

	private ResultSet[] getResultSets() {
		return results;
	}

	@Override
	public Sink getSink() {
		throw new UnsupportedOperationException();
	}

	protected abstract String getSource();

	@Override
	public boolean hasNext() throws IOException {
		return getDatabaseReader().hasNext();
	}

	@Override
	public boolean hasSink() {
		return false;
	}

	@Override
	public void open() throws IOException {
	
		try {
			if (getConnection() == null) {
				connection = getDataSource().getConnection();
			}
	
			// 1. Create view
			createView(getConnection());
	
			// 2. Get the ResultSets
			this.setResultSets(retrieveResultSets());

			// 3. Open parallel reader
			logger.fine("before dbr.open");
			getDatabaseReader().open(getResultSets());
		} catch (SQLException ex) {
			logger.severe(ex.toString());
	
			throw new IOException(ex.toString(), ex);
		}
	}

	protected abstract ResultSet[] retrieveResultSets() throws SQLException;

	public void setDataSource(String name, DataSource ds) {
		if (name == null) {
			throw new IllegalArgumentException("null dsName");
		}
		if (ds == null) {
			throw new IllegalArgumentException("null data source");
		}
		this.dsName = name;
		this.ds = ds;
	}

	@Override
	public void setModel(ImmutableProbabilityModel m) {
		if (m == null) {
			throw new IllegalArgumentException("null model");
		}
		this.model = m;
	}

	@Override
	public void setName(String name) {
		throw new UnsupportedOperationException();
	}

	private void setResultSets(ResultSet[] results) {
		this.results = results;
	}

	@Override
	public String toString() {
		return getSource() + " [fileName=" + getFileName() + ", model=" + getModel() + ", dbConfiguration="
				+ getDbConfiguration() + ", idsQuery=" + getIdsQuery() + ", dsName=" + getDataSourceName()
				+ ", dataView=" + getDataView() + "]";
	}

}
