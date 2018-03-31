package com.choicemaker.util.dates;

import com.choicemaker.util.IRegexParser;

/**
 * An extension of IRegexParser&lt;YearMonthDay&gt; which guarantees that the
 * parse method will return a {@link YearMonthDay#PLACEHOLDER placeholder} if
 * the input string cannot be parsed.
 */
public interface IDateParser extends IRegexParser<YearMonthDay> {

	/**
	 * Parses a String value that represents a date into a {@link YearMonthDay}
	 * object.
	 * 
	 * @return a non-null YearMonthDay object. If the input string is null or
	 *         empty, or cannot be parsed, the return value will be a (non-null)
	 *         {@link YearMonthDay#PLACEHOLDER placeholder}; otherwise the
	 *         return value will be a valid YearMonthDay object.
	 */
	@Override
	YearMonthDay parse(String s);

}
