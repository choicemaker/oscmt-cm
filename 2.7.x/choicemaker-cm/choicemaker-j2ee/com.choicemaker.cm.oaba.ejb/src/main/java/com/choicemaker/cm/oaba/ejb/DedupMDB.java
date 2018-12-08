/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

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
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.oaba.core.IBlockSink;
import com.choicemaker.cm.oaba.core.IBlockSinkSourceFactory;
import com.choicemaker.cm.oaba.core.IBlockSource;
import com.choicemaker.cm.oaba.core.OabaEventBean;
import com.choicemaker.cm.oaba.ejb.data.OabaJobMessage;
import com.choicemaker.cm.oaba.ejb.util.MessageBeanUtils;
import com.choicemaker.cm.oaba.impl.BlockGroup;
import com.choicemaker.cm.oaba.services.BlockDedupService4;
import com.choicemaker.cm.oaba.services.OversizedDedupService;

/**
 * This bean handles the deduping of blocks and oversized blocks.
 *
 * @author pcheung
 *
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup",
				propertyValue = "java:/choicemaker/urm/jms/dedupQueue"),
		@ActivationConfigProperty(propertyName = "destinationType",
				propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode",
				propertyValue = "Dups-ok-acknowledge") })
// @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@TransactionManagement(value = TransactionManagementType.BEAN)
public class DedupMDB extends AbstractOabaBmtMDB {

	private static final long serialVersionUID = 271L;

	private static final Logger log =
		Logger.getLogger(DedupMDB.class.getName());

	private static final Logger jmsTrace =
		Logger.getLogger("jmstrace." + DedupMDB.class.getName());

	// private static final String SOURCE = DedupMDB.class.getSimpleName();

	@Resource
	private MessageDrivenContext jmsCtx;

	@Resource(lookup = "java:/choicemaker/urm/jms/chunkQueue")
	private Queue chunkQueue;

	@Override
	protected void processOabaMessage(OabaJobMessage data, BatchJob batchJob,
			OabaParameters oabaParams, OabaSettings oabaSettings,
			ProcessingEventLog processingLog, ServerConfiguration serverConfig,
			ImmutableProbabilityModel model) throws BlockingException {
		// final String METHOD = "processOabaMessage";

		// Handle regular blocking sets
		final int maxBlock = oabaSettings.getMaxBlockSize();
		final int interval = oabaSettings.getInterval();
		final BlockGroup bGroup = new BlockGroup(
				OabaFileUtils.getBlockGroupFactory(batchJob), maxBlock);
		final BatchJobControl control =
			new BatchJobControl(this.getJobController(), batchJob);
		BlockDedupService4 dedupService = new BlockDedupService4(bGroup,
				OabaFileUtils.getBigBlocksSinkSourceFactory(batchJob),
				OabaFileUtils.getTempBlocksSinkSourceFactory(batchJob),
				OabaFileUtils.getSuffixTreeSink(batchJob), maxBlock,
				processingLog, control, interval);
		dedupService.runService();
		log.info("Done block dedup " + dedupService.getTimeElapsed());
		log.info("Blocks In " + dedupService.getNumBlocksIn());
		log.info("Blocks Out " + dedupService.getNumBlocksOut());
		log.info("Tree Out " + dedupService.getNumTreesOut());

		// Handle oversized blocking sets
		final IBlockSink osSpecial =
			OabaFileUtils.getOversizedFactory(batchJob).getNextSink();
		final IBlockSinkSourceFactory osFactory =
			OabaFileUtils.getOversizedFactory(batchJob);
		final IBlockSource osSource = osFactory.getSource(osSpecial);
		final IBlockSink osDedup = osFactory.getNextSink();

		OversizedDedupService osDedupService =
			new OversizedDedupService(osSource, osDedup,
					OabaFileUtils.getOversizedTempFactory(batchJob),
					processingLog, control);
		osDedupService.runService();
		log.info("Done oversized dedup " + osDedupService.getTimeElapsed());
		log.info("Num OS Before " + osDedupService.getNumBlocksIn());
		log.info("Num OS After Exact " + osDedupService.getNumAfterExact());
		log.info("Num OS Done " + osDedupService.getNumBlocksOut());
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
		return OabaEventBean.DONE_DEDUP_OVERSIZED;
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
		MessageBeanUtils.sendStartData(data, getJmsContext(), chunkQueue,
				getLogger());
	}

}
