/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Mar 18, 2004
 *
 */
package com.choicemaker.cm.io.db.postgres2;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.choicemaker.client.api.Decision;
import com.choicemaker.cm.core.ImmutableMarkedRecordPair;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.ImmutableRecordPair;
import com.choicemaker.cm.core.MarkedRecordPairSource;
import com.choicemaker.cm.core.MutableMarkedRecordPair;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.Sink;
import com.choicemaker.cm.io.db.base.DataSources;
import com.choicemaker.cm.io.db.base.MarkedRecordPairSourceSpec;
import com.choicemaker.cm.io.db.base.RecordPairRetrievalException;

/**
 * Based on SqlServer class of similar name.
 *
 * @author rphall
 */
@SuppressWarnings("rawtypes")
public class PostgresMarkedRecordPairSource implements MarkedRecordPairSource {

	private static final Logger logger =
		Logger.getLogger(PostgresMarkedRecordPairSource.class.getName());

	private static String createRsQuery(String mrpsQuery) {
//		String fmt = "select ID as ID from ("
//				+ "select id as ID from (%s) foo union "
//				+ "select id_matched as ID from (%s) bar"
//				+ ") bas";
		String fmt = "select id as ID from (%s) foo union "
				+ "select id_matched as ID from (%s) bar";
		String retVal = String.format(fmt, mrpsQuery, mrpsQuery);
		return retVal;
	}

	private String fileName;

	private ImmutableProbabilityModel model;
	private DataSource ds;
	private String dsName;
	private String dbConfiguration;
	private String mrpsQuery;

	private Iterator<ImmutableMarkedRecordPair<?>> pairIterator;

	public PostgresMarkedRecordPairSource() {
	}

	/**
	 * mrpsQuery should be something like:
	 * 
	 * select qid as id, mid as id_matched, decision from table where ...
	 */
	public PostgresMarkedRecordPairSource(String fileName,
			ImmutableProbabilityModel model, String dsName,
			String dbConfiguration, String mrpsQuery) {
		this.fileName = fileName;
		this.model = model;
		this.dsName = dsName;
		this.dbConfiguration = dbConfiguration;
		this.mrpsQuery = mrpsQuery;
	}

	@Override
	public void open() throws IOException {
		if (model == null) {
			throw new IllegalStateException("accessProvider is null");
		} else if (ds == null && dsName == null) {
			throw new IllegalStateException("no data source specified");
		} else if (dbConfiguration == null) {
			throw new IllegalStateException("dbConfiguration is null");
		} else if (mrpsQuery == null) {
			throw new IllegalStateException("mrpsQuery is null");
		}

		if (dsName != null && ds == null) {
			ds = DataSources.getDataSource(dsName);
			if (ds == null) {
				throw new IOException(
						"Unable to get data source named: " + dsName);
			}
		}

		try {
			Connection conn = null;
			MarkedRecordPairSourceSpec spec = null;
			try {
				conn = ds.getConnection();
				spec = createSpecFromQuery(conn, mrpsQuery);
			} catch (SQLException ex) {
				throw new IOException(
						"Problem opening MRPS: " + ex.getMessage(), ex);
			} finally {
				try {
					conn.close();
				} catch (SQLException e) {
					logger.warning(e.toString());
				}
			}

			final String rsQuery = createRsQuery(mrpsQuery);
			RecordSource rs = new PostgresParallelRecordSource(fileName, model,
					dsName, dbConfiguration, rsQuery);
			this.pairIterator = spec.createPairs(rs).iterator();
		} catch (Exception ex) {
			String msg0 = "Problem opening MRPS: %s";
			String msg = String.format(msg0, ex.toString());
			logger.severe(msg);
			throw new IOException(msg);
		}
	}

	private MarkedRecordPairSourceSpec createSpecFromQuery(Connection conn,
			String query) throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		MarkedRecordPairSourceSpec spec = new MarkedRecordPairSourceSpec();
		while (rs.next()) {
			String qIdStr = rs.getString(1);
			String mIdStr = rs.getString(2);
			String dStr = rs.getString(3);

			Decision d = Decision.valueOf(dStr.charAt(0));

			spec.addMarkedPair(qIdStr, mIdStr, d);
		}

		rs.close();
		stmt.close();

		return spec;
	}

	@Override
	public boolean hasNext() throws IOException {
		return pairIterator.hasNext();
	}

	@Override
	public ImmutableRecordPair getNext() throws IOException {
		return getNextMarkedRecordPair();
	}

	@Override
	public MutableMarkedRecordPair getNextMarkedRecordPair()
			throws IOException {
		Object obj = pairIterator.next();
		if (obj instanceof ImmutableMarkedRecordPair) {
			return (MutableMarkedRecordPair) obj;
		} else if (obj instanceof RecordPairRetrievalException) {
			throw (RecordPairRetrievalException) obj;
		} else {
			throw new IllegalStateException("pairIterator may contain only "
					+ "MarkedRecordPair objects and RecordNotFoundException objects.");
		}
	}

	@Override
	public void close() throws IOException {
		pairIterator = null;
	}

	@Override
	public String getName() {
		File f = new File(fileName);
		String name = f.getName();

		return name;
	}

	@Override
	public void setName(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setModel(ImmutableProbabilityModel m) {
		this.model = m;
	}

	@Override
	public ImmutableProbabilityModel getModel() {
		return model;
	}

	public void setDataSource(DataSource ds) {
		this.ds = ds;
	}

	public void setDataSourceName(String dsName) {
		this.dsName = dsName;
	}

	public String getDataSourceName() {
		return dsName;
	}

	public void setDbConfiguration(String dbConfiguration) {
		this.dbConfiguration = dbConfiguration;
	}

	public String getDbConfiguration() {
		return dbConfiguration;
	}

	public void setMrpsQuery(String mrpsQuery) {
		this.mrpsQuery = mrpsQuery;
	}

	public String getMrpsQuery() {
		return mrpsQuery;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	@Override
	public boolean hasSink() {
		return false;
	}

	@Override
	public Sink getSink() {
		throw new UnsupportedOperationException();
	}

}
