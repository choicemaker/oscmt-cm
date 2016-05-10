package com.choicemaker.util.dates;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.junit.Test;

public class DdMmmYyyy_DateParserTest {

	private static final Logger logger = Logger
			.getLogger(DdMmmYyyy_DateParserTest.class.getName());

	static final YearMonthDay YMD_19680905 = new YearMonthDay(1968, 9, 05);

	static final YearMonthDay YMD_19680124 = new YearMonthDay(1968, 1, 24);
	static final YearMonthDay YMD_19680224 = new YearMonthDay(1968, 2, 24);
	static final YearMonthDay YMD_19680324 = new YearMonthDay(1968, 3, 24);
	static final YearMonthDay YMD_19680424 = new YearMonthDay(1968, 4, 24);
	static final YearMonthDay YMD_19680524 = new YearMonthDay(1968, 5, 24);
	static final YearMonthDay YMD_19680624 = new YearMonthDay(1968, 6, 24);
	static final YearMonthDay YMD_19680724 = new YearMonthDay(1968, 7, 24);
	static final YearMonthDay YMD_19680824 = new YearMonthDay(1968, 8, 24);
	static final YearMonthDay YMD_19680924 = new YearMonthDay(1968, 9, 24);
	static final YearMonthDay YMD_19681024 = new YearMonthDay(1968,10, 24);
	static final YearMonthDay YMD_19681124 = new YearMonthDay(1968,11, 24);
	static final YearMonthDay YMD_19681224 = new YearMonthDay(1968,12, 24);

	static final InputAndExpectedYMD[] TESTS_PARSE =
		new InputAndExpectedYMD[] {
				new InputAndExpectedYMD(null, YearMonthDay.PLACEHOLDER),
				new InputAndExpectedYMD("05SEP1968", YMD_19680905),
				new InputAndExpectedYMD("24JAN1968", YMD_19680124),
				new InputAndExpectedYMD("24FEB1968", YMD_19680224),
				new InputAndExpectedYMD("24MAR1968", YMD_19680324),
				new InputAndExpectedYMD("24APR1968", YMD_19680424),
				new InputAndExpectedYMD("24MAY1968", YMD_19680524),
				new InputAndExpectedYMD("24JUN1968", YMD_19680624),
				new InputAndExpectedYMD("24JUL1968", YMD_19680724),
				new InputAndExpectedYMD("24AUG1968", YMD_19680824),
				new InputAndExpectedYMD("24SEP1968", YMD_19680924),
				new InputAndExpectedYMD("24OCT1968", YMD_19681024),
				new InputAndExpectedYMD("24NOV1968", YMD_19681124),
				new InputAndExpectedYMD("24DEC1968", YMD_19681224),
				new InputAndExpectedYMD("a4SEP1968", YearMonthDay.PLACEHOLDER),
				new InputAndExpectedYMD("24SEPa968", YearMonthDay.PLACEHOLDER),
				new InputAndExpectedYMD("24xSEPx1968", YearMonthDay.PLACEHOLDER),
				new InputAndExpectedYMD("5SEP1968", YearMonthDay.PLACEHOLDER),
				new InputAndExpectedYMD(" 5SEP1968", YearMonthDay.PLACEHOLDER),
				new InputAndExpectedYMD("5 SEP1968", YearMonthDay.PLACEHOLDER),
				new InputAndExpectedYMD("5xSEPx1968", YearMonthDay.PLACEHOLDER),
				new InputAndExpectedYMD("24xSEPx1968", YearMonthDay.PLACEHOLDER),
		};

	@Test
	public void testParse() {
		IDateParser parser = new DdMmmYyyy_DateParser();
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
		IDateParser parser = new DdMmmYyyy_DateParser();
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
