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

	public static final int DEFAULT_INITIAL_OFFSET = 0;

	public static final int DEFAULT_LIMIT = Integer.MAX_VALUE;

	public static final String PN_FETCH_SIZE = "cm.sqlserver.fetch";

	/**
	 * Special value for the fetch size signaling that the limit is specified by
	 * a System property; see {@link #PN_FETCH_SIZE}. If this variable is not
	 * set, or is invalid (zero or negative), then the {@link #DEFAULT_FETCH
	 * default value} is used.
	 */
	public static final int SYSPROP_OR_DEFAULT_FETCH = 0;

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

	private static void dropView(Connection conn, String viewName) {
		try (Statement view = conn.createStatement()) {
			String s0 = "IF EXISTS (SELECT TABLE_NAME FROM "
					+ "INFORMATION_SCHEMA.VIEWS WHERE TABLE_NAME = '%s') "
					+ "DROP VIEW [%s]";
			String s = String.format(s0, viewName, viewName);
			logger.fine(s);
			view.execute(s);
			view.close();
		} catch (SQLException x) {
			String msg0 = "Unable to drop view [%s]: %s";
			String msg = String.format(msg0, viewName, x.toString());
			logger.warning(msg);
		}
	}

	public static int computeFetchSize(int fetch) {
		int retVal;
		if (fetch == SYSPROP_OR_DEFAULT_FETCH) {
			retVal = getFetchSizeFromSystemProperty();
		} else {
			retVal = fetch;
		}
		Precondition.assertBoolean("Fetch size must be positive", retVal > 0);
		return retVal;
	}

	public static int getFetchSizeFromSystemProperty() {
		int retVal = DEFAULT_FETCH;
		String _value = System.getProperty(PN_FETCH_SIZE);
		if (_value == null) {
			String msg0 = "Missing value for property '%s'";
			String msg = String.format(msg0, PN_FETCH_SIZE);
			logger.warning(msg);
		} else {
			try {
				int value = Integer.parseInt(_value);
				if (value > 0) {
					String msg0 = "Fetch size specified as %d by property '%s'";
					String msg = String.format(msg0, value, PN_FETCH_SIZE);
					logger.fine(msg);
					retVal = value;
				} else {
					String msg0 =
						"Ignoring invalid value (%d) for property '%s'";
					String msg = String.format(msg0, value, PN_FETCH_SIZE);
					logger.warning(msg);
				}
			} catch (Exception x) {
				assert retVal == DEFAULT_FETCH;
			}
		}
		assert retVal == DEFAULT_FETCH || retVal > 0;
		return retVal;
	}

	public static String getOrderBy(DbView v) {
		return SqlServerParallelRecordSource.getOrderBy(v);
	}

	static boolean isValidQuery(String s) {
		return SqlServerParallelRecordSource.isValidQuery(s);
	}

	private final AtomicInteger count = new AtomicInteger(0);
	private final String dataViewName;
	private final String dbConfiguration;
	private final int fetch;
	private final String fileName;
	private final String idsQuery;
	private final int initialOffset;
	private final int limit;
	private final AtomicInteger offset = new AtomicInteger(CLOSED_OFFSET);
	private final Queue<Record<?>> records = new ConcurrentLinkedQueue<>();

	// These values may be changed only when the record source is closed
	private DataSource ds;
	private String dsName;
	private ImmutableProbabilityModel model;

	public SqlServerFetchOffsetRecordSource(String fileName,
			ImmutableProbabilityModel model, String dsName,
			String dbConfiguration, String idsQuery) {
		this(fileName, model, dsName, dbConfiguration, idsQuery,
				SYSPROP_OR_DEFAULT_FETCH, DEFAULT_INITIAL_OFFSET,
				DEFAULT_LIMIT);
	}

	public SqlServerFetchOffsetRecordSource(String fileName,
			ImmutableProbabilityModel model, String dsName,
			String dbConfiguration, String idsQuery, int fetch, int offset,
			int limit) {

		logger.fine("Constructor: " + fileName + " " + model + " " + dsName
				+ " " + dbConfiguration + " " + idsQuery);

		if (fileName == null) {
			logger.fine("Null file name for " + getSource());
		}
		Precondition.assertNonEmptyString("DB configuration must be nonblank",
				dbConfiguration);
		Precondition.assertBoolean("idsQuery must contain ' AS ID '.",
				isValidQuery(idsQuery));
		Precondition.assertBoolean("Initial offset must be non-negative",
				offset >= 0);

		String msg0 =
			"Limit (%d) <= initialOffset (%d): no records will be retrieved";
		String msg = String.format(msg0, limit, offset);
		Precondition.assertBoolean(msg, limit <= 0 || limit > offset);

		// Don't use public modifiers here -- preconditions may not apply
		this.fileName = fileName;
		this.model = model;
		this.dsName = dsName;
		this.dbConfiguration = dbConfiguration;
		this.idsQuery = idsQuery;
		this.dataViewName = createDataViewName();
		this.fetch = computeFetchSize(fetch);
		this.initialOffset = offset;
		if (limit <= 0) {
			this.limit = DEFAULT_LIMIT;
		} else {
			this.limit = limit;
		}

		assert this.count.get() == 0;
		assert this.limit > this.initialOffset;
		assert this.offset.get() == CLOSED_OFFSET;
	}

	@Override
	public void close() {
		this.offset.set(CLOSED_OFFSET);
		this.records.clear();
		try (Connection conn = getDataSource().getConnection()) {
			dropView(conn, getDataView());
		} catch (SQLException x) {
			String msg0 = "Unable to acquire connection to drop view [%s]: %s";
			String msg = String.format(msg0, getDataView(), x.toString());
			logger.warning(msg);
		}
	}

	private void fillQueue(Connection conn) throws IOException {
		assert conn != null;
		if (this.count.get() >= this.limit) {
			String msg0 = "Limit (%d) reached or exceeded: %d";
			String msg = String.format(msg0, this.limit, this.count.get());
			logger.fine(msg);
		} else {
			final Accessor accessor = getModel().getAccessor();
			final DbAccessor dbAccessor = (DbAccessor) accessor;
			final DbReaderParallel dbr =
				dbAccessor.getDbReaderParallel(getDbConfiguration());

			final String viewBase0 = "vw_cmt_%s_r_%s";
			final String viewBase = String.format(viewBase0,
					accessor.getSchemaName(), getDbConfiguration());
			final DbView[] views = dbr.getViews();
			final int numViews = views.length;

			final ResultSet[] resultSets = new ResultSet[numViews];
			final Statement[] queries = new Statement[numViews];
			try {
				String q0 = null;
				for (int i = 0; i < numViews; ++i) {
					final String masterId = dbr.getMasterId();

					String viewName = viewBase + i;
					logger.finest("view: " + viewName);
					DbView v = views[i];
					if (v.orderBy.length == 0) {
						String msg0 = "DbView[%d] is not ordered";
						String msg = String.format(msg0, i);
						logger.severe(msg);
						throw new IllegalStateException(msg);
					}

					String q;
					if (i == 0) {
						String t0 = "SELECT [%s] FROM [%s] WHERE [%s] IN "
								// + "(SELECT [ID] FROM [%s]) ORDER BY [%s] "
								+ "(SELECT [ID] FROM [%s]) ORDER BY %s "
								+ "OFFSET %d ROWS FETCH NEXT %d ROWS ONLY";
						q0 = String.format(t0, masterId, viewName, masterId,
								getDataView(), getOrderBy(v), offset.get(),
								fetch);
						logger.fine("q0: " + q0);
						String t = "SELECT * FROM [%s] WHERE [%s] IN "
								// + "(SELECT [ID] FROM [%s]) ORDER BY [%s] "
								+ "(SELECT [ID] FROM [%s]) ORDER BY %s "
								+ "OFFSET %d ROWS FETCH NEXT %d ROWS ONLY";
						q = String.format(t, viewName, masterId, getDataView(),
								getOrderBy(v), offset.get(), fetch);
					} else {
						assert q0 != null;
						String t = "SELECT * FROM [%s] WHERE [%s] IN "
								// + "(%s) ORDER BY [%s]";
								+ "(%s) ORDER BY %s";
						q = String.format(t, viewName, masterId, q0,
								getOrderBy(v));
					}
					logger.fine("Query: " + q);

					final long start = System.currentTimeMillis();
					queries[i] = conn.createStatement();
					resultSets[i] = queries[i].executeQuery(q);
					final long duration = System.currentTimeMillis() - start;
					logDbExecution("Execute query", i, q, duration);
				}

				// Open the parallel reader
				logger.fine("before dbr.open");
				dbr.open(resultSets);

				// Read a batch of records
				while (dbr.hasNext()
						&& this.count.incrementAndGet() < this.limit) {
					Record<?> r = dbr.getNext();
					records.add(r);
				}
				offset.set(count.get());

			} catch (SQLException ex) {
				close();
				logger.severe(ex.toString());
				throw new IOException(ex.toString(), ex);

			} finally {
				for (int i = 0; i < numViews; ++i) {
					if (queries[i] != null) {
						try {
							queries[i].close();
						} catch (SQLException e) {
							String msg0 = "Unable to close query %d: %s";
							String msg = String.format(msg0, i, e.toString());
							logger.warning(msg);
						}
						queries[i] = null;
					}
				}
				for (int i = 0; i < numViews; ++i) {
					if (resultSets[i] != null) {
						try {
							resultSets[i].close();
						} catch (SQLException e) {
							String msg0 = "Unable to close result set %d: %s";
							String msg = String.format(msg0, i, e.toString());
							logger.warning(msg);
						}
						resultSets[i] = null;
					}
				}
			}
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
		return "SQL Server Fetch-Offset Record Source";
	}

	@Override
	public Record<?> getNext() {
		Record<?> r = records.remove();
		return r;
	}

	public int getOffset() {
		return offset.get();
	}

	@Override
	public Sink getSink() {
		throw new UnsupportedOperationException();
	}

	protected String getSource() {
		return SOURCE;
	}

	@Override
	public boolean hasNext() throws IOException {
		boolean _hasNext = !records.isEmpty();
		if (_hasNext == false) {
			try (Connection conn = getDataSource().getConnection()) {
				fillQueue(conn);
			} catch (SQLException ex) {
				close();
				logger.severe(ex.toString());
				throw new IOException(ex.toString(), ex);
			}
			_hasNext = !records.isEmpty();
		}
		return _hasNext;
	}

	@Override
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
		logger.fine(msg);
	}

	@Override
	public void open() throws IOException {

		try (Connection conn = getDataSource().getConnection()) {
			final boolean wasClosed =
				offset.compareAndSet(CLOSED_OFFSET, initialOffset);
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
			fillQueue(conn);

		} catch (SQLException ex) {
			close();
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

	@Override
	public void setModel(ImmutableProbabilityModel m) {
		Precondition.assertBoolean(SOURCE + " must be closed", isClosed());
		if (m == null) {
			throw new IllegalArgumentException("null model");
		}
		this.model = m;
	}

	@Override
	public void setName(String name) {
		Precondition.assertBoolean(SOURCE + " must be closed", isClosed());
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return getSource() + " [fileName=" + getFileName() + ", model="
				+ getModel() + ", dbConfiguration=" + getDbConfiguration()
				+ ", idsQuery=" + getIdsQuery() + ", dsName="
				+ getDataSourceName() + ", dataView=" + getDataView()
				+ ", fetch=" + getFetch() + "]";
	}

}
