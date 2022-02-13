/*******************************************************************************
 * Copyright (c) 2003, 2021 ChoiceMaker LLC and others.
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
package com.choicemaker.cms.api;

import static com.choicemaker.client.api.WellKnownGraphProperties.GPN_SCM;
import static com.choicemaker.cm.args.AnalysisResultFormat.SORT_BY_HOLD_GROUP;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.choicemaker.cm.args.OabaLinkageType;
import com.choicemaker.cm.args.PersistableSqlRecordSource;
import com.choicemaker.cm.args.PersistentObject;
import com.choicemaker.cm.batch.api.BatchJobRigor;

public interface NamedConfiguration {

	// -- Configuration identity
	String DEFAULT_CONFIGURATIONDESC = "";
	long DEFAULT_CONFIGURATIONID = PersistentObject.NONPERSISTENT_ID;
	String DEFAULT_CONFIGURATIONNAME = "";

	// -- Online parameters
	int DEFAULT_ABALIMITPERBLOCKINGSET = 50;
	int DEFAULT_ABALIMITSINGLEBLOCKINGSET = 100;
	int DEFAULT_ABAMAXMATCHES = 0;
	int DEFAULT_ABASINGLETABLEBLOCKINGSETGRACELIMIT = 200;
	String DEFAULT_DATABASEACCESSOR = "<dbAccessor>";
	String DEFAULT_DATABASEREADER = "<dbReader>";

	// -- SQL parameters
	String DEFAULT_RS_TYPE = PersistableSqlRecordSource.TYPE;
	String DEFAULT_DATASOURCE = "/choicemaker/urm/jdbc/ChoiceMakerBlocking";
	String DEFAULT_JDBCDRIVERCLASSNAME =
		"com.choicemaker.cm.io.db.postgres2.PostgresSerializableRecordSource";

	// -- Blocking and scoring parameters
	String DEFAULT_BLOCKINGCONFIGURATION = "<blockingConfig>";
	float DEFAULT_LOWTHRESHOLD = 0.20f;
	float DEFAULT_HIGHTHRESHOLD = 0.80f;
	String DEFAULT_MODELNAME = "<modelName>";
	String DEFAULT_TASK = OabaLinkageType.STAGING_TO_MASTER_LINKAGE.name();

	// -- Offline batch parameters
	int DEFAULT_OABAINTERVAL = 100;
	int DEFAULT_OABAMAXBLOCKSIZE = 100;
	int DEFAULT_OABAMAXCHUNKSIZE = 100000;
	int DEFAULT_OABAMAXMATCHES = 500000;
	int DEFAULT_OABAMAXOVERSIZED = 1000;
	int DEFAULT_OABAMAXSINGLE = 0;
	int DEFAULT_OABAMINFIELDS = 3;

	// -- Query-record parameters
	String DEFAULT_QUERYDATABASECONFIGURATION = "<queryDBConfig>";
	boolean DEFAULT_QUERYISDEDUPLICATED = false;
	String DEFAULT_QUERYSELECTION = "select <id> as ID from <querySource>";

	// -- Reference-record parameters
	String DEFAULT_REFERENCEDATABASECONFIGURATION = "<referenceDbConfig>";
	String DEFAULT_REFERENCEDATABASEACCESSOR = DEFAULT_DATABASEACCESSOR;
	String DEFAULT_REFERENCEDATABASEREADER = DEFAULT_DATABASEREADER;
	String DEFAULT_REFERENCESELECTION = "select <id> as ID from <referenceSource>";
	String DEFAULT_RIGOR = BatchJobRigor.COMPUTED.name();

	// -- Server parameters
	String DEFAULT_SERVERFILEURI = "<fileUri>";
	int DEFAULT_SERVERMAXFILEENTRIES = 1000000;
	int DEFAULT_SERVERMAXFILESCOUNT = 2000;
	int DEFAULT_SERVERMAXTHREADS = 1;

	// -- Transitivity parameters
	String DEFAULT_TRANSITIVITYFORMAT = SORT_BY_HOLD_GROUP.name();
	String DEFAULT_TRANSITIVITYGRAPH = GPN_SCM;

	int getAbaLimitPerBlockingSet();

	int getAbaLimitSingleBlockingSet();

	int getAbaMaxMatches();

	int getAbaSingleTableBlockingSetGraceLimit();

	String getBlockingConfiguration();

	String getConfigurationDescription();

	String getConfigurationName();

	String getDataSource();

	float getHighThreshold();

	long getId();

	String getJdbcDriverClassName();

	float getLowThreshold();

	String getModelName();

	int getOabaInterval();

	int getOabaMaxBlockSize();

	int getOabaMaxChunkSize();

	int getOabaMaxMatches();

	int getOabaMaxOversized();

	int getOabaMaxSingle();

	int getOabaMinFields();

	String getQueryDatabaseConfiguration();

	String getQuerySelection();

	String getRecordSourceType();

	String getReferenceDatabaseAccessor();

	String getReferenceDatabaseConfiguration();

	String getReferenceDatabaseReader();

	String getReferenceSelection();

	String getRigor();

	String getServerFileURI();

	int getServerMaxFileEntries();

	int getServerMaxFilesCount();

	int getServerMaxThreads();

	String getTask();

	String getTransitivityFormat();

	String getTransitivityGraph();

	boolean isQueryDeduplicated();

}