package com.choicemaker.cm.io.blocking.automated.offline.server.impl;

import static com.choicemaker.cm.args.OperationalPropertyNames.PN_RECORD_MATCHING_MODE;

import java.util.logging.Logger;

import com.choicemaker.cm.batch.BatchJob;
import com.choicemaker.cm.batch.OperationalPropertyController;
import com.choicemaker.cm.io.blocking.automated.offline.core.RecordMatchingMode;

public class BatchJobUtils {

	private static final Logger log = Logger.getLogger(BatchJobUtils.class
			.getName());

	public static RecordMatchingMode getRecordMatchingMode(
			final OperationalPropertyController opPropController,
			final BatchJob job) {
		if (opPropController == null) {
			throw new IllegalArgumentException("null property controller");
		}
		if (job == null) {
			throw new IllegalArgumentException("null batch job");
		}
		String value =
			opPropController.getJobProperty(job, PN_RECORD_MATCHING_MODE);
		RecordMatchingMode retVal = null;
		try {
			retVal = RecordMatchingMode.valueOf(value);
		} catch (IllegalArgumentException | NullPointerException x) {
			String msg =
				"Job " + job.getId()
						+ ": Missing or invalid value for property '"
						+ PN_RECORD_MATCHING_MODE + "': '" + value + "'";
			log.warning(msg);
			assert retVal == null;
		}
		return retVal;
	}

	private BatchJobUtils() {
	}

}
