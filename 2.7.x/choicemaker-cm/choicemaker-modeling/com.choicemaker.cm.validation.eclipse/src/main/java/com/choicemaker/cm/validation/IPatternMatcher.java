/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checks if a String matches any pattern in a set of patterns.
 *
 * @author rphall
 */
public interface IPatternMatcher {

	/**
	 * Returns the set patterns (as an array) used by this matcher.
	 * @return a non-null array of unique non-null pattterns.
	 */
	public Pattern[] getPatterns();

	/**
	 * Checks if a string is a match to any pattern used by this instance.
	 */
	public boolean isMatch(String s);

	/**
	 * Returns the first match to the specified string, or null if no
	 * patterns match the string.
	 * @return if some pattern matches the input, returns a
	 * non-null Matcher for which the <code>matches</code>
	 * method is true; otherwise returns null.
	 */
	public Matcher getFirstMatch(String s);

	/**
	 * Returns all matches to the specified string, or an empty
	 * array if no patterns match the string.
	 */
	public Matcher[] getAllMatches(String s);

}

