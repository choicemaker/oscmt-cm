/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

/**
 * Java Persistence API (JPA) for OabaBatchProcessingEvent beans.<br/>
 * Prefixes:
 * <ul>
 * <li>JPQL -- Java Persistence Query Language</li>
 * <li>QN -- Query Name</li>
 * <li>CN -- Column Name</li>
 * </ul>
 * 
 * @author rphall
 */
public interface OabaProcessingEventJPA {

	/** Name of the table that persists batch job data */
	String TABLE_NAME = "CMT_OABA_PROCESSING";

	/** Name of the column used to distinguish between job types */
	String DISCRIMINATOR_COLUMN = "JOB_TYPE";

	/**
	 * Value of the discriminator column used to mark OabaBatchProcessingEvent types
	 * (and not sub-types)
	 */
	String DISCRIMINATOR_VALUE = "OABA";

	/**
	 * Generated id column.
	 * 
	 * @see #ID_GENERATOR_NAME
	 */
	String CN_ID = "ID";

	/**
	 * Discriminator column
	 * 
	 * @see #DISCRIMINATOR_COLUMN
	 */
	String CN_TYPE = DISCRIMINATOR_COLUMN;

	/** Link to a batch job */
	String CN_JOB_ID = "JOB_ID";

	/**
	 * A value defined by
	 * {@link com.choicemaker.cm.oaba.core.OabaProcessing
	 * OabaProcessing}
	 */
	String CN_EVENT_ID = "EVENT_ID";

	/**
	 * Free-form information.
	 */
	// FIXME: this column sometimes stores operational parameters
	String CN_INFO = "INFO";

	String CN_TIMESTAMP = "TIMESTAMP";

	String ID_GENERATOR_NAME = "OABA_PROCESSING";

	String ID_GENERATOR_TABLE = "CMT_SEQUENCE";

	String ID_GENERATOR_PK_COLUMN_NAME = "SEQ_NAME";

	String ID_GENERATOR_PK_COLUMN_VALUE = "OABA_PROCESSING";

	String ID_GENERATOR_VALUE_COLUMN_NAME = "SEQ_COUNT";

	/** Name of the query that finds all persistent status entries */
	String QN_OABAPROCESSING_FIND_ALL = "oabaProcessingFindAll";

	/** JPQL used to implement {@link #QN_OABAPROCESSING_FIND_ALL} */
	String JPQL_OABAPROCESSING_FIND_ALL =
		"Select o from OabaProcessingEventEntity o";

	/**
	 * Name of the query that finds all persistent status entries for a
	 * particular OABA job, ordered by descending timestamp
	 */
	String QN_OABAPROCESSING_FIND_BY_JOBID = "oabaProcessingFindByJobId";

	/** JPQL used to implement {@link #QN_OABAPROCESSING_FIND_BY_JOBID} */
	String JPQL_OABAPROCESSING_FIND_BY_JOBID =
		"SELECT o FROM OabaProcessingEventEntity o WHERE o.jobId = :jobId "
				+ "ORDER BY o.eventTimestamp DESC, o.id DESC";

	/**
	 * Name of the parameter used to specify the jobId parameter of
	 * {@link #QN_OABAPROCESSING_FIND_BY_JOBID}
	 */
	String PN_OABAPROCESSING_FIND_BY_JOBID_JOBID = "jobId";

	/**
	 * Name of the query that deletes all persistent status entries for a
	 * particular OABA job
	 */
	String QN_OABAPROCESSING_DELETE_BY_JOBID = "oabaProcessingDeleteByJobId";

	/** JPQL used to implement {@link #QN_OABAPROCESSING_DELETE_BY_JOBID} */
	String JPQL_OABAPROCESSING_DELETE_BY_JOBID =
		"DELETE FROM OabaProcessingEventEntity o WHERE o.jobId = :jobId";

	/**
	 * Name of the parameter used to specify the jobId parameter of
	 * {@link #QN_OABAPROCESSING_DELETE_BY_JOBID}
	 */
	String PN_OABAPROCESSING_DELETE_BY_JOBID_JOBID = "jobId";

}
