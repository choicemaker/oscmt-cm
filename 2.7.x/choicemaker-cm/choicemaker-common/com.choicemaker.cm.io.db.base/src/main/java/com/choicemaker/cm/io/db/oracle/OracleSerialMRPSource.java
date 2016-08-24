/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.db.oracle;

import java.io.IOException;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.ImmutableRecordPair;
import com.choicemaker.cm.core.MarkedRecordPairSource;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.Sink;
import com.choicemaker.cm.core.base.MutableMarkedRecordPair;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.io.db.base.DbReaderParallel;

/**
 * This object creates a MRPS from data in an Oracle database. It is also
 * serializable and can run in a J2EE server.
 * <p>
 * FIXME: rename and move this Oracle-specific implementation to the
 * com.choicemaker.cm.io.db.oracle package
 * </p>
 *
 * @author pcheung
 * @author rphall (refactored)
 *
 */
public class OracleSerialMRPSource
		implements MarkedRecordPairSource, Serializable {

	private static final long serialVersionUID = 271L;

	private static Logger logger =
		Logger.getLogger(OracleSerialMRPSource.class.getName());

	// Properties
	private String dataSourceName;
	private String modelName;
	private String selection;
	private String conf = "";

	/**
	 * A (serializable) map of record ids to full records, computed when this
	 * record source is opened.
	 */
	private Map<String, Record> recordMap;

	// Cache

	/** Computed from the dataSourceName */
	private transient DataSource ds;

	/** Retrieved from the data source */
	private transient Connection conn;

	/** Created from the database connection */
	private transient CallableStatement stmt;

	/** Retrieved using the modelName */
	private transient ImmutableProbabilityModel model;

	/** Retrieved from the model */
	private transient DbReaderParallel dbr;

	/** The next pair to be returned by this record source */
	private transient MutableMarkedRecordPair currentPair;

	/** A database cursor that iterates over record cursors */
	private transient ResultSet cursorOfRecordCursors;

	/** A database cursor that iterates over records (and sub-records) */
	private transient ResultSet[] recordCursors;

	/** A database cursor that iterates over marked pairs */
	private transient ResultSet markedPairs;

	/**
	 * Constructor.
	 */
	public OracleSerialMRPSource(String dataSourceName, String modelName,
			String conf, String selection) {
		this.dataSourceName = dataSourceName;
		this.modelName = modelName;
		this.selection = selection;
		this.conf = conf;
	}

	public void open() throws IOException {
		try {
			// Get the database reader for specified database configuration
			dbr = OracleMarkedRecordPairSource.getDatabaseReader(getModel(),
					conf);
			final int noCursors = dbr.getNoCursors();

			// Get a database connection (and optionally configure debugging)
			conn = getDataSource().getConnection();
			OracleRemoteDebugging.doDebugging(conn);

			// Execute the stored procedure that retrieves records and marked
			// pairs
			stmt = OracleMarkedRecordPairSource
					.prepareCmtTrainingAccessSnaphot(conn);
			OracleMarkedRecordPairSource.executeCmtTrainingAccessSnaphot(stmt,
					selection, dbr);

			// Update the result sets representing records and marked pairs
			markedPairs = (ResultSet) stmt.getObject(
					OracleMarkedRecordPairSource.PARAM_IDX_PAIR_CURSOR);
			cursorOfRecordCursors = (ResultSet) stmt.getObject(
					OracleMarkedRecordPairSource.PARAM_IDX_RECORD_CURSOR_CURSOR);
			recordCursors = OracleMarkedRecordPairSource
					.createRecordCursors(cursorOfRecordCursors, noCursors);

			// Create the map of record ids to full records
			dbr.open(recordCursors);
			this.recordMap = OracleMarkedRecordPairSource.createRecordMap(dbr);

			// Get the first currentPair
			this.currentPair = OracleMarkedRecordPairSource
					.getNextPairInternal(recordMap, markedPairs);

		} catch (java.sql.SQLException e) {
			throw new IOException("", e);
		}
	}

	public boolean hasNext() {
		return currentPair != null;
	}

	public ImmutableRecordPair getNext() throws IOException {
		return getNextMarkedRecordPair();
	}

	public MutableMarkedRecordPair getNextMarkedRecordPair()
			throws IOException {
		MutableMarkedRecordPair retVal = currentPair;
		this.currentPair = OracleMarkedRecordPairSource
				.getNextPairInternal(recordMap, markedPairs);
		return retVal;
	}

	public void close() throws IOException {
		List<String> exceptions = new ArrayList<>();
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

	public ImmutableProbabilityModel getModel() {
		if (model == null)
			model = PMManager.getModelInstance(modelName);
		return model;
	}

	private DataSource getDataSource() {
		try {
			if (ds == null) {
				Context ctx = new InitialContext();
				ds = (DataSource) ctx.lookup(dataSourceName);
			}
		} catch (NamingException ex) {
			logger.severe(ex.toString());
		}
		return ds;
	}

	// Unused methods

	public void setModel(ImmutableProbabilityModel m) {
	}

	public boolean hasSink() {
		return false;
	}

	public Sink getSink() {
		return null;
	}

	public String getFileName() {
		return null;
	}

	public String getName() {
		return null;
	}

	public void setName(String name) {
	}

}
