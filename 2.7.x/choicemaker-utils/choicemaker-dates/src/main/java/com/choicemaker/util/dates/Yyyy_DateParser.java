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
 * Parses dates formatted like "YYYY" such as "1971".
 */
public final class Yyyy_DateParser extends AbstractDateParser {

	private static Logger logger = Logger.getLogger(Yyyy_DateParser.class
			.getName());

	public static final String REGEX_YYYY = "^(\\d{4,4})$";
	public static final int YYYY_GROUP_YEAR = 1;
	public static final int YYYY_GROUP_MONTH = INVALID_GROUP_IDX;
	public static final int YYYY_GROUP_DAY = INVALID_GROUP_IDX;

	private static AtomicReference<Pattern> YYYY_PATTERN = new AtomicReference<>(
			null);

	public Yyyy_DateParser() {
		super(REGEX_YYYY, YYYY_GROUP_YEAR, YYYY_GROUP_MONTH, YYYY_GROUP_DAY);
	}

	// -- Parsing methods

	@Override
	public Pattern getPattern() {
		// Get the date matching pattern, or compile and set it if needed
		boolean wasNull = YYYY_PATTERN.compareAndSet(null,
				Pattern.compile(REGEX_YYYY));
		if (wasNull) {
			String msg = "Compiled the standard date pattern from '"
					+ REGEX_YYYY + "'";
			logger.info(msg);
		}
		Pattern retVal = YYYY_PATTERN.get();
		assert retVal != null;
		assert retVal.pattern().equals(REGEX_YYYY);
		return retVal;
	}

}
