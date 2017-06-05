/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.core.util;

/**
 * Duplicates <code>com.choicemaker.util.StringUtils</code> so ChoiceMaker 2.5
 * models can be used without modification in ChoiceMaker 2.7.
 *
 * @deprecated use com.choicemaker.util.StringUtils instead
 */
@SuppressWarnings({
		"rawtypes", "unchecked" })
@Deprecated
public class StringUtils {

	/** See {@ com.choicemaker.util.StringUtils#containsDigits(String)} */
	public static boolean containsDigits(String s) {
		return com.choicemaker.util.StringUtils.containsDigits(s);
	}

	/** See {@ com.choicemaker.util.StringUtils#containsDigitsOrLetters(String) } */
	public static boolean containsDigitsOrLetters(String s) {
		return com.choicemaker.util.StringUtils.containsDigitsOrLetters(s);
	}

	/** See {@ com.choicemaker.util.StringUtils#containsLetters(String) } */
	public static boolean containsLetters(String s) {
		return com.choicemaker.util.StringUtils.containsLetters(s);
	}

	/** See {@ com.choicemaker.util.StringUtils#containsNonDigits(String) } */
	public static boolean containsNonDigits(String s) {
		return com.choicemaker.util.StringUtils.containsNonDigits(s);
	}

	/** See {@ com.choicemaker.util.StringUtils#containsNonLetters(String) } */
	public static boolean containsNonLetters(String s) {
		return com.choicemaker.util.StringUtils.containsNonLetters(s);
	}

	/** See {@ com.choicemaker.util.StringUtils#containsNonWordChars(String) } */
	public static boolean containsNonWordChars(String s) {
		return com.choicemaker.util.StringUtils.containsNonWordChars(s);
	}

	/** See {@ com.choicemaker.util.StringUtils#countDigits(String) } */
	public static int countDigits(String s) {
		return com.choicemaker.util.StringUtils.countDigits(s);
	}

	/** See {@ com.choicemaker.util.StringUtils#countDigits(String, int) } */
	public static int countDigits(String s, int limit) {
		return com.choicemaker.util.StringUtils.countDigits(s, limit);
	}

	/** See {@ com.choicemaker.util.StringUtils#countLetters(String) } */
	public static int countLetters(String s) {
		return com.choicemaker.util.StringUtils.countLetters(s);
	}

	/** See {@ com.choicemaker.util.StringUtils#countOccurrences(String, char) } */
	public static int countOccurrences(String haystack, char needle) {
		return com.choicemaker.util.StringUtils.countOccurrences(haystack, needle);
	}

	/** See {@ com.choicemaker.util.StringUtils#findNumbers(String) } */
	public static boolean[] findNumbers(String s) {
		return com.choicemaker.util.StringUtils.findNumbers(s);
	}

	/** See {@ com.choicemaker.util.StringUtils#getChar(String) } */
	public static char getChar(String s) {
		return com.choicemaker.util.StringUtils.getChar(s);
	}

	/** See {@ com.choicemaker.util.StringUtils#join(String, String) } */
	public static String join(String s1, String s2) {
		return com.choicemaker.util.StringUtils.join(s1,s2);
	}

	/** See {@ com.choicemaker.util.StringUtils#join(String, String, String) } */
	public static String join(String s1, String s2, String s3) {
		return com.choicemaker.util.StringUtils.join(s1, s2, s3);
	}

	/** See {@ com.choicemaker.util.StringUtils#join(String, String, String, String) } */
	public static String join(String s1, String s2, String s3, String s4) {
		return com.choicemaker.util.StringUtils.join(s1, s2, s3, s4);
	}

	/** See {@ com.choicemaker.util.StringUtils#join(String[]) } */
	public static String join(String[] s) {
		return com.choicemaker.util.StringUtils.join(s);
	}

	/** See {@ com.choicemaker.util.StringUtils#join(String[], char) } */
	public static String join(String[] s, char delim) {
		return com.choicemaker.util.StringUtils.join(s, delim);
	}

	/** See {@ com.choicemaker.util.StringUtils#join(String[], String) } */
	public static String join(String[] s, String delim) {
		return com.choicemaker.util.StringUtils.join(s, delim);
	}

	/** See {@ com.choicemaker.util.StringUtils#keepLettersAndSpaces(String) } */
	public static String keepLettersAndSpaces(String in) {
		return com.choicemaker.util.StringUtils.keepLettersAndSpaces(in);
	}

	/** See {@ com.choicemaker.util.StringUtils#nonEmptyString(String) } */
	public static boolean nonEmptyString(String s) {
		return com.choicemaker.util.StringUtils.nonEmptyString(s);
	}

	/** See {@ com.choicemaker.util.StringUtils#numMatchingCharacters(String, String) } */
	public static int numMatchingCharacters(String s1, String s2) {
		return com.choicemaker.util.StringUtils.numMatchingCharacters(s1, s2);
	}

	/** See {@ com.choicemaker.util.StringUtils#overlap(String, String) } */
	public static boolean overlap(String s1, String s2) {
		return com.choicemaker.util.StringUtils.overlap(s1, s2);
	}

	/** See {@ com.choicemaker.util.StringUtils#padLeft(String, int) } */
	public static String padLeft(String s, int len) {
		return com.choicemaker.util.StringUtils.padLeft(s, len);
	}

	/** See {@ com.choicemaker.util.StringUtils#padLeft(String, int, char) } */
	public static String padLeft(String s, int len, char c) {
		return com.choicemaker.util.StringUtils.padLeft(s, len, c);
	}

	/** See {@ com.choicemaker.util.StringUtils#padRight(String, int) } */
	public static String padRight(String s, int len) {
		return com.choicemaker.util.StringUtils.padRight(s, len);
	}

	/** See {@ com.choicemaker.util.StringUtils#padRight(String, int, char) } */
	public static String padRight(String s, int len, char c) {
		return com.choicemaker.util.StringUtils.padRight(s, len, c);
	}

	/** See {@ com.choicemaker.util.StringUtils#parseLong(String) } */
	public static long parseLong(String s) {
		return com.choicemaker.util.StringUtils.parseLong(s);
	}

	/** See {@ com.choicemaker.util.StringUtils#parseLongString(String) } */
	public static long parseLongString(String s) {
		return com.choicemaker.util.StringUtils.parseLongString(s);
	}

	/** See {@ com.choicemaker.util.StringUtils#removeApostrophies(String) } */
	public static String removeApostrophies(String s) {
		return com.choicemaker.util.StringUtils.removeApostrophies(s);
	}

	/** See {@ com.choicemaker.util.StringUtils#removeLeadingNonterminalZeros(String) } */
	public static String removeLeadingNonterminalZeros(String s) {
		return com.choicemaker.util.StringUtils.removeLeadingNonterminalZeros(s);
	}

	/** See {@ com.choicemaker.util.StringUtils#removeLeadingZeros(String) } */
	public static String removeLeadingZeros(String s) {
		return com.choicemaker.util.StringUtils.removeLeadingZeros(s);
	}

	/** See {@ com.choicemaker.util.StringUtils#removeNonDigits(String) } */
	public static String removeNonDigits(String s) {
		return com.choicemaker.util.StringUtils.removeNonDigits(s);
	}

	/** See {@ com.choicemaker.util.StringUtils#removeNonDigitsLetters(String) } */
	public static String removeNonDigitsLetters(String s) {
		return com.choicemaker.util.StringUtils.removeNonDigitsLetters(s);
	}

	/** See {@ com.choicemaker.util.StringUtils#removeNonDigitsLetters(StringBuffer) } */
	public static String removeNonDigitsLetters(StringBuffer s) {
		return com.choicemaker.util.StringUtils.removeNonDigitsLetters(s);
	}

	/** See {@ com.choicemaker.util.StringUtils#removeNonDigitsMaxLength(String, int) } */
	public static String removeNonDigitsMaxLength(String s, int n) {
		return com.choicemaker.util.StringUtils.removeNonDigitsMaxLength(s, n);
	}

	/** See {@ com.choicemaker.util.StringUtils#removeNonLetters(String) } */
	public static String removeNonLetters(String s) {
		return com.choicemaker.util.StringUtils.removeNonLetters(s);
	}

	/** See {@ com.choicemaker.util.StringUtils#removePunctuation(String) } */
	public static String removePunctuation(String s) {
		return com.choicemaker.util.StringUtils.removePunctuation(s);
	}

	/** See {@ com.choicemaker.util.StringUtils#split(String) } */
	public static String[] split(String s) {
		return com.choicemaker.util.StringUtils.split(s);
	}

	/** See {@ com.choicemaker.util.StringUtils#split(String, char) } */
	public static String[] split(String s, char delim) {
		return com.choicemaker.util.StringUtils.split(s, delim);
	}

	/** See {@ com.choicemaker.util.StringUtils#split(String, delim) } */
	public static String[] split(String s, String delim) {
		return com.choicemaker.util.StringUtils.split(s, delim);
	}

	/** See {@ com.choicemaker.util.StringUtils#splitOnNonLetters(String) } */
	public static String[] splitOnNonLetters(String s) {
		return com.choicemaker.util.StringUtils.splitOnNonLetters(s);
	}

	/** See {@ com.choicemaker.util.StringUtils#trimLeadingZeros(String) } */
	public static String trimLeadingZeros(String s) {
		return com.choicemaker.util.StringUtils.trimLeadingZeros(s);
	}

	/** See {@ com.choicemaker.util.StringUtils#toUpperCase(String) } */
	public static String toUpperCase(String s) {
		return com.choicemaker.util.StringUtils.toUpperCase(s);
	}

}
