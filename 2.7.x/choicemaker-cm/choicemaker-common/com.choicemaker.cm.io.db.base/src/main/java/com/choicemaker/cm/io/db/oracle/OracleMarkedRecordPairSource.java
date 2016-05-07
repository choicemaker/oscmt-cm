/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
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

import com.choicemaker.cm.core.Decision;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.ImmutableRecordPair;
import com.choicemaker.cm.core.MarkedRecordPairSource;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.Sink;
import com.choicemaker.cm.core.base.MutableMarkedRecordPair;
import com.choicemaker.cm.core.util.NameUtils;
import com.choicemaker.cm.io.db.base.DataSources;
import com.choicemaker.cm.io.db.base.DbAccessor;
import com.choicemaker.cm.io.db.base.DbReaderParallel;

/**
 * Marked record source implementing <code>MarkedRecordPairSource</code>. Used
 * for reading training data from a training database.
 *
 * This version fixes a problem with the getNextMethod. dbr and markedPairs are
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
		cursorOfRecordCursors.next();
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
	 *
	 */
	public static Map createRecordMap(DbReaderParallel recordReader)
			throws SQLException {
		Map retVal = new HashMap();
		while (recordReader.hasNext()) {
			Record q = recordReader.getNext();
			retVal.put(q.getId().toString(), q);
		}
		return retVal;
	}

	public static MutableMarkedRecordPair getNextPairInternal(Map recordMap,
			ResultSet markedPairs) throws IOException {
		MutableMarkedRecordPair retVal = null;
		try {
			if (markedPairs.next()) {
				String qid = markedPairs.getString(IDX_Q_RECORD);
				Record q = (Record) recordMap.get(qid);
				String mid = markedPairs.getString(IDX_M_RECORD);
				Record m = (Record) recordMap.get(mid);
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
	private Map recordMap;

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

	/** A database cursor that iterates over record cursors */
	private ResultSet cursorOfRecordCursors;

	/** A database cursor that iterates over records (and sub-records) */
	private ResultSet[] recordCursors;

	/** A database cursor that iterates over marked pairs */
	private ResultSet markedPairs;

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

	public void open() throws IOException {
		try {
			// Get the database reader for specified database configuration
			dbr = getDatabaseReader(getModel(), conf);
			final int noCursors = dbr.getNoCursors();

			// Get a database connection (and optionally configure debugging)
			conn = getDataSource().getConnection();
			OracleRemoteDebugging.doDebugging(conn);

			// Execute the stored procedure that retrieves records and marked
			// pairs
			stmt = prepareCmtTrainingAccessSnaphot(conn);
			executeCmtTrainingAccessSnaphot(stmt, selection, dbr);

			// Update the result sets representing records and marked pairs
			markedPairs = (ResultSet) stmt.getObject(PARAM_IDX_PAIR_CURSOR);
			cursorOfRecordCursors =
				(ResultSet) stmt.getObject(PARAM_IDX_RECORD_CURSOR_CURSOR);
			recordCursors =
				createRecordCursors(cursorOfRecordCursors, noCursors);

			// Create the map of record ids to full records
			dbr.open(recordCursors);
			this.recordMap = OracleMarkedRecordPairSource.createRecordMap(dbr);

			// Get the first currentPair
			this.currentPair =
				OracleMarkedRecordPairSource.getNextPairInternal(recordMap,
						markedPairs);

		} catch (java.sql.SQLException e) {
			throw new IOException(e.toString(), e);
		}
	}

	public boolean hasNext() {
		return currentPair != null;
	}

	public ImmutableRecordPair getNext() throws IOException {
		return getNextMarkedRecordPair();
	}

	public MutableMarkedRecordPair getNextMarkedRecordPair() throws IOException {
		MutableMarkedRecordPair retVal = currentPair;
		this.currentPair = getNextPairInternal(recordMap, markedPairs);
		return retVal;
	}

	public void close() throws IOException {
		List exceptions = new ArrayList();
		try {
			if (markedPairs != null)
				markedPairs.close();
			markedPairs = null;
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
			if (cursorOfRecordCursors != null)
				cursorOfRecordCursors.close();
			cursorOfRecordCursors = null;
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

	public String getName() {
		return recordSourceName;
	}

	public void setName(String v) {
		this.recordSourceName = v;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
		setName(NameUtils.getNameFromFilePath(fileName));
	}

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

	public ImmutableProbabilityModel getModel() {
		return model;
	}

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

	public String toString() {
		return recordSourceName;
	}

	public boolean hasSink() {
		return false;
	}

	public Sink getSink() {
		return null;
	}

}
