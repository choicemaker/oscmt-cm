/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.server.impl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.choicemaker.cm.oaba.server.impl.AbstractPairResultEntity;

public class AbstractPairResultEntityTest {

	@Test
	public void testIsClassValid() {
		assertTrue(AbstractPairResultEntity.isClassValid());
	}

}
