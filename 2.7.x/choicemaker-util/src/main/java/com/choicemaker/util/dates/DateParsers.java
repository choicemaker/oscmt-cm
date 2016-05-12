package com.choicemaker.util.dates;

import java.util.AbstractMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.choicemaker.util.Precondition;

public final class DateParsers extends AbstractMap<String, IDateParser> {

	private static final Logger logger = Logger.getLogger(DateParsers.class
			.getName());

	private static final DateParsers instance = new DateParsers();

	// Synchronized access
	private final Map<String, IDateParser> namedParsers = new Hashtable<>();

	private DateParsers() {
	}

	public static DateParsers getInstance() {
		return instance;
	}

	/**
	 * Parses a String value that represents a date into a {@link YearMonthDay}
	 * object using the named parser
	 * 
	 * @return a non-null YearMonthDay object. If the input string is null or
	 *         empty, or cannot be parsed, or the named parser is not
	 *         registered, the return value will be a (non-null)
	 *         {@link YearMonthDay#PLACEHOLDER placeholder}; otherwise the
	 *         return value will be a valid YearMonthDay object.
	 */
	public static YearMonthDay parse(String parserName, String date) {
		Precondition.assertNonEmptyString("null or blank parser name",
				parserName);
		YearMonthDay retVal = YearMonthDay.PLACEHOLDER;
		IDateParser parser = getInstance().get(parserName);
		if (parser != null) {
			retVal = parser.parse(date);
		} else {
			String msg = "Unknown parser '" + parserName + "'";
			logger.warning(msg);
		}
		return retVal;
	}

	@Override
	public IDateParser put(String key, IDateParser p) {
		return namedParsers.put(key, p);
	}

	@Override
	public Set<java.util.Map.Entry<String, IDateParser>> entrySet() {
		return namedParsers.entrySet();
	}

}
