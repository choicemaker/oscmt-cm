/*******************************************************************************
 * Copyright (c) 2003, 2020 ChoiceMaker LLC and others.
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
package com.choicemaker.client.api;

import java.io.Serializable;
import java.util.List;

public interface MatchGroup<T extends Comparable<T> & Serializable>
		extends Serializable {

	/** @return a unique identifier for the group */
	String getGroupId();

	/**
	 * @return the query record against which the candidate records are
	 * compared.
	 */
	DataAccessObject<T> getQueryRecord();

	List<DataAccessObject<T>> getCandidateRecords();

	/**
	 * @return a list of all EvaluatedPairs between the query record and the
	 * candidate records.
	 */
	List<QueryCandidatePair<T>> getQueryCandidatePairs();

	/**
	 * @param candidate a non-null instance
	 * @return the EvaluatedPair between the query record and the specified
	 * candidate record.
	 */
	QueryCandidatePair<T> getQueryCandidatePair(DataAccessObject<T> candidate);
}
