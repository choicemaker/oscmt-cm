/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.api;

import java.util.List;

import com.choicemaker.cm.args.OabaParameters;

public interface OabaParametersController {

	void delete(OabaParameters p);

	void detach(OabaParameters p);

	/** Find only instances of OabaParametersEntity (but no subclasses) */
	List<OabaParameters> findAllOabaParameters();

	/** Find all instances of AbstractParametersEntity and subclasses */
	List<AbstractParameters> findAllParameters();

	/** Find only an instance of OabaParametersEntity (but not any subclass) */
	OabaParameters findOabaParameters(long id);

	/** Find any instance of AbstractParametersEntity or its subclasses */
	AbstractParameters findParameters(long id);

	OabaParameters findOabaParametersByBatchJobId(long jobId);

	OabaParameters save(OabaParameters p);

	String getQueryDatabaseConfiguration(OabaParameters oabaParams);

	/**
	 * This accessor is not currently used. Database accessors are used with
	 * online blocking, in which single query records against a database of
	 * reference records. There is no current need to perform online blocking of
	 * query records against other query records.
	 */
	String getQueryDatabaseAccessor(OabaParameters oabaParams);

	String getReferenceDatabaseConfiguration(OabaParameters oabaParams);

	/**
	 * This accessor is used to perform online blocking of single query records
	 * against a database of reference records.
	 */
	String getReferenceDatabaseAccessor(OabaParameters oabaParams);

}