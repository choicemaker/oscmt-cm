/*******************************************************************************
 * Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.cm.oaba.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.choicemaker.util.Precondition;

public class RecordTransferLogging {

	/** Interval for checks of whether a job should stop */
	public static final int CONTROL_INTERVAL = 50000;

	/** Finer grained interval for debugging */
	public static final int DEBUG_INTERVAL = CONTROL_INTERVAL / 10;

	public static void logRecordIdCount(Logger log, String fmt, String tag,
			Comparable<?> id, final int count, final int countInfo,
			final int countDebug) {
		Precondition.assertNonEmptyString(fmt);
		assert countInfo > 0;
		assert countDebug > 0;
		assert countInfo >= countDebug;
		if ((count % countInfo == 0 && log.isLoggable(Level.FINE))
				|| (count % countDebug == 0 && log.isLoggable(Level.FINER))) {
			String msg = String.format(fmt, tag, id, count);
			if (log.isLoggable(Level.FINE)) {
				log.fine(msg);
			} else {
				log.finer(msg);
			}
		}
	}

	public static void logRecordIdCount(Logger log, String fmt, String tag,
			Comparable<?> id, final int count, final int countInfo) {
		logRecordIdCount(log, fmt, tag, id, count, countInfo,
				countInfo);
	}

	public static void logRecordIdCount(Logger log, String fmt, String tag,
			Comparable<?> id, int count) {
		Precondition.assertNonEmptyString(fmt);
		if (log.isLoggable(Level.FINE)) {
			String msg = String.format(fmt, tag, id, count);
			log.fine(msg);
		} else if (log.isLoggable(Level.FINER)) {
			String msg = String.format(fmt, tag, id, count);
			log.finer(msg);
		}
	}

	public static final String FS0 = "%s Staging acquisition (msecs): %d";
	public static final String FM0 = "%s Master acquisition (msecs): %d";
	public static final String FT0 = "%s Total acquisition (msecs): %d";

	public static final String FS1 = "%s Staging record '%s' / count '%d'";
	public static final String FM1 = "%s Master record '%s' / count '%d'";

	public static final String FS2 =
		"%s Staging records: %d, msecs: %d, recs/msec: %2.1f";
	public static final String FM2 =
		"%s Master records: %d, msecs: %d, recs/msec: %2.1f";
	public static final String FT2 =
		"%s Total records: %d, msecs: %d, recs/msec: %2.1f";

	public static void logConnectionAcquisition(Logger log, String fmt,
			String tag, long acquireMsecs) {
		String msg = String.format(fmt, tag, acquireMsecs);
		log.fine(msg);
	}

	public static void logTransferRate(Logger log, String fmt, String tag,
			int count, long msecs) {
		final float rate;
		if (msecs == 0) {
			rate = 0f;
		} else {
			rate = ((float) count) / msecs;
		}
		String msg = String.format(fmt, tag, count, msecs, rate);
		log.fine(msg);
	}

}
