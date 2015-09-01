package com.choicemaker.cm.io.blocking.automated.offline.server.impl;

import static com.choicemaker.cm.args.OperationalPropertyNames.PN_MAX_TEMP_PAIRWISE_INDEX;
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

	public static void setMaxTempPairwiseIndex(
			OperationalPropertyController opPropController, BatchJob job,
			int max) {
		if (opPropController == null) {
			throw new IllegalArgumentException("null property controller");
		}
		if (job == null) {
			throw new IllegalArgumentException("null batch job");
		}
		if (max < 1) {
			throw new IllegalArgumentException(
					"invalid max index for temporary pairwise result files: "
							+ max);
		}
		String pname = PN_MAX_TEMP_PAIRWISE_INDEX;
		String pvalue = String.valueOf(max);
		opPropController.setJobProperty(job, pname, pvalue);
	}

	public static int getMaxTempPairwiseIndex(
			OperationalPropertyController opPropController, BatchJob job) {
		if (opPropController == null) {
			throw new IllegalArgumentException("null property controller");
		}
		if (job == null) {
			throw new IllegalArgumentException("null batch job");
		}
		String pname = PN_MAX_TEMP_PAIRWISE_INDEX;
		String pvalue = opPropController.getJobProperty(job, pname);
		int retVal = -1;
		try {
			retVal = Integer.parseInt(pvalue);
		} catch (Exception x) {
			assert retVal == -1;
		}
		if (retVal < 1) {
			String msg =
				"invalid max index for temporary pairwise files: " + retVal;
			log.warning(msg);
		}
		return retVal;
	}

	private BatchJobUtils() {
	}

}
