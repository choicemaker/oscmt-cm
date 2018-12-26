/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
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
package com.choicemaker.cm.io.db.sqlserver;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
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
import com.choicemaker.util.StringUtils;

/**
 * @author pcheung
 */
public class SqlServerFetchOffsetRecordSource
		implements RecordSource, AutoCloseable {

	private static final int CLOSED_OFFSET = -1;

	public static final int DEFAULT_FETCH = 1000;

	private static final int INITIAL_OFFSET = 0;

	private static Logger logger =
		Logger.getLogger(SqlServerFetchOffsetRecordSource.class.getName());

	private static final String SOURCE =
		SqlServerFetchOffsetRecordSource.class.getSimpleName();

	public static String createDataViewName() {
		return SqlServerParallelRecordSource.createDataViewName();
	}

	private static void createView(Connection conn, String viewName,
			String idsQuery) throws SQLException {
		assert conn != null;
		assert StringUtils.nonEmptyString(viewName);
		assert isValidQuery(viewName);

		try (Statement stmt = conn.createStatement()) {
			String s1_0 =
				"IF EXISTS (SELECT TABLE_NAME FROM INFORMATION_SCHEMA.VIEWS "
						+ "WHERE TABLE_NAME = '%s') DROP VIEW [%s]";
			final String s1 = String.format(s1_0, viewName, viewName);
			logger.fine(s1);
			stmt.execute(s1);

			final String s2_0 = "CREATE VIEW [%s] AS %s";
			final String s2 = String.format(s2_0, viewName, idsQuery);
			logger.fine(s2);
			stmt.execute(s2);
		}
	}

	private static void dropView(Connection conn, String viewName)
			throws SQLException {
		try (Statement view = conn.createStatement()) {
			String s0 = "IF EXISTS (SELECT TABLE_NAME FROM "
					+ "INFORMATION_SCHEMA.VIEWS WHERE TABLE_NAME = '%s') "
					+ "DROP VIEW [%s]";
			String s = String.format(s0, viewName, viewName);
			logger.fine(s);
			view.execute(s);
			view.close();

		}
	}

	public static String getOrderBy(DbView v) {
		return SqlServerParallelRecordSource.getOrderBy(v);
	}

	static boolean isValidQuery(String s) {
		return SqlServerParallelRecordSource.isValidQuery(s);
	}

	private final String dataViewName;
	private final String dbConfiguration;
	private final int fetch;
	private final String fileName;
	private final String idsQuery;
	private final AtomicInteger offset = new AtomicInteger(CLOSED_OFFSET);
	private final Queue<Record<?>> records = new ConcurrentLinkedQueue<>();

	// These values may be changed only when the record source is closed
	private DataSource ds;
	private String dsName;
	private ImmutableProbabilityModel model;

	public SqlServerFetchOffsetRecordSource(String fileName,
			ImmutableProbabilityModel model, String dsName,
			String dbConfiguration, String idsQuery) {
		this(fileName, model, dsName, dbConfiguration, idsQuery, DEFAULT_FETCH);
	}

	public SqlServerFetchOffsetRecordSource(String fileName,
			ImmutableProbabilityModel model, String dsName,
			String dbConfiguration, String idsQuery, int fetch) {

		logger.fine("Constructor: " + fileName + " " + model + " " + dsName
				+ " " + dbConfiguration + " " + idsQuery);

		if (fileName == null) {
			logger.fine("Null file name for " + getSource());
		}
		Precondition.assertNonEmptyString("DB configuration must be nonblank",
				dbConfiguration);
		Precondition.assertBoolean("idsQuery must contain ' AS ID '.",
				isValidQuery(idsQuery));
		Precondition.assertBoolean("Fetch size must be positive", fetch > 0);

		// Don't use public modifiers here -- preconditions may not apply
		this.fileName = fileName;
		this.model = model;
		this.dsName = dsName;
		this.dbConfiguration = dbConfiguration;
		this.idsQuery = idsQuery;
		this.dataViewName = createDataViewName();
		this.fetch = fetch;
		assert this.offset.get() == CLOSED_OFFSET;
	}

	public void close() {
		this.offset.set(CLOSED_OFFSET);
		this.records.clear();
		try (Connection conn = getDataSource().getConnection()) {
			dropView(conn, getDataView());
		} catch (SQLException x) {
			String msg0 = "Unable to drop view '%s': %s";
			String msg = String.format(msg0, getDataView(), x.toString());
			logger.warning(msg);
		}
	}

	private ResultSet[] fetchResultsFromOffset(DbReaderParallel dbr, int fetch,
			int offset) throws SQLException {
		assert fetch > 0;
		assert offset >= 0;

		final Accessor accessor = getModel().getAccessor();
		final String viewBase0 = "vw_cmt_%s_r_%d";
		final String viewBase = String.format(viewBase0,
				accessor.getSchemaName(), getDbConfiguration());
		final DbView[] views = dbr.getViews();
		final String masterId = dbr.getMasterId();
		final int numViews = views.length;

		final ResultSet[] retVal = new ResultSet[numViews];
		for (int i = 0; i < numViews; ++i) {
			String viewName = viewBase + i;
			logger.finest("view: " + viewName);
			DbView v = views[i];
			if (v.orderBy.length == 0) {
				String msg0 = "DbView[%d] is not ordered";
				String msg = String.format(msg0, i);
				logger.severe(msg);
				throw new IllegalStateException(msg);
			}

			String q0 = "SELECT * FROM [%s] WHERE [%s] IN "
					+ "(SELECT ID FROM [%s]) ORDER BY [%s] "
					+ "OFFSET %d ROWS FETCH NEXT %d ROWS ONLY";
			String q = String.format(q0, viewName, masterId, getDataView(),
					getOrderBy(v), offset, fetch);
			logger.fine("Query: " + q);

			try (Connection conn = getDataSource().getConnection();
					Statement select =
						conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
								ResultSet.CONCUR_READ_ONLY)) {
				final long start = System.currentTimeMillis();
				retVal[i] = select.executeQuery(q);
				final long duration = System.currentTimeMillis() - start;
				logDbExecution("Execute query", i, q, duration);
			}
		}
		return retVal;
	}

	private void fillQueue() throws IOException {

		DbAccessor accessor = (DbAccessor) getModel().getAccessor();
		DbReaderParallel dbr =
			accessor.getDbReaderParallel(getDbConfiguration());

		try (Connection conn = getDataSource().getConnection()) {
			// Get result sets
			ResultSet[] resultSets =
				fetchResultsFromOffset(dbr, fetch, offset.get());

			// Open the parallel reader
			logger.fine("before dbr.open");
			dbr.open(resultSets);

			// Read a batch of records
			int count = 0;
			while (dbr.hasNext()) {
				Record<?> r = dbr.getNext();
				records.add(r);
			}
			offset.addAndGet(count);

		} catch (SQLException ex) {
			logger.severe(ex.toString());
			throw new IOException(ex.toString(), ex);
		}
	}

	public DataSource getDataSource() {
		if (ds == null) {
			final String name = getDataSourceName();
			String msg = String.format("Looking up JDBC datasource '%s'", name);
			logger.fine(msg);
			ds = DataSources.getDataSource(name);
			if (ds == null) {
				msg = String.format("No datasource registered for name '%s'",
						name);
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

	public int getFetch() {
		return fetch;
	}

	public String getFileName() {
		return fileName;
	}

	public String getIdsQuery() {
		assert isValidQuery(this.idsQuery);
		return idsQuery;
	}

	public ImmutableProbabilityModel getModel() {
		if (model == null) {
			throw new IllegalStateException("null model");
		}
		return model;
	}

	public String getName() {
		return "SQL Server Fetch-Offset Record Source";
	}

	public Record<?> getNext() {
		Record<?> r = records.remove();
		return r;
	}

	public int getOffset() {
		return offset.get();
	}

	public Sink getSink() {
		throw new UnsupportedOperationException();
	}

	protected String getSource() {
		return SOURCE;
	}

	public boolean hasNext() throws IOException {
		boolean retVal = !records.isEmpty();
		if (retVal == false) {
			fillQueue();
			retVal = !records.isEmpty();
		}
		return retVal;
	}

	public boolean hasSink() {
		return false;
	}

	public boolean isClosed() {
		boolean retVal = offset.get() == CLOSED_OFFSET;
		if (retVal) {
			assert records.size() == 0;
		}
		return retVal;
	}

	private void logDbExecution(String tag, int i, String q, long t) {
		String msg0 = "%s [%d]: %d (msecs) [%s]";
		String msg = String.format(msg0, tag, i, t, q);
		logger.info(msg);
	}

	public void open() throws IOException {

		try (Connection conn = getDataSource().getConnection()) {
			final boolean wasClosed =
				offset.compareAndSet(CLOSED_OFFSET, INITIAL_OFFSET);
			if (wasClosed == false) {
				String msg0 = "%s was already open (offset %d)";
				String msg = String.format(msg0, SOURCE, offset.get());
				logger.severe(msg);
				throw new IllegalStateException(msg);
			}
			final int recordCount = records.size();
			if (recordCount != 0) {
				String msg0 = "%s already has records: %d";
				String msg = String.format(msg0, SOURCE, recordCount);
				logger.severe(msg);
				throw new IllegalStateException(msg);
			}

			createView(conn, getDataView(), getIdsQuery());
			fillQueue();

		} catch (SQLException ex) {
			offset.set(CLOSED_OFFSET);
			records.clear();
			logger.severe(ex.toString());
			throw new IOException(ex.toString(), ex);
		}
	}

	void setDataSource(String name, DataSource ds) {
		Precondition.assertBoolean(SOURCE + " must be closed", isClosed());
		Precondition.assertNonEmptyString("data source name must be non-blank",
				name);
		Precondition.assertNonNullArgument("Data source must be non-null", ds);
		this.dsName = name;
		this.ds = ds;
	}

	public void setModel(ImmutableProbabilityModel m) {
		Precondition.assertBoolean(SOURCE + " must be closed", isClosed());
		if (m == null) {
			throw new IllegalArgumentException("null model");
		}
		this.model = m;
	}

	public void setName(String name) {
		Precondition.assertBoolean(SOURCE + " must be closed", isClosed());
		throw new UnsupportedOperationException();
	}

	public String toString() {
		return getSource() + " [fileName=" + getFileName() + ", model="
				+ getModel() + ", dbConfiguration=" + getDbConfiguration()
				+ ", idsQuery=" + getIdsQuery() + ", dsName="
				+ getDataSourceName() + ", dataView=" + getDataView()
				+ ", fetch=" + getFetch() + "]";
	}

}
