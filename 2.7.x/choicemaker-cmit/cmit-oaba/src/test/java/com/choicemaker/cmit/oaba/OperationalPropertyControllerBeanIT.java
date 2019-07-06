package com.choicemaker.cmit.oaba;

import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.choicemaker.cm.args.PersistentObject;
import com.choicemaker.cm.args.ServerConfiguration;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.EventPersistenceManager;
import com.choicemaker.cm.batch.api.OperationalProperty;
import com.choicemaker.cm.batch.api.OperationalPropertyController;
import com.choicemaker.cm.oaba.api.OabaJobManager;
import com.choicemaker.cm.oaba.api.OabaParametersController;
import com.choicemaker.cm.oaba.api.OabaService;
import com.choicemaker.cm.oaba.api.OabaSettingsController;
import com.choicemaker.cm.oaba.api.RecordIdController;
import com.choicemaker.cm.oaba.api.RecordSourceController;
import com.choicemaker.cm.oaba.api.ServerConfigurationController;
import com.choicemaker.cm.oaba.ejb.OabaJobEntity;
import com.choicemaker.cm.oaba.ejb.StartOabaMDB;
import com.choicemaker.cmit.oaba.util.OabaDeploymentUtils;
import com.choicemaker.cmit.utils.j2ee.BatchJobUtils;
import com.choicemaker.cmit.utils.j2ee.TestEntityCounts;

@RunWith(Arquillian.class)
public class OperationalPropertyControllerBeanIT {

	private static final Logger logger =
		Logger.getLogger(OperationalPropertyControllerBeanIT.class.getName());

	private static final boolean TESTS_AS_EJB_MODULE = false;

	private final static String LOG_SOURCE =
		OperationalPropertyControllerBeanIT.class.getSimpleName();

	public static final int MAX_SINGLE_LIMIT = 1000;

	public static final int MAX_TEST_ITERATIONS = 10;

	/**
	 * Creates an EAR deployment in which the OABA server JAR is missing the
	 * StartOabaMDB message bean. This allows another class to attach to the
	 * startQueue for testing.
	 */
	@Deployment
	public static EnterpriseArchive createEarArchive() {
		Class<?>[] removedClasses = {
				StartOabaMDB.class };
		return OabaDeploymentUtils.createEarArchive(removedClasses,
				TESTS_AS_EJB_MODULE);
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Resource
	UserTransaction utx;

	@PersistenceContext(unitName = "oaba")
	EntityManager em;

	@EJB(beanName = "OabaJobControllerBean")
	private OabaJobManager oabaManager;

	@EJB
	private OabaParametersController paramsController;

	@EJB
	private OabaSettingsController oabaSettingsController;

	@EJB
	private EventPersistenceManager eventManager;

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

	final protected Random random = new Random(new Date().getTime());

	public void checkCounts() {
		if (te != null) {
			te.checkCounts(logger, em, utx, oabaManager, paramsController,
					oabaSettingsController, serverController, eventManager,
					opPropController, rsController, ridController);
		} else {
			throw new Error("Counts not initialized");
		}
	}

	@Before
	public void setUp() throws Exception {
		te = new TestEntityCounts(logger, oabaManager, paramsController,
				oabaSettingsController, serverController, eventManager,
				opPropController, rsController, ridController);
	}

	protected OabaJobEntity createEphemeralOabaJobEntity(TestEntityCounts te,
			String tag, boolean isTag) {
		ServerConfiguration sc = getDefaultServerConfiguration();
		if (sc == null) {
			sc = serverController.computeGenericConfiguration();
		}
		return BatchJobUtils.createEphemeralOabaJobEntity(MAX_SINGLE_LIMIT, utx,
				sc, em, te, tag, isTag);
	}

	protected ServerConfiguration getDefaultServerConfiguration() {
		return BatchJobUtils.getDefaultServerConfiguration(serverController);
	}

	protected static String createPropertyName(int index) {
		return "TEST_PROPERTY_" + index;
	}

	@Test
	public void testSaveFindUpdateRemove() {
		final String METHOD = "testSaveFindUpdateRemove";
		logger.entering(LOG_SOURCE, METHOD);

		OabaJobEntity _job = createEphemeralOabaJobEntity(te, METHOD, true);
		assertTrue(_job != null);
		assertTrue(_job.getId() == PersistentObject.NONPERSISTENT_ID);
		BatchJob job = oabaManager.save(_job);
		final long jobId = job.getId();
		assertTrue(jobId != PersistentObject.NONPERSISTENT_ID);

		Set<String> _expectedNames = new LinkedHashSet<>();
		Set<String> _expectedValues = new LinkedHashSet<>();
		for (int i = 0; i < MAX_TEST_ITERATIONS; i++) {
			String pn = createPropertyName(i);
			_expectedNames.add(pn);
			String pv = String.valueOf(i);
			_expectedValues.add(pv);
			opPropController.setJobProperty(job, pn, pv);
		}
		final Set<String> expectedNames =
			Collections.unmodifiableSet(_expectedNames);
		assertTrue(expectedNames.size() == MAX_TEST_ITERATIONS);
		final Set<String> expectedValues =
			Collections.unmodifiableSet(_expectedValues);
		assertTrue(expectedValues.size() == MAX_TEST_ITERATIONS);

		List<OperationalProperty> ops =
			opPropController.findOperationalProperties(job);
		assertTrue(ops.size() == MAX_TEST_ITERATIONS);
		int count = 0;
		for (OperationalProperty op : ops) {
			++count;
			final long pid = op.getId();
			final String pn = op.getName();
			final String pv1 = op.getValue();

			assertTrue(jobId == op.getJobId());
			assertTrue(op.isPersistent());
			assertTrue(expectedNames.contains(pn));
			assertTrue(expectedValues.contains(pv1));

			final String pv2 =
				opPropController.getOperationalPropertyValue(job, pn);
			assert (pv2 != null);
			assertTrue(pv2.equals(pv1));

			final OperationalProperty op2 =
				opPropController.findOperationalProperty(pid);
			assertTrue(op2 != null);
			assertTrue(op2.equals(op));

			// Implicit test of update
			final String pv3 = String.valueOf(MAX_TEST_ITERATIONS + count);
			assertTrue(!pv3.equals(pv2));
			opPropController.setJobProperty(job, pn, pv3);
			final String pv4 =
				opPropController.getOperationalPropertyValue(job, pn);
			assert (pv4 != null);
			assertTrue(pv3.equals(pv4));

			opPropController.remove(op);
			int newSize =
				opPropController.findOperationalProperties(job).size();
			assertTrue(newSize == MAX_TEST_ITERATIONS - count);

			final OperationalProperty op5 =
				opPropController.findOperationalProperty(pid);
			assertTrue(op5 == null);
			final String pv5 =
				opPropController.getOperationalPropertyValue(job, pn);
			assertTrue(pv5 == null);
		}
		assertTrue(opPropController.findOperationalProperties(job).isEmpty());
		checkCounts();
	}

}
