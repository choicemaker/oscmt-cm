package com.choicemaker.util.dates;

import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Parses dates formatted like "MM/DD/YYYY" such as "11/16/1971".
 */
public final class US_DateParser extends AbstractDateParser {

	private static Logger logger = Logger.getLogger(US_DateParser.class
			.getName());

	public static final String REGEX_US = "^(\\d{1,2}|\\s\\d|\\d\\s)\\D(\\d{1,2}|\\s\\d|\\d\\s)\\D(\\d{4,4})$";
	public static final int US_GROUP_YEAR = 3;
	public static final int US_GROUP_MONTH = 1;
	public static final int US_GROUP_DAY = 2;

	private static AtomicReference<Pattern> US_PATTERN = new AtomicReference<>(
			null);
	
	private static final US_DateParser defaultParser = new US_DateParser();
	
	public static final US_DateParser getDefaultParser() {
		return defaultParser;
	}

	public US_DateParser() {
		super(REGEX_US, US_GROUP_YEAR, US_GROUP_MONTH, US_GROUP_DAY);
	}

	// -- Parsing methods

	@Override
	public Pattern getPattern() {
		// Get the date matching pattern, or compile and set it if needed
		boolean wasNull = US_PATTERN.compareAndSet(null,
				Pattern.compile(REGEX_US));
		if (wasNull) {
			String msg = "Compiled the standard date pattern from '" + REGEX_US
					+ "'";
			logger.info(msg);
		}
		Pattern retVal = US_PATTERN.get();
		assert retVal != null;
		assert retVal.pattern().equals(REGEX_US);
		return retVal;
	}

}
