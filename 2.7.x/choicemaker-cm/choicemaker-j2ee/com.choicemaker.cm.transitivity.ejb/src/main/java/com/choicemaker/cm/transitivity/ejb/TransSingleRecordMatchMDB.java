/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.ejb;

import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Queue;

import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.ProcessingEventLog;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.ChoiceMakerExtensionPoint;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.oaba.api.AbaStatisticsController;
import com.choicemaker.cm.oaba.api.OabaParametersController;
import com.choicemaker.cm.oaba.ejb.SingleRecordProcessing;
import com.choicemaker.cm.oaba.ejb.data.OabaJobMessage;
import com.choicemaker.cm.oaba.ejb.util.MessageBeanUtils;
import com.choicemaker.cm.transitivity.core.TransitivityEventBean;

// import com.choicemaker.cm.core.base.Accessor;

/**
 * This message bean performs single record matching on the staging record
 * source.
 *
 * @author pcheung
 *
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup",
				propertyValue = "java:/choicemaker/urm/jms/transSingleMatchQueue"),
		@ActivationConfigProperty(propertyName = "destinationType",
				propertyValue = "javax.jms.Queue") })
//@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class TransSingleRecordMatchMDB extends AbstractTransitivityMDB {

	private static final long serialVersionUID = 271L;

	private static final Logger log =
		Logger.getLogger(TransSingleRecordMatchMDB.class.getName());

	private static final Logger jmsTrace = Logger
			.getLogger("jmstrace." + TransSingleRecordMatchMDB.class.getName());

	public static final String DATABASE_ACCESSOR =
		ChoiceMakerExtensionPoint.CM_IO_BLOCKING_AUTOMATED_BASE_DATABASEACCESSOR;

	public static final String MATCH_CANDIDATE =
		ChoiceMakerExtensionPoint.CM_CORE_MATCHCANDIDATE;

	@EJB
	private AbaStatisticsController statsController;

	@EJB
	private OabaParametersController paramsController;

	@Resource(lookup = "java:/choicemaker/urm/jms/transMatchDedupQueue")
	private Queue transMatchDedupQueue;

	protected final AbaStatisticsController getAbaStatisticsController() {
		return statsController;
	}

	protected OabaParametersController getOabaParametersController() {
		return new CombinedParametersController(paramsController,
				this.getParametersController());
	}

	protected Queue getTransMatchDedupQueue() {
		return transMatchDedupQueue;
	}

	@Override
	protected void processOabaMessage(OabaJobMessage data, BatchJob batchJob,
			TransitivityParameters params, OabaSettings oabaSettings,
			ProcessingEventLog processingLog, ServerConfiguration serverConfig,
			ImmutableProbabilityModel model) throws BlockingException {

		SingleRecordProcessing srp = new SingleRecordProcessing(log, jmsTrace,
				this.getOabaParametersController(),
				this.getRecordSourceController(),
				this.getSqlRecordSourceController(),
				this.getPropertyController(),
				this.getAbaStatisticsController());
		srp.processOabaMessage(data, batchJob, params, oabaSettings,
				processingLog, serverConfig, model);
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
		MessageBeanUtils.sendStartData(data, getJmsContext(),
				getTransMatchDedupQueue(), getLogger());
	}

	@Override
	protected TransitivityEventBean getCompletionEvent() {
		return TransitivityEventBean.DONE_TRANSITIVITY_PAIRWISE;
	}

}
