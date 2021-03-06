/*******************************************************************************
 * Copyright (c) 2015, 2020 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

import static com.choicemaker.cm.args.OperationalPropertyNames.PN_CHUNK_FILE_COUNT;
import static com.choicemaker.cm.args.OperationalPropertyNames.PN_CURRENT_CHUNK_INDEX;
import static com.choicemaker.cm.args.OperationalPropertyNames.PN_REGULAR_CHUNK_FILE_COUNT;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.naming.NamingException;

import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ProcessingEvent;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobManager;
import com.choicemaker.cm.batch.api.BatchJobStatus;
import com.choicemaker.cm.batch.api.EventPersistenceManager;
import com.choicemaker.cm.batch.api.OperationalPropertyController;
import com.choicemaker.cm.batch.api.ProcessingEventLog;
import com.choicemaker.cm.batch.ejb.BatchJobControl;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.oaba.api.OabaParametersController;
import com.choicemaker.cm.oaba.api.OabaSettingsController;
import com.choicemaker.cm.oaba.api.ServerConfigurationController;
import com.choicemaker.cm.oaba.core.IChunkDataSinkSourceFactory;
import com.choicemaker.cm.oaba.core.IMatchRecord2Sink;
import com.choicemaker.cm.oaba.core.OabaEventBean;
import com.choicemaker.cm.oaba.core.OabaProcessingConstants;
import com.choicemaker.cm.oaba.core.RecordMatchingMode;
import com.choicemaker.cm.oaba.ejb.data.ChunkDataStore;
import com.choicemaker.cm.oaba.ejb.data.MatchWriterMessage;
import com.choicemaker.cm.oaba.ejb.data.OabaJobMessage;
import com.choicemaker.cm.oaba.ejb.util.LoggingUtils;
import com.choicemaker.cm.oaba.ejb.util.MessageBeanUtils;
import com.choicemaker.cm.oaba.utils.MemoryEstimator;

/**
 * Common functionality of {@link MatcherScheduler2} and
 * {@link TransMatchScheduler}. This class is implemented as a Singleton EJB,
 * not an MDB, because it must retain data in memory between invocations of
 * <code>onMessage</code>.
 */
public abstract class AbstractSchedulerSingleton implements Serializable {

	private static final long serialVersionUID = 271L;

	// FIXME REMOVEME (after operational properties are completed)
	private static final String DELIM = "|";

	// -- Session data

	private RecordSource[] stageRS = null;

	private RecordSource[] masterRS = null;

	// This counts the number of messages sent to matcher and number of done
	// messages got back.
	private int countMessages;

	// this indicates which chunks is currently being processed.
	private int currentChunk = -1;

	private long numCompares;

	private long numMatches;

	private long currentJobID = -1;

	// time trackers
	private long timeStart;
	private long timeReadData;
	private long timegc;

	// array size = number of processors
	// these time tracker are active only in getLogger() debug
	private long[] timeWriting;
	private long[] inHMLookUp;
	private long[] inCompare;

	// number of processing threads to use -- write once
	private int numProcessors;

	private int getNumProcessors() {
		return numProcessors;
	}

	// max chunk
	private int maxChunkSize;

	// -- Callbacks

	protected abstract BatchJobManager getJobController();

	protected abstract OabaParametersController getOabaParametersController();

	protected abstract ServerConfigurationController getServerController();

	protected abstract OabaSettingsController getSettingsController();

	protected abstract OperationalPropertyController getPropertyController();

	protected abstract EventPersistenceManager getEventManager();

	protected abstract JMSContext getJmsContext();

	protected abstract Logger getLogger();

	protected abstract Logger getJMSTrace();

	protected abstract void cleanUp(BatchJob job, OabaJobMessage sd)
			throws BlockingException;

	protected abstract void sendToMatcher(OabaJobMessage sd);

	protected abstract void sendToUpdateStatus(BatchJob job,
			ProcessingEvent event, Date timestamp, String info);

	protected abstract void sendToMatchDebup(BatchJob job, OabaJobMessage sd);

	protected abstract void sendToSingleRecordMatching(BatchJob job,
			OabaJobMessage sd);

	// -- Message processing

	public void onMessage(Message inMessage) {
		getJMSTrace()
				.info("Entering onMessage for " + this.getClass().getName());
		ObjectMessage msg = null;
		BatchJob batchJob = null;

		getLogger().fine("MatchSchedulerMDB In onMessage");

		try {
			if (inMessage instanceof ObjectMessage) {
				msg = (ObjectMessage) inMessage;
				Object o = msg.getObject();

				if (o instanceof OabaJobMessage) {
					final OabaJobMessage sd = (OabaJobMessage) o;
					final long jobId = sd.jobID;
					batchJob = getJobController().findBatchJob(jobId);
					OabaParameters params = getOabaParametersController()
							.findOabaParametersByBatchJobId(jobId);
					OabaSettings oabaSettings =
						getSettingsController().findOabaSettingsByJobId(jobId);
					ServerConfiguration serverConfig = getServerController()
							.findServerConfigurationByJobId(jobId);

					if (batchJob == null /* FIXME || dbParams == null */
							|| params == null || oabaSettings == null
							|| serverConfig == null) {
						String s0 = "Null configuration info for job " + jobId;
						String s = LoggingUtils.buildDiagnostic(s0, batchJob,
								params, oabaSettings, serverConfig);
						getLogger().severe(s);
						throw new IllegalStateException(s);
					}

					final String modelConfigId =
						params.getModelConfigurationName();
					ImmutableProbabilityModel model =
						PMManager.getModelInstance(modelConfigId);
					if (model == null) {
						String s = "No modelId corresponding to '"
								+ modelConfigId + "'";
						getLogger().severe(s);
						throw new IllegalArgumentException(s);
					}

					countMessages = 0;
					maxChunkSize = oabaSettings.getMaxChunkSize();
					numProcessors = serverConfig.getMaxChoiceMakerThreads();
					setMaxTempPairwiseIndex(batchJob, numProcessors);
					getLogger().info("Maximum chunk size: " + maxChunkSize);
					getLogger().info("Number of processing threads: "
							+ getNumProcessors());
					getLogger()
							.info("Max index for intermediate pairwise result files: "
									+ getNumProcessors());

					ProcessingEventLog processingLog =
						getEventManager().getProcessingLog(batchJob);
					if (processingLog
							.getCurrentProcessingEventId() >= OabaProcessingConstants.EVT_DONE_MATCHING_DATA) {
						// matching is already done, so go on to the next step.
						nextSteps(batchJob, sd);
					} else {
						if (sd.jobID != currentJobID) {
							// reset counters
							numCompares = 0;
							numMatches = 0;
							currentChunk = -1;
							currentJobID = sd.jobID;
							timeStart = System.currentTimeMillis();
							timeReadData = 0;
							timegc = 0;

							timeWriting = new long[getNumProcessors()];
							inCompare = new long[getNumProcessors()];
							inHMLookUp = new long[getNumProcessors()];
						}

						// start matching
						startMatch(sd);
					}

				} else if (o instanceof MatchWriterMessage) {
					final MatchWriterMessage mwd = (MatchWriterMessage) o;
					handleNextChunk(mwd);
				}

			} else {
				getLogger().warning(
						"wrong type: " + inMessage.getClass().getName());
			}

		} catch (Exception e) {
			String msg0 = throwableToString(e);
			getLogger().severe(msg0);
			if (batchJob != null) {
				batchJob.markAsFailed();
				getJobController().save(batchJob);
			}
		}
		getJMSTrace()
				.info("Exiting onMessage for " + this.getClass().getName());
	}

	protected String throwableToString(Throwable throwable) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println(throwable.toString());
		throwable.printStackTrace(pw);
		pw.close();
		return sw.toString();
	}

	/**
	 * This method is called when a chunk is done and the system is ready for
	 * the next chunk.
	 *
	 * It tabulates the statistics from the chunk that just finished and it
	 * starts the next available chunk.
	 *
	 * @param mwd
	 *            - the message data from the chunk that just finished.
	 * @throws RemoteException
	 * @throws XmlConfException
	 * @throws BlockingException
	 * @throws NamingException
	 * @throws JMSException
	 */
	protected final void handleNextChunk(MatchWriterMessage mwd)
			throws BlockingException {

		final long jobId = mwd.jobID;
		BatchJob batchJob = getJobController().findBatchJob(jobId);
		OabaJobMessage sd = new OabaJobMessage(mwd);
		ProcessingEventLog status =
			getEventManager().getProcessingLog(batchJob);

		// keeping track of messages sent and received.
		countMessages--;
		getLogger().info("outstanding messages: " + countMessages);

		if (BatchJobStatus.ABORT_REQUESTED == batchJob.getStatus()) {
			MessageBeanUtils.stopJob(batchJob, getJobController(),
					getPropertyController(), status);

		} else if (BatchJobStatus.ABORTED != batchJob.getStatus()) {
			// if there are multiple processors, we have don't do anything for
			// STATUS_ABORTED.

			final String _latestChunkProcessed = getPropertyController()
					.getOperationalPropertyValue(batchJob, PN_CURRENT_CHUNK_INDEX);
			final int latestChunkProcessed =
				Integer.valueOf(_latestChunkProcessed);
			assert latestChunkProcessed == currentChunk;

			numCompares += mwd.numCompares;
			numMatches += mwd.numMatches;

			final int treeIndexZeroBased = mwd.treeIndex - 1;
			timeWriting[treeIndexZeroBased] += mwd.timeWriting;
			inHMLookUp[treeIndexZeroBased] += mwd.inLookup;
			inCompare[treeIndexZeroBased] += mwd.inCompare;

			recordChunkProperties(latestChunkProcessed, treeIndexZeroBased);

			// Go on to the next chunk
			if (countMessages == 0) {
				final String _numChunks = getPropertyController()
						.getOperationalPropertyValue(batchJob, PN_CHUNK_FILE_COUNT);
				final int numChunks = Integer.valueOf(_numChunks);

				final String _numRegularChunks = getPropertyController()
						.getOperationalPropertyValue(batchJob, PN_REGULAR_CHUNK_FILE_COUNT);
				final int numRegularChunks = Integer.valueOf(_numRegularChunks);

				String temp = Integer.toString(numChunks) + DELIM
						+ Integer.toString(numRegularChunks) + DELIM
						+ Integer.toString(currentChunk);
				status.setCurrentProcessingEvent(OabaEventBean.MATCHING_DATA,
						temp);

				getLogger().info("Chunk " + latestChunkProcessed + " is done.");

				ChunkDataStore.getInstance().cleanUp();
				currentChunk++;

				if (currentChunk < numChunks) {
					startChunk(sd, currentChunk);

				} else {
					// all the chunks are done
					status.setCurrentProcessingEvent(
							OabaEventBean.DONE_MATCHING_DATA);
					recordSummary();
					nextSteps(batchJob, sd);

				}
			} // end countMessages == 0
		} // end if abort requested
	}

	protected void recordChunkProperties(final int latestChunkProcessed,
			final int treeIndexZeroBased) {

		final int treeIndexOneBased = treeIndexZeroBased + 1;

		// numCompares += mwd.numCompares;
		// numMatches += mwd.numMatches;
		//
		// timeWriting[mwd.treeIndex - 1] += mwd.timeWriting;
		// inHMLookUp[mwd.treeIndex - 1] += mwd.inLookup;
		// inCompare[mwd.treeIndex - 1] += mwd.inCompare;

		getLogger().info("Current chunk: " + currentChunk);
		getLogger().info("Chunk " + latestChunkProcessed + " tree "
				+ treeIndexOneBased + " is done.");
	}

	protected void recordSummary() {
		getLogger().info("total comparisons: " + numCompares
				+ " total matches: " + numMatches);
		final long duration = System.currentTimeMillis() - timeStart;
		getLogger().info("total matching time: " + duration);
		getLogger().info("total reading data time: " + timeReadData);
		getLogger().info("total garbage collection time: " + timegc);

		// log details
		if (getLogger().isLoggable(Level.FINE)) {
			for (int i = 0; i < getNumProcessors(); i++) {
				getLogger().fine("Processor " + i + " writing time: "
						+ timeWriting[i] + " lookup time: " + inHMLookUp[i]
						+ " compare time: " + inCompare[i]);
			}
		}
	}

	protected RecordMatchingMode getRecordMatchingMode(final BatchJob job) {
		RecordMatchingMode retVal =
			BatchJobUtils.getRecordMatchingMode(getPropertyController(), job);
		if (retVal == null) {
			String msg = "Null record-matching mode for job " + job.getId();
			throw new IllegalStateException(msg);
		}
		assert retVal != null;
		return retVal;
	}

	/**
	 * This method is called when all the chunks are done.
	 */
	protected final void nextSteps(final BatchJob job, OabaJobMessage sd)
			throws BlockingException {

		// Update the processing status and remove intermediate files
		sendToUpdateStatus(job, OabaEventBean.DONE_MATCHING_CHUNKS, new Date(),
				null);
		cleanUp(job, sd);

		// Next processing stage depends on the record-matching mode
		RecordMatchingMode mode = getRecordMatchingMode(job);
		switch (mode) {
		case SRM:
			sendToSingleRecordMatching(job, sd);
			break;
		case BRM:
			sendToUpdateStatus(job, OabaEventBean.DONE_MATCHING_DATA,
					new Date(), null);
			sendToMatchDebup(job, sd);
			break;
		default:
			throw new Error("Unexpected mode: " + mode);
		}
	}

	/**
	 * This method sends the different chunks to different beans.
	 */
	protected final void startMatch(final OabaJobMessage sd)
			throws RemoteException, BlockingException, NamingException,
			XmlConfException {

		// init values
		final long jobId = sd.jobID;
		BatchJob batchJob = getJobController().findBatchJob(jobId);
		ProcessingEventLog processingLog =
			getEventManager().getProcessingLog(batchJob);

		if (BatchJobStatus.ABORT_REQUESTED == batchJob.getStatus()) {
			MessageBeanUtils.stopJob(batchJob, getJobController(),
					getPropertyController(), processingLog);

		} else {
			currentChunk = 0;
			if (processingLog
					.getCurrentProcessingEventId() == OabaProcessingConstants.EVT_MATCHING_DATA) {
				currentChunk = recover(batchJob, sd, processingLog) + 1;
				getLogger().info("recovering from " + currentChunk);
			}

			// set up the record source arrays.
			OabaParameters oabaParams = getOabaParametersController()
					.findOabaParametersByBatchJobId(jobId);
			String modelName = oabaParams.getModelConfigurationName();
			ImmutableProbabilityModel ipm =
				PMManager.getImmutableModelInstance(modelName);
			IChunkDataSinkSourceFactory stageFactory =
				OabaFileUtils.getStageDataFactory(batchJob, ipm);
			IChunkDataSinkSourceFactory masterFactory =
				OabaFileUtils.getMasterDataFactory(batchJob, ipm);

			final String _numChunks = getPropertyController()
					.getOperationalPropertyValue(batchJob, PN_CHUNK_FILE_COUNT);
			final int numChunks = Integer.valueOf(_numChunks);

			stageRS = new RecordSource[numChunks];
			masterRS = new RecordSource[numChunks];

			for (int i = 0; i < numChunks; i++) {
				stageRS[i] = stageFactory.getNextSource();
				masterRS[i] = masterFactory.getNextSource();
			}

			if (numChunks > 0) {
				startChunk(sd, currentChunk);
			} else {
				// special case of nothing to do, except to clean up
				getLogger().info("No matching chunk found.");
				noChunk(sd);
			}
		}
	}

	protected final int recover(BatchJob batchJob, OabaJobMessage sd,
			ProcessingEventLog status) throws BlockingException {

		StringTokenizer stk =
			new StringTokenizer(status.getCurrentProcessingEventInfo(), DELIM);

		final int numChunks = Integer.parseInt(stk.nextToken());
		getLogger().info("Number of chunks " + numChunks);
		getPropertyController().setJobProperty(batchJob, PN_CHUNK_FILE_COUNT,
				String.valueOf(numChunks));

		final int numRegularChunks = Integer.parseInt(stk.nextToken());
		getLogger().info("Number of regular chunks " + numChunks);
		getPropertyController().setJobProperty(batchJob,
				PN_REGULAR_CHUNK_FILE_COUNT, String.valueOf(numRegularChunks));

		int currentChunk = Integer.parseInt(stk.nextToken());
		return currentChunk;
	}

	/**
	 * This is a special case when TE is not needed, because all the match
	 * graphs are size 2 or 0.
	 *
	 */
	protected final void noChunk(final OabaJobMessage sd)
			throws XmlConfException, BlockingException, NamingException {
		final long jobId = sd.jobID;
		BatchJob batchJob = getJobController().findBatchJob(jobId);

		// This is because tree ids start with 1 and not 0.
		for (int i = 1; i <= getNumProcessors(); i++) {
			@SuppressWarnings("rawtypes")
			IMatchRecord2Sink mSink =
				OabaFileUtils.getMatchChunkFactory(batchJob).getSink(i);
			mSink.open();
			mSink.close();
			getLogger().fine("creating " + mSink.getInfo());
		}

		nextSteps(batchJob, sd);
	}

	/**
	 * This method sends messages out to matchers beans to work on the current
	 * chunk.
	 */
	protected final void startChunk(final OabaJobMessage sd,
			final int currentChunk) throws BlockingException {

		getLogger().fine("startChunk " + currentChunk);

		final long jobId = sd.jobID;
		final BatchJob batchJob = getJobController().findBatchJob(jobId);
		final OabaParameters params =
			getOabaParametersController().findOabaParametersByBatchJobId(jobId);
		final String modelConfigId = params.getModelConfigurationName();
		final ImmutableProbabilityModel model =
			PMManager.getModelInstance(modelConfigId);

		getLogger().info("Current chunk " + currentChunk);
		getPropertyController().setJobProperty(batchJob, PN_CURRENT_CHUNK_INDEX,
				String.valueOf(currentChunk));

		// call to garbage collection
		long t = System.currentTimeMillis();
		ChunkDataStore dataStore = ChunkDataStore.getInstance();
		dataStore.cleanUp();
		System.gc();
		t = System.currentTimeMillis() - t;
		this.timegc += t;

		// read in the data;
		final BatchJobControl control =
			new BatchJobControl(this.getJobController(), batchJob);
		t = System.currentTimeMillis();
		dataStore.init(stageRS[currentChunk], model, masterRS[currentChunk],
				maxChunkSize, control);

		t = System.currentTimeMillis() - t;
		this.timeReadData += t;

		MemoryEstimator.writeMem();

		// Send messages to matchers. Matcher indices are one-based.
		for (int i = 1; i <= getNumProcessors(); i++) {
			OabaJobMessage sd2 = new OabaJobMessage(sd);
			sd2.treeIndex = i;
			countMessages++;
			sendToMatcher(sd2);
			getLogger().info("outstanding messages: " + countMessages);
		}
	}

	private void setMaxTempPairwiseIndex(BatchJob job, int max) {
		BatchJobUtils.setMaxTempPairwiseIndex(getPropertyController(), job,
				max);
	}

}
