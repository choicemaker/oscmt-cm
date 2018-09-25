package com.choicemaker.cm.oaba.ejb;

import static com.choicemaker.cm.args.OperationalPropertyNames.PN_MAX_TEMP_PAIRWISE_INDEX;
import static com.choicemaker.cm.args.OperationalPropertyNames.PN_RECORD_MATCHING_MODE;

import java.util.logging.Logger;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.batch.api.OperationalProperty;
import com.choicemaker.cm.batch.api.OperationalPropertyController;
import com.choicemaker.cm.oaba.core.RecordMatchingMode;

public class BatchJobUtils {

	private static final Logger log =
		Logger.getLogger(BatchJobUtils.class.getName());

	public static void setRecordMatchingMode(
			final OperationalPropertyController opPropController,
			final BatchJob job, final RecordMatchingMode mode) {
		if (opPropController == null) {
			throw new IllegalArgumentException("null property controller");
		}
		if (job == null) {
			throw new IllegalArgumentException("null batch job");
		}
		RecordMatchingMode existing =
			getRecordMatchingMode(opPropController, job);

		final String newValue = mode == null ? null : mode.name();
		final String oldValue = existing == null ? null : existing.name();
		if (mode == existing) {
			String msg = "No change to property '" + PN_RECORD_MATCHING_MODE
					+ "', value '" + oldValue + "'";
			log.fine(msg);

		} else {
			assert mode != existing;
			OperationalProperty opProp =
				opPropController.find(job, PN_RECORD_MATCHING_MODE);
			opPropController.remove(opProp);
			opPropController.setJobProperty(job, PN_RECORD_MATCHING_MODE,
					newValue);
			String msg = "Property '" + PN_RECORD_MATCHING_MODE
					+ "' changed from '" + oldValue + "' to '" + newValue + "'";
			log.fine(msg);
		}
	}

	public static RecordMatchingMode getRecordMatchingMode(
			final OperationalPropertyController opPropController,
			final BatchJob job) {
		if (opPropController == null) {
			throw new IllegalArgumentException("null property controller");
		}
		if (job == null) {
			throw new IllegalArgumentException("null batch job");
		}
		final String value =
			opPropController.getJobProperty(job, PN_RECORD_MATCHING_MODE);
		RecordMatchingMode retVal = null;
		if (value == null) {
			retVal = null;
		} else {
			try {
				retVal = RecordMatchingMode.valueOf(value);
			} catch (IllegalArgumentException | NullPointerException x) {
				String msg =
					"Job " + job.getId() + ": invalid value for property '"
							+ PN_RECORD_MATCHING_MODE + "': '" + value + "'";
				log.warning(msg);
				assert retVal == null;
			}
		}

		final String v = retVal == null ? null : retVal.name();
		String msg = PN_RECORD_MATCHING_MODE + ": '" + v + "'";
		log.fine(msg);

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
