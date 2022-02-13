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
package com.choicemaker.cm.oaba.ejb.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.oaba.ejb.OabaEjbUtils;
import com.choicemaker.cm.oaba.ejb.OabaJobFactoryForTest;

public class OabaUtilsTest {

	@Test
	public void testIsOabaJob() {
		BatchJob job = OabaJobFactoryForTest.createOabaJobForTest();
		boolean computed = OabaEjbUtils.isOabaJob(job);
		assertTrue(computed);
	}

}
