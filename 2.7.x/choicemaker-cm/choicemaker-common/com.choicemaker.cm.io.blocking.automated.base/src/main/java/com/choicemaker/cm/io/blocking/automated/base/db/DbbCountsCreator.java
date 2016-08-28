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
		logger.info("DEBUG " + METHOD + "entering");

		Connection connection = null;
		try {
			connection = ds.getConnection();
			setConfigFields(connection);
			setMainFields(connection);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e1) {
					logger.severe(METHOD + e1.toString());
				}
			}
		}
		logger.info("DEBUG " + METHOD + "exiting");
	}

	/**
	 * Installs meta-data into the TB_CMT_COUNT_CONFIG_FIELDS table.
	 * @param connection a non-null connection
	 * @throws SQLException
	 */
	private void setConfigFields(final Connection connection)
			throws SQLException {
		final String METHOD = "DbbCountsCreator.setConfigFields: ";
		assert connection != null;
		logger.info("DEBUG " + METHOD + "entering");
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			ImmutableProbabilityModel[] models = PMManager.getModels();
			if (logger.isLoggable(Level.FINE)) {
				if (models == null) {
					logger.warning(METHOD + "null models");
				} else {
					final int mcount = models == null ? 0 : models.length;
					logger.info("DEBUG " + "model count: " + mcount);
					for (ImmutableProbabilityModel m : models) {
						logger.info("DEBUG " + METHOD + m.getModelName());
					}
				}
			}
			IBlockingConfiguration[] bcs = getBlockingConfigurations(models);
			if (logger.isLoggable(Level.FINE)) {
				if (bcs == null) {
					logger.warning(METHOD + "null bcs");
				} else {
					final int bcount = bcs == null ? 0 : bcs.length;
					logger.info("DEBUG " + "blocking config count: " + bcount);
				}
			}
			for (int i = 0; i < bcs.length; ++i) {
				IBlockingConfiguration bc = bcs[i];
				logger.info("DEBUG " + METHOD + bc.getName());
				String name = bc.getName();
				String query =
					"DELETE FROM TB_CMT_COUNT_CONFIG_FIELDS WHERE config = \'"
							+ name + "\'";
				logger.info("DEBUG " + query);
				stmt.execute(query);
				for (int j = 0; j < bc.getDbFields().length; ++j) {
					IDbField df = bc.getDbFields()[j];
					query = "INSERT INTO TB_CMT_COUNT_CONFIG_FIELDS VALUES("
							+ "'" + name + "'," + "'" + df.getTable().getName()
							+ "'," + "'" + df.getName() + "'," + "'"
							+ df.getTable().getUniqueId() + "',"
							+ df.getDefaultCount() + ")";
					logger.info("DEBUG " + query);
					stmt.execute(query);
				}
				for (int j = 0; j < bc.getDbTables().length; ++j) {
					IDbTable dt = bc.getDbTables()[j];
					query = "INSERT INTO TB_CMT_COUNT_CONFIG_FIELDS VALUES("
							+ "'" + name + "'," + "'" + dt.getName() + "',"
							+ "null," + "'" + dt.getUniqueId() + "'," + "null)";
					logger.info("DEBUG " + query);
					stmt.execute(query);
				}
			}
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					logger.severe(METHOD + e.toString());
				}
			}
		}
		logger.info("DEBUG " + METHOD + "exiting");
	}

	/**
	 * Installs meta-data into the TB_CMT_COUNT_FIELDS table.
	 * @param connection a non-null connection
	 * @throws SQLException
	 */
	private void setMainFields(final Connection connection)
			throws SQLException {
		final String METHOD = "DbbCountsCreator.setMainFields: ";
		assert connection != null;
		logger.info("DEBUG " + METHOD + "entering");
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			int maxId = -1;
			String query = "SELECT MAX(FieldId) FROM TB_CMT_COUNT_FIELDS";
			logger.info("DEBUG " + query);
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
			logger.info("DEBUG " + query);
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
				logger.info("DEBUG " + query);
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
			logger.info("DEBUG " + query);
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
				logger.info("DEBUG " + query);
				stmt.execute(query);
			}
			query = "DELETE FROM TB_CMT_COUNTS WHERE fieldId NOT IN ("
					+ "SELECT fieldId FROM TB_CMT_COUNT_FIELDS f, "
					+ "TB_CMT_COUNT_CONFIG_FIELDS k "
					+ "WHERE f.ViewName = k.ViewName AND ("
					+ "(f.ColumnName IS NULL AND k.ColumnName IS NULL) OR "
					+ "(f.ColumnName = k.ColumnName)" + ") )";
			logger.info("DEBUG " + query);
			stmt.execute(query);
			query = "DELETE FROM TB_CMT_COUNT_FIELDS "
					+ "WHERE ColumnName IS NOT NULL AND  NOT EXISTS ("
					+ "SELECT * FROM TB_CMT_COUNT_CONFIG_FIELDS t2 "
					+ "WHERE TB_CMT_COUNT_FIELDS.ViewName = t2.ViewName AND "
					+ "TB_CMT_COUNT_FIELDS.ColumnName = t2.ColumnName AND "
					+ "TB_CMT_COUNT_FIELDS.MasterId = t2.MasterId)";
			logger.info("DEBUG " + query);
			stmt.execute(query);
			query =
				"DELETE FROM TB_CMT_COUNT_FIELDS WHERE ColumnName IS NULL AND "
						+ "NOT EXISTS ("
						+ "SELECT * FROM TB_CMT_COUNT_CONFIG_FIELDS t2 "
						+ "WHERE TB_CMT_COUNT_FIELDS.ViewName = t2.ViewName "
						+ "AND TB_CMT_COUNT_FIELDS.MasterId = t2.MasterId "
						+ "AND t2.ColumnName IS NULL)";
			logger.info("DEBUG " + query);
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

		logger.info("DEBUG " + METHOD + "exiting");
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
	public void computeAbaStatistics(DataSource ds, DatabaseAbstraction databaseAbstraction,
			boolean onlyUncomputed) throws SQLException {
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
	public void computeAbaStatistics(DataSource ds, DatabaseAbstraction databaseAbstraction,
			boolean onlyUncomputed, boolean commitChanges)
			throws SQLException {
		final String METHOD = "DbbCountsCreator.create: ";
		if (ds == null) {
			throw new IllegalArgumentException(METHOD + "null data source");
		}
		if (databaseAbstraction == null) {
			throw new IllegalArgumentException(
					METHOD + "null database abstraction");
		}
		logger.info("DEBUG " + METHOD + "entering");

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
			logger.info("DEBUG " + delete);
			_latest_query = delete;
			stmt.execute(delete);
			logger.info("DEBUG " + query);
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
					logger.info("DEBUG " + query);
					_latest_query = query;
					stmt.execute(query);

				} else {
					// field
					query =
						"SELECT " + column + " FROM " + table + " WHERE 0 = 1";
					logger.info("DEBUG " + query);
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
					logger.info("DEBUG " + query);
					_latest_query = query;
					stmt.execute(query);
				}

				query = "UPDATE TB_CMT_COUNT_FIELDS SET LastUpdate = "
						+ databaseAbstraction.getSysdateExpression()
						+ " WHERE FieldId = " + fieldId;
				logger.info("DEBUG " + query);
				_latest_query = query;
				stmt.execute(query);

				if (commitChanges) {
					logger.info(METHOD + "commiting ABA statistics to DB");
					connection.commit();
				} else {
					String msg = "skipping commit of ABA statistics to DB "
							+ "-- assuming a managed connection will "
							+ "automagically commit them instead.";
					logger.info(METHOD + msg);
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
		logger.info("DEBUG " + METHOD + "entering");

		// BUG 2009-08-21 rphall
		// The "models" instance data can be null (because of
		// a flawed constructor) and if so, this method fails quietly
		ImmutableProbabilityModel[] models = PMManager.getModels();
		if (models != null) {

			Connection connection = null;
			Statement stmt = null;
			try {
				connection = ds.getConnection();

				stmt = connection.createStatement();
				List<CountField> countFields = new ArrayList<>();
				Map<DbTable, Integer> tableSizes = readTableSizes(stmt);

				IBlockingConfiguration[] blockingConfigurations =
					getBlockingConfigurations(models);
				AbaStatisticsImpl[] ccs =
					new AbaStatisticsImpl[blockingConfigurations.length];
				for (int i = 0; i < blockingConfigurations.length; ++i) {
					IBlockingConfiguration bc = blockingConfigurations[i];
					ICountField[] bcCountFields =
						new ICountField[bc.getDbFields().length];
					for (int j = 0; j < bc.getDbFields().length; ++j) {
						IDbField dbf = bc.getDbFields()[j];
						CountField f = find(countFields, dbf);
						if (f == null) { // read in
							String column = dbf.getName();
							String view = dbf.getTable().getName();
							String uniqueId = dbf.getTable().getUniqueId();
							int tableSize =
								getTableSize(tableSizes, dbf.getTable());
							f = new CountField(100, dbf.getDefaultCount(),
									tableSize, column, view, uniqueId);
							countFields.add(f);

							String query =
								"SELECT FieldId FROM TB_CMT_COUNT_FIELDS "
										+ "WHERE ViewName = '" + view
										+ "' AND ColumnName = '" + column
										+ "' AND MasterId = '" + uniqueId + "'";
							logger.info("DEBUG " + query);
							ResultSet rs = stmt.executeQuery(query);
							if (rs.next()) {
								int fieldId = rs.getInt(1);
								rs.close();

								query =
									"SELECT Value, Count FROM TB_CMT_COUNTS "
											+ "WHERE FieldId = " + fieldId;
								logger.info("DEBUG " + query);
								rs = stmt.executeQuery(query);
								while (rs.next()) {
									String value = rs.getString(1);
									Integer count =
										CountField.valueOf(rs.getInt(2));
									f.putValueCount(value, count);
								}
								rs.close();

							} else {
								rs.close();
							}
						}
						bcCountFields[j] = f;
					}
					ccs[i] = new AbaStatisticsImpl(
							getTableSize(tableSizes, bc.getDbTables()[0]),
							bcCountFields);
				}
				for (ImmutableProbabilityModel model : models) {
					final DbAccessor dbAccessor =
						(DbAccessor) model.getAccessor();
					String[] dbcNames = dbAccessor.getDbConfigurations();
					for (String dn : dbcNames) {
						final BlockingAccessor bAccessor =
							(BlockingAccessor) model.getAccessor();
						final String[] bcNames =
							bAccessor.getBlockingConfigurations();
						for (String bcName : bcNames) {
							logger.info(
									"DEBUG " + "Using blocking configuration: "
											+ bcName);
							IBlockingConfiguration bc =
								bAccessor.getBlockingConfiguration(bcName, dn);
							final String bcClassName = bc.getClass().getName();

							// This would be simpler if the collection of
							// AbaStatisticsImpl instances were not an array,
							// but rather a Map. The key could remain as
							// className, but it would be more resilient and
							// less dependent on implementation if the key were
							// a concatenation of the names of a model and
							// a blocking configuration.
							int j = 0;
							while (!blockingConfigurations[j].getClass()
									.getName().equals(bcClassName)) {
								++j;
							}

							// DEPRECATED 2014-11-18 rphall
							// model.properties().put("countSource", ccs[j]);
							// END DEPRECATED
							cache.putStatistics(model, ccs[j]);
						}
					}
				}
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
	}

	private Map<DbTable, Integer> readTableSizes(Statement stmt)
			throws SQLException {
		logger.info("DEBUG " + "readTableSizes...");
		Map<DbTable, Integer> l = new HashMap<>();
		String query = "SELECT ViewName, MasterId, Count "
				+ "FROM TB_CMT_COUNT_FIELDS f, TB_CMT_COUNTS c "
				+ "WHERE f.FieldId = c.FieldId AND f.ColumnName IS NULL";
		logger.info("DEBUG " + query);
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			l.put(new DbTable(rs.getString(1), 0, rs.getString(2)),
					new Integer(Math.max(1, rs.getInt(3))));
		}
		if (l.size() == 0) {
			String msg =
				"Required views for automated blocking were not found. "
						+ "Automated blocking will not work without them. "
						+ "Use CM-Analyzer to produce a script that will "
						+ "create them, then run the script to add them "
						+ "to the database.";
			logger.warning(msg);
		}
		logger.info("DEBUG " + "...readTableSizes");
		return l;
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
