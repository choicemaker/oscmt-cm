/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.ejb;

import static com.choicemaker.cm.args.OperationalPropertyNames.PN_TRANSITIVITY_CACHED_PAIRS_FILE;
import static com.choicemaker.cm.transitivity.core.TransitivityEventBean.DONE_TRANSITIVITY_PAIRWISE;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Queue;

import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ProcessingEvent;
import com.choicemaker.cm.args.ProcessingEventBean;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.ProcessingEventLog;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.base.MatchRecord2;
import com.choicemaker.cm.oaba.core.IMatchRecord2Sink;
import com.choicemaker.cm.oaba.core.IMatchRecord2SinkSourceFactory;
import com.choicemaker.cm.oaba.core.IMatchRecord2Source;
import com.choicemaker.cm.oaba.core.IndexedFileObserver;
import com.choicemaker.cm.oaba.data.MatchRecord2Factory;
import com.choicemaker.cm.oaba.ejb.BatchJobUtils;
import com.choicemaker.cm.oaba.ejb.OabaFileUtils;
import com.choicemaker.cm.oaba.ejb.data.OabaJobMessage;
import com.choicemaker.cm.oaba.ejb.util.MessageBeanUtils;

/**
 * This match dedup bean is used by the Transitivity Engine. It dedups the
 * temporary match results and merge them with the orginal OABA results.
 *
 * @author pcheung
 *
 */
// Singleton: maxSession = 1 (JBoss only)
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "maxSession",
				propertyValue = "1"), // Singleton (JBoss only)
		@ActivationConfigProperty(propertyName = "destinationLookup",
				propertyValue = "java:/choicemaker/urm/jms/transMatchDedupQueue"),
		@ActivationConfigProperty(propertyName = "destinationType",
				propertyValue = "javax.jms.Queue") })
// @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class TransMatchDedupMDB extends AbstractTransitivityMDB {

	private static final long serialVersionUID = 2711L;
	private static final Logger log =
		Logger.getLogger(TransMatchDedupMDB.class.getName());
	private static final Logger jmsTrace =
		Logger.getLogger("jmstrace." + TransMatchDedupMDB.class.getName());

	@Resource(lookup = "java:/choicemaker/urm/jms/transSerializationQueue")
	private Queue transSerializationQueue;

	@Override
	protected void processOabaMessage(OabaJobMessage data, BatchJob batchJob,
			TransitivityParameters params, OabaSettings oabaSettings,
			ProcessingEventLog processingLog, ServerConfiguration serverConfig,
			ImmutableProbabilityModel model) throws BlockingException {
		handleMerge(batchJob, serverConfig, processingLog);
	}

	private void handleMerge(final BatchJob transJob,
			final ServerConfiguration serverConfig,
			final ProcessingEventLog processingEntry) throws BlockingException {

		log.fine("in handleMerge");

		// get the number of intermediate files
		final int numTempResults = BatchJobUtils
				.getMaxTempPairwiseIndex(getPropertyController(), transJob);

		// now merge them all together
		mergeMatches(numTempResults, transJob);

		// mark as done
		final Date now = new Date();
		final String info = null;
		sendToUpdateStatus(transJob, DONE_TRANSITIVITY_PAIRWISE, now, info);
		processingEntry.setCurrentProcessingEvent(DONE_TRANSITIVITY_PAIRWISE);

	}

	/**
	 * This method does the following: 1. concat all the MatchRecord2 files from
	 * the processors. 2. Merge in the size 2 equivalence classes
	 * MatchRecord2's.
	 *
	 * The output file contains MatchRecord2 with separator records.
	 *
	 */
	@SuppressWarnings({
			"rawtypes", "unchecked" })
	protected void mergeMatches(final int num, final BatchJob transJob)
			throws BlockingException {

		final long jobId = transJob.getId();
		OabaSettings oabaSettings =
			getSettingsController().findSettingsByTransitivityJobId(jobId);
		final int maxMatches = oabaSettings.getMaxMatches();

		IndexedFileObserver ifo = new IndexedFileObserver() {

			@Override
			public void fileCreated(int index, String fileName) {
				getIndexedPropertyController().setIndexedPropertyValue(transJob,
						TransitiveGroupInfoBean.PN_TRANSMATCH_PAIR_FILE, index,
						fileName);
			}

		};

		// final sink
		IMatchRecord2Sink finalSink = TransitivityFileUtils
				.getCompositeTransMatchSink(transJob, maxMatches, ifo);

		IMatchRecord2SinkSourceFactory factory =
			OabaFileUtils.getMatchChunkFactory(transJob);
		ArrayList tempSinks = new ArrayList();

		// the match files start with 1, not 0.
		for (int i = 1; i <= num; i++) {
			IMatchRecord2Sink mSink = factory.getSink(i);
			tempSinks.add(mSink);

			log.info("concatenating file " + mSink.getInfo());
		}

		// concatenate all the other chunk MatchRecord2 sinks.
		finalSink.append();
		Comparable C = null;

		for (int i = 0; i < tempSinks.size(); i++) {
			IMatchRecord2Sink mSink = (IMatchRecord2Sink) tempSinks.get(i);

			IMatchRecord2Source mSource = factory.getSource(mSink);
			if (mSource.exists()) {
				mSource.open();
				while (mSource.hasNext()) {
					MatchRecord2 mr = (MatchRecord2) mSource.next();
					finalSink.writeMatch(mr);

					if (C == null) {
						C = mr.getRecordID1();
					}
				}
				mSource.close();

				// clean up
				mSource.delete();
				;
			} // end if
		}

		// finally concatenate the size-two EC file
		IMatchRecord2Source mSource =
			OabaFileUtils.getSet2MatchFactory(transJob).getNextSource();
		MatchRecord2 separator = null;
		if (C != null)
			separator = MatchRecord2Factory.getSeparator(C);

		if (mSource.exists()) {
			mSource.open();
			int i = 0;
			while (mSource.hasNext()) {
				i++;
				MatchRecord2 mr = (MatchRecord2) mSource.next();
				if (C == null) {
					C = mr.getRecordID1();
					separator = MatchRecord2Factory.getSeparator(C);
				}
				finalSink.writeMatch(mr);
				finalSink.writeMatch(separator);
			}
			mSource.close();
			log.info("Num of size 2s read in " + i);

			mSource.delete();
		}

		finalSink.close();

		log.info("final output " + finalSink.getInfo());

		try {
			String cachedFileName = finalSink.getInfo();
			log.info("Cached results file: " + cachedFileName);
			getPropertyController().setJobProperty(transJob,
					PN_TRANSITIVITY_CACHED_PAIRS_FILE, cachedFileName);
		} catch (Exception e) {
			log.severe(e.toString());
		}
	}

	protected void sendToUpdateStatus(BatchJob job, ProcessingEvent event,
			Date timestamp, String info) {
		getEventManager().updateStatusWithNotification(job, event, timestamp,
				info);
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
	protected ProcessingEventBean getCompletionEvent() {
		return DONE_TRANSITIVITY_PAIRWISE;
	}

	@Override
	protected void notifyProcessingCompleted(OabaJobMessage data) {
		MessageBeanUtils.sendStartData(data, getJmsContext(),
				transSerializationQueue, getLogger());
	}

}
