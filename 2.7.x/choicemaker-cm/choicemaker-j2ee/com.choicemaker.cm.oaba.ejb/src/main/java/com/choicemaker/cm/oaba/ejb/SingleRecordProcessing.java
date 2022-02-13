/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

import static com.choicemaker.util.Precondition.assertNonNullArgument;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.choicemaker.cm.aba.AbaStatistics;
import com.choicemaker.cm.aba.AutomatedBlocker;
import com.choicemaker.cm.aba.DatabaseAccessor;
import com.choicemaker.cm.aba.base.Blocker2;
import com.choicemaker.cm.aba.base.db.DbbCountsCreator;
import com.choicemaker.cm.aba.util.BlockingConfigurationUtils;
import com.choicemaker.cm.aba.util.DatabaseAccessorUtils;
import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.IndexedPropertyController;
import com.choicemaker.cm.batch.api.OperationalPropertyController;
import com.choicemaker.cm.batch.api.ProcessingEventLog;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.ChoiceMakerExtensionPoint;
import com.choicemaker.cm.core.DatabaseException;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Match;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.base.MatchRecord2;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.core.base.RECORD_SOURCE_ROLE;
import com.choicemaker.cm.core.base.RecordDecisionMaker;
import com.choicemaker.cm.core.util.MatchUtils;
import com.choicemaker.cm.io.db.base.DatabaseAbstraction;
import com.choicemaker.cm.io.db.base.DatabaseAbstractionManager;
import com.choicemaker.cm.oaba.api.AbaStatisticsController;
import com.choicemaker.cm.oaba.api.MatchPairInfoBean;
import com.choicemaker.cm.oaba.api.OabaParametersController;
import com.choicemaker.cm.oaba.api.RecordSourceController;
import com.choicemaker.cm.oaba.api.SqlRecordSourceController;
import com.choicemaker.cm.oaba.core.IMatchRecord2Sink;
import com.choicemaker.cm.oaba.core.IMatchRecord2SinkSourceFactory;
import com.choicemaker.cm.oaba.ejb.data.OabaJobMessage;

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

	/** Event code for incomplete blocking in SRM mode */
	public static final String BATCH_MATCHING_SRM_INCOMPLETE_BLOCKING =
		"BMSRM-00001";

	/** Event code for write failure in SRM mode */
	public static final String BATCH_MATCHING_SRM_FAILED_WRITE = "BMSRM-00002";

	/** Facility code for batch record matching, single-record mode */
	public static final String FACILITY_BATCH_MATCH_SRM = "BATCH_MATCH_SRM";

	public static final String MATCH_CANDIDATE =
		ChoiceMakerExtensionPoint.CM_CORE_MATCHCANDIDATE;

	private final Logger logger;
	private final Logger jmsTrace;
	private final OabaParametersController paramsController;
	private final RecordSourceController rsController;
	private final SqlRecordSourceController sqlRsController;
	private final OperationalPropertyController opPropController;
	private final IndexedPropertyController idxPropController;
	private final AbaStatisticsController abaStatsController;

	// -- Constructor

	public SingleRecordProcessing(Logger logger, Logger jmsTrace,
			OabaParametersController paramsController,
			RecordSourceController rsController,
			SqlRecordSourceController sqlRsController,
			OperationalPropertyController opPropController,
			IndexedPropertyController idxPropController,
			AbaStatisticsController abaStatsController) {
		assertNonNullArgument("null logger", logger);
		assertNonNullArgument("null jmsTrace", jmsTrace);
		assertNonNullArgument("null paramsController", paramsController);
		assertNonNullArgument("null rsController", rsController);
		assertNonNullArgument("null sqlRsController", sqlRsController);
		assertNonNullArgument("null opPropController", opPropController);
		assertNonNullArgument("null idxPropController", idxPropController);
		assertNonNullArgument("null abaStatsController", abaStatsController);

		this.logger = logger;
		this.jmsTrace = jmsTrace;
		this.paramsController = paramsController;
		this.rsController = rsController;
		this.sqlRsController = sqlRsController;
		this.opPropController = opPropController;
		this.idxPropController = idxPropController;
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

	protected IndexedPropertyController getIndexedPropertyController() {
		return idxPropController;
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

		final int FINAL_MS_INDEX_INCLUSIVE = handleSingleMatching(stageDS,
				masterDS, factory, batchJob, oabaParams, oabaSettings,
				FINAL_STAGING_INDEX_INCLUSIVE, maxMatches);

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
			IMatchRecord2SinkSourceFactory<?> factory, final BatchJob batchJob,
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
		final String bcId =
			BlockingConfigurationUtils.createBlockingConfigurationId(model,
					blockingConfiguration, databaseConfiguration);
		final AbaStatistics stats =
			getAbaStatisticsController().getStatistics(bcId);
		if (stats == null) {
			String msg =
				"Abastatistics are not available for blocking configuration: "
						+ bcId;
			logger.severe(msg);
			throw new IllegalStateException(msg);
		}

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
			getLogger().info(
					"Finding matches of staging records to master records...");
			stage.open();

			getLogger()
					.fine("Current pairwise sink index: " + currentSinkIndex);
			currentSink = factory.getSink(currentSinkIndex);
			getIndexedPropertyController().setIndexedPropertyValue(
					batchJob,
					MatchPairInfoBean.PN_OABA_MATCH_RESULT_FILE,
					currentSinkIndex, currentSink.getInfo());
			currentSink.open();

			int currentSinkSize = 0;
			while (stage.hasNext()) {

				Record q = stage.getNext();
				AutomatedBlocker rs = new Blocker2(databaseAccessor, model, q,
						limitPBS, stbsgl, limitSBS, stats,
						databaseConfiguration, blockingConfiguration);
				getLogger().fine(q.getId() + " " + rs + " " + model);

				List<Match> s = null;
				try {
					s = dm.getMatches(q, rs, model,
							oabaParams.getLowThreshold(),
							oabaParams.getHighThreshold());
				} catch (IOException x) {
					logErrorMatchingRecord(q, x, batchJob);
					logger.fine("Continuing iteration over staging records");
					continue;
				}
				assert s != null;

				Iterator<Match> iS = s.iterator();
				while (iS.hasNext()) {
					Match m = iS.next();
					final String noteInfo =
						MatchUtils.getNotesAsDelimitedString(m.ac, model);
					MatchRecord2 mr2 = new MatchRecord2(q.getId(), m.id,
							RECORD_SOURCE_ROLE.MASTER, m.probability,
							m.decision, noteInfo);
					try {
						currentSink.writeMatch(mr2);
					} catch (BlockingException x) {
						logErrorWritingMatchResult(mr2, x, batchJob);
						logger.fine("Continuing iteration over matches");
						continue;
					}
					++currentSinkSize;

					if (currentSinkSize > maxMatches) {
						currentSink.close();
						++currentSinkIndex;
						currentSink = factory.getSink(currentSinkIndex);
						getIndexedPropertyController().setIndexedPropertyValue(
								batchJob,
								MatchPairInfoBean.PN_OABA_MATCH_RESULT_FILE,
								currentSinkIndex, currentSink.getInfo());
						currentSinkSize = 0;
					}
				}
			}
			getLogger().info(
					"...finished finding matches of staging records to master records...");

		} catch (BlockingException | IOException x) {
			String msg = "Failed while processing pairwise sink index: "
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

	private void logErrorMatchingRecord(Record q, IOException x,
			BatchJob batchJob) {
		Comparable id1 = q == null ? null : q.getId();
		Comparable id2 = null;
		String msg = "failed to find match for record '" + id1 + "'";
		logMatchingErrorHACK(BATCH_MATCHING_SRM_INCOMPLETE_BLOCKING,
				FACILITY_BATCH_MATCH_SRM, msg, x, batchJob, id1, id2);
	}

	private void logErrorWritingMatchResult(MatchRecord2 mr,
			BlockingException x, BatchJob batchJob) {
		final Comparable id1 = mr == null ? null : mr.getRecordID1();
		final Comparable id2 = mr == null ? null : mr.getRecordID2();
		String msg = "failed to write pair '" + id1 + "' / '" + id2 + "'";
		logMatchingErrorHACK(BATCH_MATCHING_SRM_FAILED_WRITE,
				FACILITY_BATCH_MATCH_SRM, msg, x, batchJob, id1, id2);
	}

	private void logMatchingErrorHACK(final String errorCode, String facility,
			final String summary, Exception x, BatchJob batchJob,
			Comparable idQuery, Comparable idReference) {
		// FIXME log matching errors to a database table
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println(summary);
		pw.println("Error code: " + errorCode);
		pw.println("Facility: " + facility);
		pw.println("Exception: " + x.toString());
		pw.println("Query record: " + idQuery);
		pw.println("Reference record: " + idReference);
		String msg = sw.toString();
		logger.warning(msg);
	}

	/** Cache ABA statistics for field-value counts from a reference source */
	protected void cacheAbaStatistics(DataSource ds) throws BlockingException {
		getLogger().info("Caching ABA statistics for reference records..");
		try {
			DatabaseAbstractionManager mgr =
				new AggregateDatabaseAbstractionManager();
			DatabaseAbstraction dba = mgr.lookupDatabaseAbstraction(ds);
			DbbCountsCreator cc = new DbbCountsCreator();
			cc.updateAbaStatisticsCache(ds, dba, getAbaStatisticsController());
		} catch (SQLException | DatabaseException e) {
			String msg = "Unable to cache master ABA statistics: " + e;
			getLogger().severe(msg);
			throw new BlockingException(msg);
		}
		getLogger().info(
				"... finished caching ABA statistics for reference records.");
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
		DatabaseAccessor retVal =
			DatabaseAccessorUtils.getDatabaseAccessor(dbaName);
		assert retVal != null;
		return retVal;
	}

	protected void setMaxTempPairwiseIndex(BatchJob job, int max) {
		BatchJobUtils.setMaxTempPairwiseIndex(getPropertyController(), job,
				max);
	}

	protected int getMaxTempPairwiseIndex(BatchJob job) {
		return BatchJobUtils.getMaxTempPairwiseIndex(getPropertyController(),
				job);
	}

}
