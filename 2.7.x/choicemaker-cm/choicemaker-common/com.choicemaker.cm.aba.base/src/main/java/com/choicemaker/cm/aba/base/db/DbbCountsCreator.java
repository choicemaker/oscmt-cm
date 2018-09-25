/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.aba.base.db;

import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.INVALID_JDBC_TYPE;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.bindSqlDeleteCountConfigFields;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.bindSqlInsertFieldIntoCountConfigFields;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.bindSqlInsertTableIntoCountConfigFields;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.closeConnection;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.closeStatement;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.deleteCounts;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.getColumnType;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.insertFieldCounts;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.insertMissingFieldsCountFields;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.insertMissingTablesCountFields;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.insertTableSize;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.instance21;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.query10;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.query11;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.query12;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.selectEntriesCountFields;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.selectFieldId;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.selectMaxFieldIdCountFields;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.selectMissingFieldsCountFields;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.selectMissingTablesCountFields;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.selectTableSizes;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.setDateFormat;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.sqlDeleteCountConfigFields;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.sqlInsertFieldIntoCountConfigFields;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.sqlInsertTableIntoCountConfigFields;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.sqlSelectFieldIdCountFields;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.sqlSelectValueCount;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.updateCounts;
import static com.choicemaker.cm.aba.base.db.DbbCountsCreatorSQL.updateTimeStampsCountFields;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.choicemaker.cm.aba.AbaStatisticsCache;
import com.choicemaker.cm.aba.BlockingAccessor;
import com.choicemaker.cm.aba.IBlockingConfiguration;
import com.choicemaker.cm.aba.IDbField;
import com.choicemaker.cm.aba.IDbTable;
import com.choicemaker.cm.aba.IFieldValueCounts;
import com.choicemaker.cm.aba.base.DbTable;
import com.choicemaker.cm.aba.base.FieldValueCounts;
import com.choicemaker.cm.aba.cachecount.AbaStatisticsImpl;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.base.PMManager;
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
 * but this data is also duplicated in other meta-data tables. The TB_CMT_CONFIG
 * table is populated by scripts generated from model schemas. This table is not
 * referenced in Java code, only in Oracle stored procedures, such as the body
 * of the Blocking procedure in the CMTBlocking package.<br/>
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
 * value in the field for a particular blocking configuration</li>
 * </ol>
 * <br/>
 * </li>
 * <li>TB_CMT_COUNT_FIELDS: a denormalized table that defines an alternative
 * key, FieldId, that is an integer value unique to each table or table-field
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
 * value in the field <em><strong>for any blocking configuration</strong></em>.
 * This column is essentially MIN(TB_CMT_COUNT_CONFIG_FIELDS.MINCOUNT) computed
 * across all blocking configurations for a given field. If an entry refers to a
 * table, this value will be null.</li>
 * <li>LASTUPDATE the last time statistics were updated for the fields</li>
 * </ol>
 * <br/>
 * </li>
 * </ul>
 * <p>
 * <p>
 * ABA statistics are maintained in the TB_CMT_COUNTS table.
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
 * @author mbuechi (Original design and implementation)
 * @author rphall (Documentation and refactoring)
 */
public class DbbCountsCreator {

	static Logger logger = Logger.getLogger(DbbCountsCreator.class.getName());

//	private static final DbbCountsCreator instance = new DbbCountsCreator();
//
//	public static DbbCountsCreator getInstance() {
//		return instance;
//	}

	static FieldValueCounts find(List<FieldValueCounts> countFields,
			IDbField dbf) {
		String column = dbf.getName();
		String view = dbf.getTable().getName();
		String uniqueId = dbf.getTable().getUniqueId();
		Iterator<FieldValueCounts> iCountFields = countFields.iterator();
		while (iCountFields.hasNext()) {
			FieldValueCounts cf = (FieldValueCounts) iCountFields.next();
			if (column.equals(cf.getColumn()) && view.equals(cf.getView())
					&& uniqueId.equals(cf.getUniqueId())) {
				return cf;
			}
		}
		return null;
	}

	static IBlockingConfiguration[] getBlockingConfigurations(
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

	static int getTableSize(Map<DbTable, Integer> tableSizes, IDbTable dbt) {
		int retVal = 0;
		Integer s = (Integer) tableSizes.get(dbt);
		if (s == null) {
			String msg =
				"Table size is null. Have ABA statistics been computed?";
			logger.severe(msg);
		} else {
			retVal = s.intValue();
			if (retVal == 0) {
				String msg =
					"Table size is zero. Have ABA statistics been computed?";
				logger.severe(msg);
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

	/**
	 * Installs meta-data into the TB_CMT_COUNT_CONFIG_FIELDS table.
	 *
	 * @param connection
	 *            a non-null connection
	 * @throws SQLException
	 */
	static void installCountConfigFieldsMetaData(final Connection connection)
			throws SQLException {
		final String METHOD = "DbbCountsCreator.setConfigFields: ";
		logger.fine(METHOD + "entering");
		assert connection != null;
		PreparedStatement stmt1 = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		try {
			// Get the registered models and blocking configurations
			ImmutableProbabilityModel[] models = PMManager.getModels();
			logProbabilityModels(models);
			if (models == null) {
				throw new IllegalStateException("null models");
			}
			IBlockingConfiguration[] bcs = getBlockingConfigurations(models);
			logBlockingConfiguration(bcs);

			// Prepare SQL statements for a loop over blocking configurations
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

			// Loop over the blocking configurations
			for (IBlockingConfiguration bc : bcs) {
				final String bcName = bc.getBlockingConfiguationId();
				logger.fine("Blocking configuration: " + bcName);

				// For each blocking configuration, delete all tables and rows
				// from TB_CMT_COUNT_CONFIG_FIELDS
				bindSqlDeleteCountConfigFields(stmt1, bcName);
				int rowsDeleted = stmt1.executeUpdate();
				logger.fine("TB_CMT_COUNT_CONFIG_FIELDS rows deleted: "
						+ rowsDeleted);

				// For each blocking configuration, insert fields
				int fieldsInserted = 0;
				for (IDbField df : bc.getDbFields()) {
					bindSqlInsertFieldIntoCountConfigFields(stmt2, bcName, df);
					fieldsInserted += stmt2.executeUpdate();
				}
				logger.fine("TB_CMT_COUNT_CONFIG_FIELDS fields inserted: "
						+ fieldsInserted);

				// For each blocking configuration, insert tables
				int tablesInserted = 0;
				for (IDbTable dt : bc.getDbTables()) {
					bindSqlInsertTableIntoCountConfigFields(stmt3, bcName, dt);
					tablesInserted += stmt3.executeUpdate();
				}
				logger.fine("TB_CMT_COUNT_CONFIG_FIELDS tables inserted: "
						+ tablesInserted);
			}
		} finally {
			closeStatement(stmt1);
			closeStatement(stmt2);
			closeStatement(stmt3);
		}
		logger.fine(METHOD + "exiting");
	}

	/**
	 * Installs meta-data into the TB_CMT_COUNT_FIELDS table.
	 *
	 * @param connection
	 *            a non-null connection
	 * @throws SQLException
	 */
	static void installCountFieldsMetaData(final Connection connection)
			throws SQLException {
		final String METHOD = "DbbCountsCreator.setMainFields: ";
		logger.fine(METHOD + "entering");
		assert connection != null;
		Statement stmt = null;
		try {
			ResultSet rs = null;
			stmt = connection.createStatement();

			// Find the largest fieldId that's been defined in the
			// TB_CMT_COUNT_FIELDS table
			int maxFieldId = selectMaxFieldIdCountFields(stmt);
			logger.info("TB_CMT_COUNT_FIELDS max fieldId: " + maxFieldId);

			// Select fields from TB_CMT_COUNT_CONFIG_FIELDS that
			// are missing from TB_CMT_COUNT_FIELDS and insert them
			List<String[]> missingFieldEntries =
				selectMissingFieldsCountFields(connection);
			int inserted = insertMissingFieldsCountFields(connection,
					maxFieldId, missingFieldEntries);
			maxFieldId += inserted;
			logger.info("TB_CMT_COUNT_FIELDS max fieldId: " + maxFieldId);

			// Select tables from the TB_CMT_COUNT_CONFIG_FIELDS table that
			// are missing from TB_CMT_COUNT_FIELDS and insert them
			List<String[]> missingTableEntries =
				selectMissingTablesCountFields(connection);
			inserted = insertMissingTablesCountFields(connection, maxFieldId,
					missingTableEntries);
			maxFieldId += inserted;
			logger.info("TB_CMT_COUNT_FIELDS max fieldId: " + maxFieldId);

			// Remove entries from TB_CMT_COUNTS where the entry represents
			// a field or table that isn't in TB_CMT_COUNT_CONFIG_FIELDS
			logger.info("SQL to obsolete entries in TB_CMT_COUNTS: " + query10);
			int countsRemoved = stmt.executeUpdate(query10);
			logger.info("Obsolete entries removed from TB_CMT_COUNTS"
					+ countsRemoved);

			// Remove entries from the TB_CMT_COUNT_FIELDS where the entry
			// represents a field that isn't in TB_CMT_COUNT_CONFIG_FIELDS
			logger.info("SQL to obsolete fields in TB_CMT_COUNT_FIELDS: "
					+ query11);
			int fieldsRemoved = stmt.executeUpdate(query11);
			logger.info("Obsolete entries removed from TB_CMT_COUNTS"
					+ fieldsRemoved);

			// Remove entries from the TB_CMT_COUNT_FIELDS where the entry
			// represents a table that isn't in TB_CMT_COUNT_CONFIG_FIELDS
			logger.info("SQL to obsolete tables in TB_CMT_COUNT_FIELDS: "
					+ query12);
			int tablesRemoved = stmt.executeUpdate(query12);
			logger.info("Obsolete entries removed from TB_CMT_COUNTS"
					+ tablesRemoved);

			assert rs == null;

		} finally {
			closeStatement(stmt);
		}
		logger.fine(METHOD + "exiting");
	}

	static void logBlockingConfiguration(IBlockingConfiguration[] bcs) {
		if (logger.isLoggable(Level.FINE)) {
			if (bcs == null) {
				logger.warning("null blocking configurations");
			} else {
				final int bcount = bcs == null ? 0 : bcs.length;
				logger.fine("blocking config count: " + bcount);
			}
		}
	}

	static void logProbabilityModels(ImmutableProbabilityModel[] models) {
		if (logger.isLoggable(Level.FINE)) {
			if (models == null) {
				logger.severe("null models");
			} else {
				final int mcount = models == null ? 0 : models.length;
				logger.info("Model configurations: " + mcount);
				for (ImmutableProbabilityModel m : models) {
					logger.fine("Model configuration: " + m.getModelName());
				}
			}
		}
	}

	static FieldValueCounts readFieldValueCounts(PreparedStatement stmt1,
			PreparedStatement stmt2, Map<DbTable, Integer> tableSizes,
			IDbField dbf) throws SQLException {
		final String column = dbf.getName();
		final String view = dbf.getTable().getName();
		final String uniqueId = dbf.getTable().getUniqueId();
		final int tableSize = getTableSize(tableSizes, dbf.getTable());

		FieldValueCounts retVal = new FieldValueCounts(100,
				dbf.getDefaultCount(), tableSize, column, view, uniqueId);

		int fieldId = selectFieldId(stmt1, view, column, uniqueId);
		if (fieldId > 0) {
			updateCounts(stmt2, retVal, fieldId);
		}

		return retVal;
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
		logger.fine(METHOD + "entering");
		if (ds == null) {
			throw new IllegalArgumentException(METHOD + "null data source");
		}
		if (databaseAbstraction == null) {
			throw new IllegalArgumentException(
					METHOD + "null database abstraction");
		}

		Connection connection = null;
		Statement stmt = null;
		PreparedStatement stmt1 = null;
		try {
			connection = ds.getConnection();

			// Set the date format that will be used
			setDateFormat(connection, databaseAbstraction);

			// Delete entries from TB_CMT_COUNTS
			int rowsDeleted = deleteCounts(connection, onlyUncomputed);
			logger.info("TB_CMT_COUNTS deleted: " + rowsDeleted);

			// Select entries from TB_CMT_COUNT_FIELDS
			List<String[]> countsToCompute =
				selectEntriesCountFields(connection, onlyUncomputed);
			assert countsToCompute != null;
			final int computationTotal = countsToCompute.size();
			logger.info("Fields to be computed: " + computationTotal);

			// Prepare SQL statement for a loop over counts
			final String sysdate = databaseAbstraction.getSysdateExpression();
			final String sqlUpdateTimeStamps = instance21(sysdate);
			logger.info("SQL to update timestamps in TB_CMT_COUNTS: "
					+ sqlUpdateTimeStamps);
			stmt1 = connection.prepareStatement(sqlUpdateTimeStamps);

			// Compute counts and update tables
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
					logger.info("Table: " + table);

					// Add a table size to TB_CMT_COUNTS
					int rowsInserted =
						insertTableSize(connection, fieldId, table, uniqueId);
					logger.fine("TB_CMT_COUNTS rows inserted: " + rowsInserted);

				} else {
					final String TABLE_VIEW_INFO =
						"[table:" + table + "], [view:" + column + "]";
					logger.info("Updating: " + TABLE_VIEW_INFO);

					// Determine the type of a field
					int columnType = getColumnType(connection, column, table);
					assert columnType != INVALID_JDBC_TYPE;
					logger.fine(
							TABLE_VIEW_INFO + " column type: " + columnType);

					int fieldsInserted = insertFieldCounts(connection, fieldId,
							column, table, uniqueId, minCount, columnType);
					logger.info(TABLE_VIEW_INFO + " fields inserted: "
							+ fieldsInserted);

					int timestampCount =
						updateTimeStampsCountFields(stmt1, sysdate, fieldId);
					logger.info(TABLE_VIEW_INFO + " timestamps inserted: "
							+ timestampCount);

					// Optionally commit changes to the DB
					if (commitChanges) {
						logger.info(
								TABLE_VIEW_INFO + " commiting ABA statistics");
						connection.commit();
					} else {
						String msg = TABLE_VIEW_INFO
								+ " skipping commit of ABA statistics "
								+ "-- assuming a managed connection will "
								+ "automagically commit them instead.";
						logger.finer(METHOD + msg);
					}
				}
			}
		} catch (SQLException e) {
			String msg =
				METHOD + "Unable to create ABA statistics: " + e.toString();
			logger.severe(msg);
			throw e;

		} finally {
			closeStatement(stmt);
			stmt = null;
			closeStatement(stmt1);
			stmt1 = null;
			closeConnection(connection);
			connection = null;
		}
	}

	/**
	 * Installs ABA statistical meta-data into a database.
	 *
	 * @param ds
	 *            a non-null connection to the database
	 * @throws SQLException
	 */
	public void installAbaMetaData(DataSource ds) throws SQLException {
		final String METHOD = "DbbCountsCreator.install: ";
		logger.fine(METHOD + "entering");
		if (ds == null) {
			throw new IllegalArgumentException("null data source");
		}

		Connection connection = null;
		try {
			connection = ds.getConnection();
			installCountConfigFieldsMetaData(connection);
			installCountFieldsMetaData(connection);
		} finally {
			closeConnection(connection);
			connection = null;
		}
		logger.fine(METHOD + "exiting");
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
		logger.fine(METHOD + "entering");
		if (ds == null) {
			throw new IllegalArgumentException(METHOD + "null data source");
		}
		if (cache == null) {
			throw new IllegalArgumentException(
					METHOD + "null statistics cache");
		}

		ImmutableProbabilityModel[] models = PMManager.getModels();
		if (models == null) {
			String msg = "No models: statistics can not be updated";
			throw new IllegalStateException(msg);
		}

		Connection connection = null;
		PreparedStatement stmt1 = null;
		PreparedStatement stmt2 = null;
		try {
			connection = ds.getConnection();
			logger.info("SQL to select fieldId from TB_CMT_COUNT_FIELDS: "
					+ sqlSelectFieldIdCountFields);
			stmt1 = connection.prepareStatement(sqlSelectFieldIdCountFields);
			logger.info("SQL to select field, value, count from TB_CMT_COUNTS: "
					+ sqlSelectValueCount);
			stmt2 = connection.prepareStatement(sqlSelectValueCount);

			Map<DbTable, Integer> tableSizes = selectTableSizes(connection);
			IBlockingConfiguration[] blockingConfigurations =
				getBlockingConfigurations(models);

			List<FieldValueCounts> countFields = new ArrayList<>();
			for (IBlockingConfiguration bc : blockingConfigurations) {
				IFieldValueCounts[] bcCountFields =
					new IFieldValueCounts[bc.getDbFields().length];
				for (int j = 0; j < bc.getDbFields().length; ++j) {

					IDbField dbf = bc.getDbFields()[j];
					FieldValueCounts f = find(countFields, dbf);
					if (f == null) {
						f = readFieldValueCounts(stmt1, stmt2, tableSizes, dbf);
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
			closeStatement(stmt1);
			closeStatement(stmt2);
			closeConnection(connection);
		}
		logger.fine(METHOD + "exiting");
	}

}
