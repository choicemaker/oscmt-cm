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

public interface MergeGroup<T extends Comparable<T> & Serializable>
		extends Serializable {

	/** @return a unique identifier for the group */
	String getGroupId();

	/** @return the match connectivity criteria for the group */
	IGraphProperty getGraphConnectivity();

	/**
	 * @param record a non-null record instance
	 * @return true if the record belongs to this instance, false otherwise
	 */
	boolean containsRecord(DataAccessObject<T> record);

	/** @return the records in the merge group */
	List<DataAccessObject<T>> getGroupRecords();

	/**
	 * @return the evaluated match and hold relationships between records in the
	 * group.
	 */
	List<EvaluatedPair<T>> getGroupPairs();

	/**
	 * @param r1 a non-null record instance
	 * @param r2 a non-null record instance
	 * @return the match or hold relationship between two records in the group.
	 * If either record is not in the group, an illegal argument exception is
	 * thrown.
	 */
	EvaluatedPair<T> getGroupPair(DataAccessObject<T> r1,
			DataAccessObject<T> r2);

}
