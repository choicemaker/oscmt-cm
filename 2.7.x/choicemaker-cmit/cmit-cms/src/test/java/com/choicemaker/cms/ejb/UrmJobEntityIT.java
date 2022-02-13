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
package com.choicemaker.cms.ejb;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.BatchJobStatus;
import com.choicemaker.cmit.utils.j2ee.EntityManagerUtils;
import com.choicemaker.cms.api.UrmJobManager;
import com.choicemaker.cms.ejb.UrmJobEntity;

@RunWith(Arquillian.class)
public class UrmJobEntityIT {

//	private static final Logger logger = Logger
//			.getLogger(UrmJobEntityIT.class.getName());

	public static final boolean TESTS_AS_EJB_MODULE = false;

	@Deployment
	public static EnterpriseArchive createEarArchive() {
		Class<?>[] removedClasses = null;
		return UrmDeploymentUtils.createEarArchive(removedClasses,
				TESTS_AS_EJB_MODULE);
	}

	public static final int MAX_TEST_ITERATIONS = 10;

	@EJB
	protected UrmJobManager urmJobManager;

	@Test
	public void testPrerequisites() {
		assertTrue(urmJobManager != null);
	}

	@Test
	public void testConstruction() {
		final String METHOD = "testConstruction";

		final Date now = new Date();
		BatchJob job =
			createEphemeralUrmJob(METHOD);
		final Date now2 = new Date();

		assertTrue(job != null);
		assertTrue(job.getStatus().equals(BatchJobStatus.NEW));
		assertTrue(!job.isPersistent());

		Date d = job.getRequested();
		assertTrue(d != null);
		assertTrue(now.compareTo(d) <= 0);
		assertTrue(d.compareTo(now2) <= 0);

		Date d2 = job.getTimeStamp(BatchJobStatus.NEW);
		assertTrue(d.equals(d2));

	}

	@Test
	public void testPersistFindRemove() {
		final String METHOD = "testPersistFindRemove";

		// Create a job
		final BatchJob j1 = createEphemeralUrmJob(METHOD);
		assertTrue(!j1.isPersistent());

		// Save the job
		urmJobManager.save(j1);
		assertTrue(j1.isPersistent());

		// Find the job
		final BatchJob j2 = urmJobManager.findUrmJob(j1.getId());
		assertTrue(j1.getId() == j2.getId());
		assertTrue(j1.equals(j2));

		// Delete the job
		urmJobManager.delete(j2);
		BatchJob j3 = urmJobManager.findUrmJob(j1.getId());
		assertTrue(j3 == null);

	}

	@Test
	public void testFindAll() {
		final String METHOD = "testFindAll";

		List<Long> jobIds = new LinkedList<>();
		for (int i = 0; i < MAX_TEST_ITERATIONS; i++) {
			// Create and save a job
			BatchJob job = createEphemeralUrmJob(METHOD);
			urmJobManager.save(job);
			long id = job.getId();
			assertTrue(!jobIds.contains(id));
			jobIds.add(id);
		}

		// Verify the number of jobs has increased
		List<BatchJob> jobs = urmJobManager.findAllUrmJobs();
		assertTrue(jobs != null);

		// Find the jobs
		boolean isFound = false;
		for (long jobId : jobIds) {
			for (BatchJob job : jobs) {
				if (jobId == job.getId()) {
					isFound = true;
					urmJobManager.delete(job);
					break;
				}
			}
			assertTrue(isFound);
		}

	}

	private BatchJob createEphemeralUrmJob(String tag) {
		String extId = EntityManagerUtils.createExternalId(tag);
		UrmJobEntity retVal = new UrmJobEntity(extId);
		return retVal;
	}

}
