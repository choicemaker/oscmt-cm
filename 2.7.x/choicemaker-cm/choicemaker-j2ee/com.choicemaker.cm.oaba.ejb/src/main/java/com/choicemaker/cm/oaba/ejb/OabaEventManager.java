/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

import static com.choicemaker.cm.oaba.ejb.OabaProcessingEventJPA.PN_OABAPROCESSING_DELETE_BY_JOBID_JOBID;
import static com.choicemaker.cm.oaba.ejb.OabaProcessingEventJPA.PN_OABAPROCESSING_FIND_BY_JOBID_JOBID;
import static com.choicemaker.cm.oaba.ejb.OabaProcessingEventJPA.QN_OABAPROCESSING_DELETE_BY_JOBID;
import static com.choicemaker.cm.oaba.ejb.OabaProcessingEventJPA.QN_OABAPROCESSING_FIND_ALL;
import static com.choicemaker.cm.oaba.ejb.OabaProcessingEventJPA.QN_OABAPROCESSING_FIND_BY_JOBID;
import static javax.ejb.TransactionAttributeType.REQUIRED;
import static javax.ejb.TransactionAttributeType.SUPPORTS;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.jms.DeliveryMode;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.choicemaker.cm.args.ProcessingEvent;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchProcessingEvent;
import com.choicemaker.cm.batch.api.EventPersistenceManager;
import com.choicemaker.cm.batch.api.ProcessingEventLog;
import com.choicemaker.cm.batch.ejb.BatchProcessingEventEntity;
import com.choicemaker.cm.oaba.ejb.data.OabaNotification;
import com.choicemaker.cm.oaba.ejb.util.MessageBeanUtils;

/**
 * This stateless EJB provides OABA, job-specific processing logs and
 * stand-alone methods for creating and finding log entries.
 *
 * @author pcheung
 * @author rphall (migration to EJB3)
 */
@Stateless
@TransactionAttribute(REQUIRED)
public class OabaEventManager implements EventPersistenceManager {

	private static final Logger logger =
		Logger.getLogger(OabaEventManager.class.getName());

	// Don't use this directly; use isOrderByDebuggingRequested() instead
	static Boolean _isOrderByDebuggingRequested = null;

	static List<BatchProcessingEvent> findProcessingLogEntriesByJobId(
			EntityManager em, long id) {
		Query query = em.createNamedQuery(QN_OABAPROCESSING_FIND_BY_JOBID);
		query.setParameter(PN_OABAPROCESSING_FIND_BY_JOBID_JOBID, id);
		@SuppressWarnings("unchecked")
		List<BatchProcessingEvent> entries = query.getResultList();
		if (entries == null) {
			entries = Collections.emptyList();
		}
		return entries;
	}

	static List<BatchProcessingEvent> findAllOabaProcessingEvents(
			EntityManager em) {
		Query query = em.createNamedQuery(QN_OABAPROCESSING_FIND_ALL);
		@SuppressWarnings("unchecked")
		List<BatchProcessingEvent> entries = query.getResultList();
		if (entries == null) {
			entries = Collections.emptyList();
		}
		return entries;
	}

	static int deleteProcessingLogEntriesByJobId(EntityManager em, long id) {
		Query query = em.createNamedQuery(QN_OABAPROCESSING_DELETE_BY_JOBID);
		query.setParameter(PN_OABAPROCESSING_DELETE_BY_JOBID_JOBID, id);
		int deletedCount = query.executeUpdate();
		return deletedCount;
	}

	static BatchProcessingEventEntity updateStatus(EntityManager em,
			BatchJob job, ProcessingEvent event, Date timestamp, String info) {
		if (em == null) {
			throw new IllegalArgumentException("null EntityManager");
		}
		if (job == null || !job.isPersistent()) {
			throw new IllegalArgumentException("invalid OABA job");
		}
		if (event == null) {
			throw new IllegalArgumentException("null event");
		}
		if (timestamp == null) {
			throw new IllegalArgumentException("null timestamp");
		}
		BatchProcessingEventEntity ope =
			new OabaProcessingEventEntity(job, event, info);
		em.persist(ope);
		return ope;
	}

	static void updateStatusWithNotification(EntityManager em,
			JMSContext jmsContext, Topic oabaStatusTopic, BatchJob job,
			ProcessingEvent event, Date timestamp, String info) {
		if (jmsContext == null) {
			throw new IllegalStateException("null JMS context");
		}
		if (oabaStatusTopic == null) {
			throw new IllegalStateException("null JMS topic");
		}
		BatchProcessingEventEntity ope =
			updateStatus(em, job, event, new Date(), info);
		OabaNotification data = new OabaNotification(ope);
		ObjectMessage message = jmsContext.createObjectMessage(data);
		JMSProducer sender = jmsContext.createProducer();
		sender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		logger.info(
				MessageBeanUtils.topicInfo("Sending", oabaStatusTopic, data));
		sender.send(oabaStatusTopic, message);
		logger.info(MessageBeanUtils.topicInfo("Sent", oabaStatusTopic, data));
	}

	/**
	 * Checks the system property {@link #PN_SANITY_CHECK} and caches the result
	 */
	static boolean isOrderByDebuggingRequested() {
		if (_isOrderByDebuggingRequested == null) {
			String pn =
				EventPersistenceManager.PN_PROCESSINGEVENT_ORDERBY_DEBUGGING;
			String defaultValue = Boolean.FALSE.toString();
			String value = System.getProperty(pn, defaultValue);
			_isOrderByDebuggingRequested = Boolean.valueOf(value);
		}
		boolean retVal = _isOrderByDebuggingRequested.booleanValue();
		if (retVal) {
			logger.info("ProcessingEventLog order-by debugging is enabled");
		}
		return retVal;
	}

	static BatchProcessingEvent getCurrentBatchProcessingEvent(EntityManager em,
			BatchJob batchJob) {
		List<BatchProcessingEvent> entries = OabaEventManager
				.findProcessingLogEntriesByJobId(em, batchJob.getId());
		final BatchProcessingEvent retVal;
		if (entries == null || entries.isEmpty()) {
			retVal = null;
		} else {
			retVal = entries.get(0);
			if (isOrderByDebuggingRequested()) {
				final Date mostRecent = retVal.getEventTimestamp();
				if (entries.size() > 1) {
					for (int i = 1; i < entries.size(); i++) {
						final BatchProcessingEvent e2 = entries.get(i);
						final Date d2 = e2.getEventTimestamp();
						if (mostRecent.compareTo(d2) < 0) {
							String summary =
								"Invalid BatchProcessingEvent ordering";
							String msg =
								OabaEventManager.createOrderingDetailMesssage(
										summary, retVal, e2);
							logger.severe(msg);
							throw new IllegalStateException(summary);

						} else if (mostRecent.compareTo(d2) == 0) {
							// Timestamps by themselves are ambiguous
							// if events are very close together, but
							// may be disambiguated by ordering of ids
							String summary =
								"Ambiguous BatchProcessingEvent timestamps";
							String msg =
								OabaEventManager.createOrderingDetailMesssage(
										summary, retVal, e2);
							logger.fine(msg);
						}
					}
				}
			}
		}
		return retVal;
	}

	static String createOrderingDetailMesssage(String summary,
			BatchProcessingEvent e1, BatchProcessingEvent e2) {
		final String INDENT = "   ";
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println(summary);
		pw.println(INDENT + "Most recent: " + e1.getEventName() + " " + e1);
		pw.println(INDENT + " Subsequent: " + e2.getEventName() + " " + e2);
		return sw.toString();
	}

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

	@Inject
	private JMSContext jmsContext;

	@Resource(lookup = "java:/choicemaker/urm/jms/statusTopic")
	private Topic oabaStatusTopic;

	@TransactionAttribute(SUPPORTS)
	@Override
	public List<BatchProcessingEvent> findProcessingEventsByJobId(long id) {
		return findProcessingLogEntriesByJobId(em, id);
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public List<BatchProcessingEvent> findAllProcessingEvents() {
		return findAllOabaProcessingEvents(em);
	}

	@Override
	public int deleteProcessingEventsByJobId(long id) {
		return deleteProcessingLogEntriesByJobId(em, id);
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public ProcessingEventLog getProcessingLog(BatchJob job) {
		return new OabaProcessingLog(em, job);
	}

	@Override
	public void updateStatusWithNotification(BatchJob job,
			ProcessingEvent event, Date timestamp, String info) {
		updateStatusWithNotification(em, jmsContext, oabaStatusTopic, job,
				event, timestamp, info);
	}

	@TransactionAttribute(SUPPORTS)
	@Override
	public ProcessingEvent getCurrentProcessingEvent(BatchJob batchJob) {
		ProcessingEvent retVal = null;
		BatchProcessingEvent ope = getCurrentBatchProcessingEvent(em, batchJob);
		if (ope != null) {
			retVal = ope.getProcessingEvent();
		}
		return retVal;
	}

}
