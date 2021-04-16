package com.choicemaker.util.dates;

import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Parses dates formatted like "YYYYxMMxDD" such as "1971-11-16".
 */
public final class ISO_DateParser extends AbstractDateParser {

	private static Logger logger = Logger.getLogger(ISO_DateParser.class
			.getName());

	public static final String REGEX_ISO = "^(\\d{4,4})\\D(\\d{1,2}|\\s\\d|\\d\\s)\\D(\\d{1,2}|\\s\\d|\\d\\s)$";
	public static final int ISO_GROUP_YEAR = 1;
	public static final int ISO_GROUP_MONTH = 2;
	public static final int ISO_GROUP_DAY = 3;

	private static AtomicReference<Pattern> ISO_PATTERN = new AtomicReference<>(
			null);

	private static final ISO_DateParser defaultParser = new ISO_DateParser();

	public static final ISO_DateParser getDefaultParser() {
		return defaultParser;
	}

	public ISO_DateParser() {
		super(REGEX_ISO, ISO_GROUP_YEAR, ISO_GROUP_MONTH, ISO_GROUP_DAY);
	}

	// -- Parsing methods

	@Override
	public Pattern getPattern() {
		// Get the date matching pattern, or compile and set it if needed
		boolean wasNull = ISO_PATTERN.compareAndSet(null,
				Pattern.compile(REGEX_ISO));
		if (wasNull) {
			String msg = "Compiled the standard date pattern from '"
					+ REGEX_ISO + "'";
			logger.info(msg);
		}
		Pattern retVal = ISO_PATTERN.get();
		assert retVal != null;
		assert retVal.pattern().equals(REGEX_ISO);
		return retVal;
	}

}
