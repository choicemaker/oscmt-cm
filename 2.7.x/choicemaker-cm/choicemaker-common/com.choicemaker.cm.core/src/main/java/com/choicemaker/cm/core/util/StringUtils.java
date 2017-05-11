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

	/**
	 * Returns true iff <code>s</code> is non-null and contains at least one
	 * letter.
	 * 
	 * @param s
	 *            the input String
	 * @return true iff <code>s</code> contains at least one letter
	 */
	public static boolean containsDigits(String s) {
		return com.choicemaker.util.StringUtils.containsDigits(s);
	}

	/**
	 * Returns true iff <code>s</code> is non-null and contains at least one
	 * letter or digit
	 * 
	 * @param s
	 *            the input String
	 * @return true iff <code>s</code> contains at least one letter or digit
	 */
	public static boolean containsDigitsOrLetters(String s) {
		return com.choicemaker.util.StringUtils.containsDigitsOrLetters(s);
	}

	/**
	 * Returns true iff <code>s</code> is non-null and contains at least one
	 * letter.
	 * 
	 * @param s
	 *            the input String
	 * @return true iff <code>s</code> contains at least one letter
	 */
	public static boolean containsLetters(String s) {
		return com.choicemaker.util.StringUtils.containsLetters(s);
	}

	/**
	 * Returns true iff <code>s</code> is non-null and contains non-digit
	 * characters.
	 * 
	 * @param s
	 *            the input String
	 * @return true iff <code>s</code> contains at least one character that is
	 *         not a digit.
	 */
	public static boolean containsNonDigits(String s) {
		return com.choicemaker.util.StringUtils.containsNonDigits(s);
	}

	/**
	 * Returns true iff <code>s</code> is non-null and contains non-letter
	 * characters.
	 * 
	 * @param s
	 *            the input String
	 * @return true iff <code>s</code> contains at least one character that is
	 *         not a letter
	 */
	public static boolean containsNonLetters(String s) {
		return com.choicemaker.util.StringUtils.containsNonLetters(s);
	}

	/**
	 * Returns true iff <code>s</code> is non-null and contains at least one
	 * character that is not a letter, digit, or underscore character.
	 * 
	 * @param s
	 *            the input String
	 * @return true iff <code>s</code> contains at least one non-word character
	 */
	public static boolean containsNonWordChars(String s) {
		return com.choicemaker.util.StringUtils.containsNonWordChars(s);
	}

	/**
	 * Returns the number of digits in <code>str</code>.
	 * 
	 * @param s
	 *            the input String
	 * @return the number of digits found in <code>s</code>
	 */
	public static int countDigits(String s) {
		return com.choicemaker.util.StringUtils.countDigits(s);
	}

	/**
	 * Returns the number of digits in <code>s</code>, with a max of
	 * <code>limit</code>. Once the <code>limit</code>-th character is
	 * encountered, this method returns <code>limit</code>.
	 * 
	 * @param s
	 *            the input String
	 * @param limit
	 *            upper bound on the returned value
	 * @return the number of digits found in <code>s</code>
	 */
	public static int countDigits(String s, int limit) {
		return com.choicemaker.util.StringUtils.countDigits(s, limit);
	}

	/**
	 * Returns the number of letters in <code>s</code>.
	 * 
	 * @param s
	 *            the input String
	 * @return the number of letters found in <code>s</code>
	 */
	public static int countLetters(String s) {
		return com.choicemaker.util.StringUtils.countLetters(s);
	}

	/**
	 * Counts the number of occurrences of the {@code needle} character in the
	 * {@code haystack} string.
	 * 
	 * @param haystack
	 *            the string to be searched. If {@code haystack} is null,
	 *            returns 0.
	 * @param needle
	 *            the character to be counted
	 * @return returns the number of occurrences of {@code needle} in
	 *         {@code haystack} or 0 if {@code haystack} is null
	 */
	public static int countOccurrences(String haystack, char needle) {
		return com.choicemaker.util.StringUtils.countOccurrences(haystack, needle);
	}

	/**
	 * Returns a boolean array whose length is the length of the input string
	 * and whose elements are true at the indices where the string contains
	 * digits.
	 * 
	 * @param s
	 *            the input String
	 * @return a boolean array whose length is the length of the input string
	 *         and whose elements are true at the indices where the string
	 *         contains digits.
	 */
	public static boolean[] findNumbers(String s) {
		return com.choicemaker.util.StringUtils.findNumbers(s);
	}

	/**
	 * Returns the first character of a string or <code>null</code> if the
	 * latter is empty.
	 * 
	 * @param s
	 *            The string from which the first character is to be returned.
	 * @return The first character of <code>s</code> or <code>'\0'</code> if the
	 *         latter is empty.
	 */
	public static char getChar(String s) {
		return com.choicemaker.util.StringUtils.getChar(s);
	}

	/**
	 * Joins <code>s1</code> and <code>s2</code> with a space.
	 * 
	 * @param s1
	 *            the first String
	 * @param s2
	 *            the second String
	 * @return <code>s1 + &quot; &quot; + s2</code>
	 */
	public static String join(String s1, String s2) {
		return com.choicemaker.util.StringUtils.join(s1,s2);
	}

	/**
	 * Joins <code>s1</code>, <code>s2</code>, and <code>s3</code> with spaces.
	 * 
	 * @param s1
	 *            the first String
	 * @param s2
	 *            the second String
	 * @param s3
	 *            the third String
	 * @return <code>s1 + &quot; &quot; + s2 + &quot; &quot; + s3</code>
	 */
	public static String join(String s1, String s2, String s3) {
		return com.choicemaker.util.StringUtils.join(s1, s2, s3);
	}

	/**
	 * Joins <code>s1</code>, <code>s2</code>, <code>s3</code>, and
	 * <code>s4</code> with spaces.
	 * 
	 * @param s1
	 *            the first String
	 * @param s2
	 *            the second String
	 * @param s3
	 *            the third String
	 * @param s4
	 *            the fourth String
	 * @return <code>s1 + &quot; &quot; + s2 + &quot; &quot; + s3 + &quot; &quot; + s4</code>
	 */
	public static String join(String s1, String s2, String s3, String s4) {
		return com.choicemaker.util.StringUtils.join(s1, s2, s3, s4);
	}

	/**
	 * Joins the elements of <code>s</code> with spaces. Null elements are
	 * skipped and do not cause space to be inserted into the returned String.
	 * 
	 * @param s
	 *            an array of Strings to join
	 * @return the elements of <code>s</code> joined with spaces.
	 */
	public static String join(String[] s) {
		return com.choicemaker.util.StringUtils.join(s);
	}

	/**
	 * Joins the elements of <code>s</code> with <code>delim</code>. Null
	 * elements are skipped and do not cause the delimter to be inserted.
	 * 
	 * @param s
	 *            an array of Strings to join
	 * @param delim
	 *            the delimiter
	 * @return the elements of <code>s</code> joined by <code>delim</code>
	 */
	public static String join(String[] s, char delim) {
		return com.choicemaker.util.StringUtils.join(s, delim);
	}

	/**
	 * Joins the elements of <code>s</code> with <code>delim</code>. Null
	 * elements are skipped and do not cause the delimter to be inserted.
	 * 
	 * @param s
	 *            an array of Strings to join
	 * @param delim
	 *            the delimiter
	 * @return the elements of <code>s</code> joined by <code>delim</code>
	 */
	public static String join(String[] s, String delim) {
		return com.choicemaker.util.StringUtils.join(s, delim);
	}

	/**
	 * Returns a copy of s with everything except letters and spaces removed. If
	 * the input is <code>null</code>, returns <code>null</code>.
	 * 
	 * @param in
	 *            the input string
	 * @return a string with only letters and spaces
	 */
	public static String keepLettersAndSpaces(String in) {
		return com.choicemaker.util.StringUtils.keepLettersAndSpaces(in);
	}

	/**
	 * Returns <code>true</code> iff <code>s</code> is not null and
	 * <code>s.trim().length() &gt; 0</code>.
	 *
	 * @param s
	 *            The string to be tested.
	 * @return whether <code>s</code> is neither <code>null</code> nor
	 *         <code>""</code>.
	 */
	public static boolean nonEmptyString(String s) {
		return com.choicemaker.util.StringUtils.nonEmptyString(s);
	}

	/**
	 * Returns the number of position-wise character matches between the two
	 * input Strings. For example
	 * <p>
	 * <code>numMatchingCharacters(&quot;ABCD&quot;, &quot;AXXD&quot;)</code>
	 * </p>
	 * returns 2.
	 * 
	 * @param s1
	 *            the first string
	 * @param s2
	 *            the second string
	 * @return the number of position-wise character matches between
	 *         <code>s1</code> and <code>s2</code>
	 */
	public static int numMatchingCharacters(String s1, String s2) {
		return com.choicemaker.util.StringUtils.numMatchingCharacters(s1, s2);
	}

	/**
	 * Returns true if one string begins or ends with the other.
	 * 
	 * @param s1
	 *            the first String
	 * @param s2
	 *            the second String
	 * @return true if one string begins or ends with the other.
	 */
	public static boolean overlap(String s1, String s2) {
		return com.choicemaker.util.StringUtils.overlap(s1, s2);
	}

	/**
	 * Returns a version of <code>s</code> with zeros appended to the left until
	 * it is at least <code>len</code> characters long. If <code>s</code> is
	 * already <code>len</code> characters long or longer, returns
	 * <code>s</code> in its entirety.
	 * 
	 * @param s
	 *            the String to pad
	 * @param len
	 *            the desired length
	 * @return a version of <code>s</code> padded to length <code>len</code>
	 *         with zeros if necessary
	 */
	public static String padLeft(String s, int len) {
		return com.choicemaker.util.StringUtils.padLeft(s, len);
	}

	/**
	 * Returns a version of <code>s</code> with zeros appended to the left until
	 * it is at least <code>len</code> characters long. If <code>s</code> is
	 * already <code>len</code> characters long or longer, returns
	 * <code>s</code> in its entirety.
	 * 
	 * For example,
	 * <p>
	 * <code>padLeft(&quot;8675309&quot;, 10, '0')</code>
	 * </p>
	 * returns <code>&quot;0008675309&quot;</code>.
	 * 
	 * @param s
	 *            the String to pad
	 * @param len
	 *            the desired length
	 * @param c
	 *            the char to pad with
	 * @return a version of <code>s</code> padded to length <code>len</code>
	 *         with <code>c</code> if necessary
	 */
	public static String padLeft(String s, int len, char c) {
		return com.choicemaker.util.StringUtils.padLeft(s, len, c);
	}

	/**
	 * Returns a version of <code>s</code> with zeros appended to the right
	 * until it is at least <code>len</code> characters long. If <code>s</code>
	 * is already <code>len</code> characters long or longer, returns
	 * <code>s</code> in its entirety.
	 * 
	 * @param s
	 *            the String to pad
	 * @param len
	 *            the desired length
	 * @return a version of <code>s</code> padded to length <code>len</code>
	 *         with zeros if necessary
	 */
	public static String padRight(String s, int len) {
		return com.choicemaker.util.StringUtils.padRight(s, len);
	}

	/**
	 * Returns a version of <code>s</code> with zeros appended to the right
	 * until it is at least <code>len</code> characters long. If <code>s</code>
	 * is already <code>len</code> characters long or longer, returns
	 * <code>s</code> in its entirety.
	 * 
	 * @param s
	 *            the String to pad
	 * @param len
	 *            the desired length
	 * @param c
	 *            the char to pad with
	 * @return a version of <code>s</code> padded to length <code>len</code>
	 *         with <code>c</code> if necessary
	 */
	public static String padRight(String s, int len, char c) {
		return com.choicemaker.util.StringUtils.padRight(s, len, c);
	}

	/**
	 * Return the <code>long</code> value of input <code>String</code>
	 * <code>s</code>. If <code>s</code> is null or has no length, returns
	 * <code>-1</code>. Otherwise, returns <code>Long.parseLong(s)</code>.
	 * 
	 * @param s
	 *            the string for which to return the long value
	 * @return the <code>long</code> value of s
	 * @throws NumberFormatException
	 *             if s cannot be converted to a long.
	 */
	public static long parseLong(String s) {
		return com.choicemaker.util.StringUtils.parseLong(s);
	}

	/**
	 * Return the <code>long</code> value of input <code>String</code>
	 * <code>s</code> after removing all non-digits. If <code>s</code> is null
	 * or has no digits, this method returns <code>-1</code>.
	 * 
	 * updated by PC on 2/12/07. Handles the case where the string is longer
	 * than LONG.MAXVALUE.
	 * 
	 * @param s
	 *            the string to convert to a <code>long</code>
	 * @return the <code>long</code> value of s
	 */
	public static long parseLongString(String s) {
		return com.choicemaker.util.StringUtils.parseLongString(s);
	}

	/**
	 * Remove the apostrophes from the input String.
	 * 
	 * @param s
	 *            the input String
	 * @return the input with apostrophes removed
	 * @deprecated Will be removed in ChoiceMaker v3.0
	 */
	@Deprecated
	public static String removeApostrophies(String s) {
		return com.choicemaker.util.StringUtils.removeApostrophies(s);
	}

	/**
	 * Trims the input string and strips all leading zeros, but leaves any
	 * terminal zero. NOTE: any leading white space is NOT trimmed.
	 * 
	 * <pre>
	 *       null --&gt; null
	 * "        " --&gt; "        "
	 * "       0" --&gt; "       0"
	 * "00000009" --&gt; "9"
	 * "00000000" --&gt; "0"
	 * </pre>
	 * 
	 * @param s
	 *            may be null
	 * @return possibly null
	 */
	public static String removeLeadingNonterminalZeros(String s) {
		return com.choicemaker.util.StringUtils.removeLeadingNonterminalZeros(s);
	}

	/**
	 * Trims all leading zeros, even a terminal zero. NOTE: any leading white
	 * space is NOT trimmed.
	 * 
	 * <pre>
	 *       null --&gt; null
	 * "        " --&gt; "        "
	 * "       0" --&gt; "       0"
	 * "00000009" --&gt; "9"
	 * "00000000" --&gt; ""
	 * </pre>
	 * 
	 * @param s
	 *            may be null
	 * @return possibly null
	 */
	public static String removeLeadingZeros(String s) {
		return com.choicemaker.util.StringUtils.removeLeadingZeros(s);
	}

	/**
	 * Returns a copy of <code>s</code> with all non-digit characters removed.
	 * 
	 * @param s
	 *            the input String
	 * @return a copy with non-digits removed
	 */
	public static String removeNonDigits(String s) {
		return com.choicemaker.util.StringUtils.removeNonDigits(s);
	}

	/**
	 * Returns a copy of s with all non-(digit or letter) characters removed. If
	 * the input is <code>null</code>, returns <code>null</code>.
	 * 
	 * @param s
	 *            the input string
	 * @return a version of s with non-digits and -letters removed
	 */
	public static String removeNonDigitsLetters(String s) {
		return com.choicemaker.util.StringUtils.removeNonDigitsLetters(s);
	}

	/**
	 * Returns a copy of s with all non-(digit or letter) characters removed.
	 * 
	 * @param s
	 *            the input String
	 * @return a copy of the input with all non-digit and -letter characters
	 *         removed
	 * @deprecated Will be removed in ChoiceMaker v3.0
	 * @see #removeNonDigitsLetters(java.lang.String)
	 */
	@Deprecated
	public static String removeNonDigitsLetters(StringBuffer s) {
		return com.choicemaker.util.StringUtils.removeNonDigitsLetters(s);
	}

	/**
	 * Returns a copy of <code>s</code> with all non-digit characters removed
	 * and only keep the last n characters if s.length is greater than n.
	 * 
	 * @param s
	 *            - the input String
	 * @param n
	 *            - the maximum number of characters to return. If s.length is
	 *            greater than n, then return the last n letters.
	 * @return a string with only non-digit characters
	 */
	public static String removeNonDigitsMaxLength(String s, int n) {
		return com.choicemaker.util.StringUtils.removeNonDigitsMaxLength(s, n);
	}

	/**
	 * Returns a copy of s with all non-letters removed. If the input is
	 * <code>null</code>, returns <code>null</code>.
	 * 
	 * @param s
	 *            the input string
	 * @return a version of s with non-letters removed
	 */
	public static String removeNonLetters(String s) {
		return com.choicemaker.util.StringUtils.removeNonLetters(s);
	}

	/**
	 * Returns the same input string but with the following replacements:
	 * <code>'.'</code> with <code>';'</code> and <code>','</code> with
	 * <code>spaces</code> and simply remove <code>"'"</code>. Does not create
	 * leading spaces. Removes all trailing spaces. FIXME: move this mis-named
	 * method to AddressParser package com.choicemaker.cm.matching.en.us (the
	 * only place it is used)
	 * 
	 * @param s
	 *            the input String
	 * @return a mangled version of the input
	 * @deprecated Will be removed in ChoiceMaker v3.0
	 */
	@Deprecated
	public static String removePunctuation(String s) {
		return com.choicemaker.util.StringUtils.removePunctuation(s);
	}

	/**
	 * Splits <code>s</code> on whitespace using a
	 * <code>java.util.StringTokenizer</code>.
	 * 
	 * @param s
	 *            String to split
	 * @return an array of <code>s</code>'s split pieces
	 */
	public static String[] split(String s) {
		return com.choicemaker.util.StringUtils.split(s);
	}

	/**
	 * Splits <code>s</code> on <code>delim</code> using a
	 * <code>java.util.StringTokenizer</code>.
	 * 
	 * @param s
	 *            String to split
	 * @param delim
	 *            delimiter on which to split
	 * @return an array of <code>s</code>'s split pieces
	 */
	public static String[] split(String s, char delim) {
		return com.choicemaker.util.StringUtils.split(s, delim);
	}

	/**
	 * Splits <code>s</code> on <code>delim</code> using a
	 * <code>java.util.StringTokenizer</code>.
	 * 
	 * @param s
	 *            String to split
	 * @param delim
	 *            delimiter on which to split
	 * @return an array of <code>s</code>'s split pieces
	 */
	public static String[] split(String s, String delim) {
		return com.choicemaker.util.StringUtils.split(s, delim);
	}

	public static String[] splitOnNonLetters(String s) {
		return com.choicemaker.util.StringUtils.splitOnNonLetters(s);
	}

	/**
	 * Same functionality as {@link #removeLeadingZeros(String). All leading
	 * zeros, even if it is also the last character in the String, are removed.
	 */
	public static String trimLeadingZeros(String s) {
		return com.choicemaker.util.StringUtils.trimLeadingZeros(s);
	}

	public static String toUpperCase(String s) {
		return com.choicemaker.util.StringUtils.toUpperCase(s);
	}

}
