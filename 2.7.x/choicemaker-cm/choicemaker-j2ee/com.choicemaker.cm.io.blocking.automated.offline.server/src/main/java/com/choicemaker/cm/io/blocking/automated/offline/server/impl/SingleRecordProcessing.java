/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.offline.server.impl;

import static com.choicemaker.util.Precondition.assertNonNullArgument;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.batch.BatchJob;
import com.choicemaker.cm.batch.OperationalPropertyController;
import com.choicemaker.cm.batch.ProcessingEventLog;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.ChoiceMakerExtensionPoint;
import com.choicemaker.cm.core.DatabaseException;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.base.Match;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.core.base.RecordDecisionMaker;
import com.choicemaker.cm.io.blocking.automated.AbaStatistics;
import com.choicemaker.cm.io.blocking.automated.AutomatedBlocker;
import com.choicemaker.cm.io.blocking.automated.DatabaseAccessor;
import com.choicemaker.cm.io.blocking.automated.base.Blocker2;
import com.choicemaker.cm.io.blocking.automated.base.db.DbbCountsCreator;
import com.choicemaker.cm.io.blocking.automated.offline.core.IMatchRecord2Sink;
import com.choicemaker.cm.io.blocking.automated.offline.core.IMatchRecord2SinkSourceFactory;
import com.choicemaker.cm.io.blocking.automated.offline.core.RECORD_SOURCE_ROLE;
import com.choicemaker.cm.io.blocking.automated.offline.data.MatchRecord2;
import com.choicemaker.cm.io.blocking.automated.offline.data.MatchRecordUtils;
import com.choicemaker.cm.io.blocking.automated.offline.server.data.OabaJobMessage;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.AbaStatisticsController;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.OabaParametersController;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.RecordSourceController;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.SqlRecordSourceController;
import com.choicemaker.cm.io.db.base.DatabaseAbstraction;
import com.choicemaker.cm.io.db.base.DatabaseAbstractionManager;
import com.choicemaker.e2.CMConfigurationElement;
import com.choicemaker.e2.CMExtension;
import com.choicemaker.e2.E2Exception;
import com.choicemaker.e2.platform.CMPlatformUtils;

// import com.choicemaker.cm.core.base.Accessor;

/**
 * This message bean performs single record matching on the staging record
 * source.
 *
 * @author pcheung
 *
 */
@SuppressWarnings({
		"rawtypes", "unchecked" })
public class SingleRecordProcessing implements Serializable {

	private static final long serialVersionUID = 271L;

	public static final String DATABASE_ACCESSOR =
		ChoiceMakerExtensionPoint.CM_IO_BLOCKING_AUTOMATED_BASE_DATABASEACCESSOR;

	public static final String MATCH_CANDIDATE =
		ChoiceMakerExtensionPoint.CM_CORE_MATCHCANDIDATE;
	
	private final Logger logger;
	private final Logger jmsTrace;
	private final OabaParametersController paramsController;
	private final RecordSourceController rsController;
	private final SqlRecordSourceController sqlRsController;
	private final OperationalPropertyController opPropController;
	private final AbaStatisticsController abaStatsController;

	// -- Constructor
	
	public SingleRecordProcessing(
			Logger logger,
			Logger jmsTrace,
			OabaParametersController paramsController,
			RecordSourceController rsController,
			SqlRecordSourceController sqlRsController,
			OperationalPropertyController opPropController,
			AbaStatisticsController abaStatsController
			) {
		assertNonNullArgument("null logger", logger);
		assertNonNullArgument("null jmsTrace", jmsTrace);
		assertNonNullArgument("null paramsController", paramsController);
		assertNonNullArgument("null rsController", rsController);
		assertNonNullArgument("null sqlRsController", sqlRsController);
		assertNonNullArgument("null opPropController", opPropController);
		assertNonNullArgument("null abaStatsController", abaStatsController);
		
		this.logger = logger;
		this.jmsTrace = jmsTrace;
		this.paramsController = paramsController;
		this.rsController = rsController;
		this.sqlRsController = sqlRsController;
		this.opPropController = opPropController;
		this.abaStatsController = abaStatsController;
	}

	// -- Abstract call-back methods

	protected Logger getLogger() {
		return logger;
	}

	protected Logger getJmsTrace() {
		return jmsTrace;
	}

	protected OabaParametersController getParametersController() {
		return paramsController;
	}

	protected RecordSourceController getRecordSourceController() {
		return rsController;
	}

	protected SqlRecordSourceController getSqlRecordSourceController() {
		return sqlRsController;
	}

	protected OperationalPropertyController getPropertyController() {
		return opPropController;
	}

	protected AbaStatisticsController getAbaStatisticsController() {
		return abaStatsController;
	}

	// -- Template methods

	public void processOabaMessage(OabaJobMessage data, BatchJob batchJob,
			OabaParameters oabaParams, OabaSettings oabaSettings,
			ProcessingEventLog processingLog, ServerConfiguration serverConfig,
			ImmutableProbabilityModel model) throws BlockingException {

		getLogger().info("Starting Single Record Match with maxSingle = "
				+ oabaSettings.getMaxSingle());
		final long t0 = System.currentTimeMillis();

		// Get the data sources that will be used
		final SqlRecordSourceController rsCtl = getSqlRecordSourceController();
		final DataSource stageDS = rsCtl.getStageDataSource(oabaParams);
		assert stageDS != null;
		final DataSource masterDS = rsCtl.getMasterDataSource(oabaParams);

		// If there's no source for master records, there's nothing to do
		if (masterDS == null) {
			String msg =
				"Missing datasource for master records -- nothing to do";
			getLogger().warning(msg);
			return;
		}

		final int FINAL_STAGING_INDEX_INCLUSIVE =
			this.getMaxTempPairwiseIndex(batchJob);
		final int INITIAL_MS_INDEX_INCLUSIVE =
			FINAL_STAGING_INDEX_INCLUSIVE + 1;

		final int maxMatches = oabaSettings.getMaxMatches();
		final IMatchRecord2SinkSourceFactory<?> factory =
			OabaFileUtils.getMatchChunkFactory(batchJob);

		final int FINAL_MS_INDEX_INCLUSIVE =
			handleSingleMatching(stageDS, masterDS, factory, batchJob,
					oabaParams, oabaSettings, FINAL_STAGING_INDEX_INCLUSIVE,
					maxMatches);

		assert FINAL_MS_INDEX_INCLUSIVE >= INITIAL_MS_INDEX_INCLUSIVE;
		assert FINAL_MS_INDEX_INCLUSIVE > FINAL_STAGING_INDEX_INCLUSIVE;
		setMaxTempPairwiseIndex(batchJob, FINAL_MS_INDEX_INCLUSIVE);

		long duration = System.currentTimeMillis() - t0;
		getLogger().info("Msecs in single matching " + duration);
	}

	/**
	 * This method takes one record at a time from a staging source and performs
	 * matching against a master source. It's basically like findMatches.
	 *
	 * @param data
	 * @throws BlockingException
	 * @throws Exception
	 */
	protected int handleSingleMatching(DataSource stageDS, DataSource masterDS,
			IMatchRecord2SinkSourceFactory<?> factory, BatchJob batchJob,
			OabaParameters oabaParams, OabaSettings oabaSettings,
			final int previousIndex, final int maxMatches)
			throws BlockingException {

		if (stageDS == null) {
			throw new IllegalArgumentException("null query data source");
		}
		if (masterDS == null) {
			throw new IllegalArgumentException("null reference data source");
		}

		final String modelConfigId = oabaParams.getModelConfigurationName();
		final ImmutableProbabilityModel model =
			PMManager.getModelInstance(modelConfigId);
		if (model == null) {
			String msg = "Invalid probability accessProvider: " + modelConfigId;
			getLogger().severe(msg);
			throw new BlockingException(msg);
		}

		final int limitPBS = oabaSettings.getLimitPerBlockingSet();
		final int stbsgl = oabaSettings.getSingleTableBlockingSetGraceLimit();
		final int limitSBS = oabaSettings.getLimitSingleBlockingSet();
		final String blockingConfiguration =
			oabaParams.getBlockingConfiguration();

		final OabaParametersController pc = getParametersController();
		final String databaseConfiguration =
			pc.getReferenceDatabaseConfiguration(oabaParams);
		getLogger().fine("DataSource : " + masterDS);
		getLogger().fine("DatabaseConfiguration: " + databaseConfiguration);

		cacheAbaStatistics(masterDS);
		final AbaStatistics stats =
			getAbaStatisticsController().getStatistics(model);

		final String dbaName = getReferenceAccessorName(oabaParams);
		getLogger().fine("DatabaseAccessor: " + dbaName);
		final DatabaseAccessor databaseAccessor = getReferenceAccessor(dbaName);
		databaseAccessor.setCondition("");
		databaseAccessor.setDataSource(masterDS);

		RecordSource stage = null;
		try {
			stage = getRecordSourceController().getStageRs(oabaParams);
		} catch (Exception e) {
			String msg = "Unable to get staging record source: " + e;
			getLogger().severe(msg);
			throw new BlockingException(msg);
		}
		assert stage != null;
		assert stage.getModel() == model;

		final RecordDecisionMaker dm = new RecordDecisionMaker();

		int currentSinkIndex = previousIndex + 1;
		IMatchRecord2Sink currentSink = null;
		try {
			getLogger().info("Finding matches of master records to staging records...");
			stage.open();

			getLogger().fine("Current pairwise sink index: " + currentSinkIndex);
			currentSink = factory.getSink(currentSinkIndex);
			currentSink.open();

			int currentSinkSize = 0;
			while (stage.hasNext()) {

				Record q = stage.getNext();
				AutomatedBlocker rs =
					new Blocker2(databaseAccessor, model, q, limitPBS, stbsgl,
							limitSBS, stats, databaseConfiguration,
							blockingConfiguration);
				getLogger().fine(q.getId() + " " + rs + " " + model);

				SortedSet<Match> s =
					dm.getMatches(q, rs, model, oabaParams.getLowThreshold(),
							oabaParams.getHighThreshold());
				Iterator<Match> iS = s.iterator();
				while (iS.hasNext()) {
					Match m = iS.next();
					final String noteInfo =
						MatchRecordUtils.getNotesAsDelimitedString(m.ac, model);
					MatchRecord2 mr2 =
						new MatchRecord2(q.getId(), m.id,
								RECORD_SOURCE_ROLE.MASTER, m.probability,
								m.decision, noteInfo);
					currentSink.writeMatch(mr2);
					++currentSinkSize;

					if (currentSinkSize > maxMatches) {
						currentSink.close();
						++currentSinkIndex;
						currentSink = factory.getSink(currentSinkIndex);
						currentSinkSize = 0;
					}
				}
			}
			getLogger().info("...finished finding matches of master records to staging records...");

		} catch (BlockingException | IOException x) {
			String msg =
				"Failed while processing pairwise sink index: "
						+ currentSinkIndex + ": " + x.toString();
			getLogger().severe(msg);
			throw new BlockingException(msg);

		} finally {
			if (stage != null) {
				try {
					stage.close();
					stage = null;
				} catch (Exception e) {
					String msg = "Unable to close staging record source: " + e;
					getLogger().severe(msg);
				}
			}
			if (currentSink != null) {
				try {
					currentSink.close();
				} catch (BlockingException e) {
					getLogger().warning("Unable to close sink: " + currentSink);
				}
				currentSink = null;
			}
		}

		return currentSinkIndex;
	}

	/** Cache ABA statistics for field-value counts from a reference source */
	protected void cacheAbaStatistics(DataSource ds) throws BlockingException {
		getLogger().info("Caching ABA statistics for reference records..");
		try {
			DatabaseAbstractionManager mgr =
				new AggregateDatabaseAbstractionManager();
			DatabaseAbstraction dba = mgr.lookupDatabaseAbstraction(ds);
			DbbCountsCreator cc = new DbbCountsCreator();
			cc.setCacheCountSources(ds, dba, getAbaStatisticsController());
		} catch (SQLException | DatabaseException e) {
			String msg = "Unable to cache master ABA statistics: " + e;
			getLogger().severe(msg);
			throw new BlockingException(msg);
		}
		getLogger().info("... finished caching ABA statistics for reference records.");
	}

	protected String getReferenceAccessorName(OabaParameters oabaParams) {
		final OabaParametersController pc = getParametersController();
		final String retVal;
		retVal = pc.getReferenceDatabaseAccessor(oabaParams);
		if (retVal == null) {
			String msg =
				"Null database accessor name for reference record source";
			getLogger().severe(msg);
			throw new IllegalStateException(msg);
		}
		getLogger().fine("Database accessor name: " + retVal);
		return retVal;
	}

	protected DatabaseAccessor getReferenceAccessor(String dbaName)
			throws BlockingException {

		final CMExtension dbaExt =
			CMPlatformUtils.getExtension(DATABASE_ACCESSOR, dbaName);
		if (dbaExt == null) {
			String msg = "null DatabaseAccessor extension for: " + dbaName;
			getLogger().severe(msg);
			throw new IllegalStateException(msg);
		}

		DatabaseAccessor retVal = null;
		try {
			final CMConfigurationElement[] configElements =
				dbaExt.getConfigurationElements();
			if (configElements == null || configElements.length == 0) {
				String msg = "No database accessor configurations: " + dbaName;
				getLogger().severe(msg);
				throw new IllegalStateException(msg);
			} else if (configElements.length != 1) {
				String msg =
					"Multiple database accessor configurations for " + dbaName
							+ ": " + configElements.length;
				getLogger().warning(msg);
			} else {
				assert configElements.length == 1;
			}
			final CMConfigurationElement configElement = configElements[0];
			Object o = configElement.createExecutableExtension("class");
			assert o != null;
			assert o instanceof DatabaseAccessor;
			retVal = (DatabaseAccessor) o;
		} catch (E2Exception e) {
			String msg = "Unable to construct database accessor: " + e;
			getLogger().severe(msg);
			throw new BlockingException(msg);
		}
		assert retVal != null;
		return retVal;
	}

	protected void setMaxTempPairwiseIndex(BatchJob job, int max) {
		BatchJobUtils
				.setMaxTempPairwiseIndex(getPropertyController(), job, max);
	}

	protected int getMaxTempPairwiseIndex(BatchJob job) {
		return BatchJobUtils.getMaxTempPairwiseIndex(getPropertyController(),
				job);
	}

}