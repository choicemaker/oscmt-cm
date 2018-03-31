package com.choicemaker.util.dates;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.junit.Test;

public class ISO_DateParserTest {

	private static final Logger logger = Logger
			.getLogger(ISO_DateParserTest.class.getName());

	static final InputAndExpectedYMD[] TESTS_PARSE =
		new InputAndExpectedYMD[] {
			new InputAndExpectedYMD(null, YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("1968/a9/24", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("1968/9/a4", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("a968/9/24", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("1968/9/24", new YearMonthDay(1968, 9, 24)),
			new InputAndExpectedYMD("1968/09/24", new YearMonthDay(1968, 9, 24)),
			new InputAndExpectedYMD("1968/ 9/24", new YearMonthDay(1968, 9, 24)),
			new InputAndExpectedYMD("1968/9 /24", new YearMonthDay(1968, 9, 24)),
			new InputAndExpectedYMD("1968x9x24", new YearMonthDay(1968, 9, 24)),
			new InputAndExpectedYMD("1968/9/5", new YearMonthDay(1968, 9, 5)),
			new InputAndExpectedYMD("1968/09/05", new YearMonthDay(1968, 9, 5)),
			new InputAndExpectedYMD("1968/ 9/ 5", new YearMonthDay(1968, 9, 5)),
			new InputAndExpectedYMD("1968/9 /5 ", new YearMonthDay(1968, 9, 5)),
			new InputAndExpectedYMD("1968x9x5", new YearMonthDay(1968, 9, 5)),
			new InputAndExpectedYMD("1968/24/9", new YearMonthDay(1968, 24, 9)),
			new InputAndExpectedYMD("1968/24/09", new YearMonthDay(1968, 24, 9)),
			new InputAndExpectedYMD("1968/24/ 9", new YearMonthDay(1968, 24, 9)),
			new InputAndExpectedYMD("1968x24x9", new YearMonthDay(1968, 24, 9)),
		};

	@Test
	public void testParse() {
		IDateParser parser = new ISO_DateParser();
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
		IDateParser parser = new ISO_DateParser();
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
