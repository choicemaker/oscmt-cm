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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.choicemaker.cm.args.PersistentObject;
import com.choicemaker.cms.api.NamedConfiguration;
import com.choicemaker.cms.api.NamedConfigurationController;
import com.choicemaker.cms.ejb.NamedConfigurationEntity;

@RunWith(Arquillian.class)
public class NamedConfigurationControllerIT {

	private static final Logger logger = Logger
			.getLogger(NamedConfigurationControllerIT.class.getName());

	private final static String LOG_SOURCE =
			NamedConfigurationControllerIT.class.getSimpleName();

	public static final boolean TESTS_AS_EJB_MODULE = false;
	
	public static final int TEST_COLLECTION_SIZE = 10;

	@Deployment
	public static EnterpriseArchive createEarArchive() {
		Class<?>[] removedClasses = null;
		return UrmDeploymentUtils.createEarArchive(removedClasses,
				TESTS_AS_EJB_MODULE);
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
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

	@Test
	public void testFindAllNamedConfigurations() {
		final String METHOD = "testFindAllNamedConfigurations";
		logger.entering(LOG_SOURCE, METHOD);

		// Find the configurations that exist before this test gets underway
		List<NamedConfiguration> _existing1 = ncController.findAllNamedConfigurations();
		final List<NamedConfiguration> existing = Collections.unmodifiableList(_existing1);
		
		// Create some new configurations
		List<Long> newIds = new ArrayList<>();
		for (int i=0; i<TEST_COLLECTION_SIZE; i++) {
			NamedConfigurationEntity _nce = new NamedConfigurationEntity();
			final String configName = UUID.randomUUID().toString();
			_nce.setConfigurationName(configName);
			_nce.setConfigurationDescription("Description for " + configName);
			assertTrue(_nce.getId() == PersistentObject.NONPERSISTENT_ID);
			NamedConfiguration nc = ncController.save(_nce);
			final long ncId = nc.getId();
			assertTrue(ncId != PersistentObject.NONPERSISTENT_ID);
			
			newIds.add(ncId);
		}
		final int count = newIds.size();

		// Find just the new additions
		List<NamedConfiguration> additions = ncController.findAllNamedConfigurations();
		boolean isChanged = additions.removeAll(existing);
		assertTrue(isChanged);
		assertTrue(additions.size() == count);
		
		// Check the persistence ids of the additions.
		// Remove each addition as it is checked.
		for (NamedConfiguration nc : additions) {
			Long ncId = nc.getId();
			assertTrue(newIds.contains(ncId));
			isChanged = newIds.remove(ncId);
			assertTrue(isChanged);
			ncController.remove(nc);
		}
		assertTrue(newIds.size() == 0);
		
		// Verify that all the additions of this test have been removed
		List<NamedConfiguration> _existing2 = ncController.findAllNamedConfigurations();
		assertTrue(_existing2.size() == existing.size());
		assertTrue(_existing2.containsAll(existing));
		assertTrue(existing.containsAll(_existing2));
	}

	@Test
	public void testClone() {
		final String METHOD = "testClone";
		logger.entering(LOG_SOURCE, METHOD);

		// Find the configurations that exist before this test gets underway
		List<NamedConfiguration> _existing1 = ncController.findAllNamedConfigurations();
		final List<NamedConfiguration> existing = Collections.unmodifiableList(_existing1);
		
		NamedConfigurationEntity _nce = new NamedConfigurationEntity();
		final String configName = UUID.randomUUID().toString();
		_nce.setConfigurationName(configName);
		_nce.setConfigurationDescription("Description for " + configName);
		assertTrue(_nce.getId() == PersistentObject.NONPERSISTENT_ID);
		NamedConfiguration nc = ncController.save(_nce);
		final long ncId = nc.getId();
		assertTrue(ncId != PersistentObject.NONPERSISTENT_ID);

		// Verify the clone is not identical to the original, but that the
		// non-identity fields (those excluding persistence id and UUID) do
		// match
		NamedConfiguration nc2 = ncController.clone(_nce);
		assertTrue(nc2 != _nce);
		assertTrue(!nc2.equals(_nce));
		assertTrue(nc2.getId() == PersistentObject.NONPERSISTENT_ID);
		assertTrue(_nce.equalsIgnoreIdentityFields(nc2));
		
		// Remove the configuration added by this test
		ncController.remove(nc);
		
		// Verify that all the additions of this test have been removed
		List<NamedConfiguration> _existing2 = ncController.findAllNamedConfigurations();
		assertTrue(_existing2.size() == existing.size());
		assertTrue(_existing2.containsAll(existing));
		assertTrue(existing.containsAll(_existing2));
	}

	@Test
	public void testSaveFindRemove() {
		final String METHOD = "testSaveFindUpdateRemove";
		logger.entering(LOG_SOURCE, METHOD);

		NamedConfigurationEntity _nce = new NamedConfigurationEntity();
		final String configName = UUID.randomUUID().toString();
		_nce.setConfigurationName(configName);
		_nce.setConfigurationDescription("Description for " + configName);
		assertTrue(_nce.getId() == PersistentObject.NONPERSISTENT_ID);
		NamedConfiguration nc = ncController.save(_nce);
		final long ncId = nc.getId();
		assertTrue(ncId != PersistentObject.NONPERSISTENT_ID);

		nc = null;
		nc = ncController.findNamedConfiguration(ncId);
		assertTrue(nc != null);
		assertTrue(configName.equals(nc.getConfigurationName()));

		nc = null;
		nc = ncController.findNamedConfigurationByName(configName);
		assertTrue(nc != null);
		assertTrue(configName.equals(nc.getConfigurationName()));

		ncController.remove(nc);
		nc = ncController.findNamedConfiguration(ncId);
		assertTrue(nc == null);
		nc = ncController.findNamedConfigurationByName(configName);
		assertTrue(nc == null);
	}

}
