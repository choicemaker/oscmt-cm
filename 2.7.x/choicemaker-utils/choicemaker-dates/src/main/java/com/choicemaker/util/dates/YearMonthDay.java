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

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

/**
 * A utility class that represents a date in a variety of formats:
 * <ul>
 * <li>As a Date object</li>
 * <li>As a standardardized String in the format YYYYMMDD</li>
 * <li>As three positive integers, for year ([1000-9999]), month ([1-12]) and
 * day ([1-31]).</li>
 * </ul>
 * 
 * @author rphall
 */
public final class YearMonthDay {

	private static Logger logger = Logger.getLogger(YearMonthDay.class
			.getName());

	public final static int INVALID_DATE_COMPONENT = -1;
	public final static int PLACEHOLDER_YEAR = 0;
	public final static int PLACEHOLDER_MONTH = 0;
	public final static int PLACEHOLDER_DAY = 0;

	// Gregorian months are 0-based
	public final static Date PLACEHOLDER_DATE = new GregorianCalendar(
			PLACEHOLDER_YEAR, PLACEHOLDER_MONTH - 1, PLACEHOLDER_DAY).getTime();

	public static final String PLACEHOLDER_STRING = "00000000";

	private static final int YYYYMMDD_LENGTH = 8;

	public static final YearMonthDay PLACEHOLDER = new YearMonthDay(
			PLACEHOLDER_YEAR, PLACEHOLDER_MONTH, PLACEHOLDER_DAY,
			PLACEHOLDER_DATE, PLACEHOLDER_STRING);

	public final int year;
	public final int month;
	public final int day;
	public final Date date;
	public final String yyyyMmDd;

	public YearMonthDay(int y, int m, int d) {
		if (y < 1900 || y > 2099) {
			logger.warning("Unexpected year: " + y);
		}
		if (m < 1 || m > 12) {
			logger.warning("Unexpected month: " + m);
		}
		if (d < 1 || d > 31) {
			logger.warning("Unexpected day: " + d);
		}
		this.year = y;
		this.month = m;
		this.day = d;
		// Gregorian months are 0-based
		this.date = new GregorianCalendar(y, m - 1, d).getTime();
		String yyyy = String.format("%04d", y);
		String mm = String.format("%02d", m);
		String dd = String.format("%02d", d);
		this.yyyyMmDd = yyyy + mm + dd;
		assert this.yyyyMmDd.length() == YYYYMMDD_LENGTH;
	}

	/** For testing and placeholders, only */
	YearMonthDay(int y, int m, int d, Date date, String s) {
		this.year = y;
		this.month = m;
		this.day = d;
		this.date = date;
		this.yyyyMmDd = s;
		if (this.yyyyMmDd.length() != YYYYMMDD_LENGTH) {
			String msg = "Invalid date string '" + s + "'";
			throw new IllegalArgumentException(msg);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + day;
		result = prime * result + month;
		result = prime * result + year;
		result = prime * result
				+ ((yyyyMmDd == null) ? 0 : yyyyMmDd.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		YearMonthDay other = (YearMonthDay) obj;
		if (date == null) {
			if (other.date != null) {
				return false;
			}
		} else if (!date.equals(other.date)) {
			return false;
		}
		if (day != other.day) {
			return false;
		}
		if (month != other.month) {
			return false;
		}
		if (year != other.year) {
			return false;
		}
		if (yyyyMmDd == null) {
			if (other.yyyyMmDd != null) {
				return false;
			}
		} else if (!yyyyMmDd.equals(other.yyyyMmDd)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return yyyyMmDd;
	}
}
