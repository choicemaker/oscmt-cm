/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb.util;

import static com.choicemaker.cm.args.OperationalPropertyNames.PN_CLEAR_RESOURCES;

import java.util.logging.Logger;

import javax.jms.DeliveryMode;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Topic;

import com.choicemaker.cm.args.ProcessingEventBean;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobManager;
import com.choicemaker.cm.batch.api.OperationalPropertyController;
import com.choicemaker.cm.batch.api.ProcessingEventLog;
import com.choicemaker.cm.batch.ejb.BatchJobFileUtils;
import com.choicemaker.cm.oaba.ejb.data.MatchWriterMessage;
import com.choicemaker.cm.oaba.ejb.data.OabaJobMessage;
import com.choicemaker.util.Precondition;

/**
 * This object contains common message bean utilities such as canceling a Batch
 * job or sending various types of JMS messages.
 * 
 * @author pcheung
 *
 */
public class MessageBeanUtils {

	private static final Logger log0 =
		Logger.getLogger(MessageBeanUtils.class.getName());

	public static final String DEFAULT_TAG = "UNKNOWN SOURCE";
	public static final String UNKNOWN_QUEUE = "unknown queue";
	public static final String UNKNOWN_TOPIC = "unknown topic";

	/**
	 * This method stops the BatchJob by setting the status to aborted, and
	 * removes the temporary directory for the job.
	 */
	public static void stopJob(BatchJob batchJob, BatchJobManager jobManager,
			OperationalPropertyController propController,
			ProcessingEventLog status) {

		Precondition.assertNonNullArgument("null batchJob", batchJob);
		Precondition.assertNonNullArgument("null jobManager", jobManager);
		Precondition.assertNonNullArgument("null propController",
				propController);
		Precondition.assertNonNullArgument("null status", status);

		batchJob.markAsAborted();
		jobManager.save(batchJob);

		final String _clearResources =
			propController.getOperationalPropertyValue(batchJob, PN_CLEAR_RESOURCES);
		boolean clearResources = Boolean.valueOf(_clearResources);

		if (clearResources) {
			log0.info("Clearing resources for job " + batchJob.getId());
			status.setCurrentProcessingEvent(ProcessingEventBean.DONE);
			log0.info("Removing Temporary directory.");
			BatchJobFileUtils.removeTempDir(batchJob);
		} else {
			log0.info("Retaining resources for job " + batchJob.getId());
		}
	}

	public static void sendStartData(OabaJobMessage data, JMSContext jmsCtx,
			Queue q, Logger log) {
		if (data == null || jmsCtx == null || q == null || log == null) {
			throw new IllegalArgumentException("null argument");
		}
		ObjectMessage message = jmsCtx.createObjectMessage(data);
		JMSProducer sender = jmsCtx.createProducer();
		sender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		log.fine(MessageBeanUtils.queueInfo("Sending", q, data));
		sender.send(q, message);
		log.fine(MessageBeanUtils.queueInfo("Sent", q, data));
	}

	public static void sendMatchWriterData(MatchWriterMessage data,
			JMSContext jmsCtx, Queue q, Logger log) {
		if (data == null || jmsCtx == null || q == null || log == null) {
			throw new IllegalArgumentException("null argument");
		}
		ObjectMessage message = jmsCtx.createObjectMessage(data);
		JMSProducer sender = jmsCtx.createProducer();
		sender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		log.fine(MessageBeanUtils.queueInfo("Sending", q, data));
		sender.send(q, message);
		log.fine(MessageBeanUtils.queueInfo("Sent", q, data));
	}

	public static String queueInfo(String tag, Queue q, Object d) {
		if (q == null) {
			throw new IllegalArgumentException("null argument");
		}
		if (tag == null || tag.trim().isEmpty()) {
			tag = DEFAULT_TAG;
		}
		String queueName;
		try {
			queueName = q.getQueueName();
		} catch (JMSException x) {
			queueName = UNKNOWN_QUEUE;
		}
		StringBuilder sb = new StringBuilder(tag).append(" ");
		sb.append("queue: '").append(queueName).append("'");
		sb.append(", data: '").append(d).append("'");
		return sb.toString();
	}

	public static String topicInfo(String tag, Topic t, Object d) {
		if (t == null) {
			throw new IllegalArgumentException("null argument");
		}
		if (tag == null || tag.trim().isEmpty()) {
			tag = DEFAULT_TAG;
		}
		String topicName;
		try {
			topicName = t.getTopicName();
		} catch (JMSException x) {
			topicName = UNKNOWN_TOPIC;
		}
		StringBuilder sb = new StringBuilder(tag).append(" ");
		sb.append("topic: '").append(topicName).append("'");
		sb.append(", data: '").append(d).append("'");
		return sb.toString();
	}

}
