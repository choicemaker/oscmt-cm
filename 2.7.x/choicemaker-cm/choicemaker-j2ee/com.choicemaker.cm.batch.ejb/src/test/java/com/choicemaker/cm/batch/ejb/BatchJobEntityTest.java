/*******************************************************************************
 * Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
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
package com.choicemaker.cm.batch.ejb;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BatchJobEntityTest {

	@Test
	public void testGetBatchJobType() {
		BatchJobEntity entity = new BatchJobEntity() {
			private static final long serialVersionUID = 271L;
		};
		String computedType = entity.getBatchJobType();
		String expectedType = BatchJobJPA.DISCRIMINATOR_VALUE;
		assertTrue(expectedType.equals(computedType));
	}

}
