package com.choicemaker.util.dates;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.choicemaker.util.Precondition;

public class CompositeDateParser implements IDateParser {

	private final List<IDateParser> parsers;
	private AtomicReference<Pattern> disjunction = new AtomicReference<>();

	public CompositeDateParser(Iterable<IDateParser> i) {
		Precondition.assertNonNullArgument("null iterable", i);

		int idx = 0;
		parsers = new ArrayList<>();
		for (IDateParser p : i) {
			Precondition.assertNonNullArgument("null parser, index " + idx, p);
			parsers.add(p);
			++idx;
		}
	}

	public CompositeDateParser(IDateParser[] dateParsers) {
		Precondition.assertNonNullArgument("null array", dateParsers);

		int idx = 0;
		parsers = new ArrayList<>();
		for (IDateParser p : dateParsers) {
			Precondition.assertNonNullArgument("null parser, index " + idx, p);
			parsers.add(p);
			++idx;
		}
	}

	/**
	 * Iterates over the input list of parsers and returns at the first result
	 * that is not a placeholder. NOTE: does not use the disjunction pattern nor
	 * the disjunction matcher.
	 */
	@Override
	public YearMonthDay parse(String s) {
		YearMonthDay retVal = YearMonthDay.PLACEHOLDER;
		for (IDateParser p : parsers) {
			retVal = p.parse(s);
			if (!YearMonthDay.PLACEHOLDER.equals(retVal)) {
				break;
			}
		}
		return retVal;
	}

	/**
	 * Returns the disjunction of all the parser patterns. Possibly (certainly?)
	 * useless, but required by the IDateParser interface.
	 */
	@Override
	public Pattern getPattern() {
		if (disjunction.get() == null) {
			String s = disjunction(parsers);
			Pattern p = Pattern.compile(s);
			disjunction.compareAndSet(null, p);
		}
		return disjunction.get();
	}

	/**
	 * Returns a matcher from the disjunction of all the parser patterns.
	 * Possibly (certainly?) useless, but required by the IDateParser interface.
	 * 
	 * @throws NullPointerException
	 *             if the input string is null
	 */
	@Override
	public Matcher matcher(String s) {
		return getPattern().matcher(s);
	}

	/** Encloses a non-null pattern within parentheses */
	static String patternGuard(String s) {
		String retVal = null;
		if (s != null) {
			retVal = '(' + s + ')';
		}
		return retVal;
	}

	static String disjunction(Iterable<IDateParser> i) {
		StringBuilder sb = new StringBuilder();
		for (IDateParser p : i) {
			String s = p.getPattern().pattern();
			s = patternGuard(s);
			sb.append(s).append('|');
		}
		String retVal = sb.toString();
		retVal = retVal.substring(0, retVal.length() - 1);
		return retVal;
	}

}
