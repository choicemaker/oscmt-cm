/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.cmit.trans;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.choicemaker.cm.args.OabaSettings;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobStatus;
import com.choicemaker.cm.batch.api.OperationalPropertyController;
import com.choicemaker.cm.batch.api.EventPersistenceManager;
import com.choicemaker.cm.oaba.api.OabaJobManager;
import com.choicemaker.cm.oaba.api.OabaParametersController;
import com.choicemaker.cm.oaba.api.OabaService;
import com.choicemaker.cm.oaba.api.OabaSettingsController;
import com.choicemaker.cm.oaba.api.RecordIdController;
import com.choicemaker.cm.oaba.api.RecordSourceController;
import com.choicemaker.cm.oaba.api.ServerConfigurationController;
import com.choicemaker.cm.transitivity.api.TransitivityJobManager;
import com.choicemaker.cmit.trans.util.TransitivityDeploymentUtils;
import com.choicemaker.cmit.utils.j2ee.BatchJobUtils;
import com.choicemaker.cmit.utils.j2ee.TestEntityCounts;

@RunWith(Arquillian.class)
public class TransitivityJobEntityIT {

	private static final Logger logger = Logger
			.getLogger(TransitivityJobEntityIT.class.getName());

	public static final boolean TESTS_AS_EJB_MODULE = false;

	// private final static String LOG_SOURCE = TransitivityJobEntityIT.class
	// .getSimpleName();

	@Deployment
	public static EnterpriseArchive createEarArchive() {
		Class<?>[] removedClasses = null;
		return TransitivityDeploymentUtils.createEarArchive(removedClasses,
				TESTS_AS_EJB_MODULE);
	}

	public static final int MAX_SINGLE_LIMIT = 1000;

	public static final int MAX_TEST_ITERATIONS = 10;

	@Resource
	private UserTransaction utx;

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

	@EJB
	private OabaJobManager oabaJobManager;

	@EJB
	protected TransitivityJobManager transJobManager;

	@EJB
	private OabaParametersController oabaParamsController;

	@EJB
	private OabaSettingsController oabaSettingsController;

	@EJB(beanName = "OabaProcessingControllerBean")
	private EventPersistenceManager oabaProcessingController;

	@EJB(beanName = "TransitivityProcessingControllerBean")
	private EventPersistenceManager transProcessingController;

	@EJB
	private OabaService oabaService;

	@EJB
	private OperationalPropertyController opPropController;

	@EJB
	private RecordIdController ridController;

	@EJB
	private RecordSourceController rsController;

	@EJB
	private ServerConfigurationController serverController;

	private TestEntityCounts te;

	// private final Random random = new Random(new Date().getTime());

	@Before
	public void setUp() throws Exception {
		te =
			new TestEntityCounts(logger, oabaJobManager,
					oabaParamsController, oabaSettingsController,
					serverController, oabaProcessingController,
					opPropController, rsController, ridController);
	}

	public void checkCounts() {
		if (te != null) {
			te.checkCounts(logger, em, utx, oabaJobManager,
					oabaParamsController, oabaSettingsController,
					serverController, oabaProcessingController,
					opPropController, rsController, ridController);
		} else {
			throw new Error("Counts not initialized");
		}
	}

	@Test
	public void testPrerequisites() {
		assertTrue(em != null);
		assertTrue(utx != null);
		assertTrue(oabaJobManager != null);
		assertTrue(transJobManager != null);
		assertTrue(oabaSettingsController != null);
		assertTrue(serverController != null);
	}

	@Test
	public void testConstruction() {
		final String METHOD = "testConstruction";

		BatchJob batchJob = createEphemeralOabaJob(te, METHOD, true);
		oabaJobManager.save(batchJob);
		assertTrue(batchJob.isPersistent());
		final Date now = new Date();
		BatchJob job =
			createEphemeralTransitivityJob(te, batchJob, METHOD, true);
		final Date now2 = new Date();

		assertTrue(job != null);
		assertTrue(job.getBatchParentId() == batchJob.getId());
		assertTrue(job.getStatus().equals(BatchJobStatus.NEW));
		assertTrue(!job.isPersistent());

		Date d = job.getRequested();
		assertTrue(d != null);
		assertTrue(now.compareTo(d) <= 0);
		assertTrue(d.compareTo(now2) <= 0);

		Date d2 = job.getTimeStamp(BatchJobStatus.NEW);
		assertTrue(d.equals(d2));

		checkCounts();
	}

	@Test
	public void testPersistFindRemove() {
		final String METHOD = "testPersistFindRemove";

		// Create a job
		final BatchJob j1 = createEphemeralTransitivityJob(te, METHOD, true);
		assertTrue(!j1.isPersistent());

		// Save the job
		transJobManager.save(j1);
		assertTrue(j1.isPersistent());

		// Find the job
		final BatchJob j2 = transJobManager.findTransitivityJob(j1.getId());
		assertTrue(j1.getId() == j2.getId());
		assertTrue(j1.equals(j2));

		// Delete the job
		transJobManager.delete(j2);
		BatchJob j3 = transJobManager.findTransitivityJob(j1.getId());
		assertTrue(j3 == null);

		checkCounts();
	}

	@Test
	public void testFindAll() {
		final String METHOD = "testFindAll";

		List<Long> jobIds = new LinkedList<>();
		for (int i = 0; i < MAX_TEST_ITERATIONS; i++) {
			// Create and save a job
			BatchJob job = createEphemeralTransitivityJob(te, METHOD, true);
			transJobManager.save(job);
			long id = job.getId();
			assertTrue(!jobIds.contains(id));
			jobIds.add(id);
		}

		// Verify the number of jobs has increased
		List<BatchJob> jobs = transJobManager.findAllTransitivityJobs();
		assertTrue(jobs != null);

		// Find the jobs
		boolean isFound = false;
		for (long jobId : jobIds) {
			for (BatchJob job : jobs) {
				if (jobId == job.getId()) {
					isFound = true;
					break;
				}
			}
			assertTrue(isFound);
		}

		checkCounts();
	}

	private BatchJob createEphemeralTransitivityJob(TestEntityCounts te,
			String tag, boolean isTag) {
		BatchJob batchJob = createEphemeralOabaJob(te, tag, isTag);
		oabaJobManager.save(batchJob);
		assertTrue(te.contains(batchJob));
		return createEphemeralTransitivityJob(te, batchJob, tag, isTag);
	}

	protected BatchJob createEphemeralTransitivityJob(TestEntityCounts te,
			BatchJob batchJob, String tag, boolean isTag) {
		OabaSettings settings =
			oabaSettingsController.findOabaSettingsByJobId(batchJob.getId());
		ServerConfiguration sc = getDefaultServerConfiguration();
		return BatchJobUtils.createEphemeralTransitivityJob(MAX_SINGLE_LIMIT,
				utx, settings, sc, em, te, batchJob, oabaParamsController, tag,
				isTag);
	}

	protected BatchJob createEphemeralOabaJob(TestEntityCounts te, String tag,
			boolean isTag) {
		ServerConfiguration sc = getDefaultServerConfiguration();
		return BatchJobUtils.createEphemeralOabaJobEntity(MAX_SINGLE_LIMIT,
				utx, sc, em, te, tag, isTag);
	}

	protected ServerConfiguration getDefaultServerConfiguration() {
		ServerConfiguration retVal =
			BatchJobUtils.getDefaultServerConfiguration(serverController);
		if (retVal == null) {
			retVal = serverController.computeGenericConfiguration();
		}
		assertTrue(retVal != null);
		return retVal;
	}

}
