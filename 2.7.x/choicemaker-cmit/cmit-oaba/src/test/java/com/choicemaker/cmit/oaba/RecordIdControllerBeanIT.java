package com.choicemaker.cmit.oaba;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
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

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.OperationalPropertyController;
import com.choicemaker.cm.batch.api.EventPersistenceManager;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.api.ImmutableRecordIdTranslatorLocal;
import com.choicemaker.cm.oaba.api.OabaJobManager;
import com.choicemaker.cm.oaba.api.OabaParametersController;
import com.choicemaker.cm.oaba.api.OabaService;
import com.choicemaker.cm.oaba.api.OabaSettingsController;
import com.choicemaker.cm.oaba.api.RecordIdController;
import com.choicemaker.cm.oaba.api.RecordSourceController;
import com.choicemaker.cm.oaba.api.ServerConfigurationController;
import com.choicemaker.cm.oaba.api.ServerConfigurationException;
import com.choicemaker.cm.oaba.api.ImmutableRecordIdTranslatorLocal;
import com.choicemaker.cm.oaba.core.IRecordIdSink;
import com.choicemaker.cm.oaba.core.IRecordIdSource;
import com.choicemaker.cm.oaba.core.ImmutableRecordIdTranslator;
import com.choicemaker.cm.oaba.core.MutableRecordIdTranslator;
import com.choicemaker.cm.oaba.ejb.RecordIdSink;
import com.choicemaker.cm.oaba.ejb.RecordIdTranslatorIT;
import com.choicemaker.cmit.oaba.util.OabaDeploymentUtils;
import com.choicemaker.cmit.utils.j2ee.OabaTestUtils;
import com.choicemaker.cmit.utils.j2ee.TestEntityCounts;
import com.choicemaker.cmit.utils.j2ee.WellKnownTestConfiguration;
import com.choicemaker.e2.ejb.EjbPlatform;

@RunWith(Arquillian.class)
public class RecordIdControllerBeanIT {

	private static final Logger logger = Logger
			.getLogger(RecordIdControllerBeanIT.class.getName());

	public static final boolean TESTS_AS_EJB_MODULE = false;

	private final static String LOG_SOURCE = RecordIdControllerBeanIT.class
			.getSimpleName();

	@Deployment
	public static EnterpriseArchive createEarArchive() {
		Class<?>[] removedClasses = null;
		return OabaDeploymentUtils.createEarArchive(removedClasses,
				TESTS_AS_EJB_MODULE);
	}

	public static final int MAX_SOURCE_COUNT_FUNCTIONAL = 100;

	public static final int MAX_SINK_COUNT_FUNCTIONAL = 1000000;

	public static final int MAX_SOURCE_COUNT_PERFORMANCE = 100;

	public static final int MAX_SINK_COUNT_PERFORMANCE = 1000000;

	protected static IRecordIdSink createRecordIdSink(BatchJob job)
			throws IOException {
		final String prefix = "SINK";
		final String suffix = "dat";
		final File dir = job.getWorkingDirectory();
		final File tmp = File.createTempFile(prefix, suffix, dir);
		IRecordIdSink retVal = new RecordIdSink(tmp.getAbsolutePath());
		return retVal;
	}

	public static IRecordIdSource<?> createRecordIntegerIdSource(int count,
			BatchJob job) {
		throw new Error("not yet implemented");
	}

	public static IRecordIdSource<Long> createRecordLongIdSource(int count,
			BatchJob job) {
		throw new Error("not yet implemented");
	}

	public static IRecordIdSource<String> createRecordStringIdSource(int count,
			BatchJob job) {
		throw new Error("not yet implemented");
	}

	public static <T extends Comparable<T>> IRecordIdSource<T> createRecordIdSource(
			int count, BatchJob job) {
		throw new Error("not yet implemented");
	}

	public static final Random random = new Random();

	@EJB
	private EjbPlatform e2service;

	@Resource
	UserTransaction utx;

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

	@EJB
	private OabaJobManager oabaManager;

	@EJB(beanName = "OabaJobControllerBean")
	private OabaJobManager jobManager;

	@EJB
	private OabaParametersController paramsController;

	@EJB
	private OabaSettingsController oabaSettingsController;

	@EJB (beanName = "OabaEventManager")
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

	TestEntityCounts te;

	public static List<Integer> createIntegerRecordIds(final int count) {
		Set<Integer> guard = new LinkedHashSet<>();
		while (guard.size() < count) {
			int recordId = random.nextInt(Integer.MAX_VALUE);
			guard.add(recordId);
		}
		assertTrue(guard.size() == count);
		List<Integer> recordIds = new ArrayList<>();
		recordIds.addAll(guard);
		return Collections.unmodifiableList(recordIds);
	}

	public static List<Long> createLongRecordIds(final int count) {
		Set<Long> guard = new LinkedHashSet<>();
		while (guard.size() < count) {
			long recordId = random.nextLong();
			guard.add(recordId);
		}
		assertTrue(guard.size() == count);
		List<Long> recordIds = new ArrayList<>();
		recordIds.addAll(guard);
		return Collections.unmodifiableList(recordIds);
	}

	public static List<String> createStringRecordIds(final int count) {
		Set<String> guard = new LinkedHashSet<>();
		while (guard.size() < count) {
			String recordId = UUID.randomUUID().toString();
			guard.add(recordId);
		}
		assertTrue(guard.size() == count);
		List<String> recordIds = new ArrayList<>();
		recordIds.addAll(guard);
		return Collections.unmodifiableList(recordIds);
	}

	WellKnownTestConfiguration getTestConfiguration() {
		return RecordIdTranslatorIT.getTestConfiguration(e2service);
	}

	BatchJob createPersistentOabaJob(String methodName)
			throws ServerConfigurationException {
		logger.entering(LOG_SOURCE, methodName);
		WellKnownTestConfiguration c = getTestConfiguration();
		return OabaTestUtils.createPersistentOabaJob(c, e2service,
				rsController, oabaSettingsController, serverController,
				jobManager, te);
	}

	MutableRecordIdTranslator<?> createEmptyTranslator(String tag)
			throws BlockingException, ServerConfigurationException {
		BatchJob job = createPersistentOabaJob(tag);
		MutableRecordIdTranslator<?> retVal =
			ridController.createMutableRecordIdTranslator(job);
		return retVal;
	}

	@Before
	public void setUp() throws Exception {
		te =
			new TestEntityCounts(logger, oabaManager, paramsController,
					oabaSettingsController, serverController,
					eventManager, opPropController, rsController,
					ridController);
	}

	public void checkCounts() {
		if (te != null) {
			te.checkCounts(logger, em, utx, oabaManager, paramsController,
					oabaSettingsController, serverController,
					eventManager, opPropController, rsController,
					ridController);
		} else {
			throw new Error("Counts not initialized");
		}
	}

	// @Test
	// public void testGetImmutableRecordIdTranslator() {
	// fail("Not yet implemented");
	// checkCounts();
	// }

	@Test
	public void testCreateMutableRecordIdTranslator() throws BlockingException,
			ServerConfigurationException {
		final String METHOD = "testCreateMutableRecordIdTranslator";
		MutableRecordIdTranslator<?> rit = createEmptyTranslator(METHOD);
		assertTrue(rit != null);
		checkCounts();
	}

	@Test
	public void testIntegerTranslatorPersistence() throws BlockingException,
			ServerConfigurationException {
		final String METHOD = "testTranslatorPersistence";
		@SuppressWarnings("unchecked")
		final MutableRecordIdTranslator<Integer> mrit =
			(MutableRecordIdTranslator<Integer>) createEmptyTranslator(METHOD);
		assertTrue(mrit != null);

		List<Integer> recordIds =
			createIntegerRecordIds(MAX_SOURCE_COUNT_FUNCTIONAL);
		final int splitIndexInteger =
			random.nextInt(MAX_SOURCE_COUNT_FUNCTIONAL);
		int index = 0;
		mrit.open();
		for (Integer recordId : recordIds) {
			if (index == splitIndexInteger) {
				mrit.split();
			}
			int i = mrit.translate(recordId);
			assert (i == index);
			++index;
		}

		BatchJob job = createPersistentOabaJob(METHOD);
		final ImmutableRecordIdTranslator<Integer> irit =
			ridController.toImmutableTranslator(mrit);
		final ImmutableRecordIdTranslatorLocal<Integer> iritl =
			ridController.save(job, irit);
		te.add(iritl);
		for (int i = 0; i < recordIds.size(); i++) {
			int expectedId = recordIds.get(i);
			int computedId = (Integer) irit.reverseLookup(i);
			assertTrue(expectedId == computedId);
		}

		checkCounts();
	}

	@Test
	public void testLongTranslatorPersistence() throws BlockingException,
			ServerConfigurationException {
		final String METHOD = "testTranslatorPersistence";
		@SuppressWarnings("unchecked")
		final MutableRecordIdTranslator<Long> mrit =
			(MutableRecordIdTranslator<Long>) createEmptyTranslator(METHOD);
		assertTrue(mrit != null);

		List<Long> recordIds = createLongRecordIds(MAX_SOURCE_COUNT_FUNCTIONAL);
		final int splitIndexLong = random.nextInt(MAX_SOURCE_COUNT_FUNCTIONAL);
		int index = 0;
		mrit.open();
		for (Long recordId : recordIds) {
			if (index == splitIndexLong) {
				mrit.split();
			}
			int i = mrit.translate(recordId);
			assert (i == index);
			++index;
		}

		BatchJob job = createPersistentOabaJob(METHOD);
		final ImmutableRecordIdTranslator<Long> irit =
			ridController.toImmutableTranslator(mrit);
		final ImmutableRecordIdTranslatorLocal<Long> iritl =
			ridController.save(job, irit);
		te.add(iritl);
		for (int i = 0; i < recordIds.size(); i++) {
			long expectedId = recordIds.get(i);
			long computedId = (Long) irit.reverseLookup(i);
			assertTrue(expectedId == computedId);
		}

		checkCounts();
	}

	@Test
	public void testStringTranslatorPersistence() throws BlockingException,
			ServerConfigurationException {
		final String METHOD = "testTranslatorPersistence";
		@SuppressWarnings("unchecked")
		final MutableRecordIdTranslator<String> mrit =
			(MutableRecordIdTranslator<String>) createEmptyTranslator(METHOD);
		assertTrue(mrit != null);

		List<String> recordIds =
			createStringRecordIds(MAX_SOURCE_COUNT_FUNCTIONAL);
		final int splitIndexString =
			random.nextInt(MAX_SOURCE_COUNT_FUNCTIONAL);
		int index = 0;
		mrit.open();
		for (String recordId : recordIds) {
			if (index == splitIndexString) {
				mrit.split();
			}
			int i = mrit.translate(recordId);
			assert (i == index);
			++index;
		}

		BatchJob job = createPersistentOabaJob(METHOD);
		final ImmutableRecordIdTranslator<String> irit =
			ridController.toImmutableTranslator(mrit);
		final ImmutableRecordIdTranslatorLocal<String> iritl =
			ridController.save(job, irit);
		te.add(iritl);
		for (int i = 0; i < recordIds.size(); i++) {
			String expectedId = recordIds.get(i);
			String computedId = (String) irit.reverseLookup(i);
			assertTrue(expectedId.equals(computedId));
		}

		checkCounts();
	}

	@Test
	public void testToImmutableTranslator() throws BlockingException,
			ServerConfigurationException {
		final String METHOD = "testToImmutableTranslator";
		@SuppressWarnings("unchecked")
		final MutableRecordIdTranslator<String> mrit =
			(MutableRecordIdTranslator<String>) createEmptyTranslator(METHOD);
		assertTrue(mrit != null);

		List<String> recordIds =
			createStringRecordIds(MAX_SOURCE_COUNT_FUNCTIONAL);
		final int splitIndexString =
			random.nextInt(MAX_SOURCE_COUNT_FUNCTIONAL);
		int index = 0;
		mrit.open();
		for (String recordId : recordIds) {
			if (index == splitIndexString) {
				mrit.split();
			}
			int i = mrit.translate(recordId);
			assert (i == index);
			++index;
		}

		BatchJob job = createPersistentOabaJob(METHOD);
		final ImmutableRecordIdTranslator<String> irit =
			ridController.toImmutableTranslator(mrit);
		final ImmutableRecordIdTranslatorLocal<String> iritl =
			ridController.save(job, irit);
		te.add(iritl);
		for (int i = 0; i < recordIds.size(); i++) {
			String expectedId = recordIds.get(i);
			String computedId = (String) irit.reverseLookup(i);
			assertTrue(expectedId.equals(computedId));
		}

		checkCounts();
	}

}
