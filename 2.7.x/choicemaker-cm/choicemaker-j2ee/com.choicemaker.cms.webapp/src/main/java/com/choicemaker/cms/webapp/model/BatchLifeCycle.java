package com.choicemaker.cms.webapp.model;

import java.util.logging.Logger;

import com.choicemaker.cm.batch.api.BatchJobStatus;

public class BatchLifeCycle {
	
	private static final Logger logger = Logger.getLogger(BatchJobStatus.class.getName());

	public static boolean isTerminalStatus(String strStatus) {
		BatchJobStatus status = null;
		try {
			status = BatchJobStatus.valueOf(strStatus);
		} catch (Exception x) {
			String msg0 = "Invalid status: '%s'";
			String msg = String.format(msg0, strStatus);
			logger.warning(msg);
		}
		boolean retVal = isTerminalStatus(status);
		return retVal;
	}

	public static boolean isTerminalStatus(BatchJobStatus status) {
		boolean retVal = false;
		if (status != null) {
			switch (status) {
			case COMPLETED:
			case FAILED:
			case ABORTED:
				retVal = true;
				break;
			default:
				retVal = false;
			}
		}
		return retVal;
	}

}
