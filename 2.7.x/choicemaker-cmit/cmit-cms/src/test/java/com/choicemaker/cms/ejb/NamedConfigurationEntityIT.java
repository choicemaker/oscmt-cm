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

import static com.choicemaker.cm.args.PersistentObject.NONPERSISTENT_ID;
import static com.choicemaker.cms.api.NamedConfiguration.DEFAULT_ABALIMITPERBLOCKINGSET;
import static com.choicemaker.cms.api.NamedConfiguration.DEFAULT_ABALIMITSINGLEBLOCKINGSET;
import static com.choicemaker.cms.api.NamedConfiguration.DEFAULT_ABASINGLETABLEBLOCKINGSETGRACELIMIT;
import static com.choicemaker.cms.api.NamedConfiguration.DEFAULT_OABAINTERVAL;
import static com.choicemaker.cms.api.NamedConfiguration.DEFAULT_OABAMAXBLOCKSIZE;
import static com.choicemaker.cms.api.NamedConfiguration.DEFAULT_OABAMAXCHUNKSIZE;
import static com.choicemaker.cms.api.NamedConfiguration.DEFAULT_OABAMAXMATCHES;
import static com.choicemaker.cms.api.NamedConfiguration.DEFAULT_OABAMAXOVERSIZED;
import static com.choicemaker.cms.api.NamedConfiguration.DEFAULT_OABAMAXSINGLE;
import static com.choicemaker.cms.api.NamedConfiguration.DEFAULT_OABAMINFIELDS;
import static com.choicemaker.cms.api.NamedConfiguration.DEFAULT_SERVERMAXFILESCOUNT;
import static com.choicemaker.cms.api.NamedConfiguration.DEFAULT_SERVERMAXFILEENTRIES;
import static com.choicemaker.cms.api.NamedConfiguration.DEFAULT_SERVERMAXTHREADS;
import static com.choicemaker.util.ReflectionUtils.getAccessor;
import static com.choicemaker.util.ReflectionUtils.getManipulator;
import static com.choicemaker.util.ReflectionUtils.randomBoolean;
import static com.choicemaker.util.ReflectionUtils.randomFloat;
import static com.choicemaker.util.ReflectionUtils.randomInt;
import static com.choicemaker.util.ReflectionUtils.randomString;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.choicemaker.cm.args.AnalysisResultFormat;
import com.choicemaker.cm.args.OabaLinkageType;
import com.choicemaker.cm.batch.api.BatchJobRigor;
import com.choicemaker.cms.api.NamedConfiguration;
import com.choicemaker.cms.api.NamedConfigurationController;
import com.choicemaker.cms.ejb.NamedConfigurationEntity;

@RunWith(Arquillian.class)
public class NamedConfigurationEntityIT {

	private static final Random random = new Random();

	// private static final Logger logger = Logger
	// .getLogger(NamedConfigurationEntityIT.class.getName());

	public static final boolean TESTS_AS_EJB_MODULE = false;

	@Deployment
	public static EnterpriseArchive createEarArchive() {
		Class<?>[] removedClasses = null;
		return UrmDeploymentUtils.createEarArchive(removedClasses,
				TESTS_AS_EJB_MODULE);
	}

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

	@EJB
	private NamedConfigurationController ncController;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	protected <T> void testPersistentProperty(final Class<T> p,
			final String _pn, final T pv) {
		testPersistentProperty(p, _pn, pv, false);
	}

	protected <T> void testPersistentProperty(final Class<T> p,
			final String _pn, final T pv, boolean requireChange) {
		assertTrue("Class must be non-null", p != null);
		assertTrue("Property value must not be null", pv != null);

		try {
			final Class<?> c = NamedConfigurationEntity.class;
			final Method accessor = getAccessor(c, p, _pn);
			final Method manipulator = getManipulator(c, p, _pn);

			// Create a named configuration and confirm it is not persistent
			NamedConfigurationEntity nce = new NamedConfigurationEntity();
			assertTrue(NONPERSISTENT_ID == nce.getId());

			// Confirm the default value is different from the property value
			if (requireChange) {
				@SuppressWarnings("unchecked")
				T defaultValue = (T) accessor.invoke(nce, (Object[]) null);
				assertTrue(!pv.equals(defaultValue));
			}

			// Set the property value and save the configuration
			manipulator.invoke(nce, pv);
			NamedConfiguration nc0 = ncController.save(nce);
			final long ncId = nc0.getId();
			assertTrue(NONPERSISTENT_ID != nc0.getId());
			nc0 = null;

			// Look up the configuration and check the property value
			NamedConfiguration nc1 = ncController.findNamedConfiguration(ncId);
			assertTrue(nc1 != null);
			@SuppressWarnings("unchecked")
			T value = (T) accessor.invoke(nc1, (Object[]) null);
			assertTrue(pv.equals(value));

			ncController.remove(nc1);
			NamedConfiguration nc2 = ncController.findNamedConfiguration(ncId);
			assertTrue(nc2 == null);

		} catch (SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			fail(e.toString());
		}
	}

	@Test
	public void testConfigurationName() {
		testPersistentProperty(String.class, "ConfigurationName",
				randomString());
	}

	@Test
	public void testModelName() {
		testPersistentProperty(String.class, "ModelName", randomString());
	}

	@Test
	public void testLowThreshold() {
		testPersistentProperty(float.class, "LowThreshold", randomFloat());
	}

	@Test
	public void testHighThreshold() {
		testPersistentProperty(float.class, "HighThreshold", randomFloat());
	}

	@Test
	public void testTask() {
		int count = OabaLinkageType.values().length;
		int idx = random.nextInt(count);
		String s = OabaLinkageType.values()[idx].name();
		testPersistentProperty(String.class, "Task", s);
	}

	@Test
	public void testRigor() {
		int count = BatchJobRigor.values().length;
		int idx = random.nextInt(count);
		String s = BatchJobRigor.values()[idx].name();
		testPersistentProperty(String.class, "Rigor", s);
	}

	// Property can't be set in this version
	// @Test
	// public void testRecordSourceType() {
	// String s = randomString();
	// testPersistentProperty(String.class, "RecordSourceType", s);
	// }

	@Test
	public void testDataSource() {
		testPersistentProperty(String.class, "DataSource", randomString());
	}

	@Test
	public void testJdbcDriverClassName() {
		testPersistentProperty(String.class, "JdbcDriverClassName",
				randomString());
	}

	@Test
	public void testBlockingConfiguration() {
		testPersistentProperty(String.class, "BlockingConfiguration",
				randomString());
	}

	@Test
	public void testQuerySelection() {
		testPersistentProperty(String.class, "QuerySelection", randomString());
	}

	@Test
	public void testQueryDatabaseConfiguration() {
		testPersistentProperty(String.class, "QueryDatabaseConfiguration",
				randomString());
	}

	@Test
	public void testQueryDeduplicated() {
		testPersistentProperty(boolean.class, "QueryDeduplicated",
				randomBoolean());
	}

	@Test
	public void testReferenceSelection() {
		testPersistentProperty(String.class, "ReferenceSelection",
				randomString());
	}

	@Test
	public void testReferenceDatabaseConfiguration() {
		testPersistentProperty(String.class, "ReferenceDatabaseConfiguration",
				randomString());
	}

	@Test
	public void testTransitivityFormat() {
		int count = AnalysisResultFormat.values().length;
		int idx = random.nextInt(count);
		String s = AnalysisResultFormat.values()[idx].name();
		testPersistentProperty(String.class, "TransitivityFormat", s);
	}

	@Test
	public void testTransitivityGraph() {
		testPersistentProperty(String.class, "TransitivityGraph",
				randomString());
	}

	@Test
	public void testAbaLimitPerBlockingSet() {
		testPersistentProperty(int.class, "AbaLimitPerBlockingSet",
				randomInt(DEFAULT_ABALIMITPERBLOCKINGSET));
	}

	@Test
	public void testAbaLimitSingleBlockingSet() {
		testPersistentProperty(int.class, "AbaLimitSingleBlockingSet",
				randomInt(DEFAULT_ABALIMITSINGLEBLOCKINGSET));
	}

	@Test
	public void testAbaSingleTableBlockingSetGraceLimit() {
		testPersistentProperty(int.class,
				"AbaSingleTableBlockingSetGraceLimit",
				randomInt(DEFAULT_ABASINGLETABLEBLOCKINGSETGRACELIMIT));
	}

	@Test
	public void testOabaMaxSingle() {
		testPersistentProperty(int.class, "OabaMaxSingle",
				randomInt(DEFAULT_OABAMAXSINGLE));
	}

	@Test
	public void testOabaMaxBlockSize() {
		testPersistentProperty(int.class, "OabaMaxBlockSize",
				randomInt(DEFAULT_OABAMAXBLOCKSIZE));
	}

	@Test
	public void testOabaMaxChunkSize() {
		testPersistentProperty(int.class, "OabaMaxChunkSize",
				randomInt(DEFAULT_OABAMAXCHUNKSIZE));
	}

	@Test
	public void testOabaMaxOversized() {
		testPersistentProperty(int.class, "OabaMaxOversized",
				randomInt(DEFAULT_OABAMAXOVERSIZED));
	}

	@Test
	public void testOabaMaxMatches() {
		testPersistentProperty(int.class, "OabaMaxMatches",
				randomInt(DEFAULT_OABAMAXMATCHES));
	}

	@Test
	public void testOabaMinFields() {
		testPersistentProperty(int.class, "OabaMinFields",
				randomInt(DEFAULT_OABAMINFIELDS));
	}

	@Test
	public void testOabaInterval() {
		testPersistentProperty(int.class, "OabaInterval",
				randomInt(DEFAULT_OABAINTERVAL));
	}

	@Test
	public void testServerMaxThreads() {
		testPersistentProperty(int.class, "ServerMaxThreads",
				randomInt(DEFAULT_SERVERMAXTHREADS));
	}

	@Test
	public void testServerMaxChunkSize() {
		testPersistentProperty(int.class, "ServerMaxChunkSize",
				randomInt(DEFAULT_SERVERMAXFILEENTRIES));
	}

	@Test
	public void testServerMaxChunkCount() {
		testPersistentProperty(int.class, "ServerMaxChunkCount",
				randomInt(DEFAULT_SERVERMAXFILESCOUNT));
	}

	@Test
	public void testServerFileURI() {
		testPersistentProperty(String.class, "ServerFileURI", randomString());
	}

}
