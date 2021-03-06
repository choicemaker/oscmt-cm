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
import java.sql.ResultSet;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.Sink;
import com.choicemaker.cm.core.util.NameUtils;
import com.choicemaker.cm.io.db.base.DataSources;
import com.choicemaker.cm.io.db.base.DbAccessor;
import com.choicemaker.cm.io.db.base.DbReaderParallel;

/**
 * Oracle record source. This implementation assumes that various stored
 * procedures, written in PL/SQL, are installed in the target database.
 * <p>
 * FIXME: rename and move this Oracle-specific implementation
 * to the com.choicemaker.cm.io.db.oracle package
 * </p>
 *
 * @author    Adam Winkel
 */
public class OracleRecordSource implements RecordSource {
	private static Logger logger = Logger.getLogger(OracleRecordSource.class
			.getName());
	private static final int CURSOR = -10;

	// Properties
	private String fileName;
	private String name;
	private DataSource ds;
	private String dataSourceName;
	private ImmutableProbabilityModel model;
	private String selection;
	private String conf = "";

	// Cache
	private Connection conn;
	private CallableStatement stmt;
	private ResultSet[] rs;
	private ResultSet outer;
	private DbReaderParallel dbr;
	private Record<?> record;

	/**
	 * Creates an uninitialized instance.
	 */
	public OracleRecordSource() {
		name = "";
		selection = "";
	}

	/**
	 * Constructor.
	 */
	public OracleRecordSource(String fileName, String dataSourceName,
			ImmutableProbabilityModel model, String conf, String selection) {
		setFileName(fileName);
		setDataSourceName(dataSourceName);
		setModel(model);
		this.selection = selection;
		this.conf = conf;
	}

	@Override
	public void open() throws IOException {
		try {
			conn = ds.getConnection();
//			conn.setAutoCommit(false); // 2015-04-01a EJB3 CHANGE rphall

			DbAccessor dba = (DbAccessor) model.getAccessor();
			dbr = (dba).getDbReaderParallel(conf);
			logger.fine (conf + " " + dbr);

			OracleRemoteDebugging.doDebugging(conn);

			String sql = "call CMTTRAINING.RS_SNAPSHOT (?,?,?)";
			stmt = conn.prepareCall(sql);

			stmt.setString(1, selection);
			String s = dbr.getName();
			stmt.setString(2, s);
			stmt.registerOutParameter(3, CURSOR);

			logger.fine ("Oracle stored procedure: " + sql);
			logger.fine("param1 (select): " + selection);
			logger.fine("param2 (dbrName): " + s);
			logger.fine("param3 (cursor): " + CURSOR);

			stmt.execute();

			outer = (ResultSet) stmt.getObject(3);
			outer.next();

			int noCursors = dbr.getNoCursors();
			rs = new ResultSet[noCursors];
			if (noCursors == 1) {
				rs[0] = outer;
			} else {
				for (int i = 0; i < noCursors; ++i) {
					logger.fine("Get cursor: " + i);
					rs[i] = (ResultSet) outer.getObject(i + 1);
				}
			}

			dbr.open(rs);

			getNextMain();

		} catch (java.sql.SQLException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage(), e);
		}
	}

	@Override
	public boolean hasNext() {
		return record != null;
	}

	@Override
	public Record<?> getNext() throws IOException {
		Record<?> r = record;
		getNextMain();
		return r;
	}

	private void getNextMain() throws IOException {
		try {
			if (dbr.hasNext()) {
				record = dbr.getNext();
			} else {
				record = null;
			}
		} catch (java.sql.SQLException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	@Override
	public void close() throws IOException {
		Exception ex = null;
		try {
			int noCursors = dbr.getNoCursors();
			for (int i = 0; i < noCursors; ++i) {
				rs[i].close();
			}
		} catch (java.sql.SQLException e) {
			ex = e;
		}
		try {
			outer.close();
		} catch (java.sql.SQLException e) {
			ex = e;
		}
		try {
			stmt.close();
		} catch (java.sql.SQLException e) {
			ex = e;
		}
		try {
			conn.close();
		} catch (java.sql.SQLException e) {
			ex = e;
		}
		if (ex != null) {
			throw new IOException(ex.getMessage(), ex);
		}
	}

	/**
	 * Get the value of name.
	 * @return value of name.
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Set the value of name.
	 * @param v  Value to assign to name.
	 */
	@Override
	public void setName(String v) {
		this.name = v;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
		setName(NameUtils.getNameFromFilePath(fileName));
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	/**
	 * Get the value of ds.
	 * @return value of ds.
	 */
	public DataSource getDs() {
		return ds;
	}

	/**
	 * Set the value of ds.
	 * @param v  Value to assign to ds.
	 */
	public void setDs(DataSource v) {
		this.ds = v;
	}

	/**
	 * Get the value of dataSourceName.
	 * @return value of dataSourceName.
	 */
	public String getDataSourceName() {
		return dataSourceName;
	}

	/**
	 * Set the value of dataSourceName.
	 * @param v  Value to assign to dataSourceName.
	 */
	public void setDataSourceName(String v) {
		this.dataSourceName = v;
		this.ds = DataSources.getDataSource(dataSourceName);
	}

	/**
	 * Get the value of model.
	 * @return value of model. May be null.
	 */
	@Override
	public ImmutableProbabilityModel getModel() {
		return model;
	}

	/**
	 * Set the value of model.
	 * @param v  Value to assign to model.
	 * May be null (required for ISerializableRecordSource impl).
	 */
	@Override
	public void setModel(ImmutableProbabilityModel v) {
		this.model = v;
	}

	/**
	 * Get the value of selection.
	 * @return value of selection.
	 */
	public String getSelection() {
		return selection;
	}

	/**
	 * Set the value of selection.
	 * @param v  Value to assign to selection.
	 */
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
		return name;
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
