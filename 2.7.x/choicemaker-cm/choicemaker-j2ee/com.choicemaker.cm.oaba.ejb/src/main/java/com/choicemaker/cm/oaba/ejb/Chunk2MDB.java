/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

import static com.choicemaker.cm.args.OperationalPropertyNames.PN_CHUNK_COMPARISONS;
import static com.choicemaker.cm.args.OperationalPropertyNames.PN_CHUNK_FILE_COUNT;
import static com.choicemaker.cm.args.OperationalPropertyNames.PN_RECORD_ID_TYPE;
import static com.choicemaker.cm.args.OperationalPropertyNames.PN_REGULAR_CHUNK_FILE_COUNT;
import static com.choicemaker.cm.args.OperationalPropertyNames.PN_TOTAL_CHUNK_COMPARISONS;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.Queue;
import javax.transaction.UserTransaction;

import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.IndexedPropertyController;
import com.choicemaker.cm.batch.api.ProcessingEventLog;
import com.choicemaker.cm.batch.ejb.BatchJobControl;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.ISerializableRecordSource;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.oaba.core.IBlockSinkSourceFactory;
import com.choicemaker.cm.oaba.core.IComparisonSet;
import com.choicemaker.cm.oaba.core.IComparisonSetSource;
import com.choicemaker.cm.oaba.core.ImmutableRecordIdTranslator;
import com.choicemaker.cm.oaba.core.OabaEventBean;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;
import com.choicemaker.cm.oaba.core.RecordMatchingMode;
import com.choicemaker.cm.oaba.ejb.data.OabaJobMessage;
import com.choicemaker.cm.oaba.ejb.util.MessageBeanUtils;
import com.choicemaker.cm.oaba.impl.IDSetSource;
import com.choicemaker.cm.oaba.services.ChunkService3;
import com.choicemaker.cm.oaba.utils.Transformer;
import com.choicemaker.cm.oaba.utils.TreeTransformer;

/**
 * This bean handles the creation of chunks, including chunk data files and
 * their corresponding block files.
 *
 * In this version, a chunk has multiple tree or array files so mutiple beans
 * are process the same chunk at the same time.
 *
 * @author pcheung
 *
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup",
				propertyValue = "java:/choicemaker/urm/jms/chunkQueue"),
		@ActivationConfigProperty(propertyName = "destinationType",
				propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode",
				propertyValue = "Dups-ok-acknowledge") })
// @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@TransactionManagement(value = TransactionManagementType.BEAN)
public class Chunk2MDB extends AbstractOabaBmtMDB {

	private static final long serialVersionUID = 271L;

	private static final Logger log =
		Logger.getLogger(Chunk2MDB.class.getName());

	private static final Logger jmsTrace =
		Logger.getLogger("jmstrace." + Chunk2MDB.class.getName());

	// private static final String SOURCE = StartOabaMDB.class.getSimpleName();

	@EJB
	private IndexedPropertyController idxPropController;

	@Resource
	private MessageDrivenContext jmsCtx;

	@Resource(lookup = "java:/choicemaker/urm/jms/matchSchedulerQueue")
	private Queue matchSchedulerQueue;

	@Override
	protected void processOabaMessage(OabaJobMessage data, BatchJob batchJob,
			OabaParameters oabaParams, OabaSettings oabaSettings,
			ProcessingEventLog processingLog, ServerConfiguration serverConfig,
			ImmutableProbabilityModel model) throws BlockingException {
		// final String METHOD = "processOabaMessage";

		final int maxBlockSize = oabaSettings.getMaxBlockSize();
		final int maxChunk = oabaSettings.getMaxChunkSize();
		final int numProcessors = serverConfig.getMaxChoiceMakerThreads();
		final int maxChunkFiles = serverConfig.getMaxFilesCount();
		log.info("Maximum chunk size: " + maxChunk);
		log.info("Number of processors: " + numProcessors);
		log.info("Maximum chunk files: " + maxChunkFiles);

		@SuppressWarnings("rawtypes")
		ImmutableRecordIdTranslator translator =
			getRecordIdController().findRecordIdTranslator(batchJob);
		log.info("Record translator: " + translator);

		// create the os block source.
		final IBlockSinkSourceFactory osFactory =
			OabaFileUtils.getOversizedFactory(batchJob);
		log.info("Oversized factory: " + osFactory);
		osFactory.getNextSource(); // the deduped OS file is file 2.
		final IDSetSource source2 = new IDSetSource(osFactory.getNextSource());
		log.info("Deduped oversized source: " + source2);

		// create the tree transformer.
		final String _recordIdType =
			getPropertyController().getJobProperty(batchJob, PN_RECORD_ID_TYPE);
		final RECORD_ID_TYPE recordIdType =
			RECORD_ID_TYPE.valueOf(_recordIdType);
		final TreeTransformer tTransformer = new TreeTransformer(translator,
				OabaFileUtils.getComparisonTreeGroupFactory(batchJob,
						recordIdType, numProcessors));

		// create the transformer for over-sized blocks
		final Transformer transformerO =
			new Transformer(translator, OabaFileUtils
					.getComparisonArrayGroupFactoryOS(batchJob, numProcessors));

		ISerializableRecordSource staging = null;
		ISerializableRecordSource master = null;
		try {
			staging = getRecordSourceController().getStageRs(oabaParams);
			master = getRecordSourceController().getMasterRs(oabaParams);
		} catch (Exception e) {
			throw new BlockingException(e.toString());
		}
		assert staging != null;

		final RecordMatchingMode mode = getRecordMatchingMode(batchJob);

		final BatchJobControl control =
			new BatchJobControl(this.getJobController(), batchJob);

		ChunkService3 chunkService = new ChunkService3(
				OabaFileUtils.getTreeSetSource(batchJob), source2, staging,
				master, model, OabaFileUtils.getChunkIDFactory(batchJob),
				OabaFileUtils.getStageDataFactory(batchJob, model),
				OabaFileUtils.getMasterDataFactory(batchJob, model), translator,
				tTransformer, transformerO, maxChunk, maxChunkFiles,
				processingLog, control, mode, getUserTx());
		log.info("Chunk service: " + chunkService);
		chunkService.runService();
		log.info("Done creating chunks " + chunkService.getTimeElapsed());

		final int numChunks = chunkService.getNumChunks();
		final int numRegularChunks = chunkService.getNumRegularChunks();
		recordChunkProperties(batchJob, numChunks, numRegularChunks,
				recordIdType, numProcessors, maxBlockSize);
	}

	protected void recordChunkProperties(final BatchJob batchJob,
			final int numChunks, final int numRegularChunks,
			final RECORD_ID_TYPE recordIdType, final int numProcessors,
			final int maxBlockSize) throws BlockingException {

		log.info("Number of chunks " + numChunks);
		getPropertyController().setJobProperty(batchJob, PN_CHUNK_FILE_COUNT,
				String.valueOf(numChunks));

		log.info("Number of regular chunks " + numRegularChunks);
		getPropertyController().setJobProperty(batchJob,
				PN_REGULAR_CHUNK_FILE_COUNT, String.valueOf(numChunks));

		int totalComparisons = 0;
		for (int currentChunk = 0; currentChunk < numChunks; currentChunk++) {
			int chunkComparisons = 0;
			// Tree indices are one-based
			for (int treeIndex = 1; treeIndex <= numProcessors; treeIndex++) {
				int treeComparisons = getComparisons(batchJob, currentChunk,
						treeIndex, recordIdType, numRegularChunks,
						numProcessors, maxBlockSize);
				chunkComparisons += treeComparisons;
			}
			String pv = Integer.toString(chunkComparisons);
			idxPropController.setIndexedPropertyValue(batchJob,
					PN_CHUNK_COMPARISONS, currentChunk, pv);
			totalComparisons += chunkComparisons;
		}
		String pv = Integer.toString(totalComparisons);
		this.getPropertyController().setJobProperty(batchJob,
				PN_TOTAL_CHUNK_COMPARISONS, pv);

	}

	protected int getComparisons(BatchJob job, final int currentChunk,
			final int treeIndex, final RECORD_ID_TYPE recordIdType,
			final int numRegularChunks, final int numProcessors,
			final int maxBlockSize) throws BlockingException {
		assert job != null;
		assert currentChunk >= 0;
		assert treeIndex > 0 && treeIndex <= numProcessors;
		assert recordIdType != null;

		@SuppressWarnings("rawtypes")
		IComparisonSetSource sets = OabaFileUtils.getComparisonSetSource(job,
				currentChunk, treeIndex, recordIdType, numRegularChunks,
				numProcessors, maxBlockSize);

		int setCount = 0;
		int retVal = 0;
		if (sets != null && sets.exists()) {
			try {
				sets.open();
				while (sets.hasNext()) {
					IComparisonSet set = (IComparisonSet) sets.next();
					assert set != null;
					++setCount;
					while (set.hasNextPair()) {
						++retVal;
					}
				}

			} catch (Exception x) {
				String msg0 = "Could not open or read comparison-set for "
						+ "[batchJob: %d, chunk: %d, index: %d, "
						+ "recordIdType %s]: %s";
				String msg = String.format(msg0, job.getId(), currentChunk,
						treeIndex, recordIdType.toString(), x.toString());
				log.severe(msg);
				throw new BlockingException(msg);

			} finally {
				try {
					sets.close();
				} catch (Exception x) {
					String msg0 = "Unable to close %s: %s";
					String msg =
						String.format(msg0, sets.toString(), x.toString());
					log.warning(msg);
				}
			}

		} else {
			String msg0 = "Could not get comparison-set for "
					+ "[batchJob: %d, chunk: %d, index: %d, recordIdType %s]";
			String msg = String.format(msg0, job.getId(), currentChunk,
					treeIndex, recordIdType.toString());
			log.severe(msg);
			throw new BlockingException(msg);
		}

		if (log.isLoggable(Level.FINE)) {
			String msg0;
			String msg;

			msg0 = "Set count for "
					+ "[batchJob: %d, chunk: %d, index: %d]: %d";
			msg = String.format(msg0, job.getId(), currentChunk, treeIndex,
					setCount);
			log.fine(msg);

			msg0 = "Comparison count for "
					+ "[batchJob: %d, chunk: %d, index: %d]: %d";
			msg = String.format(msg0, job.getId(), currentChunk, treeIndex,
					retVal);
			log.fine(msg);
		}

		return retVal;
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
	protected OabaEventBean getCompletionEvent() {
		return OabaEventBean.DONE_CREATE_CHUNK_DATA;
	}

	@Override
	protected MessageDrivenContext getMdcCtx() {
		return jmsCtx;
	}

	@Override
	protected UserTransaction getUserTx() {
		return getMdcCtx().getUserTransaction();
	}

	@Override
	protected void notifyProcessingCompleted(OabaJobMessage data) {
		MessageBeanUtils.sendStartData(data, getJmsContext(),
				matchSchedulerQueue, getLogger());
	}

	protected IndexedPropertyController getIndexedPropertyController() {
		return idxPropController;
	}

}
