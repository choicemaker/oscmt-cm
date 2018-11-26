/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.ejb;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.transaction.UserTransaction;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.EventPersistenceManager;
import com.choicemaker.cm.batch.api.OperationalPropertyController;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.base.MatchRecord2;
import com.choicemaker.cm.oaba.api.OabaJobManager;
import com.choicemaker.cm.oaba.api.OabaParametersController;
import com.choicemaker.cm.oaba.api.OabaSettingsController;
import com.choicemaker.cm.oaba.api.ServerConfigurationController;
import com.choicemaker.cm.oaba.core.IComparableSink;
import com.choicemaker.cm.oaba.core.IMatchRecord2Sink;
import com.choicemaker.cm.oaba.data.MatchRecord2Factory;
import com.choicemaker.cm.oaba.ejb.AbstractMatcherBmt;
import com.choicemaker.cm.oaba.ejb.OabaFileUtils;
import com.choicemaker.cm.oaba.ejb.data.MatchWriterMessage;
import com.choicemaker.cm.oaba.ejb.data.OabaJobMessage;
import com.choicemaker.cm.oaba.ejb.util.MessageBeanUtils;
import com.choicemaker.cm.oaba.impl.ComparableMRSink;
import com.choicemaker.cm.transitivity.api.TransitivityParametersController;

/**
 * This is the Matcher for the Transitivity Engine. It is called by
 * TransMatchSchedulerMDB.
 * 
 * @author pcheung
 *
 */
@SuppressWarnings({
		"rawtypes", "unchecked" })
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup",
				propertyValue = "java:/choicemaker/urm/jms/transMatcherQueue"),
		@ActivationConfigProperty(propertyName = "destinationType",
				propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode",
		propertyValue = "Dups-ok-acknowledge") })
//@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@TransactionManagement(value = TransactionManagementType.BEAN)
public class TransMatcherMDB extends AbstractMatcherBmt
		implements MessageListener {

	private static final long serialVersionUID = 271L;
	private static final Logger log =
		Logger.getLogger(TransMatcherMDB.class.getName());
	private static final Logger jmsTrace =
		Logger.getLogger("jmstrace." + TransMatcherMDB.class.getName());
	private static final String SOURCE = TransMatcherMDB.class.getSimpleName();

	// -- Injected instance data

	@EJB
	private OabaJobManager jobManager;

	@EJB
	private OabaSettingsController oabaSettingsController;

	@EJB
	private OabaParametersController paramsController;

	@EJB
	TransitivityParametersController transitivityParametersController;

	@EJB
	private EventPersistenceManager eventManager;

	@EJB
	private ServerConfigurationController serverController;

	@EJB
	private OperationalPropertyController propController;

	@Inject
	private JMSContext jmsContext;

	@Resource
	private MessageDrivenContext jmsCtx;

	@Resource
	private UserTransaction userTx;

	@Resource(lookup = "java:/choicemaker/urm/jms/transMatchSchedulerQueue")
	private Queue transMatchSchedulerQueue;

	// -- Call-back methods

	@Override
	protected OabaJobManager getOabaJobManager() {
		return jobManager;
	}

	@Override
	protected OabaParametersController getOabaParametersController() {
		return new CombinedParametersController(paramsController,
				transitivityParametersController);
	}

	@Override
	protected EventPersistenceManager getEventManager() {
		return eventManager;
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
		return propController;
	}

	@Override
	protected JMSContext getJMSContext() {
		return jmsContext;
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
	protected MessageDrivenContext getMdcCtx() {
		return jmsCtx;
	}

	@Override
	protected UserTransaction getUserTx() {
		return getMdcCtx().getUserTransaction();
	}

	@Override
	protected void writeMatches(OabaJobMessage data, List<MatchRecord2> matches)
			throws BlockingException {

		assert data != null;
		assert matches != null;

		if (!matches.isEmpty()) {
			// first figure out the correct file for this processor
			final long jobId = data.jobID;
			BatchJob batchJob = getOabaJobManager().findBatchJob(jobId);
			IMatchRecord2Sink mSink = OabaFileUtils
					.getMatchChunkFactory(batchJob).getSink(data.treeIndex);
			IComparableSink sink = new ComparableMRSink(mSink);

			// write matches to this file.
			sink.append();
			sink.writeComparables(matches.iterator());

			// write the separator
			MatchRecord2 mr = matches.get(0);
			mr = MatchRecord2Factory.getSeparator(mr.getRecordID1());
			sink.writeComparable(mr);

			sink.close();
		}
	}

	@Override
	protected void sendToScheduler(MatchWriterMessage data) {
		MessageBeanUtils.sendMatchWriterData(data, getJMSContext(),
				transMatchSchedulerQueue, log);
	}

}
