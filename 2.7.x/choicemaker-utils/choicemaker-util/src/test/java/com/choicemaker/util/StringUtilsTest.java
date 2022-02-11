/*******************************************************************************
 * Copyright (c) 2003, 2021 ChoiceMaker LLC and others.
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

	static final InputAndExpectedString[] TESTS_CAMEL_CASE =
			new InputAndExpectedString[] {
					new InputAndExpectedString(null, null),
					new InputAndExpectedString("        ", ""),
					new InputAndExpectedString(" ", ""),
					new InputAndExpectedString("0001234a", "0001234a"),
					new InputAndExpectedString(" 0001234a", "0001234a"),
					new InputAndExpectedString(" 0001234a ", "0001234a"),
					new InputAndExpectedString("0001234a ", "0001234a"),
					new InputAndExpectedString("0001234A", "0001234A"),
					new InputAndExpectedString(" 0001234A", "0001234A"),
					new InputAndExpectedString(" 0001234A ", "0001234A"),
					new InputAndExpectedString("0001234A ", "0001234A"),
					new InputAndExpectedString("a", "a"),
					new InputAndExpectedString(" a", "a"),
					new InputAndExpectedString(" a ", "a"),
					new InputAndExpectedString("a ", "a"),
					new InputAndExpectedString("A", "a"),
					new InputAndExpectedString(" A", "a"),
					new InputAndExpectedString(" A ", "a"),
					new InputAndExpectedString("A ", "a"),
					new InputAndExpectedString("a0001234", "a0001234"),
					new InputAndExpectedString(" a0001234", "a0001234"),
					new InputAndExpectedString(" a0001234 ", "a0001234"),
					new InputAndExpectedString("a0001234 ", "a0001234"),
					new InputAndExpectedString("A0001234", "a0001234"),
					new InputAndExpectedString(" A0001234", "a0001234"),
					new InputAndExpectedString(" A0001234 ", "a0001234"),
					new InputAndExpectedString("A0001234 ", "a0001234"),
					new InputAndExpectedString("aB", "aB"),
					new InputAndExpectedString(" aB", "aB"),
					new InputAndExpectedString(" aB ", "aB"),
					new InputAndExpectedString("aB ", "aB"),
					new InputAndExpectedString("AB", "aB"),
					new InputAndExpectedString(" AB", "aB"),
					new InputAndExpectedString(" AB ", "aB"),
					new InputAndExpectedString("AB ", "aB"),
					};

	static final InputAndExpectedString[] TESTS_TITLE_CASE =
			new InputAndExpectedString[] {
					new InputAndExpectedString(null, null),
					new InputAndExpectedString("        ", ""),
					new InputAndExpectedString(" ", ""),
					new InputAndExpectedString("0001234a", "0001234a"),
					new InputAndExpectedString(" 0001234a", "0001234a"),
					new InputAndExpectedString(" 0001234a ", "0001234a"),
					new InputAndExpectedString("0001234a ", "0001234a"),
					new InputAndExpectedString("0001234A", "0001234A"),
					new InputAndExpectedString(" 0001234A", "0001234A"),
					new InputAndExpectedString(" 0001234A ", "0001234A"),
					new InputAndExpectedString("0001234A ", "0001234A"),
					new InputAndExpectedString("a", "A"),
					new InputAndExpectedString(" a", "A"),
					new InputAndExpectedString(" a ", "A"),
					new InputAndExpectedString("a ", "A"),
					new InputAndExpectedString("A", "A"),
					new InputAndExpectedString(" A", "A"),
					new InputAndExpectedString(" A ", "A"),
					new InputAndExpectedString("A ", "A"),
					new InputAndExpectedString("a0001234", "A0001234"),
					new InputAndExpectedString(" a0001234", "A0001234"),
					new InputAndExpectedString(" a0001234 ", "A0001234"),
					new InputAndExpectedString("a0001234 ", "A0001234"),
					new InputAndExpectedString("A0001234", "A0001234"),
					new InputAndExpectedString(" A0001234", "A0001234"),
					new InputAndExpectedString(" A0001234 ", "A0001234"),
					new InputAndExpectedString("A0001234 ", "A0001234"),
					new InputAndExpectedString("aB", "AB"),
					new InputAndExpectedString(" aB", "AB"),
					new InputAndExpectedString(" aB ", "AB"),
					new InputAndExpectedString("aB ", "AB"),
					new InputAndExpectedString("AB", "AB"),
					new InputAndExpectedString(" AB", "AB"),
					new InputAndExpectedString(" AB ", "AB"),
					new InputAndExpectedString("AB ", "AB"),
					};

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

	@Test
	public void testCamelCase() {
		for (int i = 0; i < TESTS_CAMEL_CASE.length; i++) {
			InputAndExpectedString test = TESTS_CAMEL_CASE[i];
			String computed = StringUtils.camelCase(test.input);
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
	public void testTitleCase() {
		for (int i = 0; i < TESTS_TITLE_CASE.length; i++) {
			InputAndExpectedString test = TESTS_TITLE_CASE[i];
			String computed = StringUtils.titleCase(test.input);
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
