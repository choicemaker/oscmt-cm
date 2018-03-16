/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.transitivity.ejb;

import static com.choicemaker.cm.transitivity.ejb.TransitivityProcessingEventJPA.PN_TRANSPROCESSING_DELETE_BY_JOBID_JOBID;
import static com.choicemaker.cm.transitivity.ejb.TransitivityProcessingEventJPA.PN_TRANSPROCESSING_FIND_BY_JOBID_JOBID;
import static com.choicemaker.cm.transitivity.ejb.TransitivityProcessingEventJPA.QN_TRANSPROCESSING_DELETE_BY_JOBID;
import static com.choicemaker.cm.transitivity.ejb.TransitivityProcessingEventJPA.QN_TRANSPROCESSING_FIND_ALL;
import static com.choicemaker.cm.transitivity.ejb.TransitivityProcessingEventJPA.QN_TRANSPROCESSING_FIND_BY_JOBID;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
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
import com.choicemaker.cm.oaba.ejb.util.MessageBeanUtils;
import com.choicemaker.cm.transitivity.api.TransitivityProcessingEvent;


/**
 * This stateless EJB provides Transitivity, job-specific processing logs and
 * stand-alone methods for creating and finding log entries.
 *
 * @author pcheung
 * @author rphall (migration to EJB3)
 */
@Stateless
public class TransitivityEventManager implements EventPersistenceManager {

	private static final Logger logger = Logger
			.getLogger(TransitivityEventManager.class.getName());

	// Don't use this directly; use isOrderByDebuggingRequested() instead
	static Boolean _isOrderByDebuggingRequested = null;

	static List<BatchProcessingEvent> findProcessingLogEntriesByJobId(
			EntityManager em, long id) {
		Query query = em.createNamedQuery(QN_TRANSPROCESSING_FIND_BY_JOBID);
		query.setParameter(PN_TRANSPROCESSING_FIND_BY_JOBID_JOBID, id);
		@SuppressWarnings("unchecked")
		List<BatchProcessingEvent> entries = query.getResultList();
		if (entries == null) {
			entries = Collections.emptyList();
		}
		return entries;
	}

	static List<BatchProcessingEvent> findAllTransitivityProcessingEvents(
			EntityManager em) {
		Query query = em.createNamedQuery(QN_TRANSPROCESSING_FIND_ALL);
		@SuppressWarnings("unchecked")
		List<BatchProcessingEvent> entries = query.getResultList();
		if (entries == null) {
			entries = Collections.emptyList();
		}
		return entries;
	}

	static int deleteProcessingLogEntriesByJobId(EntityManager em, long id) {
		Query query = em.createNamedQuery(QN_TRANSPROCESSING_DELETE_BY_JOBID);
		query.setParameter(PN_TRANSPROCESSING_DELETE_BY_JOBID_JOBID, id);
		int deletedCount = query.executeUpdate();
		return deletedCount;
	}

	static TransitivityProcessingEvent updateStatus(EntityManager em, BatchJob job,
			ProcessingEvent event, Date timestamp, String info) {
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
		TransitivityProcessingEventEntity ope =
			new TransitivityProcessingEventEntity(job, event, info);
		em.persist(ope);
		return ope;
	}

	static void updateStatusWithNotification(EntityManager em,
			JMSContext jmsContext, Topic statusTopic, BatchJob job,
			ProcessingEvent event, Date timestamp, String info) {
		if (jmsContext == null) {
			throw new IllegalStateException("null JMS context");
		}
		if (statusTopic == null) {
			throw new IllegalStateException("null JMS topic");
		}
		TransitivityProcessingEvent ope =
			updateStatus(em, job, event, new Date(), info);
		TransitivityNotification data = new TransitivityNotification(ope);
		ObjectMessage message = jmsContext.createObjectMessage(data);
		JMSProducer sender = jmsContext.createProducer();
		logger.info(MessageBeanUtils
				.topicInfo("Sending", statusTopic, data));
		sender.send(statusTopic, message);
		logger.info(MessageBeanUtils.topicInfo("Sent", statusTopic, data));
	}

	/**
	 * Checks the system property {@link #PN_SANITY_CHECK} and caches the result
	 */
	static boolean isOrderByDebuggingRequested() {
		if (_isOrderByDebuggingRequested == null) {
			String pn = EventPersistenceManager.PN_PROCESSINGEVENT_ORDERBY_DEBUGGING;
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
		List<BatchProcessingEvent> entries =
			TransitivityEventManager.findProcessingLogEntriesByJobId(em,
					batchJob.getId());
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
								TransitivityEventManager
										.createOrderingDetailMesssage(summary,
												retVal, e2);
							logger.severe(msg);
							throw new IllegalStateException(summary);

						} else if (mostRecent.compareTo(d2) == 0) {
							// Timestamps by themselves are ambiguous
							// if events are very close together, but
							// may be disambiguated by ordering of ids
							String summary =
								"Ambiguous BatchProcessingEvent timestamps";
							String msg =
								TransitivityEventManager
										.createOrderingDetailMesssage(summary,
												retVal, e2);
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

	@Resource(lookup = "java:/choicemaker/urm/jms/transStatusTopic")
	private Topic transStatusTopic;

	@Override
	public List<BatchProcessingEvent> findProcessingEventsByJobId(
			long id) {
		return findProcessingLogEntriesByJobId(em, id);
	}

	@Override
	public List<BatchProcessingEvent> findAllProcessingEvents() {
		return findAllTransitivityProcessingEvents(em);
	}

	@Override
	public int deleteProcessingEventsByJobId(long id) {
		return deleteProcessingLogEntriesByJobId(em, id);
	}

	@Override
	public TransitivityProcessingLog getProcessingLog(BatchJob job) {
		return new TransitivityProcessingLog(em, job);
	}

	@Override
	public void updateStatusWithNotification(BatchJob job, ProcessingEvent event,
			Date timestamp, String info) {
		updateStatusWithNotification(em, jmsContext, transStatusTopic, job,
				event, timestamp, info);
	}

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
