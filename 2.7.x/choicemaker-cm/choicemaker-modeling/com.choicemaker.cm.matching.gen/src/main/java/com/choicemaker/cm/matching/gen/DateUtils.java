/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.gen;

import java.util.Date;

import com.choicemaker.cm.core.util.DateHelper;
import com.choicemaker.util.dates.DateUtils2;

/**
 * Utilities for dealing with <code>Date</code>s. Most functionality (except
 * that which is inherited from DateHelper) has moved to
 * {@link com.choicemaker.util.dates.DateUtils2}
 *
 * @author Martin Buechi
 * @deprecated see {@link com.choicemaker.util.dates.DateUtils2}
 */
@Deprecated
public class DateUtils extends DateHelper {

	public static int numbersOnly(Date d) {
		return DateUtils2.numbersOnly(d);
	}

	public static int numericFormat(Date d) {
		return DateUtils2.numericFormat(d);
	}

	public static boolean missingNumber(Date d1, Date d2) {
		return DateUtils2.missingNumber(d1, d2);
	}

	public static float daysApart(Date d1, Date d2) {
		return DateUtils2.daysApart(d1, d2);
	}

	public static boolean moreThanYearApart(Date d1, Date d2) {
		return DateUtils2.moreThanYearApart(d1, d2);
	}

	public static boolean isFirstOfMonth(Date d) {
		return DateUtils2.isFirstOfMonth(d);
	}

	public static boolean isJanuary1(Date d) {
		return DateUtils2.isJanuary1(d);
	}

	public static boolean isFirstOfYear(Date d) {
		return DateUtils2.isFirstOfYear(d);
	}

	public static boolean sameMonthAndYear(Date d1, Date d2) {
		return DateUtils2.sameMonthAndYear(d1, d2);
	}

	public static boolean sameDayAndMonth(Date d1, Date d2) {
		return DateUtils2.sameDayAndMonth(d1, d2);
	}

	public static boolean onlyDecadeOrLastYearDigitDiff(Date d1, Date d2) {
		return DateUtils2.onlyDecadeOrLastYearDigitDiff(d1, d2);
	}

	public static boolean swappedDayMonth(Date d1, Date d2) {
		return DateUtils2.swappedDayMonth(d1, d2);
	}

	public static String yearAndMonth(Date d) {
		return DateUtils2.yearAndMonth(d);
	}

	public static int getYear(Date d) {
		return DateUtils2.getYear(d);
	}

	public static int getMonth(Date d) {
		return DateUtils2.getMonth(d);
	}

	public static int getDayOfMonth(Date d) {
		return DateUtils2.getDayOfMonth(d);
	}

	public static int getCurrentYear() {
		return DateUtils2.getCurrentYear();
	}

	public static String getDateTimeString(Date d) {
		return DateUtils2.getDateTimeString(d);
	}

	public static String getDateString(Date d) {
		return DateUtils2.getDateString(d);
	}

	public static Date getDate(int yyyy, int mm, int dd) {
		return DateUtils2.getDate(yyyy, mm, dd);
	}

	public static boolean isOverlappingWithinMilliseconds(Date startDate1,
			Date endDate1, Date startDate2, Date endDate2, long milliseconds) {
		return DateUtils2.isOverlappingWithinMilliseconds(startDate1, endDate1,
				startDate2, endDate2, milliseconds);
	}

	public static boolean isOverlapping(Date startDate1, Date endDate1,
			Date startDate2, Date endDate2) {
		return DateUtils2.isOverlapping(startDate1, endDate1, startDate2,
				endDate2);
	}

	public static final long MILLIS_PER_DAY = DateUtils2.MILLISECS_PER_DAY;

	public static boolean isOverlappingWithinDays(Date startDate1,
			Date endDate1, Date startDate2, Date endDate2, int days) {
		return DateUtils2.isOverlappingWithinDays(startDate1, endDate1,
				startDate2, endDate2, days);
	}

}
