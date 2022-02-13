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
package com.choicemaker.cm.io.db.sqlserver;

import java.io.IOException;
import java.util.Properties;

import com.choicemaker.cm.core.ISerializableDbRecordSource;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.IncompleteSpecificationException;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.Sink;

/**
 * This is a wrapper object around SqlServerParallelRecordSource and it can be
 * serialized, because it stores string values with which to create the
 * SqlServerRecordSource.
 * 
 * This is faster than SQLServerSerializableCompositeRecordSource because it
 * uses DbParallelReader instead of DbSerialReader.
 * 
 * @author pcheung
 * @deprecated Use SQLServerSerializableParallelRecordSource
 */
@Deprecated
public class SQLServerSerializableParallelSerialRecordSource
		implements ISerializableDbRecordSource {

	private static final long serialVersionUID = 271L;

	private final SQLServerSerializableParallelRecordSource delegate;

	@Override
	public ImmutableProbabilityModel getModel() {
		return delegate.getModel();
	}

	@Override
	public Record<?> getNext() throws IOException {
		return delegate.getNext();
	}

	@Override
	public void open() throws IOException {
		delegate.open();
	}

	@Override
	public void close() throws IOException {
		delegate.close();
	}

	@Override
	public boolean hasNext() throws IOException {
		return delegate.hasNext();
	}

	@Override
	public String getName() {
		return delegate.getName();
	}

	@Override
	public void setName(String name) {
		delegate.setName(name);
	}

	@Override
	public void setModel(ImmutableProbabilityModel m) {
		delegate.setModel(m);
	}

	@Override
	public boolean hasSink() {
		return delegate.hasSink();
	}

	@Override
	public Sink getSink() {
		return delegate.getSink();
	}

	@Override
	public String getFileName() {
		return delegate.getFileName();
	}

	public String getDsJNDIName() {
		return delegate.getDsJNDIName();
	}

	public String getModelName() {
		return delegate.getModelName();
	}

	public String getDbConfig() {
		return delegate.getDbConfig();
	}

	public String getSqlQuery() {
		return delegate.getSqlQuery();
	}

	@Override
	public Properties getProperties() {
		return delegate.getProperties();
	}

	@Override
	public void setProperties(Properties properties)
			throws IncompleteSpecificationException {
		delegate.setProperties(properties);
	}

	@Override
	public String toXML() {
		return delegate.toXML();
	}

	protected static final String DEFAULT_DS_MAP_NAME =
		SQLServerSerializableParallelRecordSource.DEFAULT_DS_MAP_NAME;
	protected static final int DEFAULT_MAX_COMPOSITE_SIZE =
		SQLServerSerializableParallelRecordSource.DEFAULT_MAX_COMPOSITE_SIZE;

	public SQLServerSerializableParallelSerialRecordSource(String dsJNDIName,
			String modelName, String dbConfig, String sqlQuery) {
		this(dsJNDIName, DEFAULT_DS_MAP_NAME, modelName, dbConfig, sqlQuery);
	}

	public SQLServerSerializableParallelSerialRecordSource(String dsJNDIName,
			String dsMapName, String modelName, String dbConfig,
			String sqlQuery) {
		this.delegate =
			new SQLServerSerializableParallelRecordSource(dsJNDIName, dsMapName,
					modelName, dbConfig, sqlQuery, DEFAULT_MAX_COMPOSITE_SIZE);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof SQLServerSerializableParallelSerialRecordSource) {
			SQLServerSerializableParallelSerialRecordSource rs =
				(SQLServerSerializableParallelSerialRecordSource) o;
			return rs.delegate.equals(this.delegate);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return getSqlQuery().hashCode();
	}

	@Override
	public String toString() {
		return "SQLServerSerializableParallelSerialRecordSource [dsJNDIName="
				+ this.getDsJNDIName() + ", modelName=" + this.getModelName()
				+ ", dbConfig=" + this.getDbConfig() + ", sqlQuery="
				+ this.getSqlQuery() + "]";
	}

}
