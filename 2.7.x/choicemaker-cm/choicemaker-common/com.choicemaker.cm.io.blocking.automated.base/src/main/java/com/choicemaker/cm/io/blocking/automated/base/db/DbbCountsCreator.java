/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.base.db;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.io.blocking.automated.AbaStatisticsCache;
import com.choicemaker.cm.io.blocking.automated.BlockingAccessor;
import com.choicemaker.cm.io.blocking.automated.IBlockingConfiguration;
import com.choicemaker.cm.io.blocking.automated.ICountField;
import com.choicemaker.cm.io.blocking.automated.IDbField;
import com.choicemaker.cm.io.blocking.automated.IDbTable;
import com.choicemaker.cm.io.blocking.automated.base.CountField;
import com.choicemaker.cm.io.blocking.automated.base.DbTable;
import com.choicemaker.cm.io.blocking.automated.cachecount.AbaStatisticsImpl;
import com.choicemaker.cm.io.db.base.DatabaseAbstraction;
import com.choicemaker.cm.io.db.base.DbAccessor;

/**
 * Database Blocking Counts Creator. This class has no instance data. It is a
 * collection of methods for maintaining ABA statistics in a database and
 * caching them in memory.
 * <p>
 * ABA statistics fall into two categories: meta-data about the statistics and
 * the statistics themselves. Meta-data consists of information about which
 * blocking configurations use which tables and which fields within those
 * tables, as well as the primary keys for the tables, threshold values for
 * recording statistics, and timestamps for when statistics were last computed.
 * There is also one seldom used piece of meta-data about the schema used for
 * snapshots.
 * <p>
 * ABA statistics themselves are simple. For fields, the only statistic that is
 * collected is the number of times data values appear in the field. This
 * statistic is recorded for a particular value only if the value occurs more
 * than a threshold value. For tables, the only statistic that is collected is
 * the number of rows in the table.
 * <p>
 * Meta-data is maintained in the following tables:
 * <ul>
 * <li>TB_CMT_CONFIG: a table of properties for blocking configuration. The
 * table has three columns:
 * <ol>
 * <li>CONFIG the name of the blocking configuration that owns a property</li>
 * <li>NAME the name of a property</li>
 * <li>VALUE the value assigned to the property</li>
 * </ol>
 * The TB_CMT_CONFIG table is mostly used to record the primary keys of tables,
 * but this data is then duplicated in other meta-data tables as a performance
 * optimization. The TB_CMT_CONFIG table is populated by scripts generated from
 * model schemas. This table is not referenced in Java code, only in Oracle
 * stored procedures, and even there, it doesn't appear to be read, only
 * written. In other words, the table can probably be dropped and removed from
 * generation scripts.<br/>
 * </li>
 * <li>TB_CMT_COUNT_CONFIG_FIELDS: a denormalized table that maps blocking
 * configurations to tables and fields. As an optimization, the meaning of the
 * columns depends on whether a row stores information about a table or a field.
 * The table has 5 columns:
 * <ol>
 * <li>CONFIG the name of the blocking configuration that uses a table or a
 * field</li>
 * <li>VIEWNAME the name of a table or view</li>
 * <li>COLUMNNAME of a field used in blocking</li>
 * <li>MASTERID the primary key on the table (denormalized)</li>
 * <li>MINCOUNT a threshold count, below which statistics are not kept for a
 * value in the field</li>
 * </ol>
 * <br/>
 * </li>
 * <li>TB_CMT_COUNT_FIELDS: a denormalized table that defines an alternative
 * key, FieldId, that is an integer value unique to each table and table-field
 * pair. The table also tracks the last time that statistics were updated for a
 * table or a table-field pair. The table has six columns:
 * <ol>
 * <li>FIELDID an alternative key to assigned to a table or a table-field pair
 * </li>
 * <li>VIEWNAME the name of a table or view</li>
 * <li>COLUMNNAME of a field used in blocking. If an entry is refers to a table,
 * this value will be null</li>
 * <li>MASTERID the primary key on the table (denormalized)</li>
 * <li>MINCOUNT a threshold count, below which statistics are not kept for a
 * value in the field. If an entry refers to a table, this value will be null.
 * </li>
 * <li>LASTUPDATE the last time statistics were updated for the fields</li>
 * </ol>
 * <br/>
 * </li>
 * </ul>
 * <p>
 * <p>
 * ABA statistics are maintain in the TB_CMT_COUNTS table.
 * <ul>
 * <li>TB_CMT_COUNTS: a table of counts for fields and tables. The table has
 * three columns, the meaning of which depends on whether an entry tracks a
 * table or a field:
 * <ol>
 * <li>FIELDID the alternative key of a table or a table-field pair</li>
 * <li>VALUE the value of a field or the literal value <code>table</code> for a
 * table</li>
 * <li>COUNT the number of times a value occurs within a field, if this count is
 * above the <code>MINCOUNT</threshold> specified (redundantly) in the meta-data
 * tables. For a table, this is the number of rows within the table.</li>
 * </ol>
 * </li>
 *
 * @author mbuechi
 * @author rphall (Documentation)
 */
public class DbbCountsCreator {

	public static final String sqlDeleteCountConfigFields =
		"DELETE FROM TB_CMT_COUNT_CONFIG_FIELDS WHERE config = ?";

	public static final String msgBindSqlDeleteCountConfigFields(
			String config) {
		StringBuilder sb = new StringBuilder().append("BIND ");
		sb.append(sqlDeleteCountConfigFields).append(": ");
		sb.append("[config:").append(config).append("]");
		String retVal = sb.toString();
		return retVal;
	}

	public static final String sqlInsertFieldIntoCountConfigFields =
		"INSERT INTO TB_CMT_COUNT_CONFIG_FIELDS("
				+ "CONFIG,VIEWNAME,COLUMNNAME,MASTERID,MINCOUNT) "
				+ "VALUES(?, ?, ?, ?, ?)";

	public static final String msgBindSqlInsertFieldIntoCountConfigFields(
			String config, String view, String column, String masterId,
			Integer minCount) {
		StringBuilder sb = new StringBuilder().append("BIND ");
		sb.append(sqlInsertFieldIntoCountConfigFields).append(": ");
		sb.append("[config:").append(config).append("], ");
		sb.append("[view:").append(view).append("], ");
		sb.append("[column:").append(column).append("], ");
		sb.append("[masterId:").append(masterId).append("], ");
		sb.append("[minCount:").append(minCount).append("]");
		String retVal = sb.toString();
		return retVal;
	}

	public static final String sqlInsertTableIntoCountConfigFields =
		"INSERT INTO TB_CMT_COUNT_CONFIG_FIELDS VALUES(?,?,null,?,null)";

	public static final String msgBindSqlInsertTableIntoCountConfigFields(
			String config, String view, String masterId) {
		StringBuilder sb = new StringBuilder().append("BIND ");
		sb.append(sqlInsertTableIntoCountConfigFields).append(": ");
		sb.append("[config:").append(config).append("], ");
		sb.append("[view:").append(view).append("], ");
		sb.append("[masterId:").append(masterId).append("]");
		String retVal = sb.toString();
		return retVal;
	}

	private static Logger logger =
		Logger.getLogger(DbbCountsCreator.class.getName());

	/**
	 * Installs ABA statistical meta-data into a database.
	 *
	 * @param ds
	 *            a non-null connection to the database
	 * @throws SQLException
	 */
	public void installAbaMetaData(DataSource ds) throws SQLException {
		final String METHOD = "DbbCountsCreator.install: ";
		if (ds == null) {
			throw new IllegalArgumentException(METHOD + "null data source");
		}
		logger.fine("DEBUG " + METHOD + "entering");

		Connection connection = null;
		try {
			connection = ds.getConnection();
			installCountConfigFieldsMetaData(connection);
			installCountFieldsMetaData(connection);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e1) {
					logger.severe(METHOD + e1.toString());
				}
			}
		}
		logger.fine("DEBUG " + METHOD + "exiting");
	}

	/**
	 * Installs meta-data into the TB_CMT_COUNT_CONFIG_FIELDS table.
	 *
	 * @param connection
	 *            a non-null connection
	 * @throws SQLException
	 */
	private void installCountConfigFieldsMetaData(final Connection connection)
			throws SQLException {
		final String METHOD = "DbbCountsCreator.setConfigFields: ";
		assert connection != null;
		logger.fine("DEBUG " + METHOD + "entering");
		PreparedStatement stmt1 = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		try {
			logger.info("SQL to delete TB_CMT_COUNT_CONFIG_FIELDS: "
					+ sqlDeleteCountConfigFields);
			stmt1 = connection.prepareStatement(sqlDeleteCountConfigFields);
			logger.info("SQL to insert field into TB_CMT_COUNT_CONFIG_FIELDS: "
					+ sqlInsertFieldIntoCountConfigFields);
			stmt2 = connection
					.prepareStatement(sqlInsertFieldIntoCountConfigFields);
			logger.info("SQL to insert table into TB_CMT_COUNT_CONFIG_FIELDS:  "
					+ sqlInsertTableIntoCountConfigFields);
			stmt3 = connection
					.prepareStatement(sqlInsertTableIntoCountConfigFields);

			// Get the registered model configurations
			ImmutableProbabilityModel[] models = PMManager.getModels();
			if (models == null) {
				String msg = METHOD + "null models";
				logger.severe(msg);
				throw new IllegalStateException(msg);
			}
			if (logger.isLoggable(Level.FINE)) {
				if (models != null) {
					final int mcount = models == null ? 0 : models.length;
					logger.info("Model configurations: " + mcount);
					for (ImmutableProbabilityModel m : models) {
						logger.fine("Model configuration: " + m.getModelName());
					}
				}
			}

			// Get the blocking configurations defined by the models
			IBlockingConfiguration[] bcs = getBlockingConfigurations(models);
			if (logger.isLoggable(Level.FINE)) {
				if (bcs == null) {
					logger.warning(METHOD + "null bcs");
				} else {
					final int bcount = bcs == null ? 0 : bcs.length;
					logger.fine("DEBUG " + "blocking config count: " + bcount);
				}
			}

			// Iterate over the blocking configurations
			for (IBlockingConfiguration bc : bcs) {
				final String bcName = bc.getName();
				logger.fine("Blocking configuration: " + bcName);

				// For each blocking configuration, delete all tables and rows
				// // TB_CMT_COUNT_CONFIG_FIELDS
				logger.fine(msgBindSqlDeleteCountConfigFields(bcName));
				stmt1.setString(1, bcName);
				int rowsDeleted = stmt1.executeUpdate();
				logger.fine("TB_CMT_COUNT_CONFIG_FIELDS rows deleted: "
						+ rowsDeleted);

				// For each blocking configuration, insert fields
				int fieldsInserted = 0;
				for (IDbField df : bc.getDbFields()) {
					final String view = df.getTable().getName();
					final String column = df.getName();
					final String masterId = df.getTable().getUniqueId();
					final int minCount = df.getDefaultCount();
					String msg = msgBindSqlInsertFieldIntoCountConfigFields(
							bcName, view, column, masterId, minCount);
					logger.fine(msg);
					stmt2.setString(1, bcName);
					stmt2.setString(2, view);
					stmt2.setString(3, column);
					stmt2.setString(4, masterId);
					stmt2.setInt(5, minCount);
					fieldsInserted += stmt2.executeUpdate();
				}
				logger.fine("TB_CMT_COUNT_CONFIG_FIELDS fields inserted: "
						+ fieldsInserted);

				// For each blocking configuration, insert tables
				int tablesInserted = 0;
				for (IDbTable dt : bc.getDbTables()) {
					final String view = dt.getName();
					final String masterId = dt.getUniqueId();
					String msg = msgBindSqlInsertTableIntoCountConfigFields(
							bcName, view, masterId);
					logger.fine(msg);
					stmt3.setString(1, bcName);
					stmt3.setString(2, view);
					stmt3.setString(3, masterId);
					tablesInserted += stmt3.executeUpdate();
				}
				logger.fine("TB_CMT_COUNT_CONFIG_FIELDS tables inserted: "
						+ tablesInserted);
			}
		} finally {
			if (stmt1 != null) {
				try {
					stmt1.close();
				} catch (SQLException e) {
					logger.severe(METHOD + e.toString());
				}
				stmt1 = null;
			}
			if (stmt2 != null) {
				try {
					stmt2.close();
				} catch (SQLException e) {
					logger.severe(METHOD + e.toString());
				}
				stmt2 = null;
			}
			if (stmt3 != null) {
				try {
					stmt3.close();
				} catch (SQLException e) {
					logger.severe(METHOD + e.toString());
				}
				stmt3 = null;
			}
		}
		logger.fine("DEBUG " + METHOD + "exiting");
	}

	/**
	 * Installs meta-data into the TB_CMT_COUNT_FIELDS table.
	 *
	 * @param connection
	 *            a non-null connection
	 * @throws SQLException
	 */
	private void installCountFieldsMetaData(final Connection connection)
			throws SQLException {
		final String METHOD = "DbbCountsCreator.setMainFields: ";
		assert connection != null;
		logger.fine("DEBUG " + METHOD + "entering");
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			int maxId = -1;
			String query = "SELECT MAX(FieldId) FROM TB_CMT_COUNT_FIELDS";
			logger.fine("DEBUG " + query);
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				maxId = rs.getInt(1);
			}
			rs.close();
			query = "SELECT ViewName, ColumnName, MasterId, MIN(MinCount) "
					+ "FROM TB_CMT_COUNT_CONFIG_FIELDS t1 "
					+ "WHERE ColumnName IS NOT NULL AND NOT EXISTS ("
					+ "SELECT * FROM TB_CMT_COUNT_FIELDS t2 "
					+ "WHERE t1.ViewName = t2.ViewName AND "
					+ "t1.ColumnName = t2.ColumnName AND "
					+ "t1.MasterId = t2.MasterId) "
					+ "GROUP BY ViewName, ColumnName, MasterId";
			logger.fine("DEBUG " + query);
			rs = stmt.executeQuery(query);
			// Some JDBC drivers don't support multiple statements or result
			// sets on a single connection.
			ArrayList<String> l = new ArrayList<>();
			while (rs.next()) {
				for (int i = 1; i <= 4; ++i) {
					l.add(rs.getString(i));
				}
			}
			rs.close();
			Iterator<String> iL = l.iterator();
			while (iL.hasNext()) {

				query = "INSERT INTO TB_CMT_COUNT_FIELDS VALUES(" + (++maxId)
						+ ", '" + iL.next() + "','" + iL.next() + "','"
						+ iL.next() + "'," + iL.next() + ", null)";
				logger.fine("DEBUG " + query);
				stmt.execute(query);
			}
			l.clear();
			query = "SELECT DISTINCT ViewName, MasterId "
					+ "FROM TB_CMT_COUNT_CONFIG_FIELDS t1 "
					+ "WHERE ColumnName IS NULL AND NOT EXISTS "
					+ "(SELECT * FROM TB_CMT_COUNT_FIELDS t2 "
					+ " WHERE t1.ViewName = t2.ViewName "
					+ "AND t2.ColumnName IS NULL "
					+ "AND t1.MasterId = t2.MasterId)";
			logger.fine("DEBUG " + query);
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				for (int i = 1; i <= 2; ++i) {
					l.add(rs.getString(i));
				}
			}
			rs.close();
			iL = l.iterator();
			while (iL.hasNext()) {
				query = "INSERT INTO TB_CMT_COUNT_FIELDS VALUES(" + (++maxId)
						+ ", '" + iL.next() + "', null, '" + iL.next()
						+ "', null, null)";
				logger.fine("DEBUG " + query);
				stmt.execute(query);
			}
			query = "DELETE FROM TB_CMT_COUNTS WHERE fieldId NOT IN ("
					+ "SELECT fieldId FROM TB_CMT_COUNT_FIELDS f, "
					+ "TB_CMT_COUNT_CONFIG_FIELDS k "
					+ "WHERE f.ViewName = k.ViewName AND ("
					+ "(f.ColumnName IS NULL AND k.ColumnName IS NULL) OR "
					+ "(f.ColumnName = k.ColumnName)" + ") )";
			logger.fine("DEBUG " + query);
			stmt.execute(query);
			query = "DELETE FROM TB_CMT_COUNT_FIELDS "
					+ "WHERE ColumnName IS NOT NULL AND  NOT EXISTS ("
					+ "SELECT * FROM TB_CMT_COUNT_CONFIG_FIELDS t2 "
					+ "WHERE TB_CMT_COUNT_FIELDS.ViewName = t2.ViewName AND "
					+ "TB_CMT_COUNT_FIELDS.ColumnName = t2.ColumnName AND "
					+ "TB_CMT_COUNT_FIELDS.MasterId = t2.MasterId)";
			logger.fine("DEBUG " + query);
			stmt.execute(query);
			query =
				"DELETE FROM TB_CMT_COUNT_FIELDS WHERE ColumnName IS NULL AND "
						+ "NOT EXISTS ("
						+ "SELECT * FROM TB_CMT_COUNT_CONFIG_FIELDS t2 "
						+ "WHERE TB_CMT_COUNT_FIELDS.ViewName = t2.ViewName "
						+ "AND TB_CMT_COUNT_FIELDS.MasterId = t2.MasterId "
						+ "AND t2.ColumnName IS NULL)";
			logger.fine("DEBUG " + query);
			stmt.execute(query);

		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					logger.severe(METHOD + e.toString());
				}
			}
		}

		logger.fine("DEBUG " + METHOD + "exiting");
	}

	/**
	 * Computes ABA statistical data for fields and tables.
	 *
	 * @param ds
	 *            a non-null data source
	 * @param databaseAbstraction
	 *            a non-null database abstraction
	 * @param onlyUncomputed
	 *            if true computes stats only for fields and tables for which
	 *            stats have never been computed; if false, computes stats for
	 *            all fields and tables, even if they have been computed
	 *            previously.
	 * @throws SQLException
	 */
	public void computeAbaStatistics(DataSource ds,
			DatabaseAbstraction databaseAbstraction, boolean onlyUncomputed)
			throws SQLException {
		computeAbaStatistics(ds, databaseAbstraction, onlyUncomputed, true);
	}

	/**
	 * Computes ABA statistical data for fields and tables.
	 *
	 * @param ds
	 *            a non-null data source
	 * @param databaseAbstraction
	 *            a non-null database abstraction
	 * @param onlyUncomputed
	 *            if true computes stats only for fields and tables for which
	 *            stats have never been computed; if false, computes stats for
	 *            all fields and tables, even if they have been compute
	 *            previously.
	 * @param commitChanges
	 *            if true, newly computed stats are committed the database; if
	 *            false, assumes that the application (or application container)
	 *            will do the commit. (Data sources managed by a JEE container
	 *            should be committed by the container, not an application
	 *            component.)
	 * @throws SQLException
	 */
	public void computeAbaStatistics(DataSource ds,
			DatabaseAbstraction databaseAbstraction, boolean onlyUncomputed,
			boolean commitChanges) throws SQLException {
		final String METHOD = "DbbCountsCreator.create: ";
		if (ds == null) {
			throw new IllegalArgumentException(METHOD + "null data source");
		}
		if (databaseAbstraction == null) {
			throw new IllegalArgumentException(
					METHOD + "null database abstraction");
		}
		logger.fine("DEBUG " + METHOD + "entering");

		// Debugging
		String _latest_query = null;

		// BUG 2009-08-21 rphall
		// This method may fail if two CM Server instances use the same database
		// simultaneously.
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = ds.getConnection();
			stmt = connection.createStatement();
			String q0 = databaseAbstraction.getSetDateFormatExpression();
			_latest_query = q0;
			stmt.execute(q0);
			String delete;
			String query;
			if (onlyUncomputed) {
				delete = "DELETE FROM TB_CMT_COUNTS WHERE NOT EXISTS "
						+ "(SELECT * FROM TB_CMT_COUNT_FIELDS f "
						+ "WHERE TB_CMT_COUNTS.FieldId = f.FieldId "
						+ "AND f.lastUpdate IS NOT NULL)";
				query =
					"SELECT * FROM TB_CMT_COUNT_FIELDS WHERE LastUpdate IS NULL";

			} else {

				// DESIGN BUG 2009-08-21 rphall
				// Some databases should use TRUNCATE rather than
				// DELETE for performance reasons. Other databases
				// don't support TRUNCATE. So there should be a mechanism
				// to invoke DELETE or TRUNCATE depending on the DB flavor
				// (something better than the flawed DatabaseAbstraction
				// design, which is tied to models rather than connections).
				delete = "DELETE FROM TB_CMT_COUNTS";

				// truncate is not supported in DB2
				// delete = "TRUNCATE TABLE TB_CMT_COUNTS";

				query = "SELECT * FROM TB_CMT_COUNT_FIELDS";
				// END DESIGN BUG
			}
			logger.fine("DEBUG " + delete);
			_latest_query = delete;
			stmt.execute(delete);
			logger.fine("DEBUG " + query);
			_latest_query = query;
			ResultSet rs = stmt.executeQuery(query);
			List<String> l = new ArrayList<>();
			while (rs.next()) {
				for (int i = 1; i <= 5; ++i) {
					l.add(rs.getString(i));
				}
			}
			rs.close();
			Iterator<String> iL = l.iterator();
			while (iL.hasNext()) {
				String fieldId = (String) iL.next();
				String table = (String) iL.next();
				String column = (String) iL.next();
				String uniqueId = (String) iL.next();
				String minCount = (String) iL.next();
				if (column == null || column.length() == 0) {
					// table
					query = "INSERT INTO TB_CMT_COUNTS SELECT " + fieldId
							+ ", 'table', COUNT(DISTINCT " + uniqueId
							+ ") FROM " + table;
					logger.fine("DEBUG " + query);
					_latest_query = query;
					stmt.execute(query);

				} else {
					// field
					query =
						"SELECT " + column + " FROM " + table + " WHERE 0 = 1";
					logger.fine("DEBUG " + query);
					_latest_query = query;
					ResultSet tmpRs = stmt.executeQuery(query);
					int columnType = tmpRs.getMetaData().getColumnType(1);
					boolean isDate = columnType == Types.DATE
							|| columnType == Types.TIMESTAMP;
					tmpRs.close();

					query = "INSERT INTO TB_CMT_COUNTS SELECT " + fieldId + ","
							+ (isDate
									? databaseAbstraction
											.getDateFieldExpression(column)
									: column)
							+ ", COUNT(" + uniqueId + ") FROM " + table
							+ " WHERE " + column + " IS NOT NULL " + "GROUP BY "
							+ column + " HAVING COUNT(" + uniqueId + ") > "
							+ minCount;
					logger.fine("DEBUG " + query);
					_latest_query = query;
					stmt.execute(query);
				}

				query = "UPDATE TB_CMT_COUNT_FIELDS SET LastUpdate = "
						+ databaseAbstraction.getSysdateExpression()
						+ " WHERE FieldId = " + fieldId;
				logger.fine("DEBUG " + query);
				_latest_query = query;
				stmt.execute(query);

				if (commitChanges) {
					logger.fine(METHOD + "commiting ABA statistics to DB");
					connection.commit();
				} else {
					String msg = "skipping commit of ABA statistics to DB "
							+ "-- assuming a managed connection will "
							+ "automagically commit them instead.";
					logger.fine(METHOD + msg);
				}
			}

		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			String msg =
				METHOD + "Unable to create ABA statistics: " + e.toString();
			pw.println(msg);
			pw.println("   LATEST QUERY: " + _latest_query);
			msg = sw.toString();
			logger.severe(msg);
			throw e;

		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					logger.severe(METHOD + e.toString());
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e1) {
					logger.severe(METHOD + e1.toString());
				}
			}
		}
	}

	/**
	 * Updates a cache with statistics from the database.
	 *
	 * @param ds
	 *            a non-null data source that connects to the database
	 * @param databaseAbstraction
	 *            a non-null database abstraction that handles data formats and
	 *            other DBMS-specific conversions
	 * @param cache
	 *            a non-null statistics cache
	 * @throws SQLException
	 */
	public void updateAbaStatisticsCache(DataSource ds,
			DatabaseAbstraction databaseAbstraction, AbaStatisticsCache cache)
			throws SQLException {
		final String METHOD = "DbbCountsCreator.setCacheCountSources: ";
		if (ds == null) {
			throw new IllegalArgumentException(METHOD + "null data source");
		}
		if (cache == null) {
			throw new IllegalArgumentException(
					METHOD + "null statistics cache");
		}
		logger.fine("DEBUG " + METHOD + "entering");

		ImmutableProbabilityModel[] models = PMManager.getModels();
		if (models == null) {
			String msg = "No models: statistics can not be updated";
			throw new IllegalStateException(msg);
		}

		Connection connection = null;
		try {
			connection = ds.getConnection();

			List<CountField> countFields = new ArrayList<>();
			Map<DbTable, Integer> tableSizes = readTableSizes(connection);

			IBlockingConfiguration[] blockingConfigurations =
				getBlockingConfigurations(models);
			for (IBlockingConfiguration bc : blockingConfigurations) {
				ICountField[] bcCountFields =
					new ICountField[bc.getDbFields().length];
				for (int j = 0; j < bc.getDbFields().length; ++j) {

					IDbField dbf = bc.getDbFields()[j];
					CountField f = find(countFields, dbf);
					if (f == null) {
						f = readCountField(connection, tableSizes, dbf);
						countFields.add(f);
					}
					assert f != null;
					bcCountFields[j] = f;
				}
				AbaStatisticsImpl ccs = new AbaStatisticsImpl(
						getTableSize(tableSizes, bc.getDbTables()[0]),
						bcCountFields);
				cache.putStatistics(bc, ccs);
			}
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e1) {
					logger.severe(METHOD + e1.toString());
				}
			}
		}
	}

	private CountField readCountField(Connection connection,
			Map<DbTable, Integer> tableSizes, IDbField dbf)
			throws SQLException {
		final String column = dbf.getName();
		final String view = dbf.getTable().getName();
		final String uniqueId = dbf.getTable().getUniqueId();
		final int tableSize = getTableSize(tableSizes, dbf.getTable());

		CountField retVal = new CountField(100, dbf.getDefaultCount(),
				tableSize, column, view, uniqueId);

		Integer FieldId = retrieveFieldId(connection, view, column, uniqueId);
		if (FieldId != null) {
			updateCounts(connection, retVal, FieldId.intValue());
		}

		return retVal;
	}

	private void updateCounts(Connection c, CountField f, int fieldId)
			throws SQLException {
		String query = "SELECT Value, Count FROM TB_CMT_COUNTS "
				+ "WHERE FieldId = " + fieldId;
		logger.fine("DEBUG " + query);
		Statement stmt = null;
		ResultSet rs = null;
		Integer FieldId = null;
		try {
			stmt = c.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				String value = rs.getString(1);
				Integer count = CountField.valueOf(rs.getInt(2));
				f.putValueCount(value, count);
			}
			if (f.getValueCountSize() == 0) {
				String msg = "No value/count entries for field id " + FieldId;
				logger.warning(msg);
			} else {
				String msg = "Field id " + fieldId + ": "
						+ f.getValueCountSize() + " value/count entries";
				logger.fine(msg);
			}

		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException x) {
					String msg = "Unable to close statement; " + x;
					logger.warning(msg);
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException x) {
					String msg = "Unable to close result set; " + x;
					logger.warning(msg);
				}
			}
		}
	}

	private Integer retrieveFieldId(Connection c, String view, String column,
			String masterId) throws SQLException {
		String query = "SELECT FieldId FROM TB_CMT_COUNT_FIELDS "
				+ "WHERE ViewName = '" + view + "' AND ColumnName = '" + column
				+ "' AND MasterId = '" + masterId + "'";
		logger.fine("DEBUG " + query);
		Statement stmt = null;
		ResultSet rs = null;
		Integer retVal = null;
		try {
			stmt = c.createStatement();
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				retVal = rs.getInt(1);
				rs.close();
			}
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException x) {
					String msg = "Unable to close statement; " + x;
					logger.warning(msg);
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException x) {
					String msg = "Unable to close result set; " + x;
					logger.warning(msg);
				}
			}
		}
		if (retVal == null) {
			String msg = "Unable to retrieve field id " + "for view:" + view
					+ ", column:" + column + ", masterId:" + masterId;
			logger.warning(msg);
		}
		return retVal;
	}

	private Map<DbTable, Integer> readTableSizes(Connection c)
			throws SQLException {
		logger.fine("DEBUG " + "readTableSizes...");
		Map<DbTable, Integer> retVal = new HashMap<>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			String query = "SELECT ViewName, MasterId, Count "
					+ "FROM TB_CMT_COUNT_FIELDS f, TB_CMT_COUNTS c "
					+ "WHERE f.FieldId = c.FieldId AND f.ColumnName IS NULL";
			logger.fine("DEBUG " + query);
			stmt = c.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				retVal.put(new DbTable(rs.getString(1), 0, rs.getString(2)),
						new Integer(Math.max(1, rs.getInt(3))));
			}
			if (retVal.size() == 0) {
				String msg =
					"Required views for automated blocking were not found. "
							+ "Automated blocking will not work without them. "
							+ "Use CM-Analyzer to produce a script that will "
							+ "create them, then run the script to add them "
							+ "to the database.";
				logger.warning(msg);
			}
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException x) {
					String msg = "Unable to close statement; " + x;
					logger.warning(msg);
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException x) {
					String msg = "Unable to close result set; " + x;
					logger.warning(msg);
				}
			}
		}
		logger.fine("DEBUG " + "...readTableSizes");
		return retVal;
	}

	private int getTableSize(Map<DbTable, Integer> tableSizes, IDbTable dbt) {
		int retVal = 0;
		Integer s = (Integer) tableSizes.get(dbt);
		if (s == null) {
			String msg =
				"Table size is null. Have ABA statistics been computed?";
			logger.severe(msg);
			// throw new IllegalStateException(msg);
		} else {
			retVal = s.intValue();
			if (retVal == 0) {
				String msg =
					"Table size is zero. Have ABA statistics been computed?";
				logger.severe(msg);
				// throw new IllegalStateException(msg);
			} else if (retVal < 0) {
				String msg = "negative ABA table size: " + retVal;
				logger.severe(msg);
				throw new IllegalStateException(msg);
			} else {
				String msg = "ABA table size: " + retVal + " " + dbt.toString();
				logger.fine(msg);
			}
		}
		return retVal;
	}

	private CountField find(List<CountField> countFields, IDbField dbf) {
		String column = dbf.getName();
		String view = dbf.getTable().getName();
		String uniqueId = dbf.getTable().getUniqueId();
		Iterator<CountField> iCountFields = countFields.iterator();
		while (iCountFields.hasNext()) {
			CountField cf = (CountField) iCountFields.next();
			if (column.equals(cf.getColumn()) && view.equals(cf.getView())
					&& uniqueId.equals(cf.getUniqueId())) {
				return cf;
			}
		}
		return null;
	}

	private static IBlockingConfiguration[] getBlockingConfigurations(
			ImmutableProbabilityModel[] models) {
		final Set<String> bcClassNames = new HashSet<>();
		final List<IBlockingConfiguration> ibcs0 = new ArrayList<>();
		for (ImmutableProbabilityModel model : models) {
			final DbAccessor dbAccessor = (DbAccessor) model.getAccessor();
			String[] dbcNames = dbAccessor.getDbConfigurations();
			for (String dn : dbcNames) {
				final BlockingAccessor bAccessor =
					(BlockingAccessor) model.getAccessor();
				final String[] bcNames = bAccessor.getBlockingConfigurations();
				for (String bcName : bcNames) {
					IBlockingConfiguration ibc =
						bAccessor.getBlockingConfiguration(bcName, dn);
					String bcClassName = ibc.getClass().getName();
					if (!bcClassNames.contains(bcClassName)) {
						ibcs0.add(ibc);
					}
				}
			}
		}
		final int bcCount = ibcs0.size();
		IBlockingConfiguration[] res =
			ibcs0.toArray(new IBlockingConfiguration[bcCount]);
		return res;
	}

}
