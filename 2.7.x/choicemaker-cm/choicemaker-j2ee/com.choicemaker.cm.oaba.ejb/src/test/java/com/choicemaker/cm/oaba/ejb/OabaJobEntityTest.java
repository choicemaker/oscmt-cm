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
package com.choicemaker.cm.oaba.ejb;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.choicemaker.cm.batch.api.BatchJob;

public class OabaJobEntityTest {

	@Test
	public void testGetBatchJobType() {
		BatchJob job = OabaJobFactoryForTest.createOabaJobForTest();
		String expected = OabaJobJPA.DISCRIMINATOR_VALUE;
		String computed = job.getBatchJobType();
		assertTrue(expected.equals(computed));
	}

}