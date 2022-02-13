/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.utils;

import static com.choicemaker.cm.oaba.utils.RecordValuesMapTestData.testData;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import com.choicemaker.cm.oaba.core.IRecValSource;
import com.choicemaker.cm.oaba.utils.RecordValuesMapTestTypes.Input_Output;
import com.choicemaker.util.IntArrayList;

import junit.framework.TestCase;

public class RecordValuesMapTest extends TestCase {
	
	private static Logger logger = Logger.getLogger(RecordValuesMapTest.class.getName());
	
	@Test
	public void testExpectedComputed() {
		int index = 0;
		List<Integer> failures = new ArrayList<>();
		for (Input_Output inOut : testData) {
			logger.fine("Test data " + index);
			IRecValSource in = inOut.in;
			List<IntArrayList> expected = inOut.out;
			try {
				List<IntArrayList> computed = RecordValuesMap.readColumnList(in);
				assertTrue(computed != null);
				assertTrue(computed.equals(expected));
				logger.info("Test data " + index + " PASSED");
			} catch (Exception | AssertionError e) {
				String msg = "Test data " + index + " FAILED: " + e.toString();
				logger.severe(msg);
				failures.add(index);
			}
			++index;
		}
		if (!failures.isEmpty()) {
			String msg = "Failed test data: " + failures;
			fail(msg);
		}
	}
}
