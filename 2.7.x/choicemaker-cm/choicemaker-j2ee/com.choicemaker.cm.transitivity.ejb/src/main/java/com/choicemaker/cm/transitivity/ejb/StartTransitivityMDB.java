/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.ejb;

import static com.choicemaker.cm.args.OperationalPropertyNames.PN_CHUNK_FILE_COUNT;
import static com.choicemaker.cm.args.OperationalPropertyNames.PN_REGULAR_CHUNK_FILE_COUNT;
import static com.choicemaker.cm.oaba.core.RecordMatchingMode.BRM;
import static com.choicemaker.cm.oaba.core.RecordMatchingMode.SRM;
import static com.choicemaker.cm.transitivity.core.TransitivityEventBean.DONE_CREATE_CHUNK_DATA;
import static com.choicemaker.cm.transitivity.core.TransitivityEventBean.DONE_TRANS_DEDUP_OVERSIZED;

import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.Queue;
import javax.transaction.UserTransaction;

import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.ProcessingEventLog;
import com.choicemaker.cm.batch.ejb.BatchJobControl;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.ISerializableRecordSource;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.oaba.core.IBlockSink;
import com.choicemaker.cm.oaba.core.IBlockSource;
import com.choicemaker.cm.oaba.core.IMatchRecord2SinkSourceFactory;
import com.choicemaker.cm.oaba.core.IMatchRecord2Source;
import com.choicemaker.cm.oaba.core.IRecordIdSink;
import com.choicemaker.cm.oaba.core.IRecordIdSinkSourceFactory;
import com.choicemaker.cm.oaba.core.ImmutableRecordIdTranslator;
import com.choicemaker.cm.oaba.core.RecordMatchingMode;
import com.choicemaker.cm.oaba.ejb.BatchJobUtils;
import com.choicemaker.cm.oaba.ejb.OabaFileUtils;
import com.choicemaker.cm.oaba.ejb.data.OabaJobMessage;
import com.choicemaker.cm.oaba.ejb.util.MessageBeanUtils;
import com.choicemaker.cm.oaba.impl.IDSetSource;
import com.choicemaker.cm.oaba.result.MatchToBlockTransformer2;
import com.choicemaker.cm.oaba.result.Size2MatchProducer;
import com.choicemaker.cm.oaba.services.ChunkService3;
import com.choicemaker.cm.oaba.utils.Transformer;
import com.choicemaker.cm.transitivity.core.TransitivityEventBean;

/**
 * This message bean starts the Transitivity Engine.
 */
@SuppressWarnings({
		"rawtypes" })
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup",
				propertyValue = "java:/choicemaker/urm/jms/transitivityQueue"),
		@ActivationConfigProperty(propertyName = "destinationType",
				propertyValue = "javax.jms.Queue") })
//@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@TransactionManagement(value = TransactionManagementType.BEAN)
public class StartTransitivityMDB extends AbstractTransitivityBmtMDB {

	private static final long serialVersionUID = 1L;

	private static final Logger log =
		Logger.getLogger(StartTransitivityMDB.class.getName());

	private static final Logger jmsTrace =
		Logger.getLogger("jmstrace." + StartTransitivityMDB.class.getName());

	/**
	 * The name of a system property that can be set to "true" to reuse the
	 * translator created by the preceding OABA job, assuming that the OABA job
	 * was run in BRM mode. If this property is false, or the OABA job was not
	 * run in BRM mode, then a new translator is created from the pairwise
	 * results of the OABA job. By default, if this property is not set, then
	 * reuse is presumed; i.e. not setting this property is the same as setting
	 * it to <code>true</code>. The only way to disable reuse is explicitly set
	 * this property to <code>false</code>.
	 */
	public static final String PN_REUSE_BRM_TRANSLATOR =
		"choicemaker.trans.ReuseBrmTranslator";

	public static final String DEFAULT_REUSE_BRM_TRANSLATOR = "true";

	/**
	 * Checks the system property {@link #PN_REUSE_BRM_TRANSLATOR} and caches
	 * the result
	 */
	private boolean isBrmTranslatorReuseRequested() {
		String value = System.getProperty(PN_REUSE_BRM_TRANSLATOR,
				DEFAULT_REUSE_BRM_TRANSLATOR);
		Boolean _reuse = Boolean.valueOf(value);
		boolean retVal = _reuse.booleanValue();
		return retVal;
	}

	private boolean isBrmTranslatorReuseRequested =
		isBrmTranslatorReuseRequested();

	@Resource
	private MessageDrivenContext jmsCtx;

	@Resource
	private UserTransaction userTx;

	@Resource(lookup = "java:/choicemaker/urm/jms/transMatchSchedulerQueue")
	private Queue transMatchSchedulerQueue;

	@Override
	protected void processOabaMessage(OabaJobMessage data, BatchJob batchJob,
			TransitivityParameters params, OabaSettings oabaSettings,
			ProcessingEventLog processingLog, ServerConfiguration serverConfig,
			ImmutableProbabilityModel model) throws BlockingException {

		batchJob.markAsStarted();
		getTransitivityJobController().save(batchJob);
		removeOldFiles(batchJob);
		createChunks(batchJob, params, oabaSettings, serverConfig);
	}

	/*
	 * This method calls MatchToBlockTransformer to create blocks for the
	 * equivalence classes. It then calls ChunkService3 to create chunks.
	 */
	private void createChunks(BatchJob transJob, TransitivityParameters params,
			OabaSettings oabaSettings, ServerConfiguration serverConfig)
			throws BlockingException {

		// Get the parent/predecessor OABA job
		final long oabaJobId = transJob.getBatchParentId();
		final BatchJob oabaJob =
			this.getOabaJobController().findBatchJob(oabaJobId);

		// Get the match record source from the OABA job
		IMatchRecord2Source mSource =
			OabaFileUtils.getCompositeMatchSource(oabaJob);
		assert mSource != null;

		// Create a translator for this job
		RecordMatchingMode oabaMode = BatchJobUtils
				.getRecordMatchingMode(getPropertyController(), oabaJob);
		ImmutableRecordIdTranslator currentTranslator = null;
		if (isBrmTranslatorReuseRequested && oabaMode == BRM) {
			currentTranslator =
				this.getRecordIdController().findRecordIdTranslator(oabaJob);
		} else {
			assert oabaMode == SRM || !isBrmTranslatorReuseRequested;
			// SRM mode is not working yet, nor is translator creation
			// currentTranslator = createTranslator(transJob, mSource);
			String msg;
			if (oabaMode == SRM) {
				msg = "SRM mode is not yet supported transitivity analysis";
			} else {
				msg = "Translator reuse is currently required "
						+ "during transitivity analysis in BRM mode";
			}
			log.severe(msg);
			throw new BlockingException(msg);
		}

		// Create a block sink for the Transitivity job
		IBlockSink bSink = TransitivityFileUtils
				.getTransitivityBlockFactory(transJob).getNextSink();

		// Create blocks for the Transitivity job
		IMatchRecord2SinkSourceFactory mFactory =
			OabaFileUtils.getMatchTempFactory(transJob);
		IRecordIdSinkSourceFactory idFactory =
			this.getRecordIdController().getRecordIdSinkSourceFactory(transJob);
		IRecordIdSink idSink = idFactory.getNextSink();
		MatchToBlockTransformer2 transformer = new MatchToBlockTransformer2(
				mSource, mFactory, currentTranslator, bSink, idSink);
		int numRecords = transformer.process();
		log.fine("Number of records: " + numRecords);

		// build a MatchRecord2Sink for all pairs belonging to the size 2 sets.
		Size2MatchProducer producer =
			new Size2MatchProducer(mSource, idFactory.getSource(idSink),
					OabaFileUtils.getSet2MatchFactory(transJob).getNextSink());
		int twos = producer.process();
		log.info("number of size 2 EC: " + twos);

		// Clean up the Transitivity job
		idSink.remove();

		IBlockSource bSource = TransitivityFileUtils
				.getTransitivityBlockFactory(transJob).getSource(bSink);
		IDSetSource source2 = new IDSetSource(bSource);

		final String modelConfigId = params.getModelConfigurationName();
		ImmutableProbabilityModel model =
			PMManager.getModelInstance(modelConfigId);

		int maxChunk = oabaSettings.getMaxChunkSize();
		if (transformer.getMaxEC() > maxChunk)
			throw new RuntimeException("There is an equivalence class of size "
					+ transformer.getMaxEC()
					+ ", which is bigger than the max chunk size of " + maxChunk
					+ ".");

		// get the number of processors
		int numProcessors = serverConfig.getMaxChoiceMakerThreads();

		// get the number of chunk files
		int numFiles = serverConfig.getMaxOabaChunkFileCount();

		// create the oversized block transformer
		Transformer transformerO =
			new Transformer(currentTranslator, OabaFileUtils
					.getComparisonArrayGroupFactoryOS(transJob, numProcessors));

		// Set the source for the staging records
		ISerializableRecordSource staging = null;
		try {
			staging = this.getRecordSourceController().getStageRs(params);
		} catch (Exception e) {
			String msg = "Unable to staging record source: " + e.toString();
			getLogger().severe(msg);
			throw new BlockingException(msg, e);
		}

		// Set the source for the master records
		ISerializableRecordSource master = null;
		try {
			master = this.getRecordSourceController().getMasterRs(params);
		} catch (Exception e) {
			String msg = "Unable to master record source: " + e.toString();
			getLogger().severe(msg);
			throw new BlockingException(msg, e);
		}

		// Set the correct processing status prior to chunk creation.
		// (This is a potential, but unlikely, race condition. A cleaner
		// approach would be to use a local stack implementation of
		// ProcessingEventLog, and then set the persistent value after the
		// chunk service completes.)
		ProcessingEventLog status =
			this.getEventManager().getProcessingLog(transJob);
		status.setCurrentProcessingEvent(DONE_TRANS_DEDUP_OVERSIZED);

		final RecordMatchingMode mode = getRecordMatchingMode(transJob);

		final BatchJobControl control =
			new BatchJobControl(this.getTransitivityJobController(), transJob);

		ChunkService3 chunkService = new ChunkService3(source2, null, staging,
				master, model, OabaFileUtils.getChunkIDFactory(transJob),
				OabaFileUtils.getStageDataFactory(transJob, model),
				OabaFileUtils.getMasterDataFactory(transJob, model),
				currentTranslator, transformerO, null, maxChunk, numFiles,
				status, control, mode);
		chunkService.runService();
		log.info("Done creating chunks " + chunkService.getTimeElapsed());

		final int numChunks = chunkService.getNumChunks();
		log.info("Number of chunks " + numChunks);
		this.getPropertyController().setJobProperty(transJob,
				PN_CHUNK_FILE_COUNT, String.valueOf(numChunks));

		// this is important because in transitivity, there are only OS chunks.
		final int numRegularChunks = 0;
		log.info("Number of regular chunks " + numChunks);
		this.getPropertyController().setJobProperty(transJob,
				PN_REGULAR_CHUNK_FILE_COUNT, String.valueOf(numRegularChunks));
	}

	// @SuppressWarnings("unused")
	// private ImmutableRecordIdTranslator createTranslator(BatchJob transJob,
	// IMatchRecord2Source mSource) throws BlockingException {
	//
	// MutableRecordIdTranslator mrit =
	// this.getRecordIdController().createMutableRecordIdTranslator(
	// transJob);
	// try {
	// mrit.open();
	// try {
	// mSource.open();
	// while (mSource.hasNext()) {
	// MatchRecord2 mr = (MatchRecord2) mSource.next();
	// Comparable id1 = mr.getRecordID1();
	// @SuppressWarnings({
	// "unchecked", "unused" })
	// int unused1 = mrit.translate(id1);
	// RECORD_SOURCE_ROLE r2Role = mr.getRecord2Role();
	// switch (r2Role) {
	// case STAGING:
	// case SOURCE1_NODUPES:
	// Comparable id2 = mr.getRecordID2();
	// @SuppressWarnings({
	// "unchecked", "unused" })
	// int unused2 = mrit.translate(id2);
	// break;
	// case MASTER:
	// case SOURCE2_DUPES:
	// break;
	// default:
	// throw new Error("Unexpected role: " + r2Role);
	// }
	// }
	// mSource.close();
	// mrit.split();
	// mSource.open();
	// while (mSource.hasNext()) {
	// MatchRecord2 mr = (MatchRecord2) mSource.next();
	// RECORD_SOURCE_ROLE r2Role = mr.getRecord2Role();
	// switch (r2Role) {
	// case STAGING:
	// case SOURCE1_NODUPES:
	// break;
	// case MASTER:
	// case SOURCE2_DUPES:
	// Comparable id2 = mr.getRecordID2();
	// @SuppressWarnings({
	// "unchecked", "unused" })
	// int unused2 = mrit.translate(id2);
	// break;
	// default:
	// throw new Error("Unexpected role: " + r2Role);
	// }
	// }
	// mSource.close();
	// } finally {
	// mSource.close();
	// }
	// } finally {
	// mrit.close();
	// }
	//
	// @SuppressWarnings("unchecked")
	// ImmutableRecordIdTranslator retVal =
	// this.getRecordIdController().toImmutableTranslator(mrit);
	// return retVal;
	// }

	/**
	 * This method removes any pre-existing transMatch* files
	 * 
	 * @param transJob
	 *            a transitivity batch job
	 */
	private void removeOldFiles(BatchJob transJob) throws BlockingException {
		try {
			// final sink
			IMatchRecord2Source finalSource =
				TransitivityFileUtils.getCompositeTransMatchSource(transJob);
			if (finalSource.exists()) {
				log.info("removing old transMatch files: "
						+ finalSource.getInfo());
				finalSource.delete();
			}
		} catch (IllegalArgumentException e) {
			// this is expected if the source was never created.
			log.info("No old transMatch files to remove");
		}
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
	protected MessageDrivenContext getMdcCtx() {
		return jmsCtx;
	}

	@Override
	protected UserTransaction getUserTx() {
		return userTx;
	}

	@Override
	protected TransitivityEventBean getCompletionEvent() {
		return DONE_CREATE_CHUNK_DATA;
	}

	@Override
	protected void notifyProcessingCompleted(OabaJobMessage data) {
		MessageBeanUtils.sendStartData(data, getJmsContext(),
				transMatchSchedulerQueue, getLogger());
	}

}
