package com.choicemaker.cmit.utils;

import static com.choicemaker.cm.io.blocking.automated.offline.core.OabaProcessing.PCT_DONE_OABA;
import static org.junit.Assert.fail;

import java.util.logging.Logger;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Queue;

import com.choicemaker.cm.io.blocking.automated.offline.server.data.OabaJobMessage;
import com.choicemaker.cm.io.blocking.automated.offline.server.data.OabaUpdateMessage;

public class JmsUtils {

	private static final Logger logger = Logger.getLogger(JmsUtils.class
			.getName());

	/** A short time-out for receiving messages (1 second) */
	public static final long SHORT_TIMEOUT_MILLIS = 1000;

	/** A reasonably long time-out for receiving messages (20 seconds) */
	public static final long LONG_TIMEOUT_MILLIS = 20000;

	/**
	 * A very long, rather desperate time-out for receiving messages from
	 * possibly delayed (or more likely, dead) processes (5 minutes)
	 */
	public static final long VERY_LONG_TIMEOUT_MILLIS = 300000;

	public static String queueInfo(String tag, Queue q) {
		String queueName;
		try {
			queueName = q.getQueueName();
		} catch (JMSException x) {
			queueName = "unknown";
		}
		StringBuilder sb =
			new StringBuilder(tag).append("queue: '").append(queueName)
					.append("'");
		return sb.toString();
	}

	public static String queueInfo(String tag, Queue q, Object d) {
		String queueName;
		try {
			queueName = q.getQueueName();
		} catch (JMSException x) {
			queueName = "unknown";
		}
		StringBuilder sb =
			new StringBuilder(tag).append("queue: '").append(queueName)
					.append("', data: '").append(d).append("'");
		return sb.toString();
	}

	public static String queueInfo(String tag, String queueName, Object d) {
		if (queueName == null || queueName.trim().isEmpty()) {
			queueName = "unknown";
		}
		StringBuilder sb =
			new StringBuilder(tag).append("queue: '").append(queueName)
					.append("', data: '").append(d).append("'");
		return sb.toString();
	}

	public static void clearStartDataFromQueue(String LOG_SOURCE,
			JMSContext jmsContext, Queue queue) {
		JMSConsumer consumer = jmsContext.createConsumer(queue);
		int count = 0;
		OabaJobMessage startData = null;
		do {
			startData =
				receiveStartData(LOG_SOURCE, consumer, queue,
						SHORT_TIMEOUT_MILLIS);
			if (startData != null) {
				++count;
			}
			logger.info(queueInfo("Clearing: ", queue, startData));
		} while (startData != null);
		logger.info(queueInfo("Messages cleared: " + count + " ", queue));
	}

	public static void clearUpdateDataFromQueue(String LOG_SOURCE,
			JMSContext jmsContext, Queue updateQueue) {
		JMSConsumer consumer = jmsContext.createConsumer(updateQueue);
		int count = 0;
		OabaUpdateMessage updateMessage = null;
		do {
			updateMessage =
				receiveUpdateMessage(LOG_SOURCE, consumer, updateQueue,
						SHORT_TIMEOUT_MILLIS);
			if (updateMessage != null) {
				++count;
			}
			logger.info(queueInfo("Clearing: ", updateQueue, updateMessage));
		} while (updateMessage != null);
		logger.info(queueInfo("Messages cleared: " + count + " ", updateQueue));
	}

	public static OabaJobMessage receiveStartData(String LOG_SOURCE,
			JMSContext jmsContext, Queue queue) {
		return receiveStartData(LOG_SOURCE, jmsContext, queue,
				SHORT_TIMEOUT_MILLIS);
	}

	public static OabaJobMessage receiveStartData(final String LOG_SOURCE,
			JMSContext jmsContext, Queue queue, long timeOut) {
		JMSConsumer consumer = jmsContext.createConsumer(queue);
		return receiveStartData(LOG_SOURCE, consumer, queue, timeOut);
	}

	protected static OabaJobMessage receiveStartData(final String LOG_SOURCE,
			JMSConsumer consumer, Queue queue, long timeOut) {
		final String METHOD = "receiveStartData(" + timeOut + ")";
		logger.entering(LOG_SOURCE, METHOD);
		OabaJobMessage retVal = null;
		try {
			retVal = consumer.receiveBody(OabaJobMessage.class, timeOut);
		} catch (Exception x) {
			fail(x.toString());
		}
		logger.exiting(LOG_SOURCE, METHOD);
		return retVal;
	}

	public static OabaUpdateMessage receiveUpdateMessage(String LOG_SOURCE,
			JMSContext jmsContext, Queue updateQueue) {
		return receiveUpdateMessage(LOG_SOURCE, jmsContext, updateQueue,
				SHORT_TIMEOUT_MILLIS);
	}

	public static OabaUpdateMessage receiveLatestUpdateMessage(
			final String LOG_SOURCE, JMSContext jmsContext, Queue updateQueue,
			long timeOut) {
		final String METHOD = "receiveLatestUpdateMessage(" + timeOut + ")";
		logger.entering(LOG_SOURCE, METHOD);
		JMSConsumer consumer = jmsContext.createConsumer(updateQueue);
		OabaUpdateMessage retVal = null;
		OabaUpdateMessage msg = null;
		do {
			msg =
				receiveUpdateMessage(LOG_SOURCE, consumer, updateQueue, timeOut);
			if (msg != null) {
				retVal = msg;
			}
		} while (msg != null);
		logger.exiting(LOG_SOURCE, METHOD);
		return retVal;
	}

	public static OabaUpdateMessage receiveFinalUpdateMessage(
			final String LOG_SOURCE, JMSContext jmsContext, Queue updateQueue,
			long timeOut) {
		final String METHOD = "receiveLatestUpdateMessage(" + timeOut + ")";
		JMSConsumer consumer = jmsContext.createConsumer(updateQueue);
		logger.entering(LOG_SOURCE, METHOD);
		OabaUpdateMessage retVal = null;
		OabaUpdateMessage msg = null;
		do {
			msg =
				receiveUpdateMessage(LOG_SOURCE, consumer, updateQueue, timeOut);
			if (msg != null) {
				retVal = msg;
			}
			if (msg.getPercentComplete() == PCT_DONE_OABA) {
				break;
			}
		} while (msg != null);
		logger.exiting(LOG_SOURCE, METHOD);
		return retVal;
	}

	public static OabaUpdateMessage receiveUpdateMessage(
			final String LOG_SOURCE, JMSContext jmsContext, Queue updateQueue,
			long timeOut) {
		JMSConsumer consumer = jmsContext.createConsumer(updateQueue);
		return receiveUpdateMessage(LOG_SOURCE, consumer, updateQueue, timeOut);
	}

	protected static OabaUpdateMessage receiveUpdateMessage(
			final String LOG_SOURCE, JMSConsumer consumer, Queue updateQueue,
			long timeOut) {
		final String METHOD = "receiveUpdateMessage(" + timeOut + ")";
		logger.entering(LOG_SOURCE, METHOD);
		if (consumer == null) {
			throw new IllegalArgumentException("null consumer");
		}
		Object o = null;
		try {
			o = consumer.receiveBody(Object.class, timeOut);
		} catch (Exception x) {
			fail(x.toString());
		}
		logger.info(queueInfo("Received from: ", "updateQueue", o));
		if (o != null && !(o instanceof OabaUpdateMessage)) {
			fail("Received wrong type from update queue: "
					+ o.getClass().getName());
		}
		OabaUpdateMessage retVal = (OabaUpdateMessage) o;
		logger.exiting(LOG_SOURCE, METHOD);
		return retVal;
	}

	private JmsUtils() {
	}

}