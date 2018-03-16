package com.choicemaker.cm.oaba.ejb;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Random;
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

import com.choicemaker.cm.args.OabaLinkageType;
import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.OperationalPropertyController;
import com.choicemaker.cm.batch.api.EventPersistenceManager;
import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.api.OabaJobController;
import com.choicemaker.cm.oaba.api.OabaParametersController;
import com.choicemaker.cm.oaba.api.OabaService;
import com.choicemaker.cm.oaba.api.OabaSettingsController;
import com.choicemaker.cm.oaba.api.RecordIdController;
import com.choicemaker.cm.oaba.api.RecordSourceController;
import com.choicemaker.cm.oaba.api.ServerConfigurationController;
import com.choicemaker.cm.oaba.api.ServerConfigurationException;
import com.choicemaker.cm.oaba.core.IRecordIdSink;
import com.choicemaker.cm.oaba.core.IRecordIdSinkSourceFactory;
import com.choicemaker.cm.oaba.core.IRecordIdSource;
import com.choicemaker.cm.oaba.core.ImmutableRecordIdTranslator;
import com.choicemaker.cm.oaba.core.MutableRecordIdTranslator;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;
import com.choicemaker.cm.oaba.ejb.MutableRecordIdTranslatorImpl;
import com.choicemaker.cm.oaba.ejb.RecordIdSink;
import com.choicemaker.cm.oaba.ejb.RecordIdSource;
import com.choicemaker.cmit.oaba.util.OabaDeploymentUtils;
import com.choicemaker.cmit.oaba.util.OabaMdbTestProcedures;
import com.choicemaker.cmit.testconfigs.SimplePersonSqlServerTestConfiguration;
import com.choicemaker.cmit.utils.j2ee.OabaTestUtils;
import com.choicemaker.cmit.utils.j2ee.TestEntityCounts;
import com.choicemaker.cmit.utils.j2ee.WellKnownTestConfiguration;
import com.choicemaker.e2.CMPluginRegistry;
import com.choicemaker.e2.ejb.EjbPlatform;

/**
 * Similar to {@link com.choicemaker.cmit.oaba.RecordIdControllerBeanIT
 * RecordIdControllerBeanIT} but in the
 * <code>com.choicemaker.cm.oaba.server.impl</code>
 * package so that MutableRecordIdTranslatorImpl instances can be constructed
 * directly.
 */
@RunWith(Arquillian.class)
public class RecordIdTranslatorIT {

	public static final Logger logger = Logger
			.getLogger(RecordIdTranslatorIT.class.getName());

	public static final boolean TESTS_AS_EJB_MODULE = false;

	public final static String LOG_SOURCE = RecordIdTranslatorIT.class
			.getSimpleName();

	@Deployment
	public static EnterpriseArchive createEarArchive() {
		Class<?>[] removedClasses = null;
		return OabaDeploymentUtils.createEarArchive(removedClasses,
				TESTS_AS_EJB_MODULE);
	}

	public static final Random random = new Random();

	public static final String TRANSLATOR_CHARSET = "US-ASCII";

	public static final RECORD_ID_TYPE TRANSLATOR_ID_TYPE =
		RECORD_ID_TYPE.TYPE_STRING;

	public static final String TRANSLATOR1_RESOURCE = "/com/choicemaker/cmit/oaba/translator1.dat";

	public static final String TRANSLATOR2_RESOURCE = "/com/choicemaker/cmit/oaba/translator2.dat";

	protected static class CheezyFactory implements IRecordIdSinkSourceFactory {

		@Override
		public IRecordIdSink getNextSink() throws BlockingException {
			throw new Error("not implemented");
		}

		@SuppressWarnings("rawtypes")
		@Override
		public IRecordIdSource getNextSource() throws BlockingException {
			throw new Error("not implemented");
		}

		@Override
		public int getNumSink() {
			throw new Error("not implemented");
		}

		@Override
		public int getNumSource() {
			throw new Error("not implemented");
		}

		@Override
		public IRecordIdSource<String> getSource(IRecordIdSink sink)
				throws BlockingException {
			assertTrue(sink instanceof RecordIdSink);
			String path = sink.getInfo();
			IRecordIdSource<String> retVal = new RecordIdSource<String>(String.class, path);
			return retVal;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public IRecordIdSink getSink(IRecordIdSource source)
				throws BlockingException {
			assertTrue(source instanceof RecordIdSource);
			String path = source.getInfo();
			return new RecordIdSink(path);
		}

		@Override
		public void removeSink(IRecordIdSink sink) throws BlockingException {
			throw new Error("not implemented");
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void removeSource(IRecordIdSource source)
				throws BlockingException {
			throw new Error("not implemented");
		}

	}

	public static WellKnownTestConfiguration getTestConfiguration(
			EjbPlatform e2service) {
		final Class<SimplePersonSqlServerTestConfiguration> c =
			SimplePersonSqlServerTestConfiguration.class;
		final OabaLinkageType olt = OabaLinkageType.STAGING_TO_MASTER_LINKAGE;
		CMPluginRegistry r = e2service.getPluginRegistry();
		WellKnownTestConfiguration retVal =
			OabaMdbTestProcedures.createTestConfiguration(c, olt, r);
		return retVal;
	}

	@SuppressWarnings("unchecked")
	protected static IRecordIdSource<String> createRecordIdSource(BatchJob job,
			String resourceName) {
		IRecordIdSink sink =
			createRecordIdSink(job, resourceName, "SOURCE", "dat");
		IRecordIdSinkSourceFactory factory = new CheezyFactory();
		IRecordIdSource<String> retVal = null;
		try {
			retVal = factory.getSource(sink);
		} catch (Exception x) {
			String msg =
				"Unable to create RecordIdSource for '" + resourceName + "': "
						+ x.toString();
			logger.severe(msg);
			fail(msg);
		}
		return retVal;
	}

	protected static IRecordIdSink createRecordIdSink(BatchJob job,
			String resourceName) {
		return createRecordIdSink(job, resourceName, "SINK", "dat");
	}

	protected static IRecordIdSink createRecordIdSink(BatchJob job,
			String resourceName, final String prefix, final String suffix) {

		IRecordIdSink retVal = null;
		try {
			final File dir = job.getWorkingDirectory();
			final File tmp = File.createTempFile(prefix, suffix, dir);
			retVal = new RecordIdSink(tmp.getAbsolutePath());

			InputStream is =
				RecordIdTranslatorIT.class.getResourceAsStream(resourceName);
			Reader r = new InputStreamReader(is, TRANSLATOR_CHARSET);
			BufferedReader br = new BufferedReader(r);

			String str = br.readLine();
			assertTrue(str != null);
			RECORD_ID_TYPE dataType =
				RECORD_ID_TYPE.fromValue(Integer.parseInt(str));
			assertTrue(dataType == TRANSLATOR_ID_TYPE);
			retVal.setRecordIDType(dataType);
			retVal.open();

			str = br.readLine();
			while (str != null) {
				Comparable<?> id = dataType.idFromString(str);
				assertTrue(id.equals(str));
				retVal.writeRecordID(id);
				str = br.readLine();
			}
			retVal.flush();
			retVal.close();
		} catch (Exception x) {
			String msg =
				"Unable to create RecordIdSink for '" + resourceName + "': "
						+ x.toString();
			logger.severe(msg);
			fail(msg);
		}

		return retVal;
	}

	@EJB
	private EjbPlatform e2service;

	@Resource
	UserTransaction utx;

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

	@EJB
	private OabaJobController oabaController;

	@EJB(beanName = "OabaJobControllerBean")
	private OabaJobController jobController;

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

	TestEntityCounts te;

	WellKnownTestConfiguration getTestConfiguration() {
		return getTestConfiguration(e2service);
	}

	BatchJob createPersistentOabaJob(String methodName)
			throws ServerConfigurationException {
		logger.entering(LOG_SOURCE, methodName);
		WellKnownTestConfiguration c = getTestConfiguration();
		return OabaTestUtils.createPersistentOabaJob(c, e2service,
				rsController, oabaSettingsController, serverController,
				jobController, te);
	}

	@Before
	public void setUp() throws Exception {
		te =
			new TestEntityCounts(logger, oabaController, paramsController,
					oabaSettingsController, serverController,
					eventManager, opPropController, rsController,
					ridController);
	}

	public void checkCounts() {
		if (te != null) {
			te.checkCounts(logger, em, utx, oabaController, paramsController,
					oabaSettingsController, serverController,
					eventManager, opPropController, rsController,
					ridController);
		} else {
			throw new Error("Counts not initialized");
		}
	}

	@Test
	public void testCreateMutableRecordIdTranslator() throws BlockingException,
			ServerConfigurationException {
		final String METHOD = "testCreateMutableRecordIdTranslator";
		BatchJob job = createPersistentOabaJob(METHOD);
		IRecordIdSinkSourceFactory factory = new CheezyFactory();
		IRecordIdSink sink1 = createRecordIdSink(job, TRANSLATOR1_RESOURCE);
		IRecordIdSink sink2 = createRecordIdSink(job, TRANSLATOR2_RESOURCE);
		@SuppressWarnings("unchecked")
		MutableRecordIdTranslator<String> rit =
			new MutableRecordIdTranslatorImpl(job, factory, sink1, sink2, true);
		assertTrue(rit != null);
		checkCounts();
	}

	@Test
	public void testToImmutableTranslator() throws BlockingException,
			ServerConfigurationException {
		final String METHOD = "testToImmutableTranslator";
		BatchJob job = createPersistentOabaJob(METHOD);
		IRecordIdSinkSourceFactory factory = new CheezyFactory();
		IRecordIdSink sink1 = createRecordIdSink(job, TRANSLATOR1_RESOURCE);
		IRecordIdSink sink2 = createRecordIdSink(job, TRANSLATOR2_RESOURCE);
		@SuppressWarnings("unchecked")
		MutableRecordIdTranslator<String> rit =
			new MutableRecordIdTranslatorImpl(job, factory, sink1, sink2, true);
		assertTrue(rit != null);

		ImmutableRecordIdTranslator<String> irit =
			ridController.toImmutableTranslator(rit);

		IRecordIdSource<String> source1 =
			createRecordIdSource(job, TRANSLATOR1_RESOURCE);
		source1.open();
		assertTrue(source1.hasNext());
		int internalId = ImmutableRecordIdTranslator.MINIMUM_VALID_INDEX;
		while (source1.hasNext()) {
			String expected = source1.next();
			assertTrue(expected != null);
			String computed = (String) irit.reverseLookup(internalId);
			assertTrue(expected.equals(computed));
			++internalId;
		}

		int splitIndex = irit.getSplitIndex();
		assertTrue(internalId == splitIndex);

		IRecordIdSource<String> source2 =
			createRecordIdSource(job, TRANSLATOR2_RESOURCE);
		source2.open();
		assertTrue(source2.hasNext());
		while (source2.hasNext()) {
			String expected = source2.next();
			assertTrue(expected != null);
			String computed = (String) irit.reverseLookup(internalId);
			assertTrue(expected.equals(computed));
			++internalId;
		}

		checkCounts();
	}

}
