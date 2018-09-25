package com.choicemaker.cm.urm.ejb;

import com.choicemaker.cm.batch.ejb.AbstractPersistentObjectJPA;

public interface UrmConfigurationJPA {

	String TABLE_NAME = "CMT_URM_CONFIG";

	// Basic persistence parameters
	String CN_ID = "URM_CONFIG_ID";
	String CN_UUID = AbstractPersistentObjectJPA.CN_UUID;
	String CN_OPTLOCK = AbstractPersistentObjectJPA.CN_OPTLOCK;

	/** UrmConfiguration name */
	String CN_URM_CONF_NAME = "URM_NAME";

	/** NamedConfiguration name */
	String CN_CMS_CONF_NAME = "CMS_NAME";

	// JPA ID generation
	String ID_GENERATOR_NAME = "URM_CONFIG";
	String ID_GENERATOR_TABLE = "CMT_SEQUENCE";
	String ID_GENERATOR_PK_COLUMN_NAME = "SEQ_NAME";
	String ID_GENERATOR_PK_COLUMN_VALUE = "URM_CONFIG";
	String ID_GENERATOR_VALUE_COLUMN_NAME = "SEQ_COUNT";

	/** Name of a query that selects all URM configurations */
	String QN_URMCONFIG_FIND_ALL = "urmConfigFindAll";

	/** JPQL used to implement {@link #QN_URMCONFIG_FIND_ALL} */
	String JPQL_URMCONFIG_FIND_ALL =
		"Select uc from UrmConfigurationEntity uc order by uc.urmConfigurationName";

	/** Name of a query that selects URM configurations by name */
	String QN_URMCONFIG_FIND_BY_NAME = "urmConfigFindByUrmConfigName";

	/** JPQL used to implement {@link #QN_URMCONFIG_FIND_BY_NAME} */
	String JPQL_URMCONFIG_FIND_BY_NAME =
		"Select uc from UrmConfigurationEntity uc "
				+ "where uc.urmConfigurationName = :name";

	/**
	 * Name of the parameter used to specify the configuration name of
	 * {@link #JPQL_URMCONFIG_FIND_BY_NAME}
	 */
	String PN_URMCONFIG_FIND_BY_NAME_P1 = "name";

}
