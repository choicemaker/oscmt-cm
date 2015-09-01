/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.offline.server.impl;

import static com.choicemaker.cm.args.OperationalPropertyNames.PN_OABA_CACHED_RESULTS_FILE;
import static com.choicemaker.cm.batch.impl.BatchJobFileUtils.TEXT_SUFFIX;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.sql.DataSource;

import com.choicemaker.cm.args.BatchProcessingEvent;
import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ProcessingEvent;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.batch.BatchJob;
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
import com.choicemaker.cm.io.blocking.automated.offline.core.IComparableSink;
import com.choicemaker.cm.io.blocking.automated.offline.core.IComparableSinkSourceFactory;
import com.choicemaker.cm.io.blocking.automated.offline.core.IMatchRecord2Sink;
import com.choicemaker.cm.io.blocking.automated.offline.core.IMatchRecord2SinkSourceFactory;
import com.choicemaker.cm.io.blocking.automated.offline.core.IMatchRecord2Source;
import com.choicemaker.cm.io.blocking.automated.offline.core.OabaProcessingEvent;
import com.choicemaker.cm.io.blocking.automated.offline.core.RECORD_SOURCE_ROLE;
import com.choicemaker.cm.io.blocking.automated.offline.data.MatchRecord2;
import com.choicemaker.cm.io.blocking.automated.offline.data.MatchRecordUtils;
import com.choicemaker.cm.io.blocking.automated.offline.impl.ComparableMRSink;
import com.choicemaker.cm.io.blocking.automated.offline.impl.ComparableMRSinkSourceFactory;
import com.choicemaker.cm.io.blocking.automated.offline.impl.MatchRecord2CompositeSource;
import com.choicemaker.cm.io.blocking.automated.offline.server.data.OabaJobMessage;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.OabaParametersController;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.SqlRecordSourceController;
import com.choicemaker.cm.io.blocking.automated.offline.services.GenericDedupService;
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
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup",
				propertyValue = "java:/choicemaker/urm/jms/singleMatchQueue"),
		@ActivationConfigProperty(propertyName = "destinationType",
				propertyValue = "javax.jms.Queue") })
public class SingleRecordMatchMDB extends AbstractOabaMDB {

	private static final long serialVersionUID = 271L;

	private static final Logger log = Logger
			.getLogger(SingleRecordMatchMDB.class.getName());

	private static final Logger jmsTrace = Logger.getLogger("jmstrace."
			+ SingleRecordMatchMDB.class.getName());

	private static final int FLUSH_INTERVAL = 10;

	public static final String DATABASE_ACCESSOR =
		ChoiceMakerExtensionPoint.CM_IO_BLOCKING_AUTOMATED_BASE_DATABASEACCESSOR;

	public static final String MATCH_CANDIDATE =
		ChoiceMakerExtensionPoint.CM_CORE_MATCHCANDIDATE;

	@Override
	protected void processOabaMessage(OabaJobMessage data, BatchJob batchJob,
			OabaParameters oabaParams, OabaSettings oabaSettings,
			ProcessingEventLog processingLog, ServerConfiguration serverConfig,
			ImmutableProbabilityModel model) throws BlockingException {

		log.info("Starting Single Record Match with maxSingle = "
				+ oabaSettings.getMaxSingle());

		// Get the data sources that will be used
		final SqlRecordSourceController rsCtl = getSqlRecordSourceController();
		final DataSource stageDS = rsCtl.getStageDataSource(oabaParams);
		assert stageDS != null;
		final DataSource masterDS = rsCtl.getMasterDataSource(oabaParams);

		// If there's no source for master records, there's nothing to do
		if (masterDS == null) {
			String msg =
				"Missing datasource for master records -- nothing to do";
			log.warning(msg);
			return;
		}

		// The result for intra-staging matches must already exist
		IMatchRecord2Source stagingResults = getStagingResults(batchJob);
		if (!checkStagingResults(stagingResults)) {
			String msg =
				"Missing matches from staging records: " + stagingResults;
			throw new BlockingException(msg);
		}

		// Move aside the staging results so the file name can be reused later
		final int STAGING_INDEX = 1;
		final IMatchRecord2SinkSourceFactory<?> factory =
			OabaFileUtils.getMatchTempFactory(batchJob);
		final IMatchRecord2Sink stagingSink =
			moveAsideStagingResults(stagingResults, factory, STAGING_INDEX);
		final String stagingFile = stagingSink.getInfo();
		stagingResults =
			new MatchRecord2CompositeSource(stagingFile, TEXT_SUFFIX);

		// Create a sink to hold matches between master and staging records
		final int MS_INDEX = 2;
		final IMatchRecord2Sink<?> masterStagingSink =
			factory.getSink(MS_INDEX);

		// run single record match between query and reference (if any)
		long t = System.currentTimeMillis();
		handleSingleMatching(stageDS, masterDS, data, masterStagingSink,
				batchJob, oabaParams, oabaSettings);
		long duration = System.currentTimeMillis() - t;
		log.info("Msecs in single matching " + duration);

		sendToUpdateStatus(batchJob, OabaProcessingEvent.DONE_MATCHING_DATA,
				new Date(), null);

		// Combine and deduplicate the results
		final ProcessingEventLog processingEntry =
			getProcessingController().getProcessingLog(batchJob);
		processingEntry
				.setCurrentProcessingEvent(OabaProcessingEvent.MERGE_DEDUP_MATCHES);

		List<IComparableSink> tempSinks = new ArrayList<>();
		IComparableSink<?> icsStaging = new ComparableMRSink(stagingSink);
		tempSinks.add(icsStaging);
		IComparableSink<?> icsMasterStaging =
			new ComparableMRSink(masterStagingSink);
		tempSinks.add(icsMasterStaging);

		IMatchRecord2Sink<?> mSink =
			OabaFileUtils.getCompositeMatchSink(batchJob);
		IComparableSink<?> sink = new ComparableMRSink(mSink);
		IComparableSinkSourceFactory<?> mFactory =
			new ComparableMRSinkSourceFactory(factory);
		int i = GenericDedupService.mergeFiles(tempSinks, sink, mFactory, true);
		log.info("Number of Distinct matches after merge: " + i);

		String cachedFileName = mSink.getInfo();
		log.info("Cached results file: " + cachedFileName);
		getPropertyController().setJobProperty(batchJob,
				PN_OABA_CACHED_RESULTS_FILE, cachedFileName);
		t = System.currentTimeMillis() - t;
		log.info("Time in merge dedup " + t);
	}

	private IMatchRecord2Source getStagingResults(BatchJob batchJob) {
		IMatchRecord2Source mSource =
			OabaFileUtils.getCompositeMatchSource(batchJob);
		return mSource;
	}

	private boolean checkStagingResults(IMatchRecord2Source mSource) {
		String fileName = mSource.getInfo();
		boolean retVal = fileName != null;
		if (!retVal) {
			String msg = "No file name recorded for staging match reults";
			log.warning(msg);
		}
		if (retVal && fileName.trim().isEmpty()) {
			retVal = false;
			String msg =
				"Blank name recorded for staging match reults: '" + fileName
						+ "'";
			log.warning(msg);
		}
		if (retVal) {
			File f = new File(fileName);
			retVal = f.exists();
			if (!retVal) {
				String msg =
					"Missing file for staging match reults: '" + fileName + "'";
				log.warning(msg);
			}
		}
		return retVal;
	}

	private IMatchRecord2Sink<?> moveAsideStagingResults(
			IMatchRecord2Source stagingResults,
			IMatchRecord2SinkSourceFactory<?> factory, int index)
			throws BlockingException {

		IMatchRecord2Sink<?> retVal = factory.getSink(index);
		int count = 0;
		try {
			retVal.open();
			while (stagingResults.hasNext()) {
				++count;
				MatchRecord2 mr = (MatchRecord2) stagingResults.next();
				retVal.writeMatch(mr);
				if (count % FLUSH_INTERVAL == 0) {
					retVal.flush();
				}
			}
		} finally {
			retVal.close();
		}

		String fileName = stagingResults.getInfo();
		File f = new File(fileName);
		assert f.exists();
		boolean isDeleted = f.delete();
		if (!isDeleted) {
			String msg =
				"Unable to delete staging results file: '" + fileName + "'";
			log.warning(msg);
		} else {
			assert !f.exists();
		}

		return retVal;
	}

	/**
	 * This method takes one record at a time from the staging source and
	 * performs matching against the master source. It's basically like
	 * findMatches.
	 *
	 * @param data
	 * @throws Exception
	 */
	private void handleSingleMatching(DataSource stageDS, DataSource masterDS,
			OabaJobMessage data, IMatchRecord2Sink mSinkFinal,
			BatchJob batchJob, OabaParameters oabaParams,
			OabaSettings oabaSettings) throws BlockingException {

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
			log.severe(msg);
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
		log.fine("DataSource : " + masterDS);
		log.fine("DatabaseConfiguration: " + databaseConfiguration);

		cacheAbaStatistics(masterDS);
		final AbaStatistics stats =
			getAbaStatisticsController().getStatistics(model);

		final String dbaName = getReferenceAccessorName(oabaParams);
		log.fine("DatabaseAccessor: " + dbaName);
		final DatabaseAccessor databaseAccessor = getReferenceAccessor(dbaName);
		databaseAccessor.setCondition("");
		databaseAccessor.setDataSource(masterDS);

		RecordSource stage = null;
		try {
			stage = getRecordSourceController().getStageRs(oabaParams);
		} catch (Exception e) {
			String msg = "Unable to get staging record source: " + e;
			log.severe(msg);
			throw new BlockingException(msg);
		}
		assert stage != null;
		assert stage.getModel() == model;

		final RecordDecisionMaker dm = new RecordDecisionMaker();
		try {
			log.info("Finding matches of master records to staging records...");
			stage.open();
			mSinkFinal.append();

			while (stage.hasNext()) {
				Record q = stage.getNext();
				AutomatedBlocker rs =
					new Blocker2(databaseAccessor, model, q, limitPBS, stbsgl,
							limitSBS, stats, databaseConfiguration,
							blockingConfiguration);
				log.fine(q.getId() + " " + rs + " " + model);

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

					// write match candidate to file.
					mSinkFinal.writeMatch(mr2);
				}

			}
			log.info("...finished finding matches of master records to staging records...");

		} catch (IOException x) {
			String msg = "Unable to read staging records from source: " + x;
			log.severe(msg);
			throw new BlockingException(msg);
		} finally {
			if (stage != null) {
				try {
					stage.close();
				} catch (Exception e) {
					String msg = "Unable to close staging record source: " + e;
					log.severe(msg);
				}
			}
			if (mSinkFinal != null) {
				mSinkFinal.close();
			}
		}

		// mark as done
		batchJob.markAsCompleted();
		sendToUpdateStatus(batchJob, BatchProcessingEvent.DONE, new Date(),
				null);
		final ProcessingEventLog processingEntry =
			getProcessingController().getProcessingLog(batchJob);
		processingEntry.setCurrentProcessingEvent(BatchProcessingEvent.DONE);
	}

	/** Cache ABA statistics for field-value counts from a reference source */
	private void cacheAbaStatistics(DataSource ds) throws BlockingException {
		log.info("Caching ABA statistics for reference records..");
		try {
			DatabaseAbstractionManager mgr =
				new AggregateDatabaseAbstractionManager();
			DatabaseAbstraction dba = mgr.lookupDatabaseAbstraction(ds);
			DbbCountsCreator cc = new DbbCountsCreator();
			cc.setCacheCountSources(ds, dba, getAbaStatisticsController());
		} catch (SQLException | DatabaseException e) {
			String msg = "Unable to cache master ABA statistics: " + e;
			log.severe(msg);
			throw new BlockingException(msg);
		}
		log.info("... finished caching ABA statistics for reference records.");
	}

	private String getReferenceAccessorName(OabaParameters oabaParams) {
		final OabaParametersController pc = getParametersController();
		final String retVal;
		retVal = pc.getReferenceDatabaseAccessor(oabaParams);
		if (retVal == null) {
			String msg =
				"Null database accessor name for reference record source";
			log.severe(msg);
			throw new IllegalStateException(msg);
		}
		log.fine("Database accessor name: " + retVal);
		return retVal;
	}

	private DatabaseAccessor getReferenceAccessor(String dbaName)
			throws BlockingException {

		final CMExtension dbaExt =
			CMPlatformUtils.getExtension(DATABASE_ACCESSOR, dbaName);
		if (dbaExt == null) {
			String msg = "null DatabaseAccessor extension for: " + dbaName;
			log.severe(msg);
			throw new IllegalStateException(msg);
		}

		DatabaseAccessor retVal = null;
		try {
			final CMConfigurationElement[] configElements =
				dbaExt.getConfigurationElements();
			if (configElements == null || configElements.length == 0) {
				String msg = "No database accessor configurations: " + dbaName;
				log.severe(msg);
				throw new IllegalStateException(msg);
			} else if (configElements.length != 1) {
				String msg =
					"Multiple database accessor configurations for " + dbaName
							+ ": " + configElements.length;
				log.warning(msg);
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
			log.severe(msg);
			throw new BlockingException(msg);
		}
		assert retVal != null;
		return retVal;
	}

	private void sendToUpdateStatus(BatchJob job, ProcessingEvent event,
			Date timestamp, String info) {
		getProcessingController().updateStatusWithNotification(job, event,
				timestamp, info);
	}

	@Override
	protected Logger getLogger() {
		return log;
	}

	@Override
	protected Logger getJmsTrace() {
		return jmsTrace;
	}

	@Override
	protected void notifyProcessingCompleted(OabaJobMessage data) {
		// No further processing, so no notification
	}

	@Override
	protected BatchProcessingEvent getCompletionEvent() {
		return BatchProcessingEvent.DONE;
	}

}
