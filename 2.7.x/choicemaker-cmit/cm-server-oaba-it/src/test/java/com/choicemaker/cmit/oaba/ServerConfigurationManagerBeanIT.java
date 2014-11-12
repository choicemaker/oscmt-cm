package com.choicemaker.cmit.oaba;

import static com.choicemaker.cmit.oaba.util.OabaConstants.CURRENT_MAVEN_COORDINATES;
import static com.choicemaker.cmit.oaba.util.OabaConstants.PERSISTENCE_CONFIGURATION;
import static com.choicemaker.cmit.utils.DeploymentUtils.DEFAULT_HAS_BEANS;
import static com.choicemaker.cmit.utils.DeploymentUtils.DEFAULT_MODULE_NAME;
import static com.choicemaker.cmit.utils.DeploymentUtils.DEFAULT_POM_FILE;
import static com.choicemaker.cmit.utils.DeploymentUtils.DEFAULT_TEST_CLASSES_PATH;
import static com.choicemaker.cmit.utils.DeploymentUtils.createEAR;
import static com.choicemaker.cmit.utils.DeploymentUtils.createJAR;
import static com.choicemaker.cmit.utils.DeploymentUtils.resolveDependencies;
import static com.choicemaker.cmit.utils.DeploymentUtils.resolvePom;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.DuplicateServerConfigurationNameException;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.MutableServerConfiguration;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.ServerConfiguration;
import com.choicemaker.cm.io.blocking.automated.offline.server.ejb.ServerConfigurationManager;
import com.choicemaker.cm.io.blocking.automated.offline.server.impl.DefaultServerConfigurationBean;
import com.choicemaker.cm.io.blocking.automated.offline.server.impl.ServerConfigurationBean;
import com.choicemaker.cm.io.blocking.automated.offline.server.impl.ServerConfigurationManagerBean;
import com.choicemaker.cmit.utils.TestEntities;

@RunWith(Arquillian.class)
public class ServerConfigurationManagerBeanIT {

	public static final boolean TESTS_AS_EJB_MODULE = true;

	public final int MAX_TEST_ITERATIONS = 10;

	public static final String REGEX_EJB_DEPENDENCIES =
		"com.choicemaker.cm.io.blocking.automated.offline.server.*.jar"
				+ "|com.choicemaker.e2.ejb.*.jar";

	/**
	 * Creates an EAR deployment.
	 */
	@Deployment
	public static EnterpriseArchive createEarArchive() {
		PomEquippedResolveStage pom = resolvePom(DEFAULT_POM_FILE);

		File[] libs = resolveDependencies(pom);

		// Filter the OABA server and E2Plaform JARs from the dependencies;
		// they will be added as modules.
		final Pattern p = Pattern.compile(REGEX_EJB_DEPENDENCIES);
		Set<File> ejbJARs = new LinkedHashSet<>();
		List<File> filteredLibs = new LinkedList<>();
		for (File lib : libs) {
			String name = lib.getName();
			Matcher m = p.matcher(name);
			if (m.matches()) {
				boolean isAdded = ejbJARs.add(lib);
				if (!isAdded) {
					String path = lib.getAbsolutePath();
					throw new RuntimeException("failed to add (duplicate?): "
							+ path);
				}
			} else {
				filteredLibs.add(lib);
			}
		}
		File[] libs2 = filteredLibs.toArray(new File[filteredLibs.size()]);

		JavaArchive tests =
			createJAR(pom, CURRENT_MAVEN_COORDINATES, DEFAULT_MODULE_NAME,
					DEFAULT_TEST_CLASSES_PATH, PERSISTENCE_CONFIGURATION,
					DEFAULT_HAS_BEANS);
		EnterpriseArchive retVal = createEAR(tests, libs2, TESTS_AS_EJB_MODULE);

		// Filter the targeted paths from the EJB JARs
		for (File ejb : ejbJARs) {
			JavaArchive module =
				ShrinkWrap.createFromZipFile(JavaArchive.class, ejb);
			retVal.addAsModule(module);
		}

		return retVal;
	}

	public static final String LOG_SOURCE =
		ServerConfigurationManagerBeanIT.class.getSimpleName();

	private static final Logger logger = Logger
			.getLogger(ServerConfigurationManagerBeanIT.class.getName());

	/**
	 * Workaround for logger.entering(String,String) not showing up in JBOSS
	 * server log
	 */
	private static void logEntering(String method) {
		logger.info("Entering " + LOG_SOURCE + "." + method);
	}

	/**
	 * Workaround for logger.exiting(String,String) not showing up in JBOSS
	 * server log
	 */
	private static void logExiting(String method) {
		logger.info("Exiting " + LOG_SOURCE + "." + method);
	}

	@Resource
	UserTransaction utx;

	@PersistenceContext(unitName = "oaba")
	EntityManager em;

	@EJB
	protected ServerConfigurationManager scm;

	private int initialServerConfigCount;
	private int initialDefaultServerConfigCount;
	private boolean setupOK;

	@Before
	public void setUp() throws Exception {
		final String METHOD = "setUp";
		logEntering(METHOD);
		setupOK = true;
		try {
			initialServerConfigCount = scm.findAllServerConfigurations().size();
			initialDefaultServerConfigCount = scm.findAllDefaultServerConfigurations().size();
		} catch (Exception x) {
			logger.severe(x.toString());
			setupOK = false;
		}
		logExiting(METHOD);
	}

	@After
	public void tearDown() throws Exception {
		final String METHOD = "tearDown";
		logEntering(METHOD);
		try {

			int finalServerConfigCount =
				scm.findAllServerConfigurations().size();
			String alert = "initialServerConfigCount != finalServerConfigCount";
			assertTrue(alert,
					initialServerConfigCount == finalServerConfigCount);

			int finalDefaultServerConfigCount =
					scm.findAllDefaultServerConfigurations().size();
				alert = "initialDefaultServerConfigCount != finalDefaultServerConfigCount";
				assertTrue(alert,
						initialDefaultServerConfigCount == finalDefaultServerConfigCount);

		} catch (Exception x) {
			logger.severe(x.toString());
		} catch (AssertionError x) {
			logger.severe(x.toString());
		}
		logExiting(METHOD);
	}

	@Test
	@InSequence(1)
	public void testEntityManager() {
		assertTrue(setupOK);
		assertTrue(em != null);
	}

	@Test
	@InSequence(1)
	public void testUserTransaction() {
		assertTrue(setupOK);
		assertTrue(utx != null);
	}

	@Test
	@InSequence(1)
	public void testServiceConfigurationManager() {
		assertTrue(setupOK);
		assertTrue(scm != null);
	}

	@Test
	@InSequence(10)
	public void testComputeAvailableProcessors() {
		assertTrue(setupOK);
		int count = ServerConfigurationManagerBean.computeAvailableProcessors();
		assertTrue(count > -1);
	}

	@Test
	@InSequence(10)
	public void testComputeHostName() {
		assertTrue(setupOK);
		String name = ServerConfigurationManagerBean.computeHostName();
		assertTrue(name != null && !name.trim().isEmpty());
	}

	@Test
	@InSequence(10)
	public void testComputeUniqueGenericName() {
		assertTrue(setupOK);
		Set<String> uniqueNames = new HashSet<>();
		for (int i = 0; i < MAX_TEST_ITERATIONS; i++) {
			String name =
				ServerConfigurationManagerBean.computeUniqueGenericName();
			uniqueNames.add(name);
		}
		assertTrue(uniqueNames.size() == MAX_TEST_ITERATIONS);
	}

	@Test
	@InSequence(20)
	public void testComputeGenericConfiguration() {
		assertTrue(setupOK);
		MutableServerConfiguration msc = scm.computeGenericConfiguration();
		assertTrue(msc.getId() == ServerConfigurationBean.NON_PERSISTENT_ID);
		assertTrue(msc.getHostName().equals(
				ServerConfigurationManagerBean.computeHostName()));
		assertTrue(msc.getMaxChoiceMakerThreads() == ServerConfigurationManagerBean
				.computeAvailableProcessors());
		assertTrue(msc.getMaxOabaChunkFileCount() == ServerConfigurationManagerBean.DEFAULT_MAX_CHUNK_COUNT);
		assertTrue(msc.getMaxOabaChunkFileRecords() == ServerConfigurationManagerBean.DEFAULT_MAX_CHUNK_SIZE);
	}

	@Test
	@InSequence(20)
	public void testCloneServerConfiguration() {
		assertTrue(setupOK);
		MutableServerConfiguration msc = scm.computeGenericConfiguration();
		MutableServerConfiguration msc2 = scm.clone(msc);

		assertTrue(!msc.getName().equals(msc2.getName()));

		assertTrue(msc.getHostName().equals(msc2.getHostName()));
		assertTrue(msc.getMaxChoiceMakerThreads() == msc2
				.getMaxChoiceMakerThreads());
		assertTrue(msc.getMaxOabaChunkFileCount() == msc2
				.getMaxOabaChunkFileCount());
		assertTrue(msc.getMaxOabaChunkFileRecords() == msc2
				.getMaxOabaChunkFileRecords());
	}

	@Test
	@InSequence(50)
	public void testPersistFindRemove() {
		assertTrue(setupOK);
		final String METHOD = "testPersistFindRemove";
		logEntering(METHOD);
		final TestEntities te = new TestEntities();

		// Create a configuration
		final MutableServerConfiguration msc =
			scm.computeGenericConfiguration();
		assertTrue(msc.getId() == ServerConfigurationBean.NON_PERSISTENT_ID);
		assertTrue(!ServerConfigurationBean.isPersistent(msc));

		// Save the configuration
		long id = ServerConfigurationBean.NON_PERSISTENT_ID;
		try {
			ServerConfiguration sc = null;
			sc = scm.save(msc);
			te.add(sc);
			assertTrue(sc != null);
			id = sc.getId();
			assertTrue(ServerConfigurationBean.isPersistent(sc));

			assertTrue(ServerConfigurationBean.equalsIgnoreIdUuid(msc, sc));
		} catch (DuplicateServerConfigurationNameException e) {
			fail(e.toString());
		}
		assertTrue(id != ServerConfigurationBean.NON_PERSISTENT_ID);
		final long scID = id;

		// Find the configuration
		ServerConfiguration sc = null;
		sc = scm.find(scID);
		assertTrue(sc != null);
		assertTrue(sc.getId() == scID);
		assertTrue(ServerConfigurationBean.equalsIgnoreIdUuid(msc, sc));

		try {
			te.removePersistentObjects(em, utx);
		} catch (Exception x) {
			logger.severe(x.toString());
			fail(x.toString());
		}
		logExiting(METHOD);
	}

	@Test
	@InSequence(100)
	public void testFindAllServerConfigurations() {
		assertTrue(setupOK);
		final String METHOD = "testFindAllServerConfigurations";
		logEntering(METHOD);
		final TestEntities te = new TestEntities();

		List<Long> scIds = new LinkedList<>();
		for (int i = 0; i < MAX_TEST_ITERATIONS; i++) {
			// Create and save a server configuration
			MutableServerConfiguration msc = scm.computeGenericConfiguration();
			assertTrue(msc.getId() == 0);
			ServerConfiguration sc = null;
			try {
				sc = scm.save(msc);
			} catch (DuplicateServerConfigurationNameException e) {
				fail(e.toString());
			}
			te.add(sc);
			final long id = sc.getId();
			assertTrue(id != 0);
			scIds.add(id);
		}

		// Verify the number of server configurations has increased
		List<ServerConfiguration> serverConfigs =
			scm.findAllServerConfigurations();
		assertTrue(serverConfigs != null);

		// Find the server configurations
		boolean isFound = false;
		for (long scId : scIds) {
			for (ServerConfiguration sc : serverConfigs) {
				if (scId == sc.getId()) {
					isFound = true;
					break;
				}
			}
			assertTrue(isFound);
		}

		try {
			te.removePersistentObjects(em, utx);
		} catch (Exception x) {
			logger.severe(x.toString());
			fail(x.toString());
		}
		logExiting(METHOD);
	}

	// @Test
	// @InSequence(100)
	// public void testFindServerConfigurationsByHostNameString() {
	// assertTrue(setupOK);
	// fail("Not yet implemented");
	//
	// }
	//
	// @Test
	// @InSequence(100)
	// public void testFindServerConfigurationsByHostNameStringBoolean() {
	// assertTrue(setupOK);
	// fail("Not yet implemented");
	//
	// }
	//
	// @Test
	// @InSequence(200)
	// public void testSetDefaultConfiguration() {
	// assertTrue(setupOK);
	// fail("Not yet implemented");
	//
	// }
	//
	@Test
	@InSequence(200)
	public void testSetGetDefaultConfigurationString() {
		assertTrue(setupOK);
		final String METHOD = "testSetGetDefaultConfigurationString";
		logEntering(METHOD);
		final TestEntities te = new TestEntities();

		try {
			// Check that a default configuration is returned for a fake host,
			// even though no configurations exist
			final String fakeHost1 = UUID.randomUUID().toString();
			final boolean computeFallback = true;
			final ServerConfiguration sc1 =
				scm.getDefaultConfiguration(fakeHost1, computeFallback);
			assertTrue(sc1 != null);
			assertTrue(ServerConfigurationBean.isPersistent(sc1));
			te.add(sc1);
			te.add(new DefaultServerConfigurationBean(fakeHost1, sc1.getId()));

			// Verify that the no-param method works like the
			// getDefaultConfiguration(fakeHost, true) method
			final ServerConfiguration sc2 =
				scm.getDefaultConfiguration(fakeHost1);

			// Verify that the two defaults are the same persistent object
			assertTrue(sc1.getId() == sc2.getId());
			assertTrue(sc1.getUUID().equals(sc2.getUUID()));
			assertTrue(ServerConfigurationBean.equalsIgnoreIdUuid(sc1, sc2));

			// Verify that one persistent configuration now exists for the fake
			// host
			final boolean strictNoWildcards = true;
			List<ServerConfiguration> configs =
				scm.findServerConfigurationsByHostName(fakeHost1,
						strictNoWildcards);
			assertTrue(configs.size() == 1);

			// Create and save a server configuration for another fake host
			final String fakeHost3 = UUID.randomUUID().toString();
			final MutableServerConfiguration msc3 =
				scm.computeGenericConfiguration();
			msc3.setHostName(fakeHost3);
			assertTrue(msc3.getId() == 0);
			final ServerConfiguration sc3 = scm.save(msc3);
			te.add(sc3);
			final long scId = sc3.getId();
			assertTrue(scId != 0);

			// Verify that the lone, persistent configuration is now the default
			final boolean doNotComputeFallback = false;
			final ServerConfiguration sc4 =
				scm.getDefaultConfiguration(fakeHost3, doNotComputeFallback);
			assertTrue(sc4 != null);
			assertTrue(sc3.getId() == sc4.getId());
			assertTrue(sc3.getUUID().equals(sc4.getUUID()));
			assertTrue(ServerConfigurationBean.equalsIgnoreIdUuid(sc3, sc4));

			// Add another server configuration and verify that with two
			// persistent configurations, neither of which has been specified
			// as the default, that no default exists
			MutableServerConfiguration msc5 = scm.computeGenericConfiguration();
			msc5.setHostName(fakeHost3);
			assertTrue(msc5.getId() == 0);
			final ServerConfiguration sc5 = scm.save(msc5);
			te.add(sc5);
			configs =
				scm.findServerConfigurationsByHostName(fakeHost3,
						strictNoWildcards);
			assertTrue(configs.size() == 2);
			final ServerConfiguration sc6 =
				scm.getDefaultConfiguration(fakeHost3, doNotComputeFallback);
			assertTrue(sc6 == null);

			// Set the first configuration as the default and retrieve it
			scm.setDefaultConfiguration(fakeHost3, sc3);
			te.add(new DefaultServerConfigurationBean(fakeHost3, sc3.getId()));
			final ServerConfiguration sc7 =
				scm.getDefaultConfiguration(fakeHost3, computeFallback);
			assertTrue(sc7 != null);
			assertTrue(sc3.getId() == sc7.getId());
			assertTrue(sc3.getUUID().equals(sc7.getUUID()));
			assertTrue(ServerConfigurationBean.equalsIgnoreIdUuid(sc3, sc7));

		} catch (DuplicateServerConfigurationNameException e) {
			fail(e.toString());
		} finally {
			try {
				te.removePersistentObjects(em, utx);
			} catch (Exception x) {
				logger.severe(x.toString());
				fail(x.toString());
			}
		}

		logExiting(METHOD);
	}

	// @Test
	// @InSequence(200)
	// public void testGetDefaultConfigurationStringBoolean() {
	// assertTrue(setupOK);
	// fail("Not yet implemented");
	//
	// }

}
