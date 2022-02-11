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
package com.choicemaker.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A date parser that uses regular expressions.
 */
public interface IRegexParser<T> extends IParser<T> {

	/** @return the regular-expression pattern used by this parser */
	Pattern getPattern();

	/**
	 * Creates a matcher that will match the given input against the pattern
	 * used by this parser. Equivalent to
	 * 
	 * <pre>
	 * getPattern().getMatcher(s)
	 * </pre>
	 * 
	 * @param s
	 *            a non-null string
	 * @return a non-null matcher
	 */
	Matcher matcher(String s);

}
