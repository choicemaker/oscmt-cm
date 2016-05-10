package com.choicemaker.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StringUtilsTest {

	static final InputAndExpectedString[] TESTS_REMOVE_LEADING_ZEROS =
		new InputAndExpectedString[] {
				new InputAndExpectedString(null, null),
				new InputAndExpectedString("        ", "        "),
				new InputAndExpectedString("       0", "       0"),
				new InputAndExpectedString("01234", "1234"),
				new InputAndExpectedString("0001234a", "1234a"),
				new InputAndExpectedString("101234", "101234"),
				new InputAndExpectedString("000002829839", "2829839"),
				new InputAndExpectedString("0", ""),
				new InputAndExpectedString("0000000", ""),
				new InputAndExpectedString("0000009", "9"),
				new InputAndExpectedString("000000z", "z"),
				new InputAndExpectedString("000000.z", ".z") };

	static final InputAndExpectedString[] TESTS_REMOVE_LEADING_NONTERMINAL_ZEROS =
		new InputAndExpectedString[] {
				new InputAndExpectedString(null, null),
				new InputAndExpectedString("        ", "        "),
				new InputAndExpectedString("       0", "       0"),
				new InputAndExpectedString("0001234a", "1234a"),
				new InputAndExpectedString("01234", "1234"),
				new InputAndExpectedString("0001234a", "1234a"),
				new InputAndExpectedString("101234", "101234"),
				new InputAndExpectedString("000002829839", "2829839"),
				new InputAndExpectedString("0", "0"),
				new InputAndExpectedString("0000000", "0"),
				new InputAndExpectedString("0000009", "9"),
				new InputAndExpectedString("000000z", "z"),
				new InputAndExpectedString("000000.z", ".z") };

	@Test
	public void testRemoveLeadingZeros() {
		for (int i = 0; i < TESTS_REMOVE_LEADING_ZEROS.length; i++) {
			InputAndExpectedString test = TESTS_REMOVE_LEADING_ZEROS[i];
			String computed = StringUtils.removeLeadingZeros(test.input);
			String diagnostic =
				"test[" + i + "]: computed: " + computed + ", "
						+ test.toString();
			if (test.input == null) {
				assertTrue(diagnostic, computed == null);
			} else {
				assertTrue(diagnostic, computed != null);
				assertTrue(diagnostic, computed.equals(test.expected));
			}
		}
	}

	@Test
	public void testRemoveLeadingNonterminalZeros() {
		for (int i = 0; i < TESTS_REMOVE_LEADING_NONTERMINAL_ZEROS.length; i++) {
			InputAndExpectedString test =
				TESTS_REMOVE_LEADING_NONTERMINAL_ZEROS[i];
			String computed =
				StringUtils.removeLeadingNonterminalZeros(test.input);
			String diagnostic =
				"test[" + i + "]: computed: " + computed + ", "
						+ test.toString();
			if (test.input == null) {
				assertTrue(diagnostic, computed == null);
			} else {
				assertTrue(diagnostic, computed != null);
				assertTrue(diagnostic, computed.equals(test.expected));
			}
		}
	}

}
