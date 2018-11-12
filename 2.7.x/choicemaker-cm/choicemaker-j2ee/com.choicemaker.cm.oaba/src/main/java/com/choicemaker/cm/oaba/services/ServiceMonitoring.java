package com.choicemaker.cm.oaba.services;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.choicemaker.util.Precondition;

public class ServiceMonitoring {

	/** Interval for checks of whether a job should stop */
	public static final int CONTROL_INTERVAL = 50000;

	/** Local alias for {@link #COUNT_RECORDS_BETWEEN_DEBUG_PRINTS} */
	static final int DEBUG_INTERVAL = CONTROL_INTERVAL / 10;

	public static void logRecordIdCount(Logger log, String fmt, String tag,
			Comparable<?> id, final int count, final int countInfo,
			final int countDebug) {
		Precondition.assertNonEmptyString(fmt);
		assert countInfo > 0;
		assert countDebug > 0;
		assert countInfo >= countDebug;
		if ((count % countInfo == 0 && log.isLoggable(Level.INFO))
				|| (count % countDebug == 0 && log.isLoggable(Level.FINE))) {
			String msg = String.format(fmt, tag, id, count);
			if (log.isLoggable(Level.INFO)) {
				log.info(msg);
			} else {
				log.fine(msg);
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
		if (log.isLoggable(Level.INFO)) {
			String msg = String.format(fmt, tag, id, count);
			log.info(msg);
		} else if (log.isLoggable(Level.FINE)) {
			String msg = String.format(fmt, tag, id, count);
			log.fine(msg);
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
		log.info(msg);
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
		log.info(msg);
	}

}
