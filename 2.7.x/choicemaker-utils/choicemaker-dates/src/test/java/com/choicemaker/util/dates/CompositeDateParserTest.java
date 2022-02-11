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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.junit.Before;
import org.junit.Test;

public class CompositeDateParserTest {

	private static final Logger logger = Logger
			.getLogger(CompositeDateParserTest.class.getName());

	static final InputAndExpectedYMD[] TESTS_PARSE =
		new InputAndExpectedYMD[] {
				new InputAndExpectedYMD(null, YearMonthDay.PLACEHOLDER),
				// US format
				new InputAndExpectedYMD("a9/24/1968", YearMonthDay.PLACEHOLDER),
				new InputAndExpectedYMD("9/a4/1968", YearMonthDay.PLACEHOLDER),
				new InputAndExpectedYMD("9/24/a968", YearMonthDay.PLACEHOLDER),
				new InputAndExpectedYMD("9/24/1968", new YearMonthDay(1968, 9,
						24)),
				new InputAndExpectedYMD("09/24/1968", new YearMonthDay(1968, 9,
						24)),
				new InputAndExpectedYMD(" 9/24/1968", new YearMonthDay(1968, 9,
						24)),
				new InputAndExpectedYMD("9 /24/1968", new YearMonthDay(1968, 9,
						24)),
				new InputAndExpectedYMD("9x24x1968", new YearMonthDay(1968, 9,
						24)),
				new InputAndExpectedYMD("9/5/1968",
						new YearMonthDay(1968, 9, 5)),
				new InputAndExpectedYMD("09/05/1968", new YearMonthDay(1968, 9,
						5)),
				new InputAndExpectedYMD(" 9/ 5/1968", new YearMonthDay(1968, 9,
						5)),
				new InputAndExpectedYMD("9 /5 /1968", new YearMonthDay(1968, 9,
						5)),
				new InputAndExpectedYMD("9x5x1968",
						new YearMonthDay(1968, 9, 5)),
				new InputAndExpectedYMD("24/9/1968", new YearMonthDay(1968, 24,
						9)),
				new InputAndExpectedYMD("24/09/1968", new YearMonthDay(1968,
						24, 9)),
				new InputAndExpectedYMD("24/ 9/1968", new YearMonthDay(1968,
						24, 9)),
				new InputAndExpectedYMD("24x9x1968", new YearMonthDay(1968, 24,
						9)),
				// ISO format
				new InputAndExpectedYMD("1968/a9/24", YearMonthDay.PLACEHOLDER),
				new InputAndExpectedYMD("1968/9/a4", YearMonthDay.PLACEHOLDER),
				new InputAndExpectedYMD("a968/9/24", YearMonthDay.PLACEHOLDER),
				new InputAndExpectedYMD("1968/9/24", new YearMonthDay(1968, 9,
						24)),
				new InputAndExpectedYMD("1968/09/24", new YearMonthDay(1968, 9,
						24)),
				new InputAndExpectedYMD("1968/ 9/24", new YearMonthDay(1968, 9,
						24)),
				new InputAndExpectedYMD("1968/9 /24", new YearMonthDay(1968, 9,
						24)),
				new InputAndExpectedYMD("1968x9x24", new YearMonthDay(1968, 9,
						24)),
				new InputAndExpectedYMD("1968/9/5",
						new YearMonthDay(1968, 9, 5)),
				new InputAndExpectedYMD("1968/09/05", new YearMonthDay(1968, 9,
						5)),
				new InputAndExpectedYMD("1968/ 9/ 5", new YearMonthDay(1968, 9,
						5)),
				new InputAndExpectedYMD("1968/9 /5 ", new YearMonthDay(1968, 9,
						5)),
				new InputAndExpectedYMD("1968x9x5",
						new YearMonthDay(1968, 9, 5)),
				new InputAndExpectedYMD("1968/24/9", new YearMonthDay(1968, 24,
						9)),
				new InputAndExpectedYMD("1968/24/09", new YearMonthDay(1968,
						24, 9)),
				new InputAndExpectedYMD("1968/24/ 9", new YearMonthDay(1968,
						24, 9)),
				new InputAndExpectedYMD("1968x24x9", new YearMonthDay(1968, 24,
						9)), };

	List<IDateParser> parsers;
	private IDateParser parser;

	@Before
	public void setUp() {
		parsers = new ArrayList<>();
		parsers.add(new US_DateParser());
		parsers.add(new ISO_DateParser());
		parser = new CompositeDateParser(parsers);
	}

	@Test
	public void testParse() {
		assert parser instanceof CompositeDateParser;
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

	/**
	 * Tests whether the disjunction compiles and contains the pattern of each
	 * parser delegate
	 */
	@Test
	public void testGetPattern() {
		assert parser instanceof CompositeDateParser;
		assert parsers != null;
		assert parsers.size() > 0;
		try {
			String disjunction = parser.getPattern().pattern();
			for (IDateParser delegate : parsers) {
				String pattern = delegate.getPattern().pattern();
				assertTrue(disjunction.contains(pattern));
			}
		} catch (AssertionError x) {
			throw x;
		} catch (Exception x) {
			String diagnostic = "getPattern(): " + x.toString();
			fail(diagnostic);
		}
	}

	/** Tests whether the disjunction matcher finds valid patterns */
	@Test
	public void testMatcher() {
		assert parser instanceof CompositeDateParser;
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
