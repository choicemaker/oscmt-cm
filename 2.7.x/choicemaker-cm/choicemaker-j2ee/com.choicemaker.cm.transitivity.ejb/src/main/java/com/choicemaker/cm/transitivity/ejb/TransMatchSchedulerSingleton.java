/*******************************************************************************
 * Copyright (c) 2015, 2020 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.ejb;

import static com.choicemaker.cm.args.OperationalPropertyNames.PN_CHUNK_FILE_COUNT;
import static com.choicemaker.cm.args.OperationalPropertyNames.PN_REGULAR_CHUNK_FILE_COUNT;

import java.util.Date;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Queue;

import com.choicemaker.cm.args.ProcessingEvent;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.EventPersistenceManager;
import com.choicemaker.cm.batch.api.OperationalPropertyController;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.oaba.api.OabaJobManager;
import com.choicemaker.cm.oaba.api.OabaParametersController;
import com.choicemaker.cm.oaba.api.OabaSettingsController;
import com.choicemaker.cm.oaba.api.ServerConfigurationController;
import com.choicemaker.cm.oaba.core.IChunkDataSinkSourceFactory;
import com.choicemaker.cm.oaba.core.IComparisonArraySource;
import com.choicemaker.cm.oaba.ejb.AbstractSchedulerSingleton;
import com.choicemaker.cm.oaba.ejb.OabaFileUtils;
import com.choicemaker.cm.oaba.ejb.data.OabaJobMessage;
import com.choicemaker.cm.oaba.ejb.util.MessageBeanUtils;
import com.choicemaker.cm.oaba.impl.ComparisonArrayGroupSinkSourceFactory;
import com.choicemaker.cm.oaba.services.ChunkService3;
import com.choicemaker.cm.transitivity.api.TransitivityParametersController;

/**
 * This is the match scheduler for the Transitivity Engine.
 * 
 * @author pcheung
 *
 */
@Singleton
public class TransMatchSchedulerSingleton extends AbstractSchedulerSingleton {

	private static final long serialVersionUID = 1L;
	private static final Logger log =
		Logger.getLogger(TransMatchSchedulerSingleton.class.getName());
	private static final Logger jmsTrace = Logger.getLogger(
			"jmstrace." + TransMatchSchedulerSingleton.class.getName());

	// -- Injected data

	@EJB
	private OabaJobManager jobManager;

	@EJB
	private OabaSettingsController oabaSettingsController;

	@EJB
	private OabaParametersController paramsController;

	@EJB
	TransitivityParametersController transitivityParametersController;

	@EJB
	private ServerConfigurationController serverController;

	@EJB
	private OperationalPropertyController propertyController;

	@EJB(beanName = "TransitivityEventManager")
	private EventPersistenceManager eventManager;

	@Resource(lookup = "java:/choicemaker/urm/jms/matcherQueue")
	private Queue matcherQueue;

	@Resource(lookup = "java:/choicemaker/urm/jms/singleMatchQueue")
	private Queue singleMatchQueue;

	@Inject
	private JMSContext jmsContext;

	@Resource(lookup = "java:/choicemaker/urm/jms/transMatchDedupQueue")
	private Queue transMatchDedupQueue;

	@Resource(lookup = "java:/choicemaker/urm/jms/transMatcherQueue")
	private Queue transMatcherQueue;

	@Resource(lookup = "java:/choicemaker/urm/jms/transSingleMatchQueue")
	private Queue transSingleMatchQueue;

	// -- Accessors

	@Override
	protected JMSContext getJmsContext() {
		return jmsContext;
	}

	protected OabaParametersController getParametersControllerInternal() {
		return paramsController;
	}

	protected TransitivityParametersController getTransitivityParametersController() {
		return transitivityParametersController;
	}

	// -- Callbacks

	@Override
	protected OabaJobManager getJobController() {
		return jobManager;
	}

	@Override
	protected OabaParametersController getOabaParametersController() {
		return new CombinedParametersController(paramsController,
				transitivityParametersController);
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
	protected EventPersistenceManager getEventManager() {
		return eventManager;
	}

	@Override
	protected Logger getLogger() {
		return log;
	}

	@Override
	protected Logger getJMSTrace() {
		return jmsTrace;
	}

	@Override
	protected void cleanUp(BatchJob batchJob, OabaJobMessage sd)
			throws BlockingException {
		if (ChunkService3.isKeepFilesRequested()) {
			log.info("Intermediate chunk files retained");
			return;
		}

		log.info("Cleanup: removing intermediate chunck files");

		final long jobId = batchJob.getId();
		TransitivityParameters params = getTransitivityParametersController()
				.findTransitivityParametersByBatchJobId(jobId);
		ServerConfiguration serverConfig =
			getServerController().findServerConfigurationByJobId(jobId);
		final String modelConfigId = params.getModelConfigurationName();
		ImmutableProbabilityModel model =
			PMManager.getModelInstance(modelConfigId);
		if (model == null) {
			String s = "No modelId corresponding to '" + modelConfigId + "'";
			log.severe(s);
			throw new IllegalArgumentException(s);
		}

		int numProcessors = serverConfig.getMaxChoiceMakerThreads();

		// remove the data
		final String _numChunks = getPropertyController()
				.getOperationalPropertyValue(batchJob, PN_CHUNK_FILE_COUNT);
		final int numChunks = Integer.valueOf(_numChunks);

		final String _numRegularChunks = getPropertyController()
				.getOperationalPropertyValue(batchJob, PN_REGULAR_CHUNK_FILE_COUNT);
		final int numRegularChunks = Integer.valueOf(_numRegularChunks);

		IChunkDataSinkSourceFactory stageFactory =
			OabaFileUtils.getStageDataFactory(batchJob, model);
		IChunkDataSinkSourceFactory masterFactory =
			OabaFileUtils.getMasterDataFactory(batchJob, model);
		stageFactory.removeAllSinks(numChunks);
		masterFactory.removeAllSinks(numChunks);

		final int numOS = numChunks - numRegularChunks;
		assert numOS > 0;

		// remove the oversized array files
		ComparisonArrayGroupSinkSourceFactory factoryOS = OabaFileUtils
				.getComparisonArrayGroupFactoryOS(batchJob, numProcessors);
		for (int i = 0; i < numOS; i++) {
			for (int j = 1; j <= numProcessors; j++) {
				@SuppressWarnings("rawtypes")
				IComparisonArraySource sourceOS = factoryOS.getSource(i, j);
				sourceOS.delete();
			}
		}

	}

	@Override
	protected void sendToMatchDebup(BatchJob job, OabaJobMessage sd) {
		MessageBeanUtils.sendStartData(sd, getJmsContext(),
				transMatchDedupQueue, log);
	}

	@Override
	protected void sendToMatcher(OabaJobMessage sd) {
		MessageBeanUtils.sendStartData(sd, getJmsContext(), transMatcherQueue,
				log);
	}

	@Override
	protected void sendToUpdateStatus(BatchJob job, ProcessingEvent event,
			Date timestamp, String info) {
		getEventManager().updateStatusWithNotification(job, event, timestamp,
				info);
	}

	@Override
	protected void sendToSingleRecordMatching(BatchJob job, OabaJobMessage sd) {
		MessageBeanUtils.sendStartData(sd, getJmsContext(),
				transSingleMatchQueue, getLogger());
	}

}
