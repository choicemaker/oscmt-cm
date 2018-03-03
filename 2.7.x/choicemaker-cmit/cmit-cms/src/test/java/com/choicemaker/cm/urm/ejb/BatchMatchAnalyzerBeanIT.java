package com.choicemaker.cm.urm.ejb;

import static com.choicemaker.cm.args.BatchProcessing.EVT_DONE;
import static com.choicemaker.cm.args.BatchProcessing.PCT_DONE;
import static com.choicemaker.cm.args.PersistentObject.NONPERSISTENT_ID;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.jms.Queue;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.choicemaker.cm.args.OabaLinkageType;
import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.PersistableRecordSource;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.args.TransitivityParameters;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.oaba.api.DefaultServerConfiguration;
import com.choicemaker.cm.oaba.api.ServerConfigurationException;
import com.choicemaker.cm.oaba.ejb.OabaSettingsEntity;
import com.choicemaker.cm.oaba.ejb.ServerConfigurationControllerBean;
import com.choicemaker.cm.oaba.ejb.ServerConfigurationEntity;
import com.choicemaker.cm.transitivity.ejb.TransitivityParametersEntity;
import com.choicemaker.cm.urm.api.BatchMatchAnalyzer;
import com.choicemaker.cmit.testconfigs.SimplePersonSqlServerTestConfiguration;
import com.choicemaker.cmit.trans.AbstractTransitivityMdbTest;
import com.choicemaker.cmit.utils.j2ee.BatchProcessingPhase;
import com.choicemaker.cmit.utils.j2ee.EntityManagerUtils;
import com.choicemaker.cmit.utils.j2ee.OabaTestUtils;
import com.choicemaker.cmit.utils.j2ee.TransitivityTestParameters;
import com.choicemaker.cmit.utils.j2ee.WellKnownTestConfiguration;
import com.choicemaker.cms.api.UrmJobController;
import com.choicemaker.cms.ejb.UrmDeploymentUtils;

@RunWith(Arquillian.class)
public class BatchMatchAnalyzerBeanIT extends
		AbstractTransitivityMdbTest<SimplePersonSqlServerTestConfiguration> {

	private static final Logger logger = Logger
			.getLogger(BatchMatchAnalyzerBeanIT.class.getName());

	public static final String LOG_SOURCE = BatchMatchAnalyzerBeanIT.class
			.getSimpleName();

	public static final boolean TESTS_AS_EJB_MODULE = false;

	@Deployment
	public static EnterpriseArchive createEarArchive() {
		Class<?>[] removedClasses = null;
		return UrmDeploymentUtils.createEarArchive(removedClasses,
				TESTS_AS_EJB_MODULE);
	}

	@EJB
	BatchMatchAnalyzer bma;

	@EJB
	UrmJobController urmJobController;

	// @SuppressWarnings("unused")
	// private final MatchDedupMdbProcessing oabaProcessing =
	// new MatchDedupMdbProcessing();
	//
	// private final TransSerializerMdbProcessing transProcessing =
	// new TransSerializerMdbProcessing();

	public BatchMatchAnalyzerBeanIT() {
		super(LOG_SOURCE, logger, EVT_DONE, PCT_DONE,
				SimplePersonSqlServerTestConfiguration.class,
				BatchProcessingPhase.FINAL);
	}

	@Override
	public final Queue getResultQueue() {
		return null;
	}

	/** Stubbed implementation that does not check the working directory */
	@Override
	public boolean isWorkingDirectoryCorrectAfterProcessing(BatchJob batchJob) {
		return true;
	}

	protected OabaSettings createOabaSettings(TransitivityTestParameters ttp,
			final OabaLinkageType task) {

		final WellKnownTestConfiguration c = ttp.getTestConfiguration();

		// Create default or generic settings
		final String m0 = c.getModelConfigurationName();
		final String d0 = c.getQueryDatabaseConfiguration();
		final String b0 = c.getBlockingConfiguration();
		OabaSettings _os0 =
				ttp.getSettingsController().findDefaultOabaSettings(m0, d0, b0);
		if (_os0 == null) {
			// Creates generic settings and saves them
			_os0 = new OabaSettingsEntity();
			_os0 = ttp.getSettingsController().save(_os0);
		}
		assertTrue(_os0 != null);

		// Update the default or generic settings using the test
		// parameters
		OabaSettings updatedSettings;
		OabaSettings _os1 = OabaTestUtils.updateSettings(_os0, ttp);
		if (!_os0.equals(_os1)) {
			updatedSettings = ttp.getSettingsController().save(_os1);
		} else {
			updatedSettings = _os0;
		}
		assertTrue(updatedSettings != null);

		return updatedSettings;
	}

	protected PersistableRecordSource createQueryRecordSource(
			TransitivityTestParameters ttp) {
		final WellKnownTestConfiguration c = ttp.getTestConfiguration();
		PersistableRecordSource retVal =
			ttp.getRecordSourceController().save(c.getQueryRecordSource());
		assertTrue(retVal.isPersistent());
		return retVal;
	}

	protected PersistableRecordSource createReferenceRecordSource(
			TransitivityTestParameters ttp, OabaLinkageType task) {
		PersistableRecordSource retVal;
		switch (task) {
		case STAGING_DEDUPLICATION:
		case TA_STAGING_DEDUPLICATION:
			retVal = null;
			break;
		default:
			WellKnownTestConfiguration c = ttp.getTestConfiguration();
			retVal =
				ttp.getRecordSourceController().save(
						c.getReferenceRecordSource());
			assertTrue(retVal.isPersistent());
		}
		return retVal;
	}

	protected ServerConfiguration createServerConfiguration(
			TransitivityTestParameters ttp) {
		final String hostName =
			ServerConfigurationControllerBean.computeHostName();
		logger.info("Computed host name: " + hostName);
		final DefaultServerConfiguration dsc =
			ttp.getServerController().findDefaultServerConfiguration(hostName);
		ServerConfiguration retVal = null;
		if (dsc != null) {
			long id = dsc.getServerConfigurationId();
			logger.info("Default server configuration id: " + id);
			retVal = ttp.getServerController().findServerConfiguration(id);
		}
		if (retVal == null) {
			logger.info("No default server configuration for: " + hostName);
			retVal = ttp.getServerController().computeGenericConfiguration();
			try {
				retVal = ttp.getServerController().save(retVal);
			} catch (ServerConfigurationException e) {
				fail("Unable to save server configuration: " + e.toString());
			}
		}
		logger.info(ServerConfigurationEntity.dump(retVal));
		assertTrue(retVal != null);
		return retVal;
	}

	protected TransitivityParameters createTransitivityParameters(
			TransitivityTestParameters ttp, OabaLinkageType task) {

		final WellKnownTestConfiguration c = ttp.getTestConfiguration();
		final boolean isQueryRsDeduped = false;

		final PersistableRecordSource staging = createQueryRecordSource(ttp);

		final PersistableRecordSource master =
			createReferenceRecordSource(ttp, task);

		TransitivityParameters tp =
			new TransitivityParametersEntity(c.getModelConfigurationName(), c
					.getThresholds().getDifferThreshold(), c.getThresholds()
					.getMatchThreshold(), c.getBlockingConfiguration(),
					staging, isQueryRsDeduped,
					c.getQueryDatabaseConfiguration(), master,
					c.getReferenceDatabaseConfiguration(), c.getOabaTask(),
					c.getTransitivityResultFormat(),
					c.getTransitivityGraphProperty());
		final TransitivityParameters retVal =
			this.getTransParamsController().save(tp);

		return retVal;
	}

	protected <T extends WellKnownTestConfiguration> void testBatchMatchAnalysisProcessing(
			final AbstractTransitivityMdbTest<T> ta, final OabaLinkageType task)
			throws Exception {

		// Preconditions
		if (ta == null || task == null) {
			throw new IllegalArgumentException("null argument");
		}

		final String tag = "testBatchMatchAnalysisProcessing";
		final TransitivityTestParameters ttp = ta.getTestParameters(task);
		final String LOG_SOURCE = ttp.getSourceName();
		logger.entering(LOG_SOURCE, tag);

		@SuppressWarnings("unused")
		final String extId = EntityManagerUtils.createExternalId(tag);
		@SuppressWarnings("unused")
		final TransitivityParameters tp =
			createTransitivityParameters(ttp, task);
		@SuppressWarnings("unused")
		final OabaSettings updatedSettings = createOabaSettings(ttp, task);
		@SuppressWarnings("unused")
		final ServerConfiguration serverConfiguration =
			createServerConfiguration(ttp);

		long urmId;
		switch (task) {

		case STAGING_DEDUPLICATION:
		case TA_STAGING_DEDUPLICATION: {
			fail("not implemented for URM");
			urmId = 0; // FIXME
//				bma.startDeduplicationAndAnalysis(extId, tp, updatedSettings,
//						serverConfiguration);
			break;
		}

		case STAGING_TO_MASTER_LINKAGE:
		case TA_STAGING_TO_MASTER_LINKAGE: {
			fail("not implemented for URM");
			urmId = 0; // FIXME
//				bma.startLinkageAndAnalysis(extId, tp, updatedSettings,
//						serverConfiguration);
			break;
		}

		case MASTER_TO_MASTER_LINKAGE:
		case TA_MASTER_TO_MASTER_LINKAGE:
		default:
			throw new Error("Unexpected linkage: " + task);
		}

		assertTrue(urmId != NONPERSISTENT_ID);
		BatchJob urmJob = urmJobController.findBatchJob(urmId);
		assertTrue(urmJob != null);

		// FIXME RESTOREME
		// Wait for the job to send a final processing notification
		// logger.info("Checking transStatusTopic");
		// final JMSConsumer statusListener =
		// ttp.getTransitivityStatusConsumer();
		// BatchProcessingNotification notification =
		// JmsUtils.receiveFinalBatchProcessingNotification(urmJob,
		// LOG_SOURCE, statusListener, HACK_3X_LONG_TIMEOUT);
		// assertTrue(notification != null);
		// assertTrue(notification.getJobId() == urmId);
		// assertTrue(notification.getEventId() == EVT_DONE);
		// assertTrue(notification.getJobPercentComplete() == PCT_DONE);
		//
		// // Get a fresh copy of the OABA job from the database
		// urmJob = ttp.getEm().find(OabaJobEntity.class, urmId);
		// assertTrue(urmJob != null);
		// String m = "URM job " + urmId + " status: " + urmJob.getStatus();
		// logger.info(m);

		logger.exiting(LOG_SOURCE, tag);
	}

	@Test
	@InSequence(5)
	public void testStartDeduplicationAndAnalysis() throws Exception {
		testBatchMatchAnalysisProcessing(this,
				OabaLinkageType.STAGING_DEDUPLICATION);
	}

	@Test
	@InSequence(6)
	public void testStartLinkageAndAnalysis() throws Exception {
		testBatchMatchAnalysisProcessing(this,
				OabaLinkageType.STAGING_TO_MASTER_LINKAGE);
	}

}
