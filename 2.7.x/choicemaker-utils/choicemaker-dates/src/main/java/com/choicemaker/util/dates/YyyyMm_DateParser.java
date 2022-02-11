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
 * Parses dates formatted like "YYYYMM" such as "197111".
 */
public final class YyyyMm_DateParser extends AbstractDateParser {

	private static Logger logger = Logger.getLogger(YyyyMm_DateParser.class
			.getName());

	public static final String REGEX_YYYYMM = "^(\\d{4,4})(\\d{2,2})$";
	public static final int YYYYMM_GROUP_YEAR = 1;
	public static final int YYYYMM_GROUP_MONTH = 2;
	public static final int YYYYMM_GROUP_DAY = INVALID_GROUP_IDX;

	private static AtomicReference<Pattern> YYYYMM_PATTERN = new AtomicReference<>(
			null);

	public YyyyMm_DateParser() {
		super(REGEX_YYYYMM, YYYYMM_GROUP_YEAR, YYYYMM_GROUP_MONTH,
				YYYYMM_GROUP_DAY);
	}

	// -- Parsing methods

	@Override
	public Pattern getPattern() {
		// Get the date matching pattern, or compile and set it if needed
		boolean wasNull = YYYYMM_PATTERN.compareAndSet(null,
				Pattern.compile(REGEX_YYYYMM));
		if (wasNull) {
			String msg = "Compiled the standard date pattern from '"
					+ REGEX_YYYYMM + "'";
			logger.info(msg);
		}
		Pattern retVal = YYYYMM_PATTERN.get();
		assert retVal != null;
		assert retVal.pattern().equals(REGEX_YYYYMM);
		return retVal;
	}

}
