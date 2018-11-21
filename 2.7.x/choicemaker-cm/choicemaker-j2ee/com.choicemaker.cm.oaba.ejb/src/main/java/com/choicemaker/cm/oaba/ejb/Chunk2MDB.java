/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

import static com.choicemaker.cm.args.OperationalPropertyNames.PN_CHUNK_FILE_COUNT;
import static com.choicemaker.cm.args.OperationalPropertyNames.PN_RECORD_ID_TYPE;
import static com.choicemaker.cm.args.OperationalPropertyNames.PN_REGULAR_CHUNK_FILE_COUNT;

import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
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
import com.choicemaker.cm.batch.api.ProcessingEventLog;
import com.choicemaker.cm.batch.ejb.BatchJobControl;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.ISerializableRecordSource;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.oaba.core.IBlockSinkSourceFactory;
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
public class Chunk2MDB extends AbstractOabaMDB2 {

	private static final long serialVersionUID = 271L;

	private static final Logger log =
		Logger.getLogger(Chunk2MDB.class.getName());

	private static final Logger jmsTrace =
		Logger.getLogger("jmstrace." + Chunk2MDB.class.getName());

	private static final String SOURCE = StartOabaMDB.class.getSimpleName();

	@Resource
	private MessageDrivenContext jmsCtx;

	@Resource
	private UserTransaction userTx;

	@Resource(lookup = "java:/choicemaker/urm/jms/matchSchedulerQueue")
	private Queue matchSchedulerQueue;

	@Override
	protected void processOabaMessage(OabaJobMessage data, BatchJob batchJob,
			OabaParameters oabaParams, OabaSettings oabaSettings,
			ProcessingEventLog processingLog, ServerConfiguration serverConfig,
			ImmutableProbabilityModel model) throws BlockingException {
		final String METHOD = "processOabaMessage";

		final int maxChunk = oabaSettings.getMaxChunkSize();
		final int numProcessors = serverConfig.getMaxChoiceMakerThreads();
		final int maxChunkFiles = serverConfig.getMaxOabaChunkFileCount();
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
				processingLog, control, mode);
		log.info("Chunk service: " + chunkService);
		chunkService.runService();
		log.info("Done creating chunks " + chunkService.getTimeElapsed());

		// transitivity needs the translator
		// translator.cleanUp();

		final int numChunks = chunkService.getNumChunks();
		log.info("Number of chunks " + numChunks);
		getPropertyController().setJobProperty(batchJob, PN_CHUNK_FILE_COUNT,
				String.valueOf(numChunks));

		final int numRegularChunks = chunkService.getNumRegularChunks();
		log.info("Number of regular chunks " + numRegularChunks);
		getPropertyController().setJobProperty(batchJob,
				PN_REGULAR_CHUNK_FILE_COUNT, String.valueOf(numChunks));
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
	protected MessageDrivenContext getJmsCtx() {
		return jmsCtx;
	}

	@Override
	protected UserTransaction getUserTx () {
		return getJmsCtx().getUserTransaction();
	}

	@Override
	protected void notifyProcessingCompleted(OabaJobMessage data) {
		MessageBeanUtils.sendStartData(data, getJmsContext(),
				matchSchedulerQueue, getLogger());
	}

}
