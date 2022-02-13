/*******************************************************************************
 * Copyright (c) 2003, 2021 ChoiceMaker LLC and others.
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
package com.choicemaker.cm.oaba.api;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.core.ImmutableRecordIdTranslator;
import com.choicemaker.cm.oaba.core.MutableRecordIdTranslator;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;

public interface DbRecordIdFactory {

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

	<T extends Comparable<T>> ImmutableRecordIdTranslator<T> createImmutableTranslator(
			long jobId, RECORD_ID_TYPE recordIdType,
			String queryIdSelection, String referenceIdSelection)
			throws BlockingException;

}
