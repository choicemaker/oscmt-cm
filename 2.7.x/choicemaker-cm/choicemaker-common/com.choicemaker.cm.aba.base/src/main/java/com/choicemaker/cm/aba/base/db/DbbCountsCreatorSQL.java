package com.choicemaker.cm.aba.base.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.choicemaker.cm.aba.IDbField;
import com.choicemaker.cm.aba.IDbTable;
import com.choicemaker.cm.aba.base.DbTable;
import com.choicemaker.cm.aba.base.FieldValueCounts;
import com.choicemaker.cm.io.db.base.DatabaseAbstraction;

/**
 * SQL refactored from {@link DbbCountsCreator}
 * 
 * @author rphall
 */
public class DbbCountsCreatorSQL {

	private static final Logger logger =
		Logger.getLogger(DbbCountsCreatorSQL.class.getName());

	/** A value not used by java.sql.Types */
	public static final int INVALID_JDBC_TYPE = Integer.MIN_VALUE;

	/**
	 * Delete all rows from the TB_CMT_COUNT_CONFIG_FIELDS table that correspond
	 * to a specific blocking configuration
	 */
	public static final String sqlDeleteCountConfigFields =
		"DELETE FROM TB_CMT_COUNT_CONFIG_FIELDS WHERE config = ?";

	/**
	 * Binds a blocking configuration name and a database field to a prepared
	 * statement for {@link #sqlDeleteCountConfigFields}
	 */
	static void bindSqlDeleteCountConfigFields(PreparedStatement stmt1,
			String bcName) throws SQLException {
		logger.fine(msgBindSqlDeleteCountConfigFields(bcName));
		stmt1.setString(1, bcName);
	}

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
	 * Binds a blocking configuration name and a database field to a prepared
	 * statement for {@link #sqlInsertFieldIntoCountConfigFields}
	 */
	static void bindSqlInsertFieldIntoCountConfigFields(PreparedStatement stmt2,
			String bcName, IDbField df) throws SQLException {
		final String view = df.getTable().getName();
		final String column = df.getName();
		final String masterId = df.getTable().getUniqueId();
		final int minCount = df.getDefaultCount();
		String msg = msgBindSqlInsertFieldIntoCountConfigFields(bcName, view,
				column, masterId, minCount);
		logger.fine(msg);
		stmt2.setString(1, bcName);
		stmt2.setString(2, view);
		stmt2.setString(3, column);
		stmt2.setString(4, masterId);
		stmt2.setInt(5, minCount);
	}

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
	 * Binds a blocking configuration name and a database field to a prepared
	 * statement for {@link #sqlInsertTableIntoCountConfigFields}
	 */
	static void bindSqlInsertTableIntoCountConfigFields(PreparedStatement stmt3,
			String bcName, IDbTable dt) throws SQLException {
		final String view = dt.getName();
		final String masterId = dt.getUniqueId();
		String msg =
			msgBindSqlInsertTableIntoCountConfigFields(bcName, view, masterId);
		logger.fine(msg);
		stmt3.setString(1, bcName);
		stmt3.setString(2, view);
		stmt3.setString(3, masterId);
	}

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
	public static final String sqlSelectMaxFieldIdCountFields =
		"SELECT MAX(FieldId) FROM TB_CMT_COUNT_FIELDS";

	static int selectMaxFieldIdCountFields(Statement stmt) throws SQLException {
		logger.info("SQL to find max fieldId in TB_CMT_COUNT_FIELDS: "
				+ sqlSelectMaxFieldIdCountFields);
		int maxId = -1;
		ResultSet rs = null;
		try {
			assert rs == null;
			rs = stmt.executeQuery(sqlSelectMaxFieldIdCountFields);
			if (rs.next()) {
				maxId = rs.getInt(1);
			}
		} finally {
			rs.close();
			rs = null;
		}
		assert maxId >= 0;
		return maxId;
	}

	/**
	 * Find fields in TB_CMT_COUNT_CONFIG_FIELDS that are not defined in
	 * TB_CMT_COUNT_FIELDS
	 */
	public static final String sqlSelectMissingFieldsCountFields =
		"SELECT ViewName, ColumnName, MasterId, MIN(MinCount) "
				+ "FROM TB_CMT_COUNT_CONFIG_FIELDS t1 "
				+ "WHERE ColumnName IS NOT NULL AND NOT EXISTS ("
				+ "SELECT * FROM TB_CMT_COUNT_FIELDS t2 "
				+ "WHERE t1.ViewName = t2.ViewName AND "
				+ "t1.ColumnName = t2.ColumnName AND "
				+ "t1.MasterId = t2.MasterId) "
				+ "GROUP BY ViewName, ColumnName, MasterId";

	/**
	 * Executes a SQL statement for {@link #sqlSelectMissingFieldsCountFields}
	 */
	static List<String[]> selectMissingFieldsCountFields(Connection connection)
			throws SQLException {
		List<String[]> retVal = new ArrayList<>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			logger.info(
					"SQL to select fields missing from TB_CMT_COUNT_FIELDS: "
							+ sqlSelectMissingFieldsCountFields);
			rs = stmt.executeQuery(sqlSelectMissingFieldsCountFields);
			while (rs.next()) {
				String[] entry = new String[4];
				for (int i = 1; i <= 4; ++i) {
					int idx = i - 1;
					entry[idx] = rs.getString(i);
				}
				retVal.add(entry);
			}
		} finally {
			closeResultSet(rs);
			rs = null;
			closeStatement(stmt);
			stmt = null;
		}
		return retVal;
	}

	/** Create an entry for a field in TB_CMT_COUNT_FIELDS */
	public static final String sqlInsertFieldCountFields =
		"INSERT INTO TB_CMT_COUNT_FIELDS("
				+ "FIELDID, VIEWNAME, COLUMNNAME, MASTERID, MINCOUNT, LASTUPDATE) "
				+ "VALUES(?, ?, ?, ?, ?, null)";

	/**
	 * Binds a fieldId and TB_CMT_COUNT_CONFIG_FIELDS parameters to a prepared
	 * statement for {@link #sqlInsertFieldCountFields}
	 */
	static void bindSqlInsertFieldCountFields(PreparedStatement stmt1,
			int fieldId, String[] entry) throws SQLException {
		assert entry != null && entry.length == 4;
		String view = entry[0];
		String column = entry[1];
		String masterId = entry[2];
		int minCount = Integer.valueOf(entry[3]);
		String msg = msgBindSqlInsertFieldCountFields(fieldId, view, column,
				masterId, minCount);
		logger.fine(msg);
		stmt1.setInt(1, fieldId);
		stmt1.setString(2, view);
		stmt1.setString(3, column);
		stmt1.setString(4, masterId);
		stmt1.setInt(5, minCount);
	}

	/**
	 * Debug message for binding parameters to
	 * {@link #sqlInsertFieldCountFields}
	 */
	public static String msgBindSqlInsertFieldCountFields(int fieldId,
			String view, String column, String masterId, int minCount) {
		StringBuilder sb = new StringBuilder().append("BIND ");
		sb.append(sqlInsertFieldCountFields).append(": ");
		sb.append("[fieldId:").append(fieldId).append("], ");
		sb.append("[view:").append(view).append("], ");
		sb.append("[column:").append(column).append("], ");
		sb.append("[masterId:").append(masterId).append("], ");
		sb.append("[minCount:").append(minCount).append("], ");
		String retVal = sb.toString();
		return retVal;
	}

	static int insertMissingFieldsCountFields(Connection connection,
			final int maxId, List<String[]> missingFieldEntries)
			throws SQLException {
		int retVal = 0;
		logger.info("SQL to insert missing fields into TB_CMT_COUNT_FIELDS: "
				+ sqlInsertFieldCountFields);
		PreparedStatement stmt1 = null;
		try {
			stmt1 = connection.prepareStatement(sqlInsertFieldCountFields);
			for (String[] entry : missingFieldEntries) {
				int fieldId = maxId + retVal + 1;
				bindSqlInsertFieldCountFields(stmt1, fieldId, entry);
				retVal += stmt1.executeUpdate();
			}
			logger.fine("TB_CMT_COUNT_CONFIG_FIELDS missing fields inserted: "
					+ retVal);
		} finally {
			closeStatement(stmt1);
			stmt1 = null;
		}
		return retVal;
	}

	/** Create an entry for a table in TB_CMT_COUNT_FIELDS */
	public static final String sqlInsertTableCountFields =
		"INSERT INTO TB_CMT_COUNT_FIELDS("
				+ "FIELDID, VIEWNAME, COLUMNNAME, MASTERID, MINCOUNT, LASTUPDATE) "
				+ "VALUES(?, ?, null, ?, null, null)";

	/**
	 * Binds a fieldId, view and column parameters to a prepared
	 * statement for {@link #sqlInsertTableCountFields}
	 */
	static void bindSqlInsertTableCountFields(PreparedStatement stmt2,
			int fieldId, String[] entry) throws SQLException {
		String view = entry[0];
		String masterId = entry[1];
		String msg = msgSqlInsertTableCountFields(fieldId, view, masterId);
		logger.fine(msg);
		stmt2.setInt(1, fieldId);
		stmt2.setString(2, view);
		stmt2.setString(3, masterId);
	}

	/**
	 * Debug message for binding parameters to
	 * {@link #sqlInsertTableCountFields}
	 */
	public static String msgSqlInsertTableCountFields(int fieldId, String view,
			String masterId) {
		StringBuilder sb = new StringBuilder().append("BIND ");
		sb.append(sqlInsertTableCountFields).append(": ");
		sb.append("[fieldId:").append(fieldId).append("], ");
		sb.append("[view:").append(view).append("], ");
		sb.append("[masterId:").append(masterId).append("]");
		String retVal = sb.toString();
		return retVal;
	}

	static int insertMissingTablesCountFields(Connection connection,
			final int maxFieldId, List<String[]> missingTableEntries)
			throws SQLException {
		int retVal = 0;
		PreparedStatement stmt2 = null;
		try {
			logger.info(
					"SQL to insert missing tables into TB_CMT_COUNT_FIELDS: "
							+ sqlInsertTableCountFields);
			stmt2 = connection.prepareStatement(sqlInsertTableCountFields);
			for (String[] entry : missingTableEntries) {
				int fieldId = maxFieldId + retVal + 1;
				DbbCountsCreatorSQL.bindSqlInsertTableCountFields(stmt2,
						fieldId, entry);
				retVal += stmt2.executeUpdate();
			}
			logger.fine("TB_CMT_COUNT_CONFIG_FIELDS missing tables inserted: "
					+ retVal);
		} finally {
			closeStatement(stmt2);
			stmt2 = null;
		}
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

	/** Template to determine a field type */
	public static final String template19 =
		"SELECT $column  FROM $table WHERE 0 = 1";

	/** Instantiation of {@link #template19} */
	public static String instance19(String field, String view) {
		String retVal = template19;
		retVal = retVal.replace("$column", field).replace("$table", view);
		return retVal;
	}

	static int getColumnType(Connection connection, String column, String table)
			throws SQLException {
		int retVal = INVALID_JDBC_TYPE;
		ResultSet rs = null;
		Statement stmt = null;
		try {
			String query = instance19(column, table);
			logger.info("SQL to determine a field type: " + query);
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
			assert rs.getMetaData().getColumnCount() == 1;
			retVal = rs.getMetaData().getColumnType(1);
		} finally {
			closeResultSet(rs);
			closeStatement(stmt);
		}
		return retVal;
	}

	/**
	 * Remove entries from TB_CMT_COUNTS that have be marked for re-computation
	 * in TB_CMT_COUNT_FIELDS
	 */
	public static final String query14 =
		"DELETE FROM TB_CMT_COUNTS WHERE NOT EXISTS "
				+ "(SELECT * FROM TB_CMT_COUNT_FIELDS f "
				+ "WHERE TB_CMT_COUNTS.FieldId = f.FieldId "
				+ "AND f.lastUpdate IS NOT NULL)";

	/** Remove all entries from TB_CMT_COUNTS */
	public static final String query16 = "DELETE FROM TB_CMT_COUNTS";

	static int deleteCounts(Connection connection, boolean onlyUncomputed)
			throws SQLException {
		int retVal = 0;
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			String delete = onlyUncomputed ? query14 : query16;
			logger.info("SQL to remove entries from TB_CMT_COUNTS: " + delete);
			retVal = stmt.executeUpdate(delete);
		} finally {
			closeStatement(stmt);
		}
		return retVal;
	}

	/**
	 * Select entries from TB_CMT_COUNT_FIELDS that are marked for
	 * re-computation
	 */
	public static final String query15 =
		"SELECT * FROM TB_CMT_COUNT_FIELDS WHERE LastUpdate IS NULL";

	/** Select all entries from TB_CMT_COUNT_FIELDS */
	public static final String query17 = "SELECT * FROM TB_CMT_COUNT_FIELDS";

	static List<String[]> selectEntriesCountFields(Connection connection,
			boolean onlyUncomputed) throws SQLException {
		List<String[]> retVal = new ArrayList<>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			String query = onlyUncomputed ? query15 : query17;
			logger.info(
					"SQL to select entries from TB_CMT_COUNT_FIELDS: " + query);
//			int rowsSelected = stmt.executeUpdate(query);
//			logger.fine("TB_CMT_COUNT_FIELDS selected: " + rowsSelected);
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				String[] entry = new String[5];
				for (int i = 1; i <= 5; ++i) {
					int idx = i - 1;
					entry[idx] = rs.getString(i);
				}
				retVal.add(entry);
			}
		} finally {
			closeResultSet(rs);
			rs = null;
			closeStatement(stmt);
			stmt = null;
		}
		return retVal;
	}

	/** Template to insert table size into TB_CMT_COUNTS */
	public static final String template18 =
		"INSERT INTO TB_CMT_COUNTS(FIELDID, VALUE, COUNT) "
				+ "SELECT $fieldId  as FIELDID, '$table' as VALUE, "
				+ "COUNT(DISTINCT $uniqueId) as COUNT FROM $table";

	/** Instantiation of {@link #template18} */
	public static String instance18(String fieldId, String view,
			String masterId) {
		String retVal = template18;
		retVal = retVal.replace("$fieldId", fieldId).replace("$table", view)
				.replace("$uniqueId", masterId);
		return retVal;
	}

	static int insertTableSize(Connection connection, String fieldId,
			String table, String uniqueId) throws SQLException {
		int retVal = 0;
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			String query = instance18(fieldId, table, uniqueId);
			logger.info(
					"SQL to insert a table size into TB_CMT_COUNTS: " + query);
			retVal = stmt.executeUpdate(query);
		} finally {
			closeStatement(stmt);
		}
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

	static int insertFieldCounts(Connection connection, String fieldId,
			String column, String table, String uniqueId, String minCount,
			int columnType) throws SQLException {
		int retVal = 0;
		Statement stmt = null;
		try {
			boolean isDate =
				columnType == Types.DATE || columnType == Types.TIMESTAMP;
			logger.fine("Value column type: " + (isDate ? "date" : "non-date"));
			String query =
				instance20(fieldId, column, table, uniqueId, minCount);
			logger.info(
					"SQL to insert field counts in TB_CMT_COUNTS: " + query);
			stmt = connection.createStatement();
			retVal = stmt.executeUpdate(query);
			logger.fine("Fields inserted into TB_CMT_COUNTS: " + retVal);
		} finally {
			closeStatement(stmt);
		}
		return retVal;
	}

	/**
	 * Find tables in TB_CMT_COUNT_CONFIG_FIELDS that are not defined in
	 * TB_CMT_COUNT_FIELDS
	 */
	public static final String sqlSelectMissingTablesCountFields =
		"SELECT DISTINCT ViewName, MasterId "
				+ "FROM TB_CMT_COUNT_CONFIG_FIELDS t1 "
				+ "WHERE ColumnName IS NULL AND NOT EXISTS "
				+ "(SELECT * FROM TB_CMT_COUNT_FIELDS t2 "
				+ "WHERE t1.ViewName = t2.ViewName AND t2.ColumnName IS NULL "
				+ "AND t1.MasterId = t2.MasterId)";

	static List<String[]> selectMissingTablesCountFields(Connection connection)
			throws SQLException {
		List<String[]> missingTableEntries = new ArrayList<>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			logger.info(
					"SQL to insert missing tables into TB_CMT_COUNT_FIELDS: "
							+ sqlSelectMissingTablesCountFields);
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sqlSelectMissingTablesCountFields);
			while (rs.next()) {
				String[] entry = new String[2];
				for (int i = 1; i <= 2; ++i) {
					int idx = i - 1;
					entry[idx] = rs.getString(i);
				}
				missingTableEntries.add(entry);
			}
		} finally {
			closeResultSet(rs);
			rs = null;
			closeStatement(stmt);
			stmt = null;
		}
		return missingTableEntries;
	}

	public static final String sqlSelectTableSizesCountFields =
		"SELECT ViewName, MasterId, Count "
				+ "FROM TB_CMT_COUNT_FIELDS f, TB_CMT_COUNTS c "
				+ "WHERE f.FieldId = c.FieldId AND f.ColumnName IS NULL";

	static Map<DbTable, Integer> selectTableSizes(Connection c)
			throws SQLException {
		final String METHOD = "selectTableSizes: ";
		logger.fine(METHOD + "entering");
		Map<DbTable, Integer> retVal = new HashMap<>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			logger.info("SQL to select tables sizes: " + sqlSelectTableSizesCountFields);
			stmt = c.createStatement();
			rs = stmt.executeQuery(sqlSelectTableSizesCountFields);
			while (rs.next()) {
				final String view = rs.getString(1);
				final String uniqueId = rs.getString(2);
				final int count = rs.getInt(3);
				final int safeMin = Math.max(1, count);
				DbTable dbt = new DbTable(view, 0, uniqueId);
				retVal.put(dbt,safeMin);
			}
			logger.fine("Number of table sizes: " + retVal.size());
			logger.fine("Table sizes: " + elideString(retVal.toString()));
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
			closeResultSet(rs);
			rs = null;
			closeStatement(stmt);
			stmt = null;
		}
		logger.fine(METHOD + "exiting");
		return retVal;
	}

	public static final String sqlSelectFieldIdCountFields =
		"SELECT FieldId FROM TB_CMT_COUNT_FIELDS "
				+ "WHERE ViewName = ? AND ColumnName = ? AND MasterId = ?";

	static void bindSqlSelectFieldIdCountFields(PreparedStatement stmt1,
			String view, String column, String uniqueId) throws SQLException {
		logger.fine(msgBindSqlSelectFieldIdCountFields(view, column, uniqueId));
		stmt1.setString(1, view);
		stmt1.setString(2, column);
		stmt1.setString(3, uniqueId);
	}

	static String msgBindSqlSelectFieldIdCountFields(String view, String column,
			String uniqueId) {
		StringBuilder sb = new StringBuilder().append("BIND ");
		sb.append(sqlSelectFieldIdCountFields).append(": ");
		sb.append("[table:").append(view).append("], ");
		sb.append("[column:").append(column).append("], ");
		sb.append("[masterId:").append(uniqueId).append("]");
		String retVal = sb.toString();
		return retVal;
	}

	static int selectFieldId(PreparedStatement stmt1, String view,
			String column, String masterId) throws SQLException {
		ResultSet rs = null;
		int retVal = 0;
		try {
			bindSqlSelectFieldIdCountFields(stmt1, view, column, masterId);
			rs = stmt1.executeQuery();
			if (rs.next()) {
				retVal = rs.getInt(1);
			}
			if (rs.next()) {
				StringBuilder sb = new StringBuilder();
				sb.append("Multiple field ids for ");
				sb.append("[table:").append(view).append("], ");
				sb.append("[column:").append(column).append("], ");
				sb.append("[masterId:").append(masterId).append("]");
				String msg = sb.toString();
				logger.severe(msg);
				throw new IllegalStateException(msg);
			}
		} finally {
			closeResultSet(rs);
			rs = null;
		}
		if (retVal == 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("Unable to retrieve field id for ");
			sb.append("[table:").append(view).append("], ");
			sb.append("[column:").append(column).append("], ");
			sb.append("[masterId:").append(masterId).append("]");
			String msg = sb.toString();
			logger.warning(msg);
		}
		if (retVal < 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("Illegal field id (");
			sb.append(retVal).append(" for ");
			sb.append("[table:").append(view).append("], ");
			sb.append("[column:").append(column).append("], ");
			sb.append("[masterId:").append(masterId).append("]");
			String msg = sb.toString();
			logger.severe(msg);
			throw new IllegalStateException(msg);
		}
		return retVal;
	}

	static void setDateFormat(Connection connection,
			DatabaseAbstraction databaseAbstraction) throws SQLException {
		Statement stmt = null;
		boolean returnIsResultSet = false;
		try {
			stmt = connection.createStatement();
			String query13 = databaseAbstraction.getSetDateFormatExpression();
			logger.info("SQL to set date format: " + query13);
			returnIsResultSet = stmt.execute(query13);
			if (returnIsResultSet) {
				logger.info("Date-formate SQL returned a result set");
			} else {
				int count = stmt.getUpdateCount();
				logger.info("Date-format SQL returned a count: " + count);
			}

		} finally {
			if (returnIsResultSet) {
				closeResultSet(stmt.getResultSet());
			}
			closeStatement(stmt);
		}
	}

	public static final String sqlSelectValueCount =
		"SELECT Value, Count FROM TB_CMT_COUNTS  WHERE FieldId = ?";

	static void bindSqlSelectValueCount(PreparedStatement stmt1, int fieldId)
			throws SQLException {
		logger.fine(msgBindSqlSelectValueCount(fieldId));
		stmt1.setInt(1, fieldId);
	}

	static String msgBindSqlSelectValueCount(int fieldId) {
		StringBuilder sb = new StringBuilder().append("BIND ");
		sb.append(sqlSelectValueCount).append(": ");
		sb.append("[fieldId:").append(fieldId).append("]");
		String retVal = sb.toString();
		return retVal;
	}

	static void updateCounts(PreparedStatement stmt2, FieldValueCounts f,
			int fieldId) throws SQLException {
		ResultSet rs = null;
		try {
			bindSqlSelectValueCount(stmt2, fieldId);
			rs = stmt2.executeQuery();
			while (rs.next()) {
				String value = rs.getString(1);
				Integer count = FieldValueCounts.valueOf(rs.getInt(2));
				f.putValueCount(value, count);
			}
			if (f.getValueCountSize() == 0) {
				String msg = "No value/count entries for field id " + fieldId;
				logger.warning(msg);
			} else {
				String msg = "Field id " + fieldId + ": "
						+ f.getValueCountSize() + " value/count entries";
				logger.fine(msg);
			}

		} finally {
			closeResultSet(rs);
			rs = null;
		}
	}

	static int updateTimeStampsCountFields(PreparedStatement stmt1,
			String sysdate, String strFieldId) throws SQLException {
		int retVal = 0;
		logger.fine(msgBindQuery21(sysdate, strFieldId));
		int fieldId = -1;
		try {
			fieldId = Integer.parseInt(strFieldId);
			assert fieldId > -1;
			stmt1.setInt(1, fieldId);
			retVal = stmt1.executeUpdate();
		} catch (NumberFormatException x) {
			String msg = "non-integer field id: " + strFieldId;
			logger.warning(msg);
		}
		logger.fine("TB_CMT_COUNT_FIELDS timestamps updated: " + retVal);
		return retVal;
	}

	static void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				logger.warning("Unable to close result set: " + e);
			}
		}
	}

	static void closeStatement(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				logger.warning("Unable to close statement: " + e);
			}
		}
	}

	static void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				logger.warning("Unable to close statement: " + e);
			}
		}
	}
	
	public static final int DEFAULT_ELISON_LENGTH = 50;
	
	static String elideString(String s) {
		return elideString(s,DEFAULT_ELISON_LENGTH);
	}
	
	static String elideString(String s, final int maxLength) {
		String retVal;
		if (s != null && maxLength >= 0 && s.length() > maxLength) {
			retVal = s.substring(0, maxLength) + " ...";
		} else {
			retVal = s;
		}
		return retVal;
	}

	private DbbCountsCreatorSQL() {
	}

}
