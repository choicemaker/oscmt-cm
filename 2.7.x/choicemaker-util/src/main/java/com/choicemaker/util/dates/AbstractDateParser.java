package com.choicemaker.util.dates;

import java.util.logging.Logger;
import java.util.regex.Matcher;

import com.choicemaker.util.Precondition;
import com.choicemaker.util.StringUtils;

/**
 * Parses dates using regular expressions.
 */
public abstract class AbstractDateParser implements IDateParser {

	private static Logger logger = Logger.getLogger(AbstractDateParser.class
			.getName());

	/** Value that indicates a match group does not exist */
	protected static final int INVALID_GROUP_IDX = -1;

	private final String regex;
	private final int groupYearIndex;
	private final int groupMonthIndex;
	private final int groupDayIndex;

	/**
	 * Constructs a date parser based on the specified regular expression.
	 * 
	 * @param regex
	 *            a non-null, non-blank, valid regular expression
	 * @param groupYearIndex
	 *            -- the index of the match group representing the year in the
	 *            regular expression, or {@link #INVALID_GROUP_IDX} if the
	 *            regular expression does not parse a year value
	 * @param groupMonthIndex
	 *            -- the index of the match group representing the month in the
	 *            regular expression, or {@link #INVALID_GROUP_IDX} if the
	 *            regular expression does not parse a month value
	 * @param groupDayIndex
	 *            -- the index of the match group representing the day of the
	 *            month in the regular expression, or {@link #INVALID_GROUP_IDX}
	 *            if the regular expression does not parse a day value
	 */
	public AbstractDateParser(String regex, int groupYearIndex,
			int groupMonthIndex, int groupDayIndex) {
		Precondition.assertNonEmptyString("null or blank regular expression",
				regex);
		this.regex = regex;
		this.groupYearIndex = groupYearIndex;
		this.groupMonthIndex = groupMonthIndex;
		this.groupDayIndex = groupDayIndex;
	}

	// -- Parsing methods

	/** Returns the regular expression used to parse a date string */
	protected final String getRegex() {
		return regex;
	}

	/**
	 * Returns the index for the match group representing the year in a date, or
	 * INVALID_GROUP_IDX if the match group does not exist.
	 */
	protected final int getGroupYearIndex() {
		return groupYearIndex;
	}

	/**
	 * Returns the index for the match group representing the year in a date, or
	 * INVALID_GROUP_IDX if the match group does not exist.
	 */
	protected final int getGroupMonthIndex() {
		return groupMonthIndex;
	}

	/**
	 * Returns the index for the match group representing the year in a date, or
	 * INVALID_GROUP_IDX if the match group does not exist.
	 */
	protected final int getGroupDayIndex() {
		return groupDayIndex;
	}

	protected final int parseGroupAsInt(String groupName, int groupIdx,
			Matcher matcher) {
		Precondition.assertBoolean("invalid group index: " + groupIdx,
				groupIdx >= 0);
		Precondition.assertNonNullArgument("null matcher", matcher);
		logger.fine("Parsing match group '" + groupName + "', index "
				+ groupIdx);

		int retVal = YearMonthDay.INVALID_DATE_COMPONENT;
		if (groupIdx >= 0) {
			try {
				String t = matcher.group(groupIdx);
				if (t != null) {
					t = t.trim();
					t = StringUtils.removeLeadingNonterminalZeros(t);
					try {
						retVal = Integer.valueOf(t);
					} catch (NumberFormatException x) {
						String msg =
							"Invalid number '" + retVal + "' in match group "
									+ groupIdx;
						logger.warning(msg);
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
		} else {
			String msg =
				"Skipping invalid group index '" + groupIdx
						+ " for match group '" + groupName + "'";
			logger.warning(msg);
		}
		return retVal;
	}

	protected int parseYearAsInt(int yearIndex, Matcher matcher) {
		return parseGroupAsInt("year", yearIndex, matcher);
	}

	protected int parseMonthAsInt(int monthIndex, Matcher matcher) {
		return parseGroupAsInt("year", monthIndex, matcher);
	}

	protected int parseDayAsInt(int dayIndex, Matcher matcher) {
		return parseGroupAsInt("year", dayIndex, matcher);
	}

	/**
	 * Converts a string into year, month and day components.
	 * If the input string can not be parsed, then a
	 * {@link YearMonthDay#PLACEHOLDER placeholder} is returned.
	 */
	@Override
	public YearMonthDay parse(String s) {

		YearMonthDay retVal = YearMonthDay.PLACEHOLDER;

		// Process non-empty strings
		if (StringUtils.nonEmptyString(s)) {

			Matcher matcher = this.matcher(s);
			if (matcher.find()) {
				int idx;

				int y = YearMonthDay.PLACEHOLDER_YEAR;
				idx = getGroupYearIndex();
				if (idx != INVALID_GROUP_IDX) {
					y = parseYearAsInt(idx, matcher);
				}

				int m = YearMonthDay.PLACEHOLDER_MONTH;
				idx = getGroupMonthIndex();
				if (idx != INVALID_GROUP_IDX) {
					m = parseMonthAsInt(idx, matcher);
				}

				int d = YearMonthDay.PLACEHOLDER_DAY;
				idx = getGroupDayIndex();
				if (idx != INVALID_GROUP_IDX) {
					d = parseDayAsInt(idx, matcher);
				}

				retVal = new YearMonthDay(y, m, d);

			} else {
				String msg = "Unable to parse date from '" + s + "'";
				logger.fine(msg);
			}
		}
		return retVal;
	}

	@Override
	public Matcher matcher(String s) {
		return getPattern().matcher(s);
	}

}
