package com.choicemaker.cms.zzz_todo;

import static com.choicemaker.client.api.WellKnownGraphProperties.GPN_SCM;
import static com.choicemaker.cm.args.AnalysisResultFormat.SORT_BY_HOLD_GROUP;

import com.choicemaker.cm.args.OabaLinkageType;
import com.choicemaker.cm.args.PersistableSqlRecordSource;
import com.choicemaker.cm.args.PersistentObject;

public interface NamedConfiguration {

	public static final long DEFAULT_CONFIGURATIONID =
		PersistentObject.NONPERSISTENT_ID;
	public static final String DEFAULT_CONFIGURATIONNAME = "";
	public static final String DEFAULT_MODELNAME = "";
	public static final float DEFAULT_LOWTHRESHOLD = 0.20f;
	public static final float DEFAULT_HIGHTHRESHOLD = 0.80f;
	public static final String DEFAULT_TASK =
		OabaLinkageType.STAGING_TO_MASTER_LINKAGE.name();
//	public static final String DEFAULT_RIGOR = BatchJobRigor.COMPUTED.name();
	public static final String DEFAULT_RS_TYPE =
		PersistableSqlRecordSource.TYPE;
	public static final String DEFAULT_DATASOURCE =
		"/choicemaker/urm/jdbc/ChoiceMakerEjb";
	public static final String DEFAULT_JDBCDRIVERCLASSNAME =
		"com.choicemaker.cm.io.db.sqlserver.SQLServerSerializableParallelSerialRecordSource";
	public static final String DEFAULT_BLOCKINGCONFIGURATION =
		"defaultAutomated";
	public static final String DEFAULT_QUERYSELECTION =
		"select id as ID from Staging";
	public static final String DEFAULT_QUERYDATABASECONFIGURATION =
		"defaultQuery";
	public static final boolean DEFAULT_QUERYISDEDUPLICATED = false;
	public static final String DEFAULT_REFERENCESELECTION =
		"select id as ID from Staging";
	public static final String DEFAULT_REFERENCEDATABASECONFIGURATION =
		"defaultReference";
	public static final String DEFAULT_REFERENCEDATABASEACCESSOR =
			"defaultAccessor";
	public static final String DEFAULT_TRANSITIVITYFORMAT = SORT_BY_HOLD_GROUP
			.name();
	public static final String DEFAULT_TRANSITIVITYGRAPH = GPN_SCM;
	public static final int DEFAULT_ABALIMITPERBLOCKINGSET = 50;
	public static final int DEFAULT_ABALIMITSINGLEBLOCKINGSET = 100;
	public static final int DEFAULT_ABASINGLETABLEBLOCKINGSETGRACELIMIT = 200;
	public static final int DEFAULT_OABAMAXSINGLE = 0;
	public static final int DEFAULT_OABAMAXBLOCKSIZE = 100;
	public static final int DEFAULT_OABAMAXCHUNKSIZE = 100000;
	public static final int DEFAULT_OABAMAXOVERSIZED = 1000;
	public static final int DEFAULT_OABAMAXMATCHES = 500000;
	public static final int DEFAULT_OABAMINFIELDS = 3;
	public static final int DEFAULT_OABAINTERVAL = 100;
	public static final int DEFAULT_SERVERMAXTHREADS = 1;
	public static final int DEFAULT_SERVERMAXCHUNKSIZE = 1000000;
	public static final int DEFAULT_SERVERMAXCHUNKCOUNT = 2000;
	public static final String DEFAULT_SERVERFILEURI = "";

	long getId();

	String getConfigurationName();

	String getModelName();

	float getLowThreshold();

	float getHighThreshold();

	String getTask();

	String getRigor();

	String getRecordSourceType();

	String getDataSource();

	String getJdbcDriverClassName();

	String getBlockingConfiguration();

	String getQuerySelection();

	String getQueryDatabaseConfiguration();

	boolean isQueryDeduplicated();

	String getReferenceSelection();

	String getReferenceDatabaseConfiguration();

	String getReferenceDatabaseAccessor();

	String getTransitivityFormat();

	String getTransitivityGraph();

	int getAbaLimitPerBlockingSet();

	int getAbaLimitSingleBlockingSet();

	int getAbaSingleTableBlockingSetGraceLimit();

	int getOabaMaxSingle();

	int getOabaMaxBlockSize();

	int getOabaMaxChunkSize();

	int getOabaMaxOversized();

	int getOabaMaxMatches();

	int getOabaMinFields();

	int getOabaInterval();

	int getServerMaxThreads();

	int getServerMaxChunkSize();

	int getServerMaxChunkCount();

	String getServerFileURI();

}