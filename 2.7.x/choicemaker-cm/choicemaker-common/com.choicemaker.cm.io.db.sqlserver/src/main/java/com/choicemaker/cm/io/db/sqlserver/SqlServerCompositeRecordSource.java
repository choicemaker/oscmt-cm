/*******************************************************************************
 * Copyright (c) 2015, 2021 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Sep 8, 2004
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
import com.choicemaker.cm.io.composite.base.CompositeRecordSource;
import com.choicemaker.cm.io.db.base.DataSources;
import com.choicemaker.cm.io.db.base.DbAccessor;
import com.choicemaker.cm.io.db.base.DbReaderSequential;

/**
 * This version is a workaround for SqlServerRecordSource. The
 * SqlServerRecordSource encounters OutOfMemoryException when it tries to bring
 * a lot of data. This solution is the chop up the query into smaller chunks. It
 * uses a CompositeRecordSource to store the smaller chunks.
 * 
 * This version works well with numeric primary key, but it is not tuned for
 * String primary key.
 * 
 * @author pcheung
 * @deprecated
 */
@Deprecated
public class SqlServerCompositeRecordSource implements RecordSource {

	private static final Logger log = Logger
			.getLogger(SqlServerCompositeRecordSource.class.getName());

	private CompositeRecordSource compositeSource;
	private ImmutableProbabilityModel model;
	private String dbConfiguration;
	private String idsQuery;
	private String dsName;
	private DataSource ds;
	private Connection connection;
	private Statement stmt;
	private int maxSize;
	
	/**
	 * Default Constructor that does nothing.
	 */
	public SqlServerCompositeRecordSource (){
	}

	/** This constructor takes these arguments
	 * 
	 * @param ds - data source 
	 * @param model - the probability model
	 * @param idsQuery - the query to get the ids.
	 * @param dbConfiguration - db configuration name in the schema file.
	 * @param maxSize - maximum number of records in each of the composite.
	 */
	public SqlServerCompositeRecordSource(DataSource ds,
			ImmutableProbabilityModel model, String idsQuery,
			String dbConfiguration, int maxSize) {
		this.model = model;
		this.idsQuery = idsQuery;
		this.dbConfiguration = dbConfiguration;
		this.maxSize = maxSize;
		this.ds = ds;
		this.dsName = "DS";
	}

	/** This constructor takes these arguments
	 * 
	 * @param dsName - data source name
	 * @param model - the probability model
	 * @param idsQuery - the query to get the ids.
	 * @param dbConfiguration - db configuration name in the schema file.
	 * @param maxSize - maximum number of records in each of the composite.
	 */
	public SqlServerCompositeRecordSource(String dsName,
			ImmutableProbabilityModel model, String idsQuery,
			String dbConfiguration, int maxSize) {
		this.model = model;
		this.idsQuery = idsQuery;
		this.dbConfiguration = dbConfiguration;
		this.maxSize = maxSize;
		setDataSourceName (dsName);
	}
	
	public String getDataSourceName() {
		return dsName;
	}

	private void setDataSourceName(String dsName) {
		DataSource ds = DataSources.getDataSource(dsName);
		setDataSource(dsName, ds);
	}
		
	public DataSource getDataSource() {
		return ds;
	}
	
	private void setDataSource(String name, DataSource ds) {
		this.dsName = name;
		this.ds = ds;
	}

	/** This method chops up the data and populates the CompositeRecordSource.
	 * 
	 *
	 */
	private void init () throws IOException{
		DbAccessor accessor = (DbAccessor) model.getAccessor();
		DbReaderSequential dbr = accessor.getDbReaderSequential(dbConfiguration);
		String idName = dbr.getMasterId();
		boolean isString = (dbr.getMasterIdType().indexOf("CHAR") > -1) ||
			(dbr.getMasterIdType().indexOf("STR") > -1);
						
		log.fine (dbr.getMasterIdType() + " isString " + isString);
		log.fine ("master id: " + idName);
		log.fine ("idsQuery: " + idsQuery);

		try {
			if (isString) compositeSource = handleString (idsQuery, idName);
			else  compositeSource = handleNumber (idsQuery, idName);

		} catch (SQLException ex) {
			log.severe(ex.toString());
			
			throw new IOException(ex.toString());
		}
	}

	//this handles numeric primary key
	private CompositeRecordSource handleNumber(String idsQuery, String idName)
			throws SQLException {
		String query = createQuery(idsQuery);

		CompositeRecordSource compositeSource = new CompositeRecordSource();
		compositeSource.setModel(model);

		if (connection == null) {
			connection = ds.getConnection();
//			connection.setAutoCommit(true); // 2015-04-01a EJB3 CHANGE rphall
			connection.setReadOnly(true);
		}
		stmt = connection.createStatement();
		stmt.setFetchSize(100);
		ResultSet rs = stmt.executeQuery(query);

		if (rs.next()) {
			long min = rs.getLong(1);
			long max = rs.getLong(2);
			int count = rs.getInt(3);

			log.fine("handleNumber min " + min + " max " + max + " count "
					+ count);

			while (min < max) {
				String subQuery =
					setMinMax(idsQuery, idName, min, min + maxSize);

				// since between is inclusive, we need to add another one.
				min += maxSize + 1;

				log.fine(subQuery);

				SqlServerRecordSource srs = new SqlServerRecordSource();
				srs.setDataSource(dsName, ds);
				srs.setModel(model);
				srs.setDbConfiguration(dbConfiguration);
				srs.setIdsQuery(subQuery);

				compositeSource.add(srs);
			}

			log.fine("number of sources: " + compositeSource.getNumSources());
		}

		rs.close();
		shutDown();

		return compositeSource;
	}

	//this handles String primary key
	private CompositeRecordSource handleString(String idsQuery, String idName)
			throws SQLException {

		String query = createQuery(idsQuery);
		CompositeRecordSource compositeSource = new CompositeRecordSource();
		compositeSource.setModel(model);

		if (connection == null) {
			connection = ds.getConnection();
//			connection.setAutoCommit(true); // 2015-04-01a EJB3 CHANGE rphall
			connection.setReadOnly(true);
		}

		stmt = connection.createStatement();
		stmt.setFetchSize(100);
		ResultSet rs = stmt.executeQuery(query);

		if (rs.next()) {
			String min = rs.getString(1);
			String max = rs.getString(2);
			int count = rs.getInt(3);

			log.fine("handleString min " + min + " max " + max + " count "
					+ count);

			if (count < maxSize) {
				String subQuery = setMinMax(idsQuery, idName, min, max);

				SqlServerRecordSource srs = new SqlServerRecordSource();
				srs.setDataSource(dsName, ds);
				srs.setModel(model);
				srs.setDbConfiguration(dbConfiguration);
				srs.setIdsQuery(subQuery);

				compositeSource.add(srs);

			} else {

				HorizontalPartitioner hp =
					new HorizontalPartitioner(compositeSource, dsName, ds,
							model, idsQuery, idName, dbConfiguration, maxSize);
				hp.addPartitionRecordSources(min, max, count);
			}
			log.fine("number of sources: " + compositeSource.getNumSources());
		}
		rs.close();
		shutDown();
		return compositeSource;
	}	

	private String setMinMax(String idsQuery, String idName, String min,
			String max) {

		StringBuffer sb = new StringBuffer ();
		int ind = idsQuery.toUpperCase().indexOf("WHERE");
		if ( ind > 0) {
			sb.append(idsQuery.substring(0, ind + 6));
			sb.append ("(");
			sb.append (idName);
			sb.append (" between '");
			sb.append(min);
			sb.append("' and '");
			sb.append(max);
			sb.append ("')");
			sb.append (" and ");
			sb.append(idsQuery.substring(ind + 6));
		} else {
			sb.append(idsQuery);
			sb.append(" where ");
			sb.append ("(");
			sb.append (idName);
			sb.append (" between '");
			sb.append(min);
			sb.append("' and '");
			sb.append(max);
			sb.append ("')");
		}

		return sb.toString();
	}

	private String setMinMax(String idsQuery, String idName, long min, long max) {

		StringBuffer sb = new StringBuffer ();
		int ind = idsQuery.toUpperCase().indexOf("WHERE");
		if ( ind > 0) {
			sb.append(idsQuery.substring(0, ind + 6));
			sb.append ("(");
			sb.append (idName);
			sb.append (" between ");
			sb.append(min);
			sb.append(" and ");
			sb.append(max);
			sb.append (")");
			sb.append (" and ");
			sb.append(idsQuery.substring(ind + 6));
		} else {
			sb.append(idsQuery);
			sb.append(" where ");
			sb.append ("(");
			sb.append (idName);
			sb.append (" between ");
			sb.append(min);
			sb.append(" and ");
			sb.append(max);
			sb.append (")");
		}

		return sb.toString();
	}

	private void shutDown () {
		try {
			if (stmt != null) stmt.close();
		} catch (SQLException e) {
			log.severe(e.toString());
		}

		try {
			if (connection != null) connection.close();
			connection = null;
		} catch (SQLException e) {
			log.severe(e.toString());
		}
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.RecordSource#getNext()
	 */
	@Override
	public Record getNext() throws IOException {
		return compositeSource.getNext();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#open()
	 */
	@Override
	public void open() throws IOException {
		init ();
		compositeSource.open();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#close()
	 */
	@Override
	public void close() throws IOException {
		compositeSource.close();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#hasNext()
	 */
	@Override
	public boolean hasNext() throws IOException {
		return compositeSource.hasNext();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#getName()
	 */
	@Override
	public String getName() {
		return "SQL Server Composite Record Source";
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		//do nothing
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#getModel()
	 */
	@Override
	public ImmutableProbabilityModel getModel() {
		return model;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#setModel(com.choicemaker.cm.core.ProbabilityModel)
	 */
	@Override
	public void setModel(ImmutableProbabilityModel m) {
		this.model = m;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#hasSink()
	 */
	@Override
	public boolean hasSink() {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#getSink()
	 */
	@Override
	public Sink getSink() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#getFileName()
	 */
	@Override
	public String getFileName() {
		throw new UnsupportedOperationException();
	}

	private String createQuery(String idsQuery) {
		DbAccessor accessor = (DbAccessor) model.getAccessor();
		DbReaderSequential dbr = accessor.getDbReaderSequential(dbConfiguration);

		StringBuffer b = new StringBuffer(16000);
		b.append("DECLARE @ids TABLE (id " + dbr.getMasterIdType() + ")"
				+ Constants.LINE_SEPARATOR);
		b.append("INSERT INTO @ids " + idsQuery + Constants.LINE_SEPARATOR);
		b.append("SELECT MIN(ID), MAX(ID), COUNT(*) FROM @IDS");
		return b.toString();
	}

	@Override
	public String toString() {
		return "SqlServerCompositeRecordSource [model=" + model
				+ ", dbConfiguration=" + dbConfiguration + ", idsQuery="
				+ idsQuery + ", dsName=" + dsName + ", maxSize=" + maxSize
				+ "]";
	}

}
