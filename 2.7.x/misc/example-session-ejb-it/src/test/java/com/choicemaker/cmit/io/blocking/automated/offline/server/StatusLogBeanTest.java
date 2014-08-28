package com.choicemaker.cmit.io.blocking.automated.offline.server;

import static com.choicemaker.cmit.io.blocking.automated.offline.server.BatchDeploymentUtils.DEPENDENCIES_POM;
import static com.choicemaker.cmit.io.blocking.automated.offline.server.BatchDeploymentUtils.EJB_MAVEN_COORDINATES;
import static com.choicemaker.cmit.utils.DeploymentUtils.PERSISTENCE_CONFIGURATION;
import static com.choicemaker.cmit.utils.DeploymentUtils.PROJECT_POM;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.choicemaker.cm.io.blocking.automated.offline.server.StatusLogBean;
import com.choicemaker.cmit.utils.DeploymentUtils;

@RunWith(Arquillian.class)
public class StatusLogBeanTest {

	public final int MAX_TEST_ITERATIONS = 10;

	@Deployment
	public static EnterpriseArchive createEarArchive() {
		List<Class<?>> testClasses = new ArrayList<>();
		testClasses.add(StatusLogBeanTest.class);
		testClasses.add(StatusLogController.class);
		JavaArchive ejb1 =
			DeploymentUtils.createEjbJar(PROJECT_POM, EJB_MAVEN_COORDINATES,
					testClasses, PERSISTENCE_CONFIGURATION);

		File[] deps = DeploymentUtils.createTestDependencies(DEPENDENCIES_POM);

		EnterpriseArchive retVal = DeploymentUtils.createEarArchive(ejb1, deps);
		return retVal;
	}

	private final Random random = new Random(new Date().getTime());

	@EJB
	protected StatusLogController controller;

	@Test
	public void testBatchParametersController() {
		assertTrue(controller != null);
	}

	@Test
	public void testConstruction() {
		StatusLogBean statusEntry = new StatusLogBean();
		assertTrue(0 == statusEntry.getJobId());
	}

	@Test
	public void testPersistFindRemove() {
		// Count existing jobs
		final int initialCount = controller.findAll().size();

		// Create a statusEntry
		StatusLogBean statusEntry = new StatusLogBean();
		assertTrue(statusEntry.getJobId() == 0);

		// Save the statusEntry
		controller.save(statusEntry);
		assertTrue(statusEntry.getJobId() != 0);

		// Find the statusEntry
		StatusLogBean statusEntry2 = controller.find(statusEntry.getJobId());
		assertTrue(statusEntry.getJobId() == statusEntry2.getJobId());
		assertTrue(statusEntry.equals(statusEntry2));

		// Delete the statusEntry
		controller.delete(statusEntry2);
		StatusLogBean statusEntry3 = controller.find(statusEntry.getJobId());
		assertTrue(statusEntry3 == null);

		// Check that the number of existing jobs equals the initial count
		assertTrue(initialCount == controller.findAll().size());
	}

	@Test
	public void testMerge() {
		// Count existing jobs
		final int initialCount = controller.findAll().size();

		StatusLogBean statusEntry = new StatusLogBean();
		controller.save(statusEntry);
		assertTrue(statusEntry.getJobId() != 0);
		final long id = statusEntry.getJobId();
		controller.detach(statusEntry);

		assertTrue(statusEntry.getInfo() == null);
		final String randomInfo = new Date().toString();
		statusEntry.setInfo(randomInfo);
		controller.save(statusEntry);

		statusEntry = null;
		StatusLogBean statusEntry2 = controller.find(id);
		assertTrue(id == statusEntry2.getJobId());
		assertTrue(randomInfo == statusEntry2.getInfo());
		controller.delete(statusEntry2);

		assertTrue(initialCount == controller.findAll().size());
	}

	@Test
	public void testEqualsHashCode() {
		// Create two generic parameter sets and verify equality
		StatusLogBean statusEntry1 = new StatusLogBean();
		StatusLogBean statusEntry2 = new StatusLogBean();
		assertTrue(statusEntry1.equals(statusEntry2));
		assertTrue(statusEntry1.hashCode() == statusEntry2.hashCode());

		// Change something on one of the parameter sets and verify inequality
		statusEntry1.setInfo(new Date().toString());
		assertTrue(!statusEntry1.getInfo().equals(statusEntry2.getInfo()));
		assertTrue(!statusEntry1.equals(statusEntry2));
		assertTrue(statusEntry1.hashCode() != statusEntry2.hashCode());

		// Restore equality
		statusEntry2.setInfo(statusEntry1.getInfo());
		assertTrue(statusEntry1.equals(statusEntry2));
		assertTrue(statusEntry1.hashCode() == statusEntry2.hashCode());

		// Verify non-persistent status is not equal to persistent status
		statusEntry1 = controller.save(statusEntry1);
		assertTrue(!statusEntry1.equals(statusEntry2));
		assertTrue(statusEntry1.hashCode() != statusEntry2.hashCode());

		// Verify that equality of persisted parameter sets is set only by
		// persistence id
		controller.detach(statusEntry1);
		statusEntry2 = controller.find(statusEntry1.getJobId());
		controller.detach(statusEntry2);
		assertTrue(statusEntry1.equals(statusEntry2));
		assertTrue(statusEntry1.hashCode() == statusEntry2.hashCode());

		statusEntry1.setInfo("nonsense");
		assertTrue(!statusEntry1.getInfo().equals(statusEntry2.getInfo()));
		assertTrue(statusEntry1.equals(statusEntry2));
		assertTrue(statusEntry1.hashCode() == statusEntry2.hashCode());
	}

	@Test
	public void testJobType() {
		// Count existing jobs
		final int initialCount = controller.findAll().size();

		// Create a statusEntry and set a value
		StatusLogBean statusEntry = new StatusLogBean();
		final String v1 = new Date().toString();
		statusEntry.setJobType(v1);

		// Save the statusEntry
		final long id1 = controller.save(statusEntry).getJobId();
		assertTrue(initialCount + 1 == controller.findAll().size());
		statusEntry = null;

		// Retrieve the statusEntry
		statusEntry = controller.find(id1);

		// Check the value
		final String v2 = statusEntry.getJobType();
		assertTrue(v1.equals(v2));

		// Remove the statusEntry and the number of remaining jobs
		controller.delete(statusEntry);
		assertTrue(initialCount == controller.findAll().size());
	}

	@Test
	public void testStatusId() {
		// Count existing jobs
		final int initialCount = controller.findAll().size();

		// Create a statusEntry and set a value
		StatusLogBean statusEntry = new StatusLogBean();
		final int v1 = random.nextInt();
		statusEntry.setStatusId(v1);

		// Save the statusEntry
		final long id1 = controller.save(statusEntry).getJobId();
		assertTrue(initialCount + 1 == controller.findAll().size());
		statusEntry = null;

		// Get the statusEntry
		statusEntry = controller.find(id1);

		// Check the value
		final int v2 = statusEntry.getStatusId();
		assertTrue(v1 == v2);

		// Remove the statusEntry and the number of remaining jobs
		controller.delete(statusEntry);
		assertTrue(initialCount == controller.findAll().size());
	}

	@Test
	public void testVersion() {
		// Count existing jobs
		final int initialCount = controller.findAll().size();

		// Create a statusEntry and set a value
		StatusLogBean statusEntry = new StatusLogBean();
		final int v1 = random.nextInt();
		statusEntry.setVersion(v1);

		// Save the statusEntry
		final long id1 = controller.save(statusEntry).getJobId();
		assertTrue(initialCount + 1 == controller.findAll().size());
		statusEntry = null;

		// Get the statusEntry
		statusEntry = controller.find(id1);

		// Check the value
		final int v2 = statusEntry.getVersion();
		assertTrue(v1 == v2);

		// Remove the statusEntry and the number of remaining jobs
		controller.delete(statusEntry);
		assertTrue(initialCount == controller.findAll().size());
	}

	@Test
	public void testInfo() {
		// Count existing jobs
		final int initialCount = controller.findAll().size();

		// Create a statusEntry and set a value
		StatusLogBean statusEntry = new StatusLogBean();
		final String v1 = new Date().toString();
		statusEntry.setInfo(v1);

		// Save the statusEntry
		final long id1 = controller.save(statusEntry).getJobId();
		assertTrue(initialCount + 1 == controller.findAll().size());
		statusEntry = null;

		// Retrieve the statusEntry
		statusEntry = controller.find(id1);

		// Check the value
		final String v2 = statusEntry.getInfo();
		assertTrue(v1.equals(v2));

		// Remove the statusEntry and the number of remaining jobs
		controller.delete(statusEntry);
		assertTrue(initialCount == controller.findAll().size());
	}

}