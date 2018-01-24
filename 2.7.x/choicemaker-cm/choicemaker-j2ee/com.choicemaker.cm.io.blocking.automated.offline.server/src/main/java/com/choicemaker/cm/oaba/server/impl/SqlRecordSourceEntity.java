/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.server.impl;

import static com.choicemaker.cm.oaba.server.impl.SqlRecordSourceJPA.CN_ABA_PLUGIN;
import static com.choicemaker.cm.oaba.server.impl.SqlRecordSourceJPA.CN_CM_IO_CLASS;
import static com.choicemaker.cm.oaba.server.impl.SqlRecordSourceJPA.CN_DATASOURCE;
import static com.choicemaker.cm.oaba.server.impl.SqlRecordSourceJPA.CN_DBCONFIG;
import static com.choicemaker.cm.oaba.server.impl.SqlRecordSourceJPA.CN_MODEL;
import static com.choicemaker.cm.oaba.server.impl.SqlRecordSourceJPA.CN_SQL;
import static com.choicemaker.cm.oaba.server.impl.SqlRecordSourceJPA.DISCRIMINATOR_VALUE;
import static com.choicemaker.cm.oaba.server.impl.SqlRecordSourceJPA.JPQL_SQLRS_FIND_ALL;
import static com.choicemaker.cm.oaba.server.impl.SqlRecordSourceJPA.QN_SQLRS_FIND_ALL;
import static com.choicemaker.cm.oaba.server.impl.SqlRecordSourceJPA.TABLE_NAME;

import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.choicemaker.cm.args.PersistableSqlRecordSource;

@NamedQuery(name = QN_SQLRS_FIND_ALL, query = JPQL_SQLRS_FIND_ALL)
@Entity
@Table(/* schema = "CHOICEMAKER", */name = TABLE_NAME)
@DiscriminatorValue(DISCRIMINATOR_VALUE)
public class SqlRecordSourceEntity extends BaseRecordSourceEntity implements
		PersistableSqlRecordSource {

	private static final Logger logger = Logger.getLogger(SqlRecordSourceEntity.class.getName());

	private static final long serialVersionUID = 271L;

	// -- Instance data

	@Column(name = CN_CM_IO_CLASS)
	protected final String className;

	@Column(name = CN_DATASOURCE)
	protected final String dataSource;

	@Column(name = CN_MODEL)
	protected final String modelId;

	@Column(name = CN_SQL)
	protected final String sql;

	@Column(name = CN_DBCONFIG)
	protected final String dbConfig;

	@Column(name = CN_ABA_PLUGIN)
	protected final String abaAccessor;

	// -- Constructors

	/** Required by JPA */
	protected SqlRecordSourceEntity() {
		super(TYPE);
		this.className = null;
		this.dataSource = null;
		this.modelId = null;
		this.sql = null;
		this.dbConfig = null;
		this.abaAccessor = null;
	}

	public SqlRecordSourceEntity(PersistableSqlRecordSource psrs) {
		this(psrs.getDatabaseReader(), psrs.getDataSource(), psrs.getModelId(), psrs
				.getSqlSelectStatement(), psrs.getDatabaseConfiguration(),
				psrs.getDatabaseAccessor());
	}

	public SqlRecordSourceEntity(String className, String dataSource,
			String model, String sql, String dbConfig, String abaAccessor) {
		super(TYPE);

		if (className == null || !className.equals(className.trim())
				|| className.isEmpty()) {
			String msg = "invalid class name '" + className + "'";
			throw new IllegalArgumentException(msg);
		}
		if (dataSource == null || !dataSource.equals(dataSource.trim())
				|| dataSource.isEmpty()) {
			String msg = "invalid data source name '" + dataSource + "'";
			throw new IllegalArgumentException(msg);
		}
		if (model == null || !model.equals(model.trim()) || model.isEmpty()) {
			String msg = "invalid model id '" + model + "'";
			throw new IllegalArgumentException(msg);
		}
		if (sql == null || !sql.equals(sql.trim()) || sql.isEmpty()) {
			String msg = "invalid sql select statement '" + sql + "'";
			throw new IllegalArgumentException(msg);
		}
		if (dbConfig == null || !dbConfig.equals(dbConfig.trim())
				|| dbConfig.isEmpty()) {
			String msg = "invalid data configuration name '" + dbConfig + "'";
			throw new IllegalArgumentException(msg);
		}
		if (abaAccessor == null || !abaAccessor.equals(abaAccessor.trim())
				|| abaAccessor.isEmpty()) {
			String msg = "null or blank ABA database accessor'" + abaAccessor + "'";
			logger.fine(msg);
			abaAccessor = null;
		} else {
			String msg = "non-null ABA database accessor'" + abaAccessor + "'";
			logger.fine(msg);
		}

		this.className = className;
		this.dataSource = dataSource;
		this.modelId = model;
		this.sql = sql;
		this.dbConfig = dbConfig;
		this.abaAccessor = abaAccessor;
	}

	// -- Accessors

	@Override
	public String getDatabaseReader() {
		return className;
	}

	@Override
	public String getDataSource() {
		return dataSource;
	}

	@Override
	public String getModelId() {
		return modelId;
	}

	@Override
	public String getSqlSelectStatement() {
		return sql;
	}

	@Override
	public String getDatabaseConfiguration() {
		return dbConfig;
	}

	@Override
	public String getDatabaseAccessor() {
		return abaAccessor;
	}

	// -- Identity

	@Override
	public String toString() {
		return "SqlRecordSource [getId()=" + getId() + ", getDataSourceName()="
				+ getDataSource() + ", getSqlSelectStatement()="
				+ getSqlSelectStatement() + "]";
	}

}
