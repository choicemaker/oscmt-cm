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
package com.choicemaker.cmit.io.db.oracle;

import static com.choicemaker.cm.io.db.oracle.OracleMarkedRecordPairSource.PARAM_IDX_PAIR_CURSOR;
import static com.choicemaker.cm.io.db.oracle.OracleMarkedRecordPairSource.PARAM_IDX_RECORD_CURSOR_CURSOR;
import static com.choicemaker.cm.io.db.oracle.OracleMarkedRecordPairSource.createRecordCursors;
import static com.choicemaker.cm.io.db.oracle.OracleMarkedRecordPairSource.createRecordMap;
import static com.choicemaker.cm.io.db.oracle.OracleMarkedRecordPairSource.executeCmtTrainingAccessSnaphot;
import static com.choicemaker.cm.io.db.oracle.OracleMarkedRecordPairSource.getDatabaseReader;
import static com.choicemaker.cm.io.db.oracle.OracleMarkedRecordPairSource.getNextPairInternal;
import static com.choicemaker.cm.io.db.oracle.OracleMarkedRecordPairSource.prepareCmtTrainingAccessSnaphot;
import static com.choicemaker.cmit.io.db.oracle.OracleTestProperties.DEFAULT_DATABASE_CONFIGURATION;
import static com.choicemaker.cmit.io.db.oracle.OracleTestProperties.DEFAULT_MODEL_NAME;
import static com.choicemaker.cmit.io.db.oracle.OracleTestProperties.DEFAULT_PROPERTY_FILE;
import static com.choicemaker.cmit.io.db.oracle.OracleTestProperties.DEFAULT_SQL_RECORD_SELECTION;
import static com.choicemaker.cmit.io.db.oracle.OracleTestProperties.PN_DATABASE_CONFIGURATION;
import static com.choicemaker.cmit.io.db.oracle.OracleTestProperties.PN_MODEL_NAME;
import static com.choicemaker.cmit.io.db.oracle.OracleTestProperties.PN_PROPERTY_FILE;
import static com.choicemaker.cmit.io.db.oracle.OracleTestProperties.PN_SQL_RECORD_SELECTION;
import static com.choicemaker.cmit.io.db.oracle.Oracle_DataSource.configureDatasource;
import static com.choicemaker.cmit.io.db.oracle.JdbcTestUtils.loadProperties;
import static com.choicemaker.cmit.io.db.oracle.JdbcTestUtils.logProperty;
import static com.choicemaker.e2.platform.InstallablePlatform.INSTALLABLE_PLATFORM;

import java.io.FileReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import com.choicemaker.cm.core.ImmutableMarkedRecordPair;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.io.db.base.DbReaderParallel;
import com.choicemaker.cm.io.db.oracle.OracleRemoteDebugging;
import com.choicemaker.e2.embed.EmbeddedPlatform;
import com.choicemaker.util.SystemPropertyUtils;

public class AccessSnapshotApp {

	// private static final Logger logger = Logger
	// .getLogger(AccessSnapshotApp.class.getName());

	public static void main(String[] args) throws Exception {

		String pName = EmbeddedPlatform.class.getName();
		SystemPropertyUtils.setPropertyIfMissing(INSTALLABLE_PLATFORM, pName);
		// CMPlatform cmp = InstallablePlatform.getInstance();

		String propertyFileName =
			System.getProperty(PN_PROPERTY_FILE, DEFAULT_PROPERTY_FILE);
		FileReader fr = new FileReader(propertyFileName);
		Properties p = loadProperties(fr);
		DataSource ds = configureDatasource(p);

		String modelName = p.getProperty(PN_MODEL_NAME, DEFAULT_MODEL_NAME);
		logProperty(PN_MODEL_NAME, modelName);

		String databaseConfiguration =
			p.getProperty(PN_DATABASE_CONFIGURATION,
					DEFAULT_DATABASE_CONFIGURATION);
		logProperty(PN_DATABASE_CONFIGURATION, databaseConfiguration);

		String selection =
			p.getProperty(PN_SQL_RECORD_SELECTION, DEFAULT_SQL_RECORD_SELECTION);
		logProperty(PN_SQL_RECORD_SELECTION, selection);

		PMManager.loadModelPlugins();
		ImmutableProbabilityModel m1 =
			PMManager.getImmutableModelInstance(modelName);
		assert m1 != null : "null model (" + modelName + ")";

		DbReaderParallel dbr = getDatabaseReader(m1, databaseConfiguration);
		final int noCursors = dbr.getNoCursors();

		// Get a database connection (and optionally configure debugging)
		Connection conn = ds.getConnection();
		OracleRemoteDebugging.doDebugging(conn);

		// Execute the stored procedure to retrieve records and marked pairs
		CallableStatement stmt = prepareCmtTrainingAccessSnaphot(conn);
		executeCmtTrainingAccessSnaphot(stmt, selection, dbr);

		// Update the result sets representing records and marked pairs
		ResultSet markedPairs =
			(ResultSet) stmt.getObject(PARAM_IDX_PAIR_CURSOR);
		ResultSet cursorOfRecordCursors =
			(ResultSet) stmt.getObject(PARAM_IDX_RECORD_CURSOR_CURSOR);
		ResultSet[] recordCursors =
			createRecordCursors(cursorOfRecordCursors, noCursors);

		// Create the map of record ids to full records
		dbr.open(recordCursors);
		Map<String, Record> recordMap = createRecordMap(dbr);
		System.out.println("Number of records: " + recordMap.size());

		// Get the first currentPair
		int pairCount = -1;
		ImmutableMarkedRecordPair currentPair;
		do {
			++pairCount;
			currentPair = getNextPairInternal(recordMap, markedPairs);
		} while (currentPair != null);
		System.out.println("Number of pairs: " + pairCount);

	}

}
