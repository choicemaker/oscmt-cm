/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Aug 18, 2004
 *
 */
package com.choicemaker.cm.io.db.postgres2;

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
 * This is a wrapper object around PostgresParallelRecordSource and it can be
 * serialized, because it stores string values with which to create the
 * PostgresRecordSource.
 * 
 * This is faster than PostgresSerializableCompositeRecordSource because it uses
 * DbParallelReader instead of DbSerialReader.
 * 
 * @author pcheung
 *
 */
public class PostgresSerializableRecordSource implements
		ISerializableDbRecordSource {
	
	private static final long serialVersionUID = 271L;

	private static final Logger log = Logger
			.getLogger(PostgresSerializableRecordSource.class.getName());
	
	protected static final String DEFAULT_DS_MAP_NAME = null;
	protected static final int DEFAULT_MAX_COMPOSITE_SIZE = 0;

	private String dsJNDIName;
//	private String dsMapName;
	private String modelName;
	private String dbConfig;
	private String sqlQuery;
	
	private transient PostgresParallelRecordSource sqlRS;
	private transient DataSource ds;
	private transient ImmutableProbabilityModel model;

	/**
	 * Constructs a serializable version of {@link PostgresParallelRecordSource}.
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
	 * </pre>
	 */
	public PostgresSerializableRecordSource(String dsJNDIName,
			String modelName, String dbConfig, String sqlQuery) {
		this(dsJNDIName, DEFAULT_DS_MAP_NAME, modelName, dbConfig, sqlQuery,
				DEFAULT_MAX_COMPOSITE_SIZE);
	}

	/**
	 * A constructor with unused parameters but the same signature as
	 * PostgresSerializableCompositeRecordSource
	 * 
	 * @param dsJNDIName
	 *            JNDI name of a configured data source
	 * @param dsMapName
	 *            unused, may be null.
	 * @param modelName
	 *            Name of a configured model
	 * @param dbConfig
	 *            Name of a database configuration defined by the model
	 * @param sqlQuery
	 *            A SQL query that selects record IDs from the datasource; e.g.
	 * 
	 *            <pre>
	 * SELECT record_id AS ID FROM records
	 * </pre>
	 * @param maxCompositeSize
	 *            unused
	 */
	public PostgresSerializableRecordSource(String dsJNDIName,
			String dsMapName, String modelName, String dbConfig,
			String sqlQuery, int maxCompositeSize) {

		validateString(dsJNDIName, "null or blank JNDI name for data source");
		validateString(modelName, "null or blank model configuration name");
		validateString(dbConfig,
				"null or blank database configuration for model");
		validateString(sqlQuery,
				"null or blank SQL to select records from data source");

		this.dsJNDIName = dsJNDIName;
//		this.dsMapName = dsMapName;
		this.modelName = modelName;
		this.dbConfig = dbConfig;
		this.sqlQuery = sqlQuery;
	}
	
	private static void validateString(String s, String msg) {
		if (s == null || s.trim().length() == 0) {
			throw new IllegalArgumentException(msg);
		}
	}

	private DataSource getDataSource () {
		try {
			if (ds == null) {
				Context ctx = new InitialContext();
				ds = (DataSource) ctx.lookup (dsJNDIName);
			}
		} catch (NamingException ex) {
			log.severe(ex.toString());
		}
		return ds;
	}
	
	private PostgresParallelRecordSource getRS () {
		if (sqlRS == null) {
			sqlRS = new PostgresParallelRecordSource ("", getModel(),
				dsJNDIName, dbConfig, getSqlQuery());
			sqlRS.setDataSource(dsJNDIName, getDataSource());

		}
		return sqlRS;
	}
	
	@Override
	public ImmutableProbabilityModel getModel () {
		if (model == null) {
			model = PMManager.getModelInstance(modelName);
		}
		return model;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.RecordSource#getNext()
	 */
	@Override
	public Record getNext() throws IOException {
		return getRS().getNext();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#open()
	 */
	@Override
	public void open() throws IOException {
		getRS().open ();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#close()
	 */
	@Override
	public void close() throws IOException {
		getRS().close();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#hasNext()
	 */
	@Override
	public boolean hasNext() throws IOException {
		return getRS().hasNext();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#getName()
	 */
	@Override
	public String getName() {
		return getRS().getName();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		getRS().setName(name);
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#setModel(com.choicemaker.cm.core.ImmutableProbabilityModel)
	 */
	@Override
	public void setModel(ImmutableProbabilityModel m) {
		getRS().setModel(m);
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#hasSink()
	 */
	@Override
	public boolean hasSink() {
		return getRS().hasSink();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#getSink()
	 */
	@Override
	public Sink getSink() {
		return getRS().getSink();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.core.Source#getFileName()
	 */
	@Override
	public String getFileName() {
		return getRS().getFileName();
	}

	@Override
	public boolean equals (Object o) {
		if (o instanceof PostgresSerializableRecordSource) {
			PostgresSerializableRecordSource rs =
				(PostgresSerializableRecordSource) o;
			return rs.dbConfig.equals(this.dbConfig)
					&& rs.dsJNDIName.equals(this.dsJNDIName)
					&& rs.modelName.equals(this.modelName)
					&& rs.getSqlQuery().equals(this.getSqlQuery());
			// && rs.dsMapName.equals(this.dsMapName);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode () {
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

		Precondition.assertNonNullArgument("null properties",properties);

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

		// Reset the underlaying record source (it will be lazily recreated)
		try {
			if (sqlRS != null) {
				sqlRS.close();
			}
		} catch (Exception x) {
			String msg =
				"Unable to close "
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
		return "PostgresSerializableParallelSerialRecordSource [dsJNDIName="
				+ dsJNDIName + ", modelName=" + modelName + ", dbConfig="
				+ dbConfig + ", sqlQuery=" + sqlQuery + "]";
	}

}
