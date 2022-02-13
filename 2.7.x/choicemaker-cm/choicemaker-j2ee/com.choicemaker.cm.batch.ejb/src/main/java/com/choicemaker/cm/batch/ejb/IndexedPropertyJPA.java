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
package com.choicemaker.cm.batch.ejb;

public interface IndexedPropertyJPA {

	/** Name of the table that persists batch job data */
	String TABLE_NAME = "CMT_INDEXED_PROPERTY";

	/**
	 * Generated persistence key.
	 * 
	 * @see #ID_GENERATOR_NAME
	 */
	String CN_ID = "ID";

	/** Required link to a batch job */
	String CN_JOB_ID = "JOB_ID";

	/** Name of the property */
	String CN_NAME = "PNAME";

	/** Index of the property */
	String CN_INDEX = "PINDEX";

	/** Value of the property */
	String CN_VALUE = "PVALUE";

	String ID_GENERATOR_NAME = "INDEXED_PROPERTY";
	String ID_GENERATOR_TABLE = "CMT_SEQUENCE";
	String ID_GENERATOR_PK_COLUMN_NAME = "SEQ_NAME";
	String ID_GENERATOR_PK_COLUMN_VALUE = "IDX_PROP";
	String ID_GENERATOR_VALUE_COLUMN_NAME = "SEQ_COUNT";

	/**
	 * Name of a query that selects indexed properties by job id and property
	 * name
	 */
	String QN_IDXPROP_FIND_BY_JOB_PNAME_INDEX = "idxPropFindByJobPnameIndex";

	/** JPQL used to implement {@link #QN_IDXPROP_FIND_BY_JOB_PNAME_INDEX} */
	String JPQL_IDXPROP_FIND_BY_JOB_PNAME_INDEX =
		"Select ipe from IndexedPropertyEntity ipe "
				+ "where ipe.jobId = :jobId and ipe.name = :name "
				+ "and ipe.index = :index";

	/**
	 * Name of the parameter used to specify the jobId of
	 * {@link #JPQL_IDXPROP_FIND_BY_JOB_PNAME_INDEX}
	 */
	String PN_IDXPROP_FIND_BY_JOB_PNAME_INDEX_P1 = "jobId";

	/**
	 * Name of the parameter used to specify the property name of
	 * {@link #JPQL_IDXPROP_FIND_BY_JOB_PNAME_INDEX}
	 */
	String PN_IDXPROP_FIND_BY_JOB_PNAME_INDEX_P2 = "name";

	/**
	 * Name of the parameter used to specify the index of
	 * {@link #JPQL_IDXPROP_FIND_BY_JOB_PNAME_INDEX}
	 */
	String PN_IDXPROP_FIND_BY_JOB_PNAME_INDEX_P3 = "index";

	/**
	 * Name of a query that selects indexed properties by job id and property
	 * name
	 */
	String QN_IDXPROP_FIND_BY_JOB_PNAME = "idxPropFindByJobPname";

	/** JPQL used to implement {@link #QN_IDXPROP_FIND_BY_JOB_PNAME} */
	String JPQL_IDXPROP_FIND_BY_JOB_PNAME =
		"Select ipe from IndexedPropertyEntity ipe "
				+ "where ipe.jobId = :jobId and ipe.name = :name";

	/**
	 * Name of the parameter used to specify the jobId of
	 * {@link #JPQL_IDXPROP_FIND_BY_JOB_PNAME}
	 */
	String PN_IDXPROP_FIND_BY_JOB_PNAME_P1 = "jobId";

	/**
	 * Name of the parameter used to specify the property name of
	 * {@link #JPQL_IDXPROP_FIND_BY_JOB_PNAME}
	 */
	String PN_IDXPROP_FIND_BY_JOB_PNAME_P2 = "name";

	/**
	 * Name of a query that selects operational properties by job id
	 */
	String QN_IDXPROP_FIND_BY_JOB = "idxPropFindByJob";

	/** JPQL used to implement {@link #QN_IDXPROP_FIND_BY_JOB} */
	String JPQL_IDXPROP_FIND_BY_JOB =
		"Select ipe from IndexedPropertyEntity ipe where ipe.jobId = :jobId";

	/**
	 * Name of the parameter used to specify the jobId of
	 * {@link #JPQL_IDXPROP_FIND_BY_JOB}
	 */
	String PN_IDXPROP_FIND_BY_JOB_P1 = "jobId";

	/**
	 * Name of a query that deletes indexed properties by job id and property
	 * name
	 */
	String QN_IDXPROP_DELETE_BY_JOB_PNAME = "idxPropDeleteByJobPname";

	/** JPQL used to implement {@link #QN_IDXPROP_DELETE_BY_JOB_PNAME} */
	String JPQL_IDXPROP_DELETE_BY_JOB_PNAME =
		"Delete from IndexedPropertyEntity ipe where ipe.jobId = :jobId "
		+ "and ipe.name = :name";

	/**
	 * Name of the parameter used to specify the jobId of
	 * {@link #JPQL_IDXPROP_DELETE_BY_JOB_NAME_PNAME}
	 */
	String PN_IDXPROP_DELETE_BY_JOB_PNAME_P1 = "jobId";

	/**
	 * Name of the parameter used to specify the indexed property of
	 * {@link #JPQL_IDXPROP_DELETE_BY_JOB_PNAME}
	 */
	String PN_IDXPROP_DELETE_BY_JOB_PNAME_P2 = "name";

}
