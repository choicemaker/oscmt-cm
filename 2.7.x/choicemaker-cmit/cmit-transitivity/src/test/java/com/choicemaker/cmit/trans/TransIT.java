/*******************************************************************************
 * Copyright (c) 2003, 2015 ChoiceMaker LLC and others.
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.choicemaker.cmit.trans.util.TransitivityDeploymentUtils;

/**
 * Trivial tests -- but useful for checking that Arquillian is configured
 * properly for the other, more comprehensive tests in this module.
 *
 * @author rphall
 */
@RunWith(Arquillian.class)
public class TransIT {

	public static final boolean TESTS_AS_EJB_MODULE = false;

	@Deployment
	public static Archive<?> createEAR() {
		Class<?>[] removedClasses = null;
		return TransitivityDeploymentUtils.createEarArchive(removedClasses,
				TESTS_AS_EJB_MODULE);
	}

	@PersistenceContext(unitName = "oaba")
	private EntityManager em;

	@Test
	public void testTrivialTrue() {
		System.out.println("testTrivialTrue: TRIVIALLY VALID TEST");
		assertTrue(true);
	}

	@Test
	public void testEntityManager() {
		assertTrue(em != null);
	}

}
