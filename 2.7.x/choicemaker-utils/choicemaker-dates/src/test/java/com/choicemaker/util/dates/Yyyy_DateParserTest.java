package com.choicemaker.util.dates;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.junit.Test;

public class Yyyy_DateParserTest {

	private static final Logger logger = Logger
			.getLogger(Yyyy_DateParserTest.class.getName());

	static final YearMonthDay YYYY_1968 = new YearMonthDay(1968, YearMonthDay.PLACEHOLDER_MONTH,
			YearMonthDay.PLACEHOLDER_DAY);

	static final InputAndExpectedYMD[] TESTS_PARSE = new InputAndExpectedYMD[] {
			new InputAndExpectedYMD(null, YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("1968a924", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("19689a4", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("a968924", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("1968924", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("1968", YYYY_1968),
			new InputAndExpectedYMD("196809", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("19680924", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("1968 924", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("19689 24", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("1968x9x24", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("19680905", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("1968 9 5", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("19689 5 ", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("1968x9x5", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("1968249", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("196824", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("19682409", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("196824 9", YearMonthDay.PLACEHOLDER),
			new InputAndExpectedYMD("1968x24x9", YearMonthDay.PLACEHOLDER), };

	@Test
	public void testParse() {
		IDateParser parser = new Yyyy_DateParser();
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
		IDateParser parser = new Yyyy_DateParser();
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
