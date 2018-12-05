package com.choicemaker.cm.batch.ejb;

public interface IndexedPropertyJPA {

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
	String ID_GENERATOR_PK_COLUMN_VALUE = "IDX_PROP";
	String ID_GENERATOR_VALUE_COLUMN_NAME = "SEQ_COUNT";

	/**
	 * Name of a query that selects operational properties by job id and
	 * property name
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
	 * Name of a query that selects operational properties by job id and
	 * property name
	 */
	String QN_IDXPROP_FINDALL_BY_JOB = "idxPropFindAllByJob";

	/** JPQL used to implement {@link #QN_IDXPROP_FINDALL_BY_JOB} */
	String JPQL_IDXPROP_FINDALL_BY_JOB =
		"Select ipe from IndexedPropertyEntity ipe where ipe.jobId = :jobId";

	/**
	 * Name of the parameter used to specify the jobId of
	 * {@link #JPQL_IDXPROP_FINDALL_BY_JOB}
	 */
	String PN_IDXPROP_FINDALL_BY_JOB_P1 = "jobId";

	/**
	 * Name of a query that deletes operational properties by job id
	 */
	String QN_IDXPROP_DELETE_BY_JOB = "idxPropDeleteByJob";

	/** JPQL used to implement {@link #QN_IDXPROP_DELETE_BY_JOB} */
	String JPQL_IDXPROP_DELETE_BY_JOB =
		"Delete from IndexedPropertyEntity ipe where ipe.jobId = :jobId";

	/**
	 * Name of the parameter used to specify the jobId of
	 * {@link #JPQL_IDXPROP_DELETE_BY_JOB}
	 */
	String PN_IDXPROP_DELETE_BY_JOB_P1 = "jobId";

	/**
	 * Name of a query that selects operational properties by job id and
	 * property name
	 */
	String QN_IDXPROP_FINDALL = "idxPropFindAll";

	/** JPQL used to implement {@link #QN_IDXPROP_FINDALL} */
	String JPQL_IDXPROP_FINDALL =
		"Select ipe from IndexedPropertyEntity ipe";

}
