/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

/**
 * Java Persistence API (JPA) for RecordIdTranslation beans.<br/>
 * Prefixes:
 * <ul>
 * <li>JPQL -- Java Persistence Query Language</li>
 * <li>QN -- Query Name</li>
 * <li>CN -- Column Name</li>
 * <li>DV -- Discriminator Value</li>
 * </ul>
 * 
 * @author rphall
 */
public interface RecordIdTranslationJPA {

	/** Name of the table that persists record-id translations */
	String TABLE_NAME = "CMT_RECORD_ID";

	/** Name of the column used to distinguish between record-id types */
	String DISCRIMINATOR_COLUMN = "TYPE";

	/** Discriminator value column used to mark abstract record-id types */
	String DV_ABSTRACT = "0";

	/** Discriminator value column used to mark integer record-id types */
	String DV_INTEGER = "1";

	/** Discriminator value column used to mark long record-id types */
	String DV_LONG = "2";

	/** Discriminator value column used to mark String record-id types */
	String DV_STRING = "3";

	String ID_GENERATOR_NAME = "OABA_TRANSLATED_ID";
	String ID_GENERATOR_TABLE = "CMT_SEQUENCE";
	String ID_GENERATOR_PK_COLUMN_NAME = "SEQ_NAME";
	String ID_GENERATOR_PK_COLUMN_VALUE = "TRANSLATED_ID";
	String ID_GENERATOR_VALUE_COLUMN_NAME = "SEQ_COUNT";

	/**
	 * Column for generated persistence identifier
	 * 
	 * @see #ID_GENERATOR_NAME
	 */
	String CN_ID = "ID";

	/**
	 * Discriminator column
	 * 
	 * @see #DISCRIMINATOR_COLUMN
	 */
	String CN_RECORD_TYPE = DISCRIMINATOR_COLUMN;

	/** Link to a batch job */
	String CN_JOB_ID = "JOB_ID";

	/** Record identifier */
	String CN_RECORD_ID = "RECORD_ID";

	/** Translated record identifier */
	String CN_TRANSLATED_ID = "TRANSLATED_ID";

	/** Record source -- master or staging */
	String CN_RECORD_SOURCE = "SOURCE";

	// -- Queries for translations of abstract record ids

	/**
	 * Name of the query that finds all persistent, abstract translation entries
	 */
	String QN_TRANSLATEDID_FIND_ALL = "oabaTranslatedAbstractIdFindAll";

	/** JPQL used to implement {@link #QN_TRANSLATEDID_FIND_ALL} */
	String JPQL_TRANSLATEDID_FIND_ALL =
		"SELECT o FROM AbstractRecordIdTranslationEntity o "
				+ "ORDER BY o.jobId, o.translatedId";

	/**
	 * Name of the query that finds all persistent, abstract translation entries
	 * for a particular OABA job
	 */
	String QN_TRANSLATEDID_FIND_BY_JOBID =
		"oabaTranslatedAbstractIdFindByJobId";

	/** JPQL used to implement {@link #QN_TRANSLATEDID_FIND_BY_JOBID} */
	String JPQL_TRANSLATEDID_FIND_BY_JOBID =
		"SELECT o FROM AbstractRecordIdTranslationEntity o "
				+ "WHERE o.jobId = :jobId ORDER BY o.translatedId";

	/**
	 * Name of the parameter used to specify the jobId parameter of
	 * {@link #QN_TRANSLATEDID_FIND_BY_JOBID}
	 */
	String PN_TRANSLATEDID_FIND_BY_JOBID_JOBID = "jobId";

	/**
	 * Name of the query that deletes all persistent entries for a particular
	 * OABA job
	 */
	String QN_TRANSLATEDID_DELETE_BY_JOBID =
		"oabaTranslatedAbstractIdDeleteByJobId";

	/** JPQL used to implement {@link #QN_TRANSLATEDID_DELETE_BY_JOBID} */
	String JPQL_TRANSLATEDID_DELETE_BY_JOBID =
		"DELETE FROM AbstractRecordIdTranslationEntity o "
				+ "WHERE o.jobId = :jobId";

	/**
	 * Name of the parameter used to specify the jobId parameter of
	 * {@link #QN_TRANSLATEDID_DELETE_BY_JOBID}
	 */
	String PN_TRANSLATEDID_DELETE_BY_JOBID_JOBID = "jobId";

	// -- Queries for translations of integer record ids

	/**
	 * Name of the query that finds all persistent, Integer id translation
	 * entries
	 */
	String QN_TRANSLATEDINTEGERID_FIND_ALL = "oabaTranslatedIntegerIdFindAll";

	/** JPQL used to implement {@link #QN_TRANSLATEDINTEGERID_FIND_ALL} */
	String JPQL_TRANSLATEDINTEGERID_FIND_ALL =
		"SELECT o FROM RecordIdIntegerTranslation o "
				+ "ORDER BY o.jobId, o.translatedId";

	/**
	 * Name of the query that finds all persistent, Integer id translation
	 * entries for a particular OABA job
	 */
	String QN_TRANSLATEDINTEGERID_FIND_BY_JOBID =
		"oabaTranslatedIntegerIdFindByJobId";

	/** JPQL used to implement {@link #QN_TRANSLATEDINTEGERID_FIND_BY_JOBID} */
	String JPQL_TRANSLATEDINTEGERID_FIND_BY_JOBID =
		"SELECT o FROM RecordIdIntegerTranslation o "
				+ "WHERE o.jobId = :jobId ORDER BY o.translatedId";

	/**
	 * Name of the parameter used to specify the jobId parameter of
	 * {@link #QN_TRANSLATEDINTEGERID_FIND_BY_JOBID}
	 */
	String PN_TRANSLATEDINTEGERID_FIND_BY_JOBID_JOBID = "jobId";

	// -- Queries for translations of long record ids

	/**
	 * Name of the query that finds all persistent, Long id translation entries
	 */
	String QN_TRANSLATEDLONGID_FIND_ALL = "oabaTranslatedLongIdFindAll";

	/** JPQL used to implement {@link #QN_TRANSLATEDLONGID_FIND_ALL} */
	String JPQL_TRANSLATEDLONGID_FIND_ALL =
		"SELECT o FROM RecordIdLongTranslation o "
				+ "ORDER BY o.jobId, o.translatedId";

	/**
	 * Name of the query that finds all persistent, Long id translation entries
	 * for a particular OABA job
	 */
	String QN_TRANSLATEDLONGID_FIND_BY_JOBID =
		"oabaTranslatedLongIdFindByJobId";

	/** JPQL used to implement {@link #QN_TRANSLATEDLONGID_FIND_BY_JOBID} */
	String JPQL_TRANSLATEDLONGID_FIND_BY_JOBID =
		"SELECT o FROM RecordIdLongTranslation o "
				+ "WHERE o.jobId = :jobId ORDER BY o.translatedId";

	/**
	 * Name of the parameter used to specify the jobId parameter of
	 * {@link #QN_TRANSLATEDLONGID_FIND_BY_JOBID}
	 */
	String PN_TRANSLATEDLONGID_FIND_BY_JOBID_JOBID = "jobId";

	// -- Queries for translations of String record ids

	/**
	 * Name of the query that finds all persistent, String id translation
	 * entries
	 */
	String QN_TRANSLATEDSTRINGID_FIND_ALL = "oabaTranslatedStringIdFindAll";

	/** JPQL used to implement {@link #QN_TRANSLATEDSTRINGID_FIND_ALL} */
	String JPQL_TRANSLATEDSTRINGID_FIND_ALL =
		"SELECT o FROM RecordIdStringTranslation o "
				+ "ORDER BY o.jobId, o.translatedId";

	/**
	 * Name of the query that finds all persistent, String id translation
	 * entries for a particular OABA job
	 */
	String QN_TRANSLATEDSTRINGID_FIND_BY_JOBID =
		"oabaTranslatedStringIdFindByJobId";

	/** JPQL used to implement {@link #QN_TRANSLATEDSTRINGID_FIND_BY_JOBID} */
	String JPQL_TRANSLATEDSTRINGID_FIND_BY_JOBID =
		"SELECT o FROM RecordIdStringTranslation o "
				+ "WHERE o.jobId = :jobId ORDER BY o.translatedId";

	/**
	 * Name of the parameter used to specify the jobId parameter of
	 * {@link #QN_TRANSLATEDSTRINGID_FIND_BY_JOBID}
	 */
	String PN_TRANSLATEDSTRINGID_FIND_BY_JOBID_JOBID = "jobId";

}
