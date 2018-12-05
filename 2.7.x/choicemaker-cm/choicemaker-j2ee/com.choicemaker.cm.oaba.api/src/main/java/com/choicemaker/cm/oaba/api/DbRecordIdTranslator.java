package com.choicemaker.cm.oaba.api;

import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;

public interface DbRecordIdTranslator {

	/**
	 * Returns a code representing the Java type of the record key for the
	 * specified batch job.
	 */
	RECORD_ID_TYPE determineRecordIdType(long jobId, String sqlIdSelection);

	/**
	 * Computes and persists translations for a set of record specified by a SQL
	 * query.
	 * 
	 * @param jobId
	 *            the id of the batch job that will own the translations
	 * @param recordIdType
	 *            a code representing the Java type of the record key for the
	 *            specified batch job
	 * @param queryIdSelection
	 *            a SQL select statement that specifies query records for which
	 *            translations will be computed.
	 * @param referenceIdSelection
	 *            a SQL select statement that specifies reference records for
	 *            which translations will be computed.
	 * @return a count off the number of records for which translations were
	 *         computed
	 */
	int computeRecordIdTranslations(long jobId, RECORD_ID_TYPE recordIdType,
			String queryIdSelection, String referenceIdSelection);

}
