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

public interface TransitiveGroup<T extends Comparable<T> & Serializable>
		extends MatchGroup<T> {

	/**
	 * @return a list of merge groups formed by the query record and candidate
	 * records.
	 */
	List<MergeGroup<T>> getMergeGroups();

	/**
	 * @param record a non-null record
	 * @return the merge group to which the specified record belongs. The
	 * specified record may be the query record or any candidate record. If the
	 * specified record does not belong to a merge candidate, or if the
	 * specified record is not the query record or a candidate record, a null
	 * merge candidate is returned.
	 */
	MergeGroup<T> getMergeGroup(DataAccessObject<T> record);

	/**
	 * @param mergeGroupId a non-null, non-empty String value
	 * @return the merge group with the specified merge group id, or null if no
	 * such group exists.
	 */
	MergeGroup<T> getMergeGroup(String mergeGroupId);

}
