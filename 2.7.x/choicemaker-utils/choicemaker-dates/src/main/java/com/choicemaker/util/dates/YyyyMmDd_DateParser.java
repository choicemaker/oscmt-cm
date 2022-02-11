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
package com.choicemaker.util.dates;

import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Parses dates formatted like "YYYYMMDD" such as "19711116".
 */
public final class YyyyMmDd_DateParser extends AbstractDateParser {

	private static Logger logger = Logger.getLogger(YyyyMmDd_DateParser.class
			.getName());

	public static final String REGEX_YYYYMMDD = "^(\\d{4,4})(\\d{2,2})(\\d{2,2})$";
	public static final int YYYYMMDD_GROUP_YEAR = 1;
	public static final int YYYYMMDD_GROUP_MONTH = 2;
	public static final int YYYYMMDD_GROUP_DAY = 3;

	private static AtomicReference<Pattern> YYYYMMDD_PATTERN = new AtomicReference<>(
			null);

	public YyyyMmDd_DateParser() {
		super(REGEX_YYYYMMDD, YYYYMMDD_GROUP_YEAR, YYYYMMDD_GROUP_MONTH,
				YYYYMMDD_GROUP_DAY);
	}

	// -- Parsing methods

	@Override
	public Pattern getPattern() {
		// Get the date matching pattern, or compile and set it if needed
		boolean wasNull = YYYYMMDD_PATTERN.compareAndSet(null,
				Pattern.compile(REGEX_YYYYMMDD));
		if (wasNull) {
			String msg = "Compiled the standard date pattern from '"
					+ REGEX_YYYYMMDD + "'";
			logger.info(msg);
		}
		Pattern retVal = YYYYMMDD_PATTERN.get();
		assert retVal != null;
		assert retVal.pattern().equals(REGEX_YYYYMMDD);
		return retVal;
	}

}
