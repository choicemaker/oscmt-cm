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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.junit.Test;

public class YyyyMmDd_DateParserTest {

	private static final Logger logger = Logger
			.getLogger(YyyyMmDd_DateParserTest.class.getName());

	static final InputAndExpectedYMD[] TESTS_PARSE =
		new InputAndExpectedYMD[] {
			new InputAndExpectedYMD(null, YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("1968a924", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("19689a4", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("a968924", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("1968924", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("19680924", new YearMonthDay(1968, 9, 24)),
			new InputAndExpectedYMD("1968 924", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("19689 24", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("1968x9x24", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("196895", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("19680905", new YearMonthDay(1968, 9, 5)),
			new InputAndExpectedYMD("1968 9 5", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("19689 5 ", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("1968x9x5", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("1968249", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("19682409", new YearMonthDay(1968, 24, 9)),
			new InputAndExpectedYMD("196824 9", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("1968x24x9", YearMonthDay.PLACEHOLDER),
		};

	@Test
	public void testParse() {
		IDateParser parser = new YyyyMmDd_DateParser();
		for (int i = 0; i < TESTS_PARSE.length; i++) {
			InputAndExpectedYMD test = TESTS_PARSE[i];
			try {
				YearMonthDay computed = parser.parse(test.input);
				String diagnostic =
					"test[" + i + "]: computed: " + computed + ", "
							+ test.toString();
				assertTrue(diagnostic, computed != null);
				assertTrue(diagnostic, computed.equals(test.expected));
			} catch (Exception x) {
				String diagnostic =
					"test[" + i + "]: exception: " + x.toString() + ", "
							+ test.toString();
				fail(diagnostic);
			}
		}
	}

	/** Tests whether the disjunction matcher finds valid patterns */
	@Test
	public void testMatcher() {
		IDateParser parser = new YyyyMmDd_DateParser();
		for (int i = 0; i < TESTS_PARSE.length; i++) {
			InputAndExpectedYMD test = TESTS_PARSE[i];
			if (test.input == null) {
				try {
					@SuppressWarnings("unused")
					Matcher disjunction = parser.matcher(test.input);
					fail("Null point exception expected");
				} catch (NullPointerException x) {
					logger.finer("Expected: " + x.toString());
				}
			} else {
				try {
					Matcher disjunction = parser.matcher(test.input);
					if (YearMonthDay.PLACEHOLDER.equals(test.expected)) {
						assertFalse(disjunction.find());
					} else {
						assertTrue(disjunction.find());
					}
				} catch (Exception x) {
					String diagnostic =
						"test[" + i + "]: exception: " + x.toString() + ", "
								+ test.toString();
					fail(diagnostic);
				}
			}
		}
	}

}
