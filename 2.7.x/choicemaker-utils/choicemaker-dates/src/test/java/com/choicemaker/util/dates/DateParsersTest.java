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
 *     ChoiceMaker LLC - initial API and implementation
 *******************************************************************************/
package com.choicemaker.util.dates;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class DateParsersTest {

	static final String TEST_DATA_NAME = "TESTS_PARSE";

	private static IDateParser compositeParser = null;
	static {
		List<IDateParser> parsers = new ArrayList<>();
		parsers.add(new US_DateParser());
		parsers.add(new ISO_DateParser());
		compositeParser = new CompositeDateParser(parsers);
	}

	private static IDateParser[] instances = new IDateParser[] {
			compositeParser, new DdMmmYyyy_DateParser(), new ISO_DateParser(),
			new US_DateParser(), new Yyyy_DateParser(),
			new YyyyMm_DateParser(), new YyyyMmDd_DateParser() };
	
	static String[] getInstanceNames() {
		String[] retVal = new String[instances.length];
		for (int i=0; i<instances.length; i++) {
			retVal[i] = name(instances[i]);
		}
		return retVal;
	}

	static String name(IDateParser p) {
		String retVal = null;
		if (p != null) {
			retVal = p.getClass().getSimpleName();
		}
		return retVal;
	}

	static String testClassName(IDateParser p) {
		String retVal = null;
		if (p != null) {
			Class<? extends IDateParser> c = p.getClass();
			String s = c.getName();
			retVal = s + "Test";
		}
		return retVal;
	}

	static InputAndExpectedYMD[] testData(String name)
			throws ClassNotFoundException, NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		IDateParser p = DateParsers.getInstance().get(name);
		return testData(p);
	}

	static InputAndExpectedYMD[] testData(IDateParser p)
			throws ClassNotFoundException, NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		InputAndExpectedYMD[] retVal = null;
		if (p != null) {
			String cname = testClassName(p);
			Class<?> c = Class.forName(cname);
			Field f = c.getDeclaredField(TEST_DATA_NAME);
			Object o = f.get(null);
			assertTrue(o instanceof InputAndExpectedYMD[]);
			retVal = (InputAndExpectedYMD[]) o;
		}
		return retVal;
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DateParsers dp = DateParsers.getInstance();
		for (IDateParser p : instances) {
			String key = name(p);
			dp.put(key, p);
		}
	}

	@Test
	public void testParse() {
		String[] names = getInstanceNames();
		assertTrue(names != null);
		assertTrue(names.length > 0);
		for (String name : names) {
			assertTrue(name != null);

			InputAndExpectedYMD[] testData = null;
			try {
				testData = testData(name);
			} catch (Exception x) {
				fail(x.toString());
			}
			assertTrue(testData != null);

			for (InputAndExpectedYMD datum : testData) {
				YearMonthDay computed = DateParsers.parse(name, datum.input);
				YearMonthDay expected = datum.expected;
				assertTrue(computed != null);
				assertTrue(computed.equals(expected));
			}
		}
	}

}
