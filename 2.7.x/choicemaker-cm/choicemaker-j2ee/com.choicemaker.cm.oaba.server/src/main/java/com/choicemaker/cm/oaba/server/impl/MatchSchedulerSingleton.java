/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.server.impl;

import static com.choicemaker.cm.args.OperationalPropertyNames.PN_CHUNK_FILE_COUNT;
import static com.choicemaker.cm.args.OperationalPropertyNames.PN_RECORD_ID_TYPE;
import static com.choicemaker.cm.args.OperationalPropertyNames.PN_REGULAR_CHUNK_FILE_COUNT;

import java.util.Date;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Queue;

import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.ProcessingEvent;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.OperationalPropertyController;
import com.choicemaker.cm.batch.api.ProcessingController;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.oaba.core.IChunkDataSinkSourceFactory;
import com.choicemaker.cm.oaba.core.IComparisonArraySource;
import com.choicemaker.cm.oaba.core.IComparisonTreeSource;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;
import com.choicemaker.cm.oaba.impl.ComparisonArrayGroupSinkSourceFactory;
import com.choicemaker.cm.oaba.impl.ComparisonTreeGroupSinkSourceFactory;
import com.choicemaker.cm.oaba.server.data.OabaJobMessage;
import com.choicemaker.cm.oaba.server.ejb.OabaJobController;
import com.choicemaker.cm.oaba.server.ejb.OabaParametersController;
import com.choicemaker.cm.oaba.server.ejb.OabaSettingsController;
import com.choicemaker.cm.oaba.server.ejb.ServerConfigurationController;
import com.choicemaker.cm.oaba.server.util.MessageBeanUtils;
import com.choicemaker.cm.oaba.services.ChunkService3;

/**
 * This bean delegates the different chunks to different matcher message beans.
 * It listens for done messages from the matchers bean and when every chunk is
 * done, it calls the MatchDedup bean.
 * 
 * This version reads in one chunk at a time and splits the trees for processing
 * by different MatcherMDB beans.
 * 
 * @author pcheung
 *
 */
@Singleton
public class MatchSchedulerSingleton extends AbstractSchedulerSingleton {

	private static final long serialVersionUID = 271L;
	private static final Logger log = Logger
			.getLogger(MatchSchedulerSingleton.class.getName());
	private static final Logger jmsTrace = Logger.getLogger("jmstrace."
			+ MatchSchedulerSingleton.class.getName());

	// -- Injected data

	@EJB
	private OabaJobController jobController;

	@EJB
	private OabaSettingsController oabaSettingsController;

	@EJB
	private OabaParametersController paramsController;

	@EJB
	private ServerConfigurationController serverController;

	@EJB
	private OperationalPropertyController propertyController;

	@EJB
	private ProcessingController processingController;

	@Resource(lookup = "java:/choicemaker/urm/jms/matchDedupQueue")
	private Queue matchDedupQueue;

	@Resource(lookup = "java:/choicemaker/urm/jms/matcherQueue")
	private Queue matcherQueue;

	@Resource(lookup = "java:/choicemaker/urm/jms/singleMatchQueue")
	private Queue singleMatchQueue;

	@Inject
	private JMSContext jmsContext;

	@Override
	protected JMSContext getJmsContext() {
		return jmsContext;
	}

	// -- Callbacks

	@Override
	protected OabaJobController getJobController() {
		return jobController;
	}

	@Override
	protected OabaParametersController getOabaParametersController() {
		return paramsController;
	}

	@Override
	protected ServerConfigurationController getServerController() {
		return serverController;
	}

	@Override
	protected OabaSettingsController getSettingsController() {
		return oabaSettingsController;
	}

	@Override
	protected OperationalPropertyController getPropertyController() {
		return propertyController;
	}

	@Override
	protected ProcessingController getProcessingController() {
		return processingController;
	}

	@Override
	protected Logger getLogger() {
		return log;
	}

	@Override
	protected Logger getJMSTrace() {
		return jmsTrace;
	}

	/** Remove up the chunk files */
	@Override
	protected void cleanUp(BatchJob batchJob, OabaJobMessage sd)
			throws BlockingException {
		if (ChunkService3.isKeepFilesRequested()) {
			log.info("Intermediate chunk files retained");
			return;
		}

		log.info("Cleanup: removing intermediate chunck files");

		final long jobId = batchJob.getId();
		OabaParameters params =
			getOabaParametersController().findOabaParametersByBatchJobId(jobId);
		ServerConfiguration serverConfig =
			getServerController().findServerConfigurationByJobId(jobId);
		final String modelConfigId = params.getModelConfigurationName();
		ImmutableProbabilityModel model = PMManager.getModelInstance(modelConfigId);
		if (model == null) {
			String s = "No modelId corresponding to '" + modelConfigId + "'";
			log.severe(s);
			throw new IllegalArgumentException(s);
		}

		int numProcessors = serverConfig.getMaxChoiceMakerThreads();

		// remove the data
		final String _numChunks =
			getPropertyController()
					.getJobProperty(batchJob, PN_CHUNK_FILE_COUNT);
		final int numChunks = Integer.valueOf(_numChunks);

		final String _numRegularChunks =
			getPropertyController().getJobProperty(batchJob,
					PN_REGULAR_CHUNK_FILE_COUNT);
		final int numRegularChunks = Integer.valueOf(_numRegularChunks);

		IChunkDataSinkSourceFactory stageFactory =
			OabaFileUtils.getStageDataFactory(batchJob, model);
		IChunkDataSinkSourceFactory masterFactory =
			OabaFileUtils.getMasterDataFactory(batchJob, model);
		stageFactory.removeAllSinks(numChunks);
		masterFactory.removeAllSinks(numChunks);

		// remove the trees
		final String _recordIdType =
			getPropertyController().getJobProperty(batchJob, PN_RECORD_ID_TYPE);
		final RECORD_ID_TYPE recordIdType =
			RECORD_ID_TYPE.valueOf(_recordIdType);
		ComparisonTreeGroupSinkSourceFactory factory =
			OabaFileUtils.getComparisonTreeGroupFactory(batchJob, recordIdType,
					numProcessors);
		for (int i = 0; i < numRegularChunks; i++) {
			for (int j = 1; j <= numProcessors; j++) {
				@SuppressWarnings("rawtypes")
				IComparisonTreeSource source = factory.getSource(i, j);
				source.delete();
			}
		}

		final int numOS = numChunks - numRegularChunks;

		// remove the oversized array files
		ComparisonArrayGroupSinkSourceFactory factoryOS =
			OabaFileUtils.getComparisonArrayGroupFactoryOS(batchJob,
					numProcessors);
		for (int i = 0; i < numOS; i++) {
			for (int j = 1; j <= numProcessors; j++) {
				@SuppressWarnings("rawtypes")
				IComparisonArraySource sourceOS = factoryOS.getSource(i, j);
				sourceOS.delete();
			}
		}
	}

	@Override
	protected void sendToMatcher(OabaJobMessage sd) {
		MessageBeanUtils.sendStartData(sd, getJmsContext(), matcherQueue, log);
	}

	@Override
	protected void sendToUpdateStatus(BatchJob job, ProcessingEvent event,
			Date timestamp, String info) {
		getProcessingController().updateStatusWithNotification(job, event,
				timestamp, info);
	}

	@Override
	protected void sendToMatchDebup(BatchJob job, OabaJobMessage sd) {
		MessageBeanUtils.sendStartData(sd, getJmsContext(), matchDedupQueue, log);
	}

	@Override
	protected void sendToSingleRecordMatching(BatchJob job, OabaJobMessage sd) {
		MessageBeanUtils.sendStartData(sd, getJmsContext(), singleMatchQueue,
				getLogger());
	}

}
