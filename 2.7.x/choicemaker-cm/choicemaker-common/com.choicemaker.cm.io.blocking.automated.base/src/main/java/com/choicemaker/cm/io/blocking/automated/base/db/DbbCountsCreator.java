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

	/**
	 * Delete all rows from the TB_CMT_COUNT_CONFIG_FIELDS table that correspond
	 * to a specific blocking configuration
	 */
	public static final String sqlDeleteCountConfigFields =
		"DELETE FROM TB_CMT_COUNT_CONFIG_FIELDS WHERE config = ?";

	/**
	 * Debug message for binding parameters to
	 * {@link #sqlDeleteCountConfigFields}
	 */
	public static String msgBindSqlDeleteCountConfigFields(String config) {
		StringBuilder sb = new StringBuilder().append("BIND ");
		sb.append(sqlDeleteCountConfigFields).append(": ");
		sb.append("[config:").append(config).append("]");
		String retVal = sb.toString();
		return retVal;
	}

	/**
	 * Insert a field entry into TB_CMT_COUNT_CONFIG_FIELDS for the specified
	 * parameters.
	 */
	public static final String sqlInsertFieldIntoCountConfigFields =
		"INSERT INTO TB_CMT_COUNT_CONFIG_FIELDS("
				+ "CONFIG,VIEWNAME,COLUMNNAME,MASTERID,MINCOUNT) "
				+ "VALUES(?, ?, ?, ?, ?)";

	/**
	 * Debug message for binding parameters to
	 * {@link #sqlInsertFieldIntoCountConfigFields}
	 */
	public static String msgBindSqlInsertFieldIntoCountConfigFields(
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

	/**
	 * Insert a table entry into TB_CMT_COUNT_CONFIG_FIELDS for the specified
	 * parameters.
	 */
	public static final String sqlInsertTableIntoCountConfigFields =
		"INSERT INTO TB_CMT_COUNT_CONFIG_FIELDS VALUES(?,?,null,?,null)";

	/**
	 * Debug message for binding parameters to
	 * {@link #sqlInsertTableIntoCountConfigFields}
	 */
	public static String msgBindSqlInsertTableIntoCountConfigFields(
			String config, String view, String masterId) {
		StringBuilder sb = new StringBuilder().append("BIND ");
		sb.append(sqlInsertTableIntoCountConfigFields).append(": ");
		sb.append("[config:").append(config).append("], ");
		sb.append("[view:").append(view).append("], ");
		sb.append("[masterId:").append(masterId).append("]");
		String retVal = sb.toString();
		return retVal;
	}

	/** Find the largest fieldId defined in the TB_CMT_COUNT_FIELDS table */
	public static final String query5 =
		"SELECT MAX(FieldId) FROM TB_CMT_COUNT_FIELDS";

	/**
	 * Find fields in TB_CMT_COUNT_CONFIG_FIELDS that are not defined in
	 * TB_CMT_COUNT_FIELDS
	 */
	public static final String query6 =
		"SELECT ViewName, ColumnName, MasterId, MIN(MinCount) "
				+ "FROM TB_CMT_COUNT_CONFIG_FIELDS t1 "
				+ "WHERE ColumnName IS NOT NULL AND NOT EXISTS ("
				+ "SELECT * FROM TB_CMT_COUNT_FIELDS t2 "
				+ "WHERE t1.ViewName = t2.ViewName AND "
				+ "t1.ColumnName = t2.ColumnName AND "
				+ "t1.MasterId = t2.MasterId) "
				+ "GROUP BY ViewName, ColumnName, MasterId";

	/** Create an entry for a field in TB_CMT_COUNT_FIELDS */
	public static final String query7 = "INSERT INTO TB_CMT_COUNT_FIELDS("
			+ "FIELDID, VIEWNAME, COLUMNNAME, MASTERID, MINCOUNT, LASTUPDATE) "
			+ "VALUES(?, ?, ?, ?, ?, null)";

	/** Debug message for binding parameters to {@link #query7} */
	public static String msgBindQuery7(int fieldId, String view, String column,
			String masterId, int minCount) {
		StringBuilder sb = new StringBuilder().append("BIND ");
		sb.append(query7).append(": ");
		sb.append("[fieldId:").append(fieldId).append("], ");
		sb.append("[view:").append(view).append("], ");
		sb.append("[column:").append(column).append("], ");
		sb.append("[masterId:").append(masterId).append("], ");
		sb.append("[minCount:").append(minCount).append("], ");
		String retVal = sb.toString();
		return retVal;
	}

	/**
	 * Find tables in TB_CMT_COUNT_CONFIG_FIELDS that are not defined in
	 * TB_CMT_COUNT_FIELDS
	 */
	public static final String query8 = "SELECT DISTINCT ViewName, MasterId "
			+ "FROM TB_CMT_COUNT_CONFIG_FIELDS t1 "
			+ "WHERE ColumnName IS NULL AND NOT EXISTS "
			+ "(SELECT * FROM TB_CMT_COUNT_FIELDS t2 "
			+ "WHERE t1.ViewName = t2.ViewName AND t2.ColumnName IS NULL "
			+ "AND t1.MasterId = t2.MasterId)";

	/** Create an entry for a table in TB_CMT_COUNT_FIELDS */
	public static final String query9 = "INSERT INTO TB_CMT_COUNT_FIELDS("
			+ "FIELDID, VIEWNAME, COLUMNNAME, MASTERID, MINCOUNT, LASTUPDATE) "
			+ "VALUES(?, ?, null, ?, null, null)";

	/** Debug message for binding parameters to {@link #query9} */
	public static String msgBindQuery9(int fieldId, String view,
			String masterId) {
		StringBuilder sb = new StringBuilder().append("BIND ");
		sb.append(query7).append(": ");
		sb.append("[fieldId:").append(fieldId).append("], ");
		sb.append("[view:").append(view).append("], ");
		sb.append("[masterId:").append(masterId).append("]");
		String retVal = sb.toString();
		return retVal;
	}

	/**
	 * Remove entries from TB_CMT_COUNTS that do not correspond to fields or
	 * tables defined in TB_CMT_COUNT_CONFIG_FIELDS
	 */
	public static final String query10 =
		"DELETE FROM TB_CMT_COUNTS WHERE fieldId NOT IN ("
				+ "SELECT fieldId FROM TB_CMT_COUNT_FIELDS f, "
				+ "TB_CMT_COUNT_CONFIG_FIELDS k "
				+ "WHERE f.ViewName = k.ViewName AND ("
				+ "(f.ColumnName IS NULL AND k.ColumnName IS NULL) OR "
				+ "(f.ColumnName = k.ColumnName)" + ") )";

	/**
	 * Remove entries from TB_CMT_COUNT_FIELDS that do not correspond to fields
	 * defined in TB_CMT_COUNT_CONFIG_FIELDS
	 */
	public static final String query11 = "DELETE FROM TB_CMT_COUNT_FIELDS "
			+ "WHERE ColumnName IS NOT NULL AND  NOT EXISTS ("
			+ "SELECT * FROM TB_CMT_COUNT_CONFIG_FIELDS t2 "
			+ "WHERE TB_CMT_COUNT_FIELDS.ViewName = t2.ViewName AND "
			+ "TB_CMT_COUNT_FIELDS.ColumnName = t2.ColumnName AND "
			+ "TB_CMT_COUNT_FIELDS.MasterId = t2.MasterId)";

	/**
	 * Remove entries from TB_CMT_COUNT_FIELDS that do not correspond to tables
	 * defined in TB_CMT_COUNT_CONFIG_FIELDS
	 */
	public static final String query12 =
		"DELETE FROM TB_CMT_COUNT_FIELDS WHERE ColumnName IS NULL AND "
				+ "NOT EXISTS ("
				+ "SELECT * FROM TB_CMT_COUNT_CONFIG_FIELDS t2 "
				+ "WHERE TB_CMT_COUNT_FIELDS.ViewName = t2.ViewName "
				+ "AND TB_CMT_COUNT_FIELDS.MasterId = t2.MasterId "
				+ "AND t2.ColumnName IS NULL)";

	/**
	 * Remove entries from TB_CMT_COUNTS that have be marked for re-computation
	 * in TB_CMT_COUNT_FIELDS
	 */
	public static final String query14 =
		"DELETE FROM TB_CMT_COUNTS WHERE NOT EXISTS "
				+ "(SELECT * FROM TB_CMT_COUNT_FIELDS f "
				+ "WHERE TB_CMT_COUNTS.FieldId = f.FieldId "
				+ "AND f.lastUpdate IS NOT NULL)";

	/**
	 * Select entries from TB_CMT_COUNT_FIELDS that are marked for
	 * re-computation
	 */
	public static final String query15 =
		"SELECT * FROM TB_CMT_COUNT_FIELDS WHERE LastUpdate IS NULL";

	/** Remove all entries from TB_CMT_COUNTS */
	public static final String query16 = "DELETE FROM TB_CMT_COUNTS";

	/** Select all entries from TB_CMT_COUNT_FIELDS */
	public static final String query17 = "SELECT * FROM TB_CMT_COUNT_FIELDS";

	/** Template to insert table size into TB_CMT_COUNTS */
	public static final String template18 =
		"INSERT INTO TB_CMT_COUNTS(FIELDID, VALUE, COUNT) "
				+ "SELECT $fieldId  as FIELDID, $table as VALUE, "
				+ "COUNT(DISTINCT $uniqueId) as COUNT FROM $table";

	/** Instantiation of {@link #template18} */
	public static String instance18(String fieldId, String view,
			String masterId) {
		String retVal = template18;
		retVal = retVal.replace("$fieldId", fieldId).replace("$table", view)
				.replace("$uniqueId", masterId);
		return retVal;
	}

	/** Template to determine a field type */
	public static final String template19 =
		"SELECT $column  FROM $table WHERE 0 = 1";

	/** Instantiation of {@link #template19} */
	public static String instance19(String field, String view) {
		String retVal = template19;
		retVal = retVal.replace("$column", field).replace("$table", view);
		return retVal;
	}

	/** Template to insert field statistics into TB_CMT_COUNTS */
	public static final String template20 =
		"INSERT INTO TB_CMT_COUNTS(FIELDID, VALUE, COUNT) "
				+ "SELECT $fieldId as FIELDID, $column as VALUE, "
				+ "COUNT($uniqueId) as COUNT "
				+ "FROM $table WHERE $column IS NOT NULL " + "GROUP BY $column "
				+ "HAVING COUNT($uniqueId) > $minCount";

	/** Instantiation of {@link #template20} */
	public static String instance20(String fieldId, String field, String view,
			String uniqueId, String minCount) {
		String retVal = template20;
		retVal = retVal.replace("$fieldId", fieldId).replace("$column", field)
				.replace("$table", view).replace("$uniqueId", uniqueId)
				.replace("$minCount", minCount);
		return retVal;
	}
	
	/** Update timestamps in TB_CMT_COUNT_FIELDS */
	public static final String template21 = "UPDATE TB_CMT_COUNT_FIELDS "
			+ "SET LastUpdate = $sysdate WHERE FieldId = ?";
	
	/** Instantiation of {@link #template21} */
	public static String instance21(String sysdate) {
		String retVal = template21;
		retVal = retVal.replace("$sysdate", sysdate);
		return retVal;
	}

	public static String msgBindQuery21(String sysdate, String fieldId) {
		StringBuilder sb = new StringBuilder().append("BIND ");
		sb.append(instance21(sysdate)).append(": ");
		sb.append("[fieldId:").append(fieldId).append("]");
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
					connection = null;
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
		PreparedStatement stmt1 = null;
		PreparedStatement stmt2 = null;
		try {
			ResultSet rs = null;
			stmt = connection.createStatement();

			// Find the largest fieldId that's been defined in the
			// TB_CMT_COUNT_FIELDS table
			logger.info("SQL to find max fieldId in TB_CMT_COUNT_FIELDS: "
					+ query5);
			int maxId = -1;
			try {
				assert rs == null;
				rs = stmt.executeQuery(query5);
				if (rs.next()) {
					maxId = rs.getInt(1);
				}
			} finally {
				rs.close();
				rs = null;
			}
			assert maxId >= 0;
			logger.info("max fieldId in TB_CMT_COUNT_FIELDS: " + maxId);

			// Select fields from the TB_CMT_COUNT_CONFIG_FIELDS table that
			// are not defined in the TB_CMT_COUNT_FIELDS table
			logger.info(
					"SQL to select fields missing from TB_CMT_COUNT_FIELDS: "
							+ query6);
			List<String[]> missingFieldEntries = new ArrayList<>();
			try {
				assert rs == null;
				rs = stmt.executeQuery(query6);
				while (rs.next()) {
					String[] entry = new String[4];
					for (int i = 1; i <= 4; ++i) {
						int idx = i - 1;
						entry[idx] = rs.getString(i);
					}
					missingFieldEntries.add(entry);
				}
			} finally {
				rs.close();
				rs = null;
			}

			// Create entries for missing fields in the TB_CMT_COUNT_FIELDS
			// table
			int fieldsInserted = 0;
			logger.info(
					"SQL to insert missing fields into TB_CMT_COUNT_FIELDS: "
							+ query7);
			stmt1 = connection.prepareStatement(query7);
			for (String[] entry : missingFieldEntries) {
				int fieldId = ++maxId;
				String view = entry[0];
				String column = entry[1];
				String masterId = entry[2];
				int minCount = Integer.valueOf(entry[3]);
				String msg =
					msgBindQuery7(fieldId, view, column, masterId, minCount);
				logger.fine(msg);
				stmt1.setInt(1, fieldId);
				stmt1.setString(2, view);
				stmt1.setString(3, column);
				stmt1.setString(4, masterId);
				stmt1.setInt(5, minCount);
				fieldsInserted += stmt1.executeUpdate();
			}
			logger.fine("TB_CMT_COUNT_CONFIG_FIELDS missing fields inserted: "
					+ fieldsInserted);

			// Select tables from the TB_CMT_COUNT_CONFIG_FIELDS table that
			// are not defined in the TB_CMT_COUNT_FIELDS table
			logger.info(
					"SQL to insert missing tables into TB_CMT_COUNT_FIELDS: "
							+ query8);
			stmt1 = connection.prepareStatement(query8);
			List<String[]> missingTableEntries = new ArrayList<>();
			try {
				assert rs == null;
				rs = stmt.executeQuery(query8);
				while (rs.next()) {
					String[] entry = new String[2];
					for (int i = 1; i <= 2; ++i) {
						int idx = i - 1;
						entry[idx] = rs.getString(i);
					}
					missingTableEntries.add(entry);
				}
			} finally {
				rs.close();
				rs = null;
			}

			// Create entries for missing fields in the TB_CMT_COUNT_FIELDS
			// table
			int tablesInserted = 0;
			logger.info(
					"SQL to insert missing tables into TB_CMT_COUNT_FIELDS: "
							+ query9);
			stmt2 = connection.prepareStatement(query9);
			for (String[] entry : missingFieldEntries) {
				int fieldId = ++maxId;
				String view = entry[0];
				String masterId = entry[1];
				String msg = msgBindQuery9(fieldId, view, masterId);
				logger.fine(msg);
				stmt2.setInt(1, fieldId);
				stmt2.setString(2, view);
				stmt2.setString(4, masterId);
				tablesInserted += stmt2.executeUpdate();
			}
			logger.fine("TB_CMT_COUNT_CONFIG_FIELDS missing tables inserted: "
					+ tablesInserted);

			// Remove entries from TB_CMT_COUNTS where the fieldId does
			// not correspond to some column or table defined by the
			// just updated TB_CMT_COUNT_CONFIG_FIELDS table
			logger.info("SQL to obsolete entries in TB_CMT_COUNTS: " + query10);
			int countsRemoved = stmt.executeUpdate(query10);
			logger.info("Obsolete entries removed from TB_CMT_COUNTS"
					+ countsRemoved);

			// Remove entries from the TB_CMT_COUNT_FIELDS where the entry
			// represents a field but the field does not correspond to a field
			// defined by the just updated TB_CMT_COUNT_CONFIG_FIELDS table
			logger.info("SQL to obsolete fields in TB_CMT_COUNT_FIELDS: "
					+ query11);
			int fieldsRemoved = stmt.executeUpdate(query11);
			logger.info("Obsolete entries removed from TB_CMT_COUNTS"
					+ fieldsRemoved);

			// Remove entries from the TB_CMT_COUNT_FIELDS where the entry
			// represents a table but the table does not correspond to a table
			// defined by the just updated TB_CMT_COUNT_CONFIG_FIELDS table
			logger.info("SQL to obsolete tables in TB_CMT_COUNT_FIELDS: "
					+ query12);
			int tablesRemoved = stmt.executeUpdate(query12);
			logger.info("Obsolete entries removed from TB_CMT_COUNTS"
					+ tablesRemoved);

			assert rs == null;

		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					logger.severe(METHOD + e.toString());
				}
				stmt = null;
			}
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

		Connection connection = null;
		Statement stmt = null;
		PreparedStatement stmt1 = null;
		try {
			ResultSet rs = null;
			connection = ds.getConnection();

			final String sysdate = databaseAbstraction.getSysdateExpression();
			final String sqlUpdateTimeStamps = instance21(sysdate);
			logger.info("SQL to update timestamps in TB_CMT_COUNTS: "
					+ sqlUpdateTimeStamps);
			stmt1 = connection.prepareStatement(sqlUpdateTimeStamps);

			stmt = connection.createStatement();
			Boolean returnIsResultSet = null;
			try {
				String query13 =
					databaseAbstraction.getSetDateFormatExpression();
				logger.info("SQL to set date format: " + query13);
				_latest_query = query13;
				returnIsResultSet = stmt.execute(query13);

			} finally {
				if (returnIsResultSet == null) {
					logger.warning("FAILED: " + _latest_query);
				} else if (returnIsResultSet) {
					logger.info("Date-formate SQL returned a result set");
					rs = stmt.getResultSet();
					rs.close();
					rs = null;
				} else {
					int count = stmt.getUpdateCount();
					logger.info("Date-format SQL returned a count: " + count);
				}
				assert rs == null;
				_latest_query = null;

			}

			// Delete entries from TB_CMT_COUNTS
			String delete;
			String query;
			if (onlyUncomputed) {
				delete = query14;
				query = query15;

			} else {
				delete = query16;
				query = query17;

			}
			List<String[]> countsToCompute = new ArrayList<>();
			try {
				logger.info(
						"SQL to remove entries from TB_CMT_COUNTS: " + delete);
				_latest_query = delete;
				int rowsDeleted = stmt.executeUpdate(delete);
				logger.info("Rows deleted from TB_CMT_COUNTS: " + rowsDeleted);

				logger.info("SQL to select entries from TB_CMT_COUNT_FIELDS: "
						+ query);
				_latest_query = query;
				int rowsSelected = stmt.executeUpdate(query);
				logger.info("Rows selected from TB_CMT_COUNT_FIELDS: "
						+ rowsSelected);
				assert rs == null;
				rs = stmt.executeQuery(query);
				while (rs.next()) {
					String[] entry = new String[5];
					for (int i = 1; i <= 5; ++i) {
						int idx = i - 1;
						entry[idx] = rs.getString(i);
						countsToCompute.add(entry);
					}
				}

			} finally {
				rs.close();
				rs = null;
				_latest_query = null;
				delete = null;
				query = null;

			}
			final int computationTotal = countsToCompute.size();
			logger.info("Fields to be computed: " + computationTotal);

			int currentComputation = 0;
			for (String[] countToCompute : countsToCompute) {
				++currentComputation;
				String status = String.format("Compute counts: %d/%d: ",
						currentComputation, computationTotal);
				logger.info(status);

				final String fieldId = countToCompute[0];
				final String table = countToCompute[1];
				final String column = countToCompute[2];
				final String uniqueId = countToCompute[3];
				final String minCount = countToCompute[4];

				if (column == null || column.length() == 0) {
					// Add a table size to TB_CMT_COUNTS
					query = instance18(fieldId, table, uniqueId);
					logger.info(
							"SQL to insert a table size into TB_CMT_COUNTS: "
									+ query);
					_latest_query = query;
					int rowsInserted = stmt.executeUpdate(query);
					logger.fine("TB_CMT_COUNTS rows inserted: " + rowsInserted);

				} else {
					// Determine the type of a field
					Integer columnType = null;
					try {
						query = instance19(column, table);
						logger.info("SQL to determine a field type: " + query);
						_latest_query = query;
						assert rs == null;
						rs = stmt.executeQuery(query);
						assert rs.getMetaData().getColumnCount() == 1;
						columnType = rs.getMetaData().getColumnType(1);

					} finally {
						rs.close();
						rs = null;

					}
					assert columnType != null;

					int fieldsInserted = 0;
					try {
						boolean isDate = columnType == Types.DATE
								|| columnType == Types.TIMESTAMP;
						logger.fine("Value column type: "
								+ (isDate ? "date" : "non-date"));
						query = instance20(fieldId, column, table, uniqueId,
								minCount);
						logger.info(
								"SQL to insert field counts in TB_CMT_COUNTS: "
										+ query);
						_latest_query = query;
						fieldsInserted = stmt.executeUpdate(query);
					} finally {
						assert rs == null;
						_latest_query = null;
					}
					logger.info("Fields inserted into TB_CMT_COUNTS: "
							+ fieldsInserted);

					logger.fine(msgBindQuery21(sysdate,fieldId));
					int timestampCount = 0;
					try {
						_latest_query = sqlUpdateTimeStamps;
						stmt1.setInt(1, Integer.parseInt(fieldId));
						timestampCount = stmt1.executeUpdate();
					} finally {
						assert rs == null;
						_latest_query = null;
					}
					logger.fine("TB_CMT_COUNT_FIELDS timestamps updated: " + timestampCount);

					if (commitChanges) {
						logger.info(METHOD + "commiting ABA statistics to DB");
						connection.commit();
					} else {
						String msg = "skipping commit of ABA statistics to DB "
								+ "-- assuming a managed connection will "
								+ "automagically commit them instead.";
						logger.finer(METHOD + msg);
					}
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
				stmt = null;
			}
			if (stmt1 != null) {
				try {
					stmt1.close();
				} catch (SQLException e) {
					logger.severe(METHOD + e.toString());
				}
				stmt1 = null;
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e1) {
					logger.severe(METHOD + e1.toString());
				}
				connection = null;
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
