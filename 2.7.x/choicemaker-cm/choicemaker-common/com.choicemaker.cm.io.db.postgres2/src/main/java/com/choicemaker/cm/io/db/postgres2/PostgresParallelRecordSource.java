/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Feb 5, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.choicemaker.cm.io.db.postgres2;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.choicemaker.cm.core.Accessor;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.Sink;
import com.choicemaker.cm.io.db.base.DataSources;
import com.choicemaker.cm.io.db.base.DbAccessor;
import com.choicemaker.cm.io.db.base.DbReaderParallel;
import com.choicemaker.cm.io.db.base.DbView;
import com.choicemaker.util.Precondition;

/**
 * @author pcheung
 */
public class PostgresParallelRecordSource implements RecordSource {

	private static Logger logger =
		Logger.getLogger(PostgresParallelRecordSource.class.getName());

	private static final String SOURCE = PostgresParallelRecordSource.class.getSimpleName();

	public static String createDataViewName() {
		String s = "CMT_DATAVIEW_" + UUID.randomUUID().toString();
		String retVal = s.replace('-', '_');
		return retVal;
	}

	private final String fileName;
	private final String dbConfiguration;
	private ImmutableProbabilityModel model;
	private final String idsQuery;

	private String dsName;
	private DataSource ds;
	private Connection connection;

	private DbReaderParallel dbr;
	private Statement[] selects;
	private ResultSet[] results;

	private final String dataViewName;

	public PostgresParallelRecordSource(String fileName,
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
	}

	@Override
	public void open() throws IOException {

		DbAccessor accessor = (DbAccessor) getModel().getAccessor();
		dbr = accessor.getDbReaderParallel(getDbConfiguration());

		try {
			if (getConnection() == null) {
				connection = getDataSource().getConnection();
				if (connection == null) {
					throw new IOException("null connection");
				}
			}

			// 1. Create view
			createView(getConnection());

			// 2. Get the ResultSets
			getResultSets();

			// 3. Open parallel reader
			logger.fine("before dbr.open");
			getDatabaseReader().open(results);
		} catch (SQLException ex) {
			logger.severe(ex.toString());

			throw new IOException(ex.toString(), ex);
		}
	}

	private void createView(Connection conn) throws SQLException {
		Statement view = conn.createStatement();
		String s = "DROP VIEW IF EXISTS " + getDataView();
		logger.fine(s);
		view.execute(s);

		s = "create view " + getDataView() + " as " + getIdsQuery();
		logger.fine(s);
		view.execute(s);
		view.close();
	}

	private void dropView(Connection conn) throws SQLException {
		Statement view = conn.createStatement();
		String s = "DROP VIEW IF EXISTS " + getDataView();
		logger.fine(s);
		view.execute(s);
		view.close();
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
	static boolean isValidQuery(String s) {
		if (s == null || s.toUpperCase().indexOf(" AS ID ") == -1)
			return false;
		else
			return true;
	}

	private void getResultSets() throws SQLException {
		Accessor accessor = getModel().getAccessor();
		String viewBase0 = "vw_cmt_%s_r_%s";
		String viewBase = String.format(viewBase0, accessor.getSchemaName(), getDbConfiguration());
		DbView[] views = getDatabaseReader().getViews();
		String masterId = getDatabaseReader().getMasterId();

		int numViews = views.length;
		selects = new Statement[numViews];
		results = new ResultSet[numViews];

		for (int i = 0; i < numViews; ++i) {
			String viewName = viewBase + i;
			logger.finest("view: " + viewName);
			DbView v = views[i];

			StringBuffer sb = new StringBuffer("select * from ");
			sb.append(viewBase);
			sb.append(i);
			sb.append(" where ");
			sb.append(masterId);
			sb.append(" in (select id from ");
			sb.append(getDataView());
			sb.append(")");

			if (v.orderBy.length > 0) {
				sb.append(" ORDER BY ");
				sb.append(getOrderBy(v));
			}

			String queryString = sb.toString();

			logger.fine("Query: " + queryString);

			selects[i] = getConnection().prepareStatement(queryString);
			logger.fine("Prepared statement");
			selects[i].setFetchSize(100);
			logger.fine("Changed Fetch Size to 100");
			results[i] = ((PreparedStatement) selects[i]).executeQuery();
			logger.fine("Executed query " + i);
		}
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

	@Override
	public boolean hasNext() throws IOException {
		return getDatabaseReader().hasNext();
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

	@Override
	public void close() /* throws IOException */ {

		List<String> exceptionMessages = new ArrayList<>();
		if (selects != null) {
			int s = selects.length;
			for (int i = 0; i < s; i++) {
				if (selects[i] != null) {
					try {
						selects[i].close();
					} catch (SQLException e) {
						String msg0 = "Problem closing statement [%d]: %s";
						String msg = String.format(msg0, i, e.toString());
						exceptionMessages.add(msg);
					}
				}
				selects[i] = null;
			}
			selects = null;
		}

		if (results != null) {
			int r = results.length;
			for (int i = 0; i < r; i++) {
				if (results[i] != null) {
					try {
						results[i].close();
					} catch (SQLException e) {
						String msg0 = "Problem closing result set [%d]: %s";
						String msg = String.format(msg0, i, e.toString());
						exceptionMessages.add(msg);
					}
				}
				results[i] = null;
			}
			results = null;
		}

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

		// Log any exception messages as warnings
		if (!exceptionMessages.isEmpty()) {
			final int count = exceptionMessages.size();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			String msg0 = "Problem(s) closing %s: %d";
			String msg = String.format(msg0, getSource(), count);
			pw.println(msg);
			for (int i = 0; i < count; i++) {
				msg = exceptionMessages.get(i);
				pw.println(msg);
			}
			msg = sw.toString();
			logger.warning(msg);
		}

		// Postconditions
		assert selects == null;
		assert results == null;
		assert getConnection() == null;
	}

	@Override
	protected void finalize() {
		close();
	}

	@Override
	public ImmutableProbabilityModel getModel() {
		if (model == null) {
			throw new IllegalStateException("null model");
		}
		return model;
	}

	@Override
	public void setModel(ImmutableProbabilityModel m) {
		if (m == null) {
			throw new IllegalArgumentException("null model");
		}
		this.model = m;
	}

	public String getDataSourceName() {
		if (dsName == null) {
			throw new IllegalStateException("null dsName");
		}
		return dsName;
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

	void setDataSource(String name, DataSource ds) {
		if (name == null) {
			throw new IllegalArgumentException("null dsName");
		}
		if (ds == null) {
			throw new IllegalArgumentException("null data source");
		}
		this.dsName = name;
		this.ds = ds;
	}

	public String getDataView() {
		return dataViewName;
	}

	protected String getSource() {
		return SOURCE;
	}

	private Connection getConnection() {
		return connection;
	}

	private DbReaderParallel getDatabaseReader() {
		return dbr;
	}

	public String getDbConfiguration() {
		assert dbConfiguration != null;
		return dbConfiguration;
	}

	public String getIdsQuery() {
		assert isValidQuery(this.idsQuery);
		return idsQuery;
	}

	@Override
	public String getName() {
		return "SQL Server Parallel Record Source";
	}

	@Override
	public void setName(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasSink() {
		return false;
	}

	@Override
	public Sink getSink() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	@Override
	public String toString() {
		return "PostgresParallelRecordSource [fileName=" + getFileName()
				+ ", model=" + getModel() + ", dbConfiguration="
				+ getDbConfiguration() + ", idsQuery=" + getIdsQuery()
				+ ", dsName=" + getDataSourceName()
				+ ", dataView=" + getDataView() + "]";
	}

}
