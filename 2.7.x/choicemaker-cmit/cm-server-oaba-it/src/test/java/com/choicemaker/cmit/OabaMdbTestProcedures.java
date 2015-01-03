package com.choicemaker.cmit;

import static com.choicemaker.cm.batch.BatchJob.INVALID_ID;
import static com.choicemaker.cmit.utils.JmsUtils.LONG_TIMEOUT_MILLIS;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.logging.Logger;

import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import com.choicemaker.cm.args.OabaLinkageType;
import com.choicemaker.cm.args.OabaParameters;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.PersistableRecordSource;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.io.blocking.automated.offline.core.OabaEventLog;
import com.choicemaker.cm.io.blocking.automated.offline.server.data.OabaJobMessage;
import com.choicemaker.cm.io.blocking.automated.offline.server.data.OabaNotification;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.OabaJob;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.OabaService;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.ServerConfigurationException;
import com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaJobControllerBean;
import com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaParametersControllerBean;
import com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaParametersEntity;
import com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaProcessingControllerBean;
import com.choicemaker.cm.io.blocking.automated.offline.server.impl.OabaUtils;
import com.choicemaker.cmit.utils.EntityManagerUtils;
import com.choicemaker.cmit.utils.JmsUtils;
import com.choicemaker.cmit.utils.OabaProcessingPhase;
import com.choicemaker.cmit.utils.TestEntities;
import com.choicemaker.cmit.utils.WellKnownTestConfiguration;
import com.choicemaker.e2.CMPluginRegistry;

public class OabaMdbTestProcedures {

	private static final Logger logger = Logger
			.getLogger(OabaMdbTestProcedures.class.getName());

	public static boolean isValidConfigurationClass(Class<?> c) {
		boolean retVal = false;
		if (c != null && WellKnownTestConfiguration.class.isAssignableFrom(c)) {
			retVal = true;
		}
		return retVal;
	}

	public static <T extends WellKnownTestConfiguration> T createTestConfiguration(
			Class<T> c, OabaLinkageType task, CMPluginRegistry registry) {

		if (!isValidConfigurationClass(c)) {
			String msg = "invalid configuration class: " + c;
			throw new IllegalArgumentException(msg);
		}
		if (registry == null) {
			throw new IllegalArgumentException("null registry");
		}

		T retVal = null;
		try {
			Class<T> cWKTC = (Class<T>) c;
			retVal = cWKTC.newInstance();
			retVal.initialize(task, registry);
		} catch (Exception x) {
			fail(x.toString());
		}
		assertTrue(retVal != null);
		return retVal;
	}

	public static <T extends WellKnownTestConfiguration> void testLinkageProcessing(
			AbstractOabaMdbTest<T> test) throws ServerConfigurationException {
		if (test == null) {
			throw new IllegalArgumentException("null argument");
		}

		assertTrue(test.isSetupOK());
		String TEST = "testStartOABALinkage";
		test.getLogger().entering(test.getSourceName(), TEST);

		final String externalID = EntityManagerUtils.createExternalId(TEST);
		testOabaProcessing(OabaLinkageType.STAGING_TO_MASTER_LINKAGE, TEST,
				test, externalID);

		test.getLogger().exiting(test.getSourceName(), TEST);
	}

	public static <T extends WellKnownTestConfiguration> void testDeduplicationProcessing(
			AbstractOabaMdbTest<T> test) throws ServerConfigurationException {
		if (test == null) {
			throw new IllegalArgumentException("null argument");
		}

		assertTrue(test.isSetupOK());
		String TEST = "testStartOABAStage";
		test.getLogger().entering(test.getSourceName(), TEST);

		final String externalID = EntityManagerUtils.createExternalId(TEST);
		testOabaProcessing(OabaLinkageType.STAGING_DEDUPLICATION, TEST, test,
				externalID);

		test.getLogger().exiting(test.getSourceName(), TEST);
	}

	protected static <T extends WellKnownTestConfiguration> void testOabaProcessing(
			final OabaLinkageType linkage, final String tag,
			final AbstractOabaMdbTest<T> test, final String externalId) {

		// Preconditions
		if (linkage == null || tag == null || test == null
				|| externalId == null) {
			throw new IllegalArgumentException("null argument");
		}

		final String LOG_SOURCE = test.getSourceName();
		logger.entering(LOG_SOURCE, tag);

		final OabaProcessingPhase oabaPhase = test.getOabaProcessingPhase();
		final boolean isIntermediateExpected = oabaPhase.isIntermediateExpected;
		final boolean isUpdateExpected = oabaPhase.isUpdateExpected;
		final Queue listeningQueue = test.getResultQueue();
		final Topic oabaStatusTopic = test.getOabaStatusTopic();
		validateDestinations(oabaPhase, listeningQueue, oabaStatusTopic);

		final WellKnownTestConfiguration c = test.getTestConfiguration(linkage);

		final PersistableRecordSource staging =
			test.getRecordSourceController().save(c.getStagingRecordSource());
		assertTrue(staging.getId() != PersistableRecordSource.NONPERSISTENT_ID);

		final PersistableRecordSource master;
		if (OabaLinkageType.STAGING_DEDUPLICATION == linkage) {
			master = null;
		} else {
			master =
				test.getRecordSourceController()
						.save(c.getMasterRecordSource());
			assertTrue(master.getId() != PersistableRecordSource.NONPERSISTENT_ID);
		}

		final OabaSettings oabaSettings =
			OabaUtils.getDefaultOabaSettings(test.getSettingsController(),
					c.getModelConfigurationName());
		final ServerConfiguration serverConfiguration =
			OabaUtils.getDefaultServerConfiguration(test.getServerController());

		final TestEntities te = new TestEntities();
		final OabaParameters bp =
			new OabaParametersEntity(c.getModelConfigurationName(), c
					.getThresholds().getDifferThreshold(), c.getThresholds()
					.getMatchThreshold(), staging, master, c.getOabaTask());
		te.add(bp);

		final OabaService batchQuery = test.getOabaService();
		long jobId = INVALID_ID;
		try {
			switch (linkage) {
			case STAGING_DEDUPLICATION:
				logger.info(tag
						+ ": invoking BatchQueryService.startDeduplication");
				jobId =
					batchQuery.startDeduplication(externalId, bp, oabaSettings,
							serverConfiguration);
				logger.info(tag
						+ ": returned from BatchQueryService.startDeduplication");
				break;
			case STAGING_TO_MASTER_LINKAGE:
			case MASTER_TO_MASTER_LINKAGE:
				logger.info(tag + ": invoking BatchQueryService.startLinkage");
				jobId =
					batchQuery.startLinkage(externalId, bp, oabaSettings,
							serverConfiguration);
				logger.info(tag
						+ ": returned from BatchQueryService.startLinkage");
				break;
			default:
				fail("Unexpected linkage type: " + linkage);
			}
		} catch (ServerConfigurationException e) {
			fail(e.toString());
		}

		final OabaJobControllerBean jobController = test.getJobController();
		assertTrue(INVALID_ID != jobId);
		OabaJob oabaJob = jobController.findOabaJob(jobId);
		assertTrue(oabaJob != null);
		te.add(oabaJob);
		assertTrue(externalId != null
				&& externalId.equals(oabaJob.getExternalId()));

		// Find the persistent OabaParameters object created by the call to
		// BatchQueryService.startLinkage...
		final OabaParametersControllerBean paramsController =
			test.getParamsController();
		OabaParameters params = paramsController.findBatchParamsByJobId(jobId);
		te.add(params);

		// Validate that the job parameters are correct
		assertTrue(params != null);
		assertTrue(params.getLowThreshold() == bp.getLowThreshold());
		assertTrue(params.getHighThreshold() == bp.getHighThreshold());
		if (OabaLinkageType.STAGING_DEDUPLICATION == linkage) {
			assertTrue(params.getMasterRsId() == null);
			assertTrue(params.getMasterRsType() == null);
		} else {
			assertTrue(params.getMasterRsId() != null
					&& params.getMasterRsId().equals(bp.getMasterRsId()));
			assertTrue(params.getMasterRsType() != null
					&& params.getMasterRsType().equals(bp.getMasterRsType()));
		}
		assertTrue(params.getStageRsId() == bp.getStageRsId());
		assertTrue(params.getStageRsType() != null
				&& params.getStageRsType().equals(bp.getStageRsType()));
		assertTrue(params.getModelConfigurationName() != null
				&& params.getModelConfigurationName().equals(
						bp.getModelConfigurationName()));

		final JMSContext jmsContext = test.getJmsContext();
		if (isIntermediateExpected) {
			// Check that OABA processing completed and sent out a
			// message on the intermediate result queue
			assert listeningQueue != null;
			String listeningQueueName;
			try {
				listeningQueueName = listeningQueue.getQueueName();
			} catch (JMSException x) {
				logger.warning(x.toString());
				listeningQueueName = "listeningQueue";
			}
			logger.info("Checking " + listeningQueueName);
			OabaJobMessage startData =
				JmsUtils.receiveStartData(LOG_SOURCE, jmsContext,
						listeningQueue, JmsUtils.LONG_TIMEOUT_MILLIS);
			logger.info(JmsUtils.queueInfo("Received from: ", listeningQueue,
					startData));
			if (startData == null) {
				fail("did not receive data from " + listeningQueueName);
			}
			assertTrue(startData.jobID == jobId);
		}
		if (isUpdateExpected) {
			// Check that OABA processing sent out an expected status
			// on the update queue
			logger.info("Checking oabaStatusTopic");
			OabaNotification oabaNotification = null;
			if (oabaPhase == OabaProcessingPhase.INTERMEDIATE
					|| oabaPhase == OabaProcessingPhase.INITIAL) {
				oabaNotification =
					JmsUtils.receiveLatestOabaNotification(oabaJob, LOG_SOURCE,
							jmsContext, oabaStatusTopic,
							JmsUtils.SHORT_TIMEOUT_MILLIS);
			} else if (oabaPhase == OabaProcessingPhase.FINAL) {
				oabaNotification =
				// JmsUtils.receiveFinalOabaNotification(LOG_SOURCE, jmsContext,
				// oabaStatusTopic, VERY_LONG_TIMEOUT_MILLIS);
					JmsUtils.receiveFinalOabaNotification(oabaJob, LOG_SOURCE,
							jmsContext, oabaStatusTopic, LONG_TIMEOUT_MILLIS);
			} else {
				throw new Error("unexpected phase: " + oabaPhase);
			}
			assertTrue(oabaNotification != null);
			assertTrue(oabaNotification.getJobId() == jobId);
			final float expectPercentDone = test.getResultPercentComplete();
			assertTrue(oabaNotification.getJobPercentComplete() == expectPercentDone);
		}

		// Find the entry in the processing history updated by the OABA
		final OabaProcessingControllerBean processingController =
			test.getProcessingController();
		OabaEventLog processingEntry =
			processingController.getProcessingLog(oabaJob);
		// te.add(processingEntry);

		// Validate that processing entry is correct for this stage of the OABA
		assertTrue(processingEntry != null);
		final int expectedEventId = test.getResultEventId();
		assertTrue(processingEntry.getCurrentOabaEventId() == expectedEventId);

		// Check that the working directory contains what it should
		assertTrue(test.isWorkingDirectoryCorrectAfterProcessing(linkage,
				oabaJob, bp, oabaSettings, serverConfiguration));

		// Clean up
		try {
			final EntityManager em = test.getEm();
			final UserTransaction utx = test.getUtx();
			te.removePersistentObjects(em, utx);
		} catch (Exception x) {
			logger.severe(x.toString());
			fail(x.toString());
		}

		logger.exiting(LOG_SOURCE, tag);
	}

	public static void validateDestinations(OabaProcessingPhase oabaPhase,
			Queue listeningQueue, Topic oabaStatusTopic) {
		if (oabaPhase == null) {
			throw new IllegalArgumentException("null OABA processing phase");
		}
		final boolean isIntermediateExpected = oabaPhase.isIntermediateExpected;
		final boolean isUpdateExpected = oabaPhase.isUpdateExpected;
		assertTrue(isUpdateExpected);
		if (oabaStatusTopic == null) {
			throw new IllegalArgumentException("status topic is null");
		}
		// /* if (isIntermediateExpected && !isUpdateExpected) {
		// if (listeningQueue == null) {
		// throw new IllegalArgumentException(
		// "intermediate-result queue is null");
		// }
		// if (oabaStatusTopic != null) {
		// String msg =
		// "Ignoring update queue -- results expected only "
		// + "from intermediate-result queue";
		// logger.warning(msg);
		// }
		// } else */
		if (isIntermediateExpected /* && isUpdateExpected */) {
			if (listeningQueue == null) {
				throw new IllegalArgumentException(
						"intermediate-result queue is null");
			}
			// if (oabaStatusTopic == null) {
			// throw new IllegalArgumentException("status topic is null");
			// }
		} else {
			assertTrue(!isIntermediateExpected /* && isUpdateExpected */);
			if (listeningQueue != null) {
				String msg =
					"Ignoring intermediate-result queue -- "
							+ "final results expected from status topic";
				logger.warning(msg);
			}
			// if (oabaStatusTopic == null) {
			// throw new IllegalArgumentException("status topic is null");
			// }
		}
	}

	private OabaMdbTestProcedures() {
	}

}
