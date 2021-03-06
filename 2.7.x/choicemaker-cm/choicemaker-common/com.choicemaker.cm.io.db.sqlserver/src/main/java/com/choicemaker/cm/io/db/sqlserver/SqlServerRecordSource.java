/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Feb 5, 2004
 *
 */
package com.choicemaker.cm.io.db.sqlserver;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.choicemaker.cm.core.Constants;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.Sink;
import com.choicemaker.cm.io.db.base.DataSources;
import com.choicemaker.cm.io.db.base.DbAccessor;
import com.choicemaker.cm.io.db.base.DbReaderSequential;
import com.choicemaker.cm.io.db.sqlserver.dbom.SqlDbObjectMaker;

/**
 * @author ajwinkel
 *
 */
public class SqlServerRecordSource implements RecordSource {
	
	private static final Logger logger = Logger
			.getLogger(SqlServerRecordSource.class.getName());

	private String fileName;
	private ImmutableProbabilityModel model;
	private String dbConfiguration;
	private String idsQuery;

	private String dsName;
	private DataSource ds;
	private Connection connection;
	private Statement stmt;

	private DbReaderSequential dbr;
	
	public SqlServerRecordSource() {
	}
	
	public SqlServerRecordSource(String fileName,
			ImmutableProbabilityModel model, String dsName,
			String dbConfiguration, String idsQuery) {
		this.model = model;
		setDataSourceName(dsName);
		this.dbConfiguration = dbConfiguration;
		this.idsQuery = idsQuery;
		setFileName(fileName);
	}

	@Override
	public void open() throws IOException {
		DbAccessor accessor = (DbAccessor) model.getAccessor();
		dbr = accessor.getDbReaderSequential(dbConfiguration);
		String query = createQuery();
		
		logger.fine(query);
		
		try {
			if (connection == null) {
				connection = ds.getConnection();
//				connection.setAutoCommit(true); // 2015-04-01a EJB3 CHANGE rphall
			}
			//connection.setAutoCommit(false);
			connection.setReadOnly(true);
			stmt = connection.createStatement();
			stmt.setFetchSize(100);

			// BUG 2011-05-08 rphall
			// SQL query may (and usually does) return multiple result sets,
			// 		but executeQuery can't handle more than one.
			//ResultSet rs = stmt.executeQuery(query);
			// BUGFIX: see
			// How to Retrieve Multiple Result Sets from a Stored Procedure in JDBC
			// http://links.rph.cx/m9jJav
			ResultSet rs = null;
			boolean isResultSet = stmt.execute(query);
			int count = 0;
			do {
				if (isResultSet) {
					rs = stmt.getResultSet();
					break;
				} else {
					count = stmt.getUpdateCount();
					if (count >= 0) {
						String msg =
							"Query returned update count == '" + count + "'";
						logger.fine(msg);
					} else {
						String msg =
							"Query '" + query + "' did not return a result set";
						logger.severe(msg);
						throw new SQLException(msg);
					}
				}
				isResultSet = stmt.getMoreResults();
			} while (isResultSet || count != -1);
			// ENDBUGFIX
			
			dbr.open(rs, stmt);
		} catch (SQLException ex) {
			logger.severe(ex.toString());
			try {
				close();
			} catch (IOException x) {
				logger.severe(x.toString());
			}
			throw new IOException(ex.toString());
		}
	}

	@Override
	public boolean hasNext() throws IOException {
		return dbr.hasNext();
	}

	@Override
	public Record getNext() throws IOException {
		return dbr.getNext();
	}

	@Override
	public void close() throws IOException {
		try {
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
		} catch (SQLException ex) {
			throw new IOException(
					"Problem closing a SQL statement.", ex);
		}
		try {
			if (connection != null) {
				connection.close();
				connection = null;
			}
		} catch (SQLException ex) {
			throw new IOException(
					"Problem closing a SQL connection.", ex);
		} finally {
			dbr = null;
		}
	}

	@Override
	public ImmutableProbabilityModel getModel() {
		return model;
	}

	@Override
	public void setModel(ImmutableProbabilityModel model) {
		this.model = model;
	}
		
	public String getDataSourceName() {
		return dsName;
	}

	public void setDataSourceName(String dsName) {
		DataSource ds = DataSources.getDataSource(dsName);
		setDataSource(dsName, ds);
	}
		
	public DataSource getDataSource() {
		return ds;
	}
	
	public void setDataSource(String name, DataSource ds) {
		this.dsName = name;
		this.ds = ds;
	}
	
	public void setConnection(Connection connection) {
		this.dsName = null;
		this.ds = null;
		
		this.connection = connection;
	}
	
	public String getDbConfiguration() {
		return dbConfiguration;
	}
	
	public void setDbConfiguration(String dbConfiguration) {
		this.dbConfiguration = dbConfiguration;
	}
	
	public String getIdsQuery() {
		return idsQuery;
	}
	
	public void setIdsQuery(String idsQuery) {
		this.idsQuery = idsQuery;
	}
	
	@Override
	public String getName() {
		return "SQL Server Record Source";
	}

	@Override
	public void setName(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasSink() {
		return false;
	}

	@Override
	public Sink getSink() {
		throw new UnsupportedOperationException();
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String getFileName() {
		return fileName;
	}
	
	private String createQuery() {
		StringBuffer b = new StringBuffer(16000);
		b.append("DECLARE @ids TABLE (id " + dbr.getMasterIdType() + ")"
				+ Constants.LINE_SEPARATOR);
		b.append("INSERT INTO @ids " + idsQuery + Constants.LINE_SEPARATOR);
		b.append(SqlDbObjectMaker.getMultiQuery(model, dbConfiguration));
		return b.toString();
	}

	@Override
	public String toString() {
		return "SqlServerRecordSource [fileName=" + fileName + ", model="
				+ model + ", dbConfiguration=" + dbConfiguration
				+ ", idsQuery=" + idsQuery + ", dsName=" + dsName + "]";
	}

}
