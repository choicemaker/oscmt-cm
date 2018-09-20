package com.choicemaker.cm.matching.gen;

import static junit.framework.Assert.assertTrue;

import java.util.logging.Logger;

import org.junit.Test;

public class LongestCommonSubstringTest {

	private static final Logger logger =
		Logger.getLogger(LongestCommonSubstringTest.class.getName());

	public static class PairExpectedLCS {
		public final String s1;
		public final String s2;
		public final String expectedLCS;

		public PairExpectedLCS(String str1, String str2, String expected) {
			this.s1 = str1;
			this.s2 = str2;
			this.expectedLCS = expected;
		}

		@Override
		public String toString() {
			return "PairExpectedLCS [s1=" + s1 + ", s2=" + s2 + ", expectedLCS="
					+ expectedLCS + "]";
		}
	}

	private static final PairExpectedLCS[] testData = new PairExpectedLCS[] {
			new PairExpectedLCS("2519932", "2519932", "2519932"),
			new PairExpectedLCS("CD2519932AB", "CD2519932AB", "CD2519932AB"),
			new PairExpectedLCS("CD2519932A", "CD2519932AB", "CD2519932A"),
			new PairExpectedLCS("CD2519932A", "CD2519932B", "CD2519932"),
			new PairExpectedLCS("D2519932AB", "CD2519932AB", "D2519932AB"),
			new PairExpectedLCS("C2519932AB", "CD2519932AB", "2519932AB"),
			new PairExpectedLCS("2519932", "CD2519932AB", "2519932"),
			new PairExpectedLCS("D2519932A", "CD2519932AB", "D2519932A"),
			new PairExpectedLCS("C2519932B", "CD2519932AB", "2519932") };

	@Test
	public void testLongestCommonSubstring() {
		for (int i = 0; i < testData.length; i++) {
			final PairExpectedLCS s = testData[i];
			logger.fine(String.format("Testing: '%s'", s));
			int expected = s.expectedLCS == null ? 0 : s.expectedLCS.length();
			logger.fine(String.format("expected length: %d", expected));
			int computed =
				LongestCommonSubstring.longestCommonSubstring(s.s1, s.s2);
			logger.fine(String.format("computed length: %d", computed));
			assertTrue(String.format(
					"Expected lcs length (%d) != computed lcs length (%d)"
							+ "for %s (testData[%d])",
					expected, computed, s, i), expected == computed);
		}
	}

}
