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

public interface OperationalPropertyJPA {

	/** Name of the table that persists batch job data */
	String TABLE_NAME = "CMT_OPERATION_PROPERTY";

	/**
	 * Generated persistence key.
	 * 
	 * @see #ID_GENERATOR_NAME
	 */
	String CN_ID = "ID";

	/** Required link to a batch job */
	String CN_JOB_ID = "JOB_ID";

	/** Name of the property */
	String CN_NAME = "NAME";

	/** Value of the property */
	String CN_VALUE = "VALUE";

	String ID_GENERATOR_NAME = "OPERATIONAL_PROPERTY";
	String ID_GENERATOR_TABLE = "CMT_SEQUENCE";
	String ID_GENERATOR_PK_COLUMN_NAME = "SEQ_NAME";
	String ID_GENERATOR_PK_COLUMN_VALUE = "OP_PROP";
	String ID_GENERATOR_VALUE_COLUMN_NAME = "SEQ_COUNT";

	/**
	 * Name of a query that selects operational properties by job id and
	 * property name
	 */
	String QN_OPPROP_FIND_BY_JOB_PNAME = "opPropFindByJobPname";

	/** JPQL used to implement {@link #QN_OPPROP_FIND_BY_JOB_PNAME} */
	String JPQL_OPPROP_FIND_BY_JOB_PNAME =
		"Select ope from OperationalPropertyEntity ope "
				+ "where ope.jobId = :jobId and ope.name = :name";

	/**
	 * Name of the parameter used to specify the jobId of
	 * {@link #JPQL_OPPROP_FIND_BY_JOB_PNAME}
	 */
	String PN_OPPROP_FIND_BY_JOB_PNAME_P1 = "jobId";

	/**
	 * Name of the parameter used to specify the property name of
	 * {@link #JPQL_OPPROP_FIND_BY_JOB_PNAME}
	 */
	String PN_OPPROP_FIND_BY_JOB_PNAME_P2 = "name";

	/**
	 * Name of a query that selects operational properties by job id and
	 * property name
	 */
	String QN_OPPROP_FINDALL_BY_JOB = "opPropFindAllByJob";

	/** JPQL used to implement {@link #QN_OPPROP_FINDALL_BY_JOB} */
	String JPQL_OPPROP_FINDALL_BY_JOB =
		"Select ope from OperationalPropertyEntity ope where ope.jobId = :jobId";

	/**
	 * Name of the parameter used to specify the jobId of
	 * {@link #JPQL_OPPROP_FINDALL_BY_JOB}
	 */
	String PN_OPPROP_FINDALL_BY_JOB_P1 = "jobId";

	/**
	 * Name of a query that deletes operational properties by job id
	 */
	String QN_OPPROP_DELETE_BY_JOB = "opPropDeleteByJob";

	/** JPQL used to implement {@link #QN_OPPROP_DELETE_BY_JOB} */
	String JPQL_OPPROP_DELETE_BY_JOB =
		"Delete from OperationalPropertyEntity ope where ope.jobId = :jobId";

	/**
	 * Name of the parameter used to specify the jobId of
	 * {@link #JPQL_OPPROP_DELETE_BY_JOB}
	 */
	String PN_OPPROP_DELETE_BY_JOB_P1 = "jobId";

	/**
	 * Name of a query that selects operational properties by job id and
	 * property name
	 */
	String QN_OPPROP_FINDALL = "opPropFindAll";

	/** JPQL used to implement {@link #QN_OPPROP_FINDALL} */
	String JPQL_OPPROP_FINDALL =
		"Select ope from OperationalPropertyEntity ope";

}
