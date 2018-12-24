package com.choicemaker.cms.ejb;

import com.choicemaker.cm.batch.ejb.AbstractPersistentObjectJPA;
import com.choicemaker.cm.batch.ejb.BatchJobJPA;
import com.choicemaker.cm.oaba.ejb.AbaSettingsJPA;
import com.choicemaker.cm.oaba.ejb.AbstractParametersJPA;
import com.choicemaker.cm.oaba.ejb.DefaultSettingsJPA;
import com.choicemaker.cm.oaba.ejb.OabaSettingsJPA;
import com.choicemaker.cm.oaba.ejb.ServerConfigurationJPA;
import com.choicemaker.cm.oaba.ejb.SqlRecordSourceJPA;

public interface NamedConfigurationJPA {

	String TABLE_NAME = "CMT_NAMED_CONFIG2";

	// Basic persistence parameters
	String CN_ID = "NC_ID";
	String CN_UUID = AbstractPersistentObjectJPA.CN_UUID;
	String CN_OPTLOCK = AbstractPersistentObjectJPA.CN_OPTLOCK;

	/** Configuration name and description */
	String CN_CONF_NAME = "NC_NAME";
	String CN_CONF_DESCRIPTION = "NC_DESC";

	// Basic matching parameters
	String CN_MODEL = DefaultSettingsJPA.CN_MODEL;
	String CN_LOW_THRESHOLD = AbstractParametersJPA.CN_LOW_THRESHOLD;
	String CN_HIGH_THRESHOLD = AbstractParametersJPA.CN_HIGH_THRESHOLD;
	String CN_TASK = AbstractParametersJPA.CN_TASK;
	String CN_RIGOR = BatchJobJPA.CN_RIGOR;

	// Common record source parameters
	String CN_RS_TYPE = "RS_TYPE";
	String CN_DATASOURCE = SqlRecordSourceJPA.CN_DATASOURCE;
	String CN_CM_IO_CLASS = SqlRecordSourceJPA.CN_CM_IO_CLASS;
	String CN_BKCONF = "BLOCKING_CONF";

	// Query record source parameters
	String CN_QUERY_SQL = "QUERY_SQL";
	String CN_QUERY_DBCONF = "QUERY_DBCONF";
	String CN_QUERY_IS_DEDUPED = AbstractParametersJPA.CN_QUERY_RS_DEDUPED;

	// Reference record source parameters
	String CN_REF_SQL = "REF_SQL";
	String CN_REF_DBCONF = "REF_DBCONF";
	String CN_REF_DBACCESSOR = "REF_DBACC";
	String CN_REF_DBREADER = "REF_DBREAD";

	// Transitivity parameters
	String CN_FORMAT = AbstractParametersJPA.CN_FORMAT;
	String CN_GRAPH = AbstractParametersJPA.CN_GRAPH;

	// ABA settings (nearly independent of a particular job)
	String CN_ABA_MAX_MATCHES = "ABA_MATCHES";
	String CN_ABA_LIMIT_BLOCKSET = "ABA_BLOCKSET";
	String CN_ABA_LIMIT_SINGLESET = "ABA_SINGLESET";
	String CN_ABA_LIMIT_SINGLETABLE = "ABA_SINGLETABLE";

	// OABA settings (nearly independent of a particular job)
	String CN_OABA_MAX_SINGLE = "OABA_SINGLE";
	String CN_OABA_MAX_BLOCKSIZE = "OABA_BLOCKSIZE";
	String CN_OABA_MAX_CHUNKSIZE = "OABA_CHUNKSIZE";
	String CN_OABA_MAX_OVERSIZE = "OABA_OVERSIZE";
	String CN_OABA_MAX_MATCHES = "OABA_MATCHES";
	String CN_OABA_MIN_FIELDS = "OABA_OSFIELDS";
	String CN_OABA_INTERVAL = "OABA_INTERVAL";

	// Server settings (dependent only on a particular host)
	String CN_SVR_MAX_THREADS = "SVR_THREADS";
	String CN_SVR_MAX_CHUNKSIZE = "SVR_FILE_ENTRIES";
	String CN_SVR_MAX_CHUNKCOUNT = "SVR_FILES_COUNT";
	String CN_SVR_FILE = ServerConfigurationJPA.CN_FILE;

	// JPA ID generation
	String ID_GENERATOR_NAME = "NAMED_CONFIG";
	String ID_GENERATOR_TABLE = "CMT_SEQUENCE";
	String ID_GENERATOR_PK_COLUMN_NAME = "SEQ_NAME";
	String ID_GENERATOR_PK_COLUMN_VALUE = "NAMED_CONFIG";
	String ID_GENERATOR_VALUE_COLUMN_NAME = "SEQ_COUNT";

	/** Name of a query that selects all named configurations */
	String QN_NAMEDCONFIG_FIND_ALL = "namedConfigFindAll";

	/** JPQL used to implement {@link #QN_NAMEDCONFIG_FIND_ALL} */
	String JPQL_NAMEDCONFIG_FIND_ALL =
		"Select nc from NamedConfigurationEntity nc order by nc.configurationName";

	/** Name of a query that selects configurations by name */
	String QN_NAMEDCONFIG_FIND_BY_NAME = "namedConfigFindByConfigName";

	/** JPQL used to implement {@link #QN_NAMEDCONFIG_FIND_BY_NAME} */
	String JPQL_NAMEDCONFIG_FIND_BY_NAME =
		"Select nc from NamedConfigurationEntity nc "
				+ "where nc.configurationName = :name";

	/**
	 * Name of the parameter used to specify the configuration name of
	 * {@link #JPQL_NAMEDCONFIG_FIND_BY_NAME}
	 */
	String PN_NAMEDCONFIG_FIND_BY_NAME_P1 = "name";

}
