/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.ejb;

import java.util.Properties;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.choicemaker.cm.analyzer.filter.DefaultPairFilter;
import com.choicemaker.cm.analyzer.filter.Filter;
import com.choicemaker.cm.analyzer.sampler.DefaultPairSampler;
import com.choicemaker.cm.analyzer.sampler.PairSampler;
import com.choicemaker.cm.args.PersistableRecordSource;
import com.choicemaker.cm.core.IControl;
import com.choicemaker.cm.core.ISerializableRecordSource;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.oaba.api.RecordSourceController;
import com.choicemaker.cm.oaba.filter.DefaultMatchRecord2Filter;
import com.choicemaker.cm.oaba.filter.IMatchRecord2Filter;
import com.choicemaker.cm.oaba.result.MRPSCreator;

/**
 * A long-running backend process that computes Marked Record Pairs.
 * @author rphall
 */
@SuppressWarnings({"rawtypes"})
public class MrpsBackend implements MessageDrivenBean, MessageListener {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(MrpsBackend.class.getName());
	private static final Logger jmsTrace = Logger.getLogger("jmstrace." + MrpsBackend.class.getName());

	@PersistenceContext (unitName = "oaba")
	private EntityManager em;

	@EJB
	private RecordSourceController rsController;

	private transient MessageDrivenContext mdc = null;

	/**
	 * Constructor, which is public and takes no arguments.
	 */
	public MrpsBackend() {
		log.fine("constractor");
	}

	public void setMessageDrivenContext(MessageDrivenContext mdc) {
		log.fine("setMessageDrivenContext()");
		this.mdc = mdc;
	}

	public void ejbCreate() {
		log.fine("starting ejbCreate...");
		log.fine("...finished ejbCreate");
	}


	public void onMessage(Message inMessage) {
		jmsTrace.info("Entering onMessage for " + this.getClass().getName());

		ObjectMessage msg = null;
		Object msgPayload = null;
		IMrpsRequest request = null;
		IControl control = null; 

		log.fine("starting onMessage...");
		try {

			if (inMessage instanceof ObjectMessage) {

				msg = (ObjectMessage) inMessage;
				msgPayload = msg.getObject();
				if (msgPayload instanceof IMrpsRequest) {

					request = (IMrpsRequest) msg.getObject();
					log.fine("received: " + request.getMrpsConvJobId());

					long jobId = request.getMrpsConvJobId().longValue();
					CmsJob job = Single.getInst().findCmsJobById(jobId);
					log.fine("starting backend process, id == " + jobId);
					
					Properties p = request.getProperties();
					int batchSize = getBatchSize(p);
					//control = new MrpsController(job);
					control = MRPSCreator.NO_CONTROL;
					IMatchRecord2Filter preFilter = getPreFilter(p);
					Filter postFilter = getPostFilter(p);
					ImmutableProbabilityModel model = request.getStagingModel(em);
					PairSampler sampler = getPairSampler(model,p);

					PersistableRecordSource prsStaging =
						request.getRsStage(em, rsController);
					ISerializableRecordSource staging =
						rsController.getRecordSource(prsStaging.getId(),
								prsStaging.getType());
					PersistableRecordSource prsMaster =
						request.getRsStage(em, rsController);
					ISerializableRecordSource master =
						rsController.getRecordSource(prsMaster.getId(),
								prsMaster.getType());

					MRPSCreator mrpsCreator =
						new MRPSCreator(
							request.getMatchPairs(em),
							staging,
							master,
							request.getMarkedRecordPairSink(em),
							batchSize,
							control,
							preFilter,
							postFilter,
							sampler);
					mrpsCreator.createMRPS();
					job.markAsCompleted();
				} else {
					log.warning(
						"wrong object type: '"
							+ msgPayload.getClass().getName());
				}
			} else {
				log.warning(
					"wrong message type: " + inMessage.getClass().getName());
			}

		} catch (JMSException e) {
			log.severe(e.toString());
			mdc.setRollbackOnly();
		} catch (Exception e) {
			log.severe(e.toString());
			e.printStackTrace();
		} finally {
			if (control != null) {
				//control.finalize();
				//TODO - add finilize to the control interface
			}
			control = null;
		}

		log.fine("...finished onMessage");
		jmsTrace.info("Exiting onMessage for " + this.getClass().getName());
		return;
	} // onMessage(Message)

	public void ejbRemove() {
		log.fine("ejbRemove()");
	}

	static int getBatchSize(Properties p) {
		String strValue = p.getProperty(IMrpsRequestConfiguration.PN_BATCH_SIZE);
		int retVal = Integer.parseInt(strValue);
		return retVal;		
	}
	
	static IMatchRecord2Filter getPreFilter(Properties p) {
		IMatchRecord2Filter retVal = MRPSCreator.NO_PRE_FILTER;
		String strValue = p.getProperty(IMrpsRequestConfiguration.PN_USE_DEFAULT_PREFILTER);
		boolean usePreFilter = Boolean.valueOf(strValue).booleanValue();
		if (usePreFilter) {
			strValue = p.getProperty(IMrpsRequestConfiguration.PN_DEFAULT_PREFILTER_FROM_PERCENTAGE);
			float from = Float.parseFloat(strValue);
			strValue = p.getProperty(IMrpsRequestConfiguration.PN_DEFAULT_PREFILTER_TO_PERCENTAGE);
			float to = Float.parseFloat(strValue);
			retVal = new DefaultMatchRecord2Filter(from,to);
		}
		return retVal;
	}
	
	static Filter getPostFilter(Properties p) {
		Filter retVal = MRPSCreator.NO_POST_FILTER;
		String strValue = p.getProperty(IMrpsRequestConfiguration.PN_USE_DEFAULT_POSTFILTER);
		boolean usePostFilter = Boolean.valueOf(strValue).booleanValue();
		if (usePostFilter) {
			strValue = p.getProperty(IMrpsRequestConfiguration.PN_DEFAULT_POSTFILTER_FROM_PERCENTAGE);
			float from = Float.parseFloat(strValue);
			strValue = p.getProperty(IMrpsRequestConfiguration.PN_DEFAULT_POSTFILTER_TO_PERCENTAGE);
			float to = Float.parseFloat(strValue);
			retVal = new DefaultPairFilter(from,to);
		}
		return retVal;
	}
	
	static PairSampler getPairSampler(ImmutableProbabilityModel model, Properties p) {
		PairSampler retVal = null;
		String strValue = p.getProperty(IMrpsRequestConfiguration.PN_USE_DEFAULT_PAIR_SAMPLER);
		boolean usePairSampler = Boolean.valueOf(strValue).booleanValue();
		if (usePairSampler) {
			strValue = p.getProperty(IMrpsRequestConfiguration.PN_DEFAULT_PAIR_SAMPLER_SIZE);
			int samplerSize = Integer.parseInt(strValue);
			retVal = new DefaultPairSampler(model,samplerSize);
		}
		return retVal;
	}

/*
	static class MrpsController implements IControl, MessageListener {

		private transient String lastStatus = null;
		private transient MrpsJob job = null;
		private transient TopicConnection statusConnection = null;
		private transient TopicSession statusSession = null;
		private transient TopicSubscriber statusSubscriber = null;

		private void invariant() {
			if (job == null) {
				throw new IllegalArgumentException("null job");
			}
			if (lastStatus == null) {
				throw new IllegalArgumentException("null lastStatus");
			}
			if (statusConnection == null) {
				throw new IllegalArgumentException("null statusConnection");
			}
			if (statusSession == null) {
				throw new IllegalArgumentException("null statusSession");
			}
			if (statusSubscriber == null) {
				throw new IllegalArgumentException("null statusSubscriber");
			}
		}

		MrpsController(MrpsJob job)
			throws NamingException, JMSException, RemoteException {
			this.job = job;
			this.lastStatus = job.getStatus();
			Topic statusTopic = ServerConfig.getInst().getStatusTopic();
			this.statusConnection =
				ServerConfig
					.getInst()
					.getTopicConnectionFactory()
					.createTopicConnection();
			this.statusConnection.start();
			this.statusSession =
				this.statusConnection.createTopicSession(
					false,
					Session.AUTO_ACKNOWLEDGE);
			this.statusSubscriber =
				this.statusSession.createSubscriber(statusTopic);
			this.statusSubscriber.setMessageListener(this);
			invariant();
		}

		public void onMessage(Message statusMessage) {
			try {
				if (this.job == null) {
					throw new IllegalStateException("null job in MrpsController.onMessage");
				}
				TextMessage msg = null;
				if (statusMessage instanceof TextMessage) {
					msg = (TextMessage) statusMessage;
					String msgText = msg.getText();
					log.fine(
						"received status change notification :" + msgText);
					this.lastStatus = msgText;
				} else {
					log.fine("received unexpected notification ...");
				}
			} catch (Exception x) {
				log.severe(
					"Error in MrpsController.onMessage: " + x.toString());
			}
			throw new RuntimeException("not yet implemented");
		}

		public void finalize() {
			if (this.statusSession != null) {
				try {
					this.statusSession.close();
				} catch (JMSException x) {
					log.warning("can't close session: " + x.toString());
				}
				this.statusSession = null;
			}
			if (this.statusConnection != null) {
				try {
					this.statusConnection.stop();
					this.statusConnection.close();
				} catch (JMSException x) {
					log.warning("can't close connection: " + x.toString());
				}
				this.statusConnection = null;
			}
			if (this.statusSubscriber != null) {
				try {
					this.statusSubscriber.setMessageListener(null);
					this.statusSubscriber.close();
				} catch (JMSException x) {
					log.warning("can't close subscriber: " + x.toString());
				}
				this.statusSubscriber = null;
			}
			this.job = null;
			this.lastStatus = null;
		}

		public boolean shouldStop() {
			invariant();
			return MrpsBackend.isAbortRequested(this.lastStatus);
		}

	} // MrpsBackend.MrpsController
*/
} // MrpsBackend

