/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.db.oracle;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.choicemaker.client.api.Decision;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.ImmutableRecordPair;
import com.choicemaker.cm.core.MarkedRecordPairSource;
import com.choicemaker.cm.core.MutableMarkedRecordPair;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.Sink;
import com.choicemaker.cm.core.util.NameUtils;
import com.choicemaker.cm.io.db.base.DataSources;
import com.choicemaker.cm.io.db.base.DbAccessor;
import com.choicemaker.cm.io.db.base.DbReaderParallel;
import com.choicemaker.util.Precondition;

/**
 * Marked record source implementing <code>MarkedRecordPairSource</code>. Used
 * for reading training data from a training database.
 *
 * This version fixes a problem with the getNextMethod. dbr and pairCursor are
 * not necessarily in order so we need to put dbr records in a hashmap first.
 * <p>
 * FIXME: rename and move this Oracle-specific implementation to the
 * com.choicemaker.cm.io.db.oracle package
 * </p>
 *
 * @author Martin Buechi
 * @author rphall (refactored)
 */
public class OracleMarkedRecordPairSource implements MarkedRecordPairSource {

	private static Logger logger = Logger
			.getLogger(OracleMarkedRecordPairSource.class.getName());

	static final int CURSOR = -10;

	// SQL that invokes CMTTraining.Access_Snapshot */
	public static final String SQL_CMT_TRAINING_ACCESS_SNAPSHOT =
		"call CMTTRAINING.ACCESS_SNAPSHOT (?,?,?,?)";

	// Indices of parameters used by CMTTraining.Access_Snapshot */
	public static final int PARAM_IDX_PAIR_SELECTION_QUERY = 1;
	public static final int PARAM_IDX_DB_CONFIGURATION_NAME = 2;
	public static final int PARAM_IDX_PAIR_CURSOR = 3;
	public static final int PARAM_IDX_RECORD_CURSOR_CURSOR = 4;

	// Indices of fields returned by cursor for marked pairs
	public static final int IDX_Q_RECORD = 1;
	public static final int IDX_M_RECORD = 2;
	public static final int IDX_DECISION = 3;

	public static String getSqlCmtTrainingAccessSnaphot() {
		return SQL_CMT_TRAINING_ACCESS_SNAPSHOT;
	}

	public static CallableStatement prepareCmtTrainingAccessSnaphot(
			Connection conn) throws SQLException {
		if (conn == null) {
			throw new IllegalArgumentException("null JDBC connection");
		}
		String sql = getSqlCmtTrainingAccessSnaphot();
		logger.fine("Oracle stored procedure: " + sql);
		CallableStatement retVal = conn.prepareCall(sql);
		return retVal;
	}

	public static void executeCmtTrainingAccessSnaphot(CallableStatement stmt,
			String selection, DbReaderParallel dbr) throws SQLException {
		if (stmt == null) {
			throw new IllegalArgumentException("null JDBC statement");
		}
		if (selection == null || selection.isEmpty()) {
			throw new IllegalArgumentException(
					"null or blank SQL for record selection");
		}
		if (dbr == null) {
			throw new IllegalArgumentException("null database reader");
		}
		String dbrName = dbr.getName();
		stmt.setString(PARAM_IDX_PAIR_SELECTION_QUERY, selection);
		stmt.setString(PARAM_IDX_DB_CONFIGURATION_NAME, dbrName);
		stmt.registerOutParameter(PARAM_IDX_PAIR_CURSOR, CURSOR);
		stmt.registerOutParameter(PARAM_IDX_RECORD_CURSOR_CURSOR, CURSOR);

		logger.fine("Oracle stored procedure param1 (select): " + selection);
		logger.fine("Oracle stored procedure param2 (dbrName): " + dbrName);
		logger.fine("Oracle stored procedure param3 (cursor): " + CURSOR);
		logger.fine("Oracle stored procedureparam4 (cursor): " + CURSOR);

		stmt.execute();
	}

	public static ResultSet[] createRecordCursors(
			ResultSet cursorOfRecordCursors, int noCursors) throws SQLException {
		if (cursorOfRecordCursors == null) {
			throw new IllegalArgumentException("null cursor of record cursors");
		}
		if (noCursors < 1) {
			throw new IllegalArgumentException("invalid number of cursors: "
					+ noCursors);
		}
		boolean hasMore = cursorOfRecordCursors.next();
		logger.fine("hasMore: " + hasMore);
		ResultSet[] retVal = new ResultSet[noCursors];
		if (noCursors == 1) {
			retVal[0] = cursorOfRecordCursors;
		} else {
			for (int i = 0; i < noCursors; ++i) {
				if (i == 0) {
					logger.fine("Set record cursor: " + i);
				} else {
					logger.fine("Set sub-record cursor: " + i);
				}
				retVal[i] = (ResultSet) cursorOfRecordCursors.getObject(i + 1);
			}
		}
		return retVal;
	}

	public static DbReaderParallel getDatabaseReader(
			ImmutableProbabilityModel model, String databaseConfiguration) {
		if (model == null) {
			throw new IllegalArgumentException("null model");
		}
		if (databaseConfiguration == null || databaseConfiguration.isEmpty()) {
			throw new IllegalArgumentException(
					"null or blank database configuration recordSourceName");
		}
		DbReaderParallel retVal =
			((DbAccessor) model.getAccessor())
					.getDbReaderParallel(databaseConfiguration);
		return retVal;
	}

	/**
	 * This method loads the records into a map.
	 */
	public static Map<String, Record> createRecordMap(
			DbReaderParallel recordReader) throws SQLException {
		Map<String, Record> retVal = new HashMap<>();
		while (recordReader.hasNext()) {
			Record q = recordReader.getNext();
			retVal.put(q.getId().toString(), q);
		}
		return retVal;
	}

	public static MutableMarkedRecordPair getNextPairInternal(
			Map<String, Record> recordMap, ResultSet markedPairs)
			throws IOException {
		MutableMarkedRecordPair retVal = null;
		try {
			if (markedPairs.next()) {
				String qid = markedPairs.getString(IDX_Q_RECORD);
				Record q = recordMap.get(qid);
				String mid = markedPairs.getString(IDX_M_RECORD);
				Record m = recordMap.get(mid);
				String d = markedPairs.getString(IDX_DECISION);
				Decision decision = null;
				if (d != null && d.length() > 0) {
					decision = Decision.valueOf(d.charAt(0));
				}
				Date date = markedPairs.getDate(4);
				String user = markedPairs.getString(5);
				String src = markedPairs.getString(6);
				String comment = markedPairs.getString(7);
				retVal =
					new MutableMarkedRecordPair(q, m, decision, date, user,
							src, comment);
			}
		} catch (java.sql.SQLException e) {
			throw new IOException("", e);
		}
		return retVal;
	}

	// Properties
	private String recordSourceName;
	private String dataSourceName;
	private String fileName;
	private String selection;
	private String conf = "";

	/**
	 * A (serializable) map of record ids to full records, computed when this
	 * record source is opened.
	 */
	private Map<String, Record> recordMap;

	// Cache

	private DataSource ds;

	/** Retrieved from the data source */
	private Connection conn;

	/** Created from the database connection */
	private CallableStatement stmt;

	private ImmutableProbabilityModel model;

	/** Retrieved from the model */
	private DbReaderParallel dbr;

	/** The next pair to be returned by this record source */
	private MutableMarkedRecordPair currentPair;

	/**
	 * A result set that iterates over a collection of table cursors, sometimes
	 * described as a cursor of cursors. The table cursors are all ordered in a
	 * consistent way, so that the <br/>
	 * <p>
	 * If the record layout does not define any stacked fields, then only one
	 * cursor is returned; i.e. the top-level table cursor is this cursor.
	 * </p>
	 */
	private ResultSet rsTableCursors;

	/**
	 * An array of database cursors that iterates over the tables that contain
	 * record information. The top-level table is iterated by the first cursor
	 * (array index 0). Tables representing stacked fields are iterated by the
	 * remaining cursors (array index 1 and greater). The rows returned by each
	 * table cursors are ordered in a way that is consistent across all the
	 * cursors. As a parallel database reader iterates through the top-level
	 * table, the reader is able to advance the stacked-field cursors as
	 * appropriate. <br/>
	 * <p>
	 * If the record layout does not define any stacked fields, then only one
	 * cursor is returned; i.e. the top-level table cursor is
	 * {@link #rsTableCursors}
	 * 
	 * <pre>
	 * recordCursors[0] = rsTableCursors
	 * </pre>
	 * </p>
	 */
	private ResultSet[] recordCursors;

	/** A database cursor that iterates over marked pairs */
	private ResultSet pairCursor;

	/**
	 * Creates an uninitialized instance.
	 */
	public OracleMarkedRecordPairSource() {
		recordSourceName = "";
		selection = "";
	}

	/**
	 * Constructor.
	 */
	public OracleMarkedRecordPairSource(String fileName, String dataSourceName,
			ImmutableProbabilityModel model, String conf, String selection) {
		setFileName(fileName);
		setDataSourceName(dataSourceName);
		setModel(model);
		this.selection = selection;
		this.conf = conf;
	}

	public OracleMarkedRecordPairSource(String fileName, DataSource ds,
			ImmutableProbabilityModel model, String conf, String selection) {
		setFileName(fileName);
		this.dataSourceName = ds.toString();
		this.ds = ds;
		setModel(model);
		this.selection = selection;
		this.conf = conf;
	}

	@Override
	public void open() throws IOException {
		// Check all implicit preconditions
		Precondition.assertNonNullArgument("null model", getModel());
		Precondition.assertNonEmptyString(
				"null or blank database configuration", this.conf);
		Precondition.assertNonEmptyString("null or blank SQL query",
				this.selection);
		Precondition.assertNonNullArgument("null datasource", getDataSource());

		// Get the database reader for specified database configuration
		dbr = getDatabaseReader(getModel(), conf);
		Precondition.assertNonNullArgument("null database reader", dbr);

		final int noCursors = dbr.getNoCursors();
		Precondition.assertBoolean("Nonpositive number of cursors: "
				+ noCursors, noCursors > 0);

		try {
			// Get a database connection (and optionally configure debugging)
			conn = getDataSource().getConnection();
			assert conn != null : "null connection";

			OracleRemoteDebugging.doDebugging(conn);

			// Check if autocommit is on. It should not be, because the
			// stored procedure will work unexpectedly since it uses a
			// global temporary that is emptied when the JDBC statement
			// executes and autocommits the snapshot procedure.
			//
			// However, autocommit should not be set or unset here. This is
			// an application configuration issue. The connection pool used
			// by the datasource should ensure that autocommit is off for the
			// connections used by this method.
			boolean isAutoCommitEnabled = conn.getAutoCommit();
			if (isAutoCommitEnabled) {
				String msg =
					"JDBC autocommit is enabled for connections "
							+ "used in OracleMarkedRecordPairSource.open(). "
							+ "The method will probably return no records "
							+ "and only empty pairs.";
				logger.warning(msg);
			}

			// Execute the stored procedure that retrieves records and marked
			// pairs
			stmt = prepareCmtTrainingAccessSnaphot(conn);
			assert stmt != null : "null statement";
			executeCmtTrainingAccessSnaphot(stmt, selection, dbr);

			// Update the result sets representing records and marked pairs
			pairCursor = (ResultSet) stmt.getObject(PARAM_IDX_PAIR_CURSOR);
			assert pairCursor != null : "null record-pair cursor";
			rsTableCursors =
				(ResultSet) stmt.getObject(PARAM_IDX_RECORD_CURSOR_CURSOR);
			assert rsTableCursors != null : "null result set for record cursors";
			recordCursors = createRecordCursors(rsTableCursors, noCursors);
			assert recordCursors != null : "null array of record cursors";
			assert recordCursors.length > 0 : "empty array of record cursors";

			// Check each cursor in the array.
			// NOTE: the second assignment in the assert is deliberate. The
			// second assignment executes only if assertions are enabled.
			// This is the only way to check if assertions are enabled.
			boolean _assertsEnabled = false;
			assert _assertsEnabled = true;
			if (_assertsEnabled) {
				for (int _i = 0; _i < recordCursors.length; _i++) {
					String msg = "invalid record cursor [" + _i + "]";
					assert recordCursors[_i] != null : msg;
				}
			}

			// Create the map of record ids to full records
			dbr.open(recordCursors);
			recordMap = OracleMarkedRecordPairSource.createRecordMap(dbr);
			assert recordMap != null;
			if (recordMap.isEmpty()) {
				String msg = "Record map is empty";
				logger.warning(msg);
			}

			// Get the first pair
			this.currentPair =
				OracleMarkedRecordPairSource.getNextPairInternal(recordMap,
						pairCursor);
			if (this.currentPair == null) {
				String msg = "No pairs found";
				logger.warning(msg);
			} else {
				Record q = this.currentPair.getQueryRecord();
				if (q == null) {
					String msg = "Null query record in first pair";
					logger.warning(msg);
				}
				Record m = this.currentPair.getMatchRecord();
				if (m == null) {
					String msg = "Null match record in first pair";
					logger.warning(msg);
				}
			}

		} catch (java.sql.SQLException e) {
			throw new IOException(e.toString(), e);
		}
	}

	@Override
	public boolean hasNext() {
		return currentPair != null;
	}

	@Override
	public ImmutableRecordPair getNext() throws IOException {
		return getNextMarkedRecordPair();
	}

	@Override
	public MutableMarkedRecordPair getNextMarkedRecordPair() throws IOException {
		MutableMarkedRecordPair retVal = currentPair;
		this.currentPair = getNextPairInternal(recordMap, pairCursor);
		return retVal;
	}

	@Override
	public void close() throws IOException {
		List<String> exceptions = new ArrayList<>();
		try {
			if (pairCursor != null)
				pairCursor.close();
			pairCursor = null;
		} catch (java.sql.SQLException e) {
			exceptions.add(e.toString());
		}
		try {
			if (dbr != null) {
				int noCursors = dbr.getNoCursors();
				for (int i = 0; i < noCursors; ++i) {
					if (recordCursors[i] != null) {
						recordCursors[i].close();
						recordCursors[i] = null;
					}
				}
			}
			dbr = null;
		} catch (java.sql.SQLException e) {
			exceptions.add(e.toString());
		}
		try {
			if (rsTableCursors != null)
				rsTableCursors.close();
			rsTableCursors = null;
		} catch (java.sql.SQLException e) {
			exceptions.add(e.toString());

		}
		try {
			if (stmt != null)
				stmt.close();
			stmt = null;
		} catch (java.sql.SQLException e) {
			exceptions.add(e.toString());
		}
		try {
			if (conn != null)
				conn.close();
			conn = null;
		} catch (java.sql.SQLException e) {
			exceptions.add(e.toString());
		}

		recordMap.clear();

		assert exceptions != null;
		if (!exceptions.isEmpty()) {
			final int exceptionCount = exceptions.size();
			assert exceptionCount > 0;
			final int lastExceptionIndex = exceptionCount - 1;
			assert exceptionCount >= 0;
			StringBuilder details = new StringBuilder();
			for (int i = 0; i < exceptionCount; i++) {
				String exception = exceptions.get(i).toString();
				details.append(exception);
				if (i < lastExceptionIndex) {
					details.append(", ");
				}
			}
			String msg =
				"Errors when record source was closed: [" + details + "]";
			throw new IOException(msg);
		}
	}

	@Override
	public String getName() {
		return recordSourceName;
	}

	@Override
	public void setName(String v) {
		this.recordSourceName = v;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
		setName(NameUtils.getNameFromFilePath(fileName));
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	private DataSource getDataSource() {
		return ds;
	}

	public void setDataSource(DataSource v) {
		this.ds = v;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String v) {
		this.dataSourceName = v;
		this.ds = DataSources.getDataSource(dataSourceName);
	}

	@Override
	public ImmutableProbabilityModel getModel() {
		return model;
	}

	@Override
	public void setModel(ImmutableProbabilityModel v) {
		this.model = v;
	}

	public String getSelection() {
		return selection;
	}

	public void setSelection(String v) {
		this.selection = v;
	}

	public String getConf() {
		return conf;
	}

	public void setConf(String v) {
		conf = v;
	}

	@Override
	public String toString() {
		return recordSourceName;
	}

	@Override
	public boolean hasSink() {
		return false;
	}

	@Override
	public Sink getSink() {
		return null;
	}

}
