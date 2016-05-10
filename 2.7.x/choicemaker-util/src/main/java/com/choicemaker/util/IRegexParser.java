package com.choicemaker.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A date parser that uses regular expressions.
 */
public interface IRegexParser<T> extends IParser<T> {

	/** Returns the regular-expression pattern used by this parser */
	Pattern getPattern();

	/**
	 * Creates a matcher that will match the given input against the pattern
	 * used by this parser. Equivalent to
	 * 
	 * <pre>
	 * getPattern().getMatcher(s)
	 * </pre>
	 */
	Matcher matcher(String s);

}
