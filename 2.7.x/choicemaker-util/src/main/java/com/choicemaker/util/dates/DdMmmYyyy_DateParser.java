package com.choicemaker.util.dates;

import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.choicemaker.util.Precondition;
import com.choicemaker.util.StringUtils;

/**
 * Parses dates formatted like "DDMMMYYYY" such as "19711116".
 */
public final class DdMmmYyyy_DateParser extends AbstractDateParser {

	private static Logger logger = Logger.getLogger(DdMmmYyyy_DateParser.class
			.getName());

	private static enum MMM {
		JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC
	};

	public static final String REGEX_DDMMMYYYY =
		"^(\\d{2,2})(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)(\\d{4,4})$";
	public static final int DDMMMYYYY_GROUP_YEAR = 3;
	public static final int DDMMMYYYY_GROUP_MONTH = 2;
	public static final int DDMMMYYYY_GROUP_DAY = 1;

	private static AtomicReference<Pattern> DDMMMYYYY_PATTERN =
		new AtomicReference<>(null);

	public DdMmmYyyy_DateParser() {
		super(REGEX_DDMMMYYYY, DDMMMYYYY_GROUP_YEAR, DDMMMYYYY_GROUP_MONTH,
				DDMMMYYYY_GROUP_DAY);
	}

	// -- Parsing methods

	/** Converts s to upper case before invoking super.parse(s) */
	@Override
	public YearMonthDay parse(String s) {
		if (s != null) {
			s = s.toUpperCase();
		}
		return super.parse(s);
	}

	@Override
	protected int parseMonthAsInt(int groupIdx, Matcher matcher) {
		assert groupIdx == DDMMMYYYY_GROUP_MONTH;
		Precondition.assertNonNullArgument("null matcher", matcher);
		final String groupName = "month";
		logger.fine("Parsing match group '" + groupName + "', index "
				+ groupIdx);

		int retVal = YearMonthDay.INVALID_DATE_COMPONENT;
		try {
			String t = matcher.group(groupIdx);
			if (t != null) {
				assert t.equals(t.trim());
				assert t.equals(t.toUpperCase());
				assert t.length() == 3;

				try {
					MMM mmm = MMM.valueOf(t);
					retVal = mmm.ordinal() + 1;
				} catch (IllegalArgumentException x) {
					// Unreachable code
					String msg =
						"The MMM enum type has no constant with the specified name '"
								+ t + "'";
					logger.severe(msg);
					throw new Error(msg);
				} catch (NullPointerException x) {
					// Unreachable code
					String msg = "The MMM enum type or t is null";
					logger.severe(msg);
					throw new Error(msg);
				}
			} else {
				String msg = "Match group " + groupIdx + " returned null";
				logger.warning(msg);
			}
		} catch (IllegalStateException x) {
			String msg =
				"Match group " + groupIdx
						+ ": No match has yet been attempted, "
						+ "or the previous match operation failed";
			logger.warning(msg);
		} catch (IndexOutOfBoundsException x) {
			String msg =
				"Match group " + groupIdx
						+ ": There is no capturing group in the pattern "
						+ "with the given index " + groupIdx;
			logger.warning(msg);
		}
		return retVal;
	}

	@Override
	public Pattern getPattern() {
		// Get the date matching pattern, or compile and set it if needed
		boolean wasNull =
			DDMMMYYYY_PATTERN.compareAndSet(null,
					Pattern.compile(REGEX_DDMMMYYYY));
		if (wasNull) {
			String msg =
				"Compiled the standard date pattern from '" + REGEX_DDMMMYYYY
						+ "'";
			logger.info(msg);
		}
		Pattern retVal = DDMMMYYYY_PATTERN.get();
		assert retVal != null;
		assert retVal.pattern().equals(REGEX_DDMMMYYYY);
		return retVal;
	}

}
