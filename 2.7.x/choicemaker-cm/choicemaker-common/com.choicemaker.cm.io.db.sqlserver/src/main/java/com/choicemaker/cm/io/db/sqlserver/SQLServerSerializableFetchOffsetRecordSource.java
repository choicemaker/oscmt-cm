/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Aug 18, 2004
 *
 */
package com.choicemaker.cm.io.db.sqlserver;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.choicemaker.cm.core.ISerializableDbRecordSource;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.IncompleteSpecificationException;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.Sink;
import com.choicemaker.cm.core.base.AbstractRecordSourceSerializer;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.util.Precondition;
import com.choicemaker.util.StringUtils;

/**
 * This is a wrapper object around SqlServerFetchOffsetRecordSource and it can be
 * serialized, because it stores string values with which to create the
 * SqlServerRecordSource.
 * 
 * This is faster than SQLServerSerializableCompositeRecordSource because it
 * uses DbParallelReader instead of DbSerialReader.
 * 
 * @author pcheung
 *
 */
public class SQLServerSerializableFetchOffsetRecordSource
		implements ISerializableDbRecordSource {

	private static final long serialVersionUID = 271L;

	private static final Logger log = Logger.getLogger(
			SQLServerSerializableFetchOffsetRecordSource.class.getName());

	protected static final String DEFAULT_DS_MAP_NAME = null;
	protected static final int DEFAULT_MAX_COMPOSITE_SIZE = 0;

	private String dsJNDIName;
	private String modelName;
	private String dbConfig;
	private String sqlQuery;

	private transient SqlServerFetchOffsetRecordSource sqlRS;
	private transient DataSource ds;
	private transient ImmutableProbabilityModel model;

	/**
	 * Constructs a serializable version of
	 * {@link SqlServerFetchOffsetRecordSource}.
	 * 
	 * @param dsJNDIName
	 *            JNDI name of a configured data source
	 * @param modelName
	 *            Name of a configured model
	 * @param dbConfig
	 *            Name of a database configuration defined by the model
	 * @param sqlQuery
	 *            A SQL query that selects record IDs from the datasource; e.g.
	 * 
	 *            <pre>
	 * SELECT record_id AS ID FROM records
	 *            </pre>
	 */
	public SQLServerSerializableFetchOffsetRecordSource(String dsJNDIName,
			String modelName, String dbConfig, String sqlQuery) {

		Precondition.assertNonEmptyString(
				"null or blank JNDI name for data source", dsJNDIName);
		Precondition.assertNonEmptyString(
				"null or blank model configuration name", modelName);
		Precondition.assertNonEmptyString(
				"null or blank database configuration for model", dbConfig);
		Precondition.assertNonEmptyString(
				"null or blank SQL to select data source records", sqlQuery);

		this.dsJNDIName = dsJNDIName;
		this.modelName = modelName;
		this.dbConfig = dbConfig;
		this.sqlQuery = sqlQuery;
	}

	private DataSource getDataSource() {
		try {
			if (ds == null) {
				Context ctx = new InitialContext();
				ds = (DataSource) ctx.lookup(dsJNDIName);
			}
		} catch (NamingException ex) {
			log.severe(ex.toString());
		}
		return ds;
	}

	private SqlServerFetchOffsetRecordSource getRS() {
		if (sqlRS == null) {
			sqlRS = new SqlServerFetchOffsetRecordSource("", getModel(),
					dsJNDIName, dbConfig, getSqlQuery());
			sqlRS.setDataSource(dsJNDIName, getDataSource());

		}
		return sqlRS;
	}

	@Override
	public ImmutableProbabilityModel getModel() {
		if (model == null) {
			model = PMManager.getModelInstance(modelName);
		}
		return model;
	}

	@Override
	public Record<?> getNext() throws IOException {
		return getRS().getNext();
	}

	@Override
	public void open() throws IOException {
		getRS().open();
	}

	@Override
	public void close() throws IOException {
		getRS().close();
	}

	@Override
	public boolean hasNext() throws IOException {
		return getRS().hasNext();
	}

	@Override
	public String getName() {
		return getRS().getName();
	}

	@Override
	public void setName(String name) {
		getRS().setName(name);
	}

	@Override
	public void setModel(ImmutableProbabilityModel m) {
		getRS().setModel(m);
	}

	@Override
	public boolean hasSink() {
		return getRS().hasSink();
	}

	@Override
	public Sink getSink() {
		return getRS().getSink();
	}

	@Override
	public String getFileName() {
		return getRS().getFileName();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof SQLServerSerializableFetchOffsetRecordSource) {
			SQLServerSerializableFetchOffsetRecordSource rs =
				(SQLServerSerializableFetchOffsetRecordSource) o;
			return rs.dbConfig.equals(this.dbConfig)
					&& rs.dsJNDIName.equals(this.dsJNDIName)
					&& rs.modelName.equals(this.modelName)
					&& rs.getSqlQuery().equals(this.getSqlQuery());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return getSqlQuery().hashCode();
	}

	public String getDsJNDIName() {
		return dsJNDIName;
	}

	public String getModelName() {
		return modelName;
	}

	public String getDbConfig() {
		return dbConfig;
	}

	public String getSqlQuery() {
		return sqlQuery;
	}

	@Override
	public Properties getProperties() {
		Properties retVal = new Properties();
		retVal.setProperty(PN_DATASOURCE_JNDI_NAME, this.getDsJNDIName());
		retVal.setProperty(PN_MODEL_NAME, this.getModelName());
		retVal.setProperty(PN_DATABASE_CONFIG, this.getDbConfig());
		retVal.setProperty(PN_SQL_QUERY, this.getSqlQuery());
		return retVal;
	}

	@Override
	public void setProperties(Properties properties)
			throws IncompleteSpecificationException {

		Precondition.assertNonNullArgument("null properties", properties);

		String s = properties.getProperty(PN_DATABASE_CONFIG);
		if (!StringUtils.nonEmptyString(s)) {
			String msg = "Missing property '" + PN_DATABASE_CONFIG + "'";
			log.severe(msg);
			throw new IncompleteSpecificationException(msg);
		}
		this.dbConfig = s;

		s = properties.getProperty(PN_DATASOURCE_JNDI_NAME);
		if (!StringUtils.nonEmptyString(s)) {
			String msg = "Missing property '" + PN_DATASOURCE_JNDI_NAME + "'";
			log.severe(msg);
			throw new IncompleteSpecificationException(msg);
		}
		this.dsJNDIName = s;

		s = properties.getProperty(PN_MODEL_NAME);
		if (!StringUtils.nonEmptyString(s)) {
			String msg = "Missing property '" + PN_MODEL_NAME + "'";
			log.severe(msg);
			throw new IncompleteSpecificationException(msg);
		}
		this.modelName = s;

		s = properties.getProperty(PN_SQL_QUERY);
		if (!StringUtils.nonEmptyString(s)) {
			String msg = "Missing property '" + PN_SQL_QUERY + "'";
			log.severe(msg);
			throw new IncompleteSpecificationException(msg);
		}
		this.sqlQuery = s;

		// Reset the underlying record source (it will be lazily recreated)
		try {
			if (sqlRS != null) {
				sqlRS.close();
			}
		} catch (Exception x) {
			String msg = "Unable to close "
					+ (sqlRS == null ? "record source" : sqlRS.getName());
			log.warning(msg);
		} finally {
			sqlRS = null;
		}
	}

	@Override
	public String toXML() {
		String retVal = AbstractRecordSourceSerializer.toXML(this);
		return retVal;
	}

	@Override
	public String toString() {
		return "SQLServerSerializableParallelSerialRecordSource [dsJNDIName="
				+ dsJNDIName + ", modelName=" + modelName + ", dbConfig="
				+ dbConfig + ", sqlQuery=" + sqlQuery + "]";
	}

}
