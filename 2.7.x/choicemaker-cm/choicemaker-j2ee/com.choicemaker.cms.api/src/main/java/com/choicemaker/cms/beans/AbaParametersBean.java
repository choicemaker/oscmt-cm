/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.cms.beans;

import com.choicemaker.cms.api.AbaParameters;

public class AbaParametersBean implements AbaParameters {

	private static final long serialVersionUID = 1L;

	private long id;
	private String databaseAccessorName;
	private String databaseReaderName;
	private float highThreshold;
	private float lowThreshold;
	private String modelConfigurationName;
	private String queryToReferenceBlockingConfiguration;
	private String referenceDatabaseConfiguration;
	private String referenceDatasource;
	private String referenceSelectionView;
	private String referenceSelectionViewAsSQL;

	public AbaParametersBean() {
	}

	/** Treats a non-persistent copy of the specified template */
	public AbaParametersBean(AbaParameters template) {
		this.databaseAccessorName = template.getDatabaseAccessorName();
		this.databaseReaderName = template.getDatabaseReaderName();
		this.highThreshold = template.getHighThreshold();
		this.lowThreshold = template.getLowThreshold();
		this.modelConfigurationName = template.getModelConfigurationName();
		this.queryToReferenceBlockingConfiguration =
			template.getQueryToReferenceBlockingConfiguration();
		this.referenceDatabaseConfiguration =
			template.getReferenceDatabaseConfiguration();
		this.referenceDatasource = template.getReferenceDatasource();
		this.referenceSelectionView = template.getReferenceSelectionView();
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public boolean isPersistent() {
		return getId() != NONPERSISTENT_PARAMETERS_ID;
	}

	@Override
	public String getDatabaseAccessorName() {
		return databaseAccessorName;
	}

	@Override
	public String getDatabaseReaderName() {
		return databaseReaderName;
	}

	@Override
	public float getHighThreshold() {
		return highThreshold;
	}

	@Override
	public float getLowThreshold() {
		return lowThreshold;
	}

	@Override
	public String getModelConfigurationName() {
		return modelConfigurationName;
	}

	@Override
	public String getQueryToReferenceBlockingConfiguration() {
		return queryToReferenceBlockingConfiguration;
	}

	public void setQueryToReferenceBlockingConfiguration(
			String queryToReferenceBlockingConfiguration) {
		this.queryToReferenceBlockingConfiguration =
			queryToReferenceBlockingConfiguration;
	}

	public void setReferenceDatabaseConfiguration(
			String referenceDatabaseConfiguration) {
		this.referenceDatabaseConfiguration = referenceDatabaseConfiguration;
	}

	@Override
	public String getReferenceDatabaseConfiguration() {
		return referenceDatabaseConfiguration;
	}

	@Override
	public String getReferenceDatasource() {
		return referenceDatasource;
	}

	@Override
	public String getReferenceSelectionView() {
		return referenceSelectionView;
	}

	@Override
	public String getReferenceSelectionViewAsSQL() {
		return referenceSelectionViewAsSQL;
	}

	public void setDatabaseAccessorName(String databaseAccessorName) {
		this.databaseAccessorName = databaseAccessorName;
	}

	public void setDatabaseReaderName(String databaseReaderName) {
		this.databaseReaderName = databaseReaderName;
	}

	public void setHighThreshold(float highThreshold) {
		this.highThreshold = highThreshold;
	}

	public void setLowThreshold(float lowThreshold) {
		this.lowThreshold = lowThreshold;
	}

	public void setModelConfigurationName(String modelConfigurationName) {
		this.modelConfigurationName = modelConfigurationName;
	}

	public void setReferenceDatasource(String referenceDatasource) {
		this.referenceDatasource = referenceDatasource;
	}

	public void setReferenceSelectionView(String referenceSelectionView) {
		this.referenceSelectionView = referenceSelectionView;
	}

	@Deprecated
	public void setReferenceSelectionViewAsSQL(String name, String sql) {
		this.referenceSelectionView = name;
		this.referenceSelectionViewAsSQL = sql;
	}

}
