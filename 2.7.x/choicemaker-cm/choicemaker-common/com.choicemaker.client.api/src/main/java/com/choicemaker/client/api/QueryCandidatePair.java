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

/**
 * Essentially the same as {@link EvaluatedPair}, but with a naming convention
 * in which record1 is regarded as a query record and record2 as a match
 * candidate. In some contexts, these names can add clarity about how record1
 * and record2 are being used, but there's no way to enforce this use.
 * <p>
 * This class also defines equality and hashCode methods that are only
 * appropriate for OnlineMatching, because they assume that in a given
 * collection of records, only the query might have a null identifier; the match
 * candidate is assumed to be drawn from a database where the identifier is
 * non-null.
 * 
 * @author rphall
 *
 * @param <T> The Java type of record ids
 */
public /* final */ class QueryCandidatePair<T extends Comparable<T> & Serializable>
		extends EvaluatedPair<T> {

	private static final long serialVersionUID = 271L;
	private T queryId = this.getRecord1().getId();
	private T candidateId = this.getRecord2().getId();

	public QueryCandidatePair(DataAccessObject<T> q, DataAccessObject<T> m,
			float p, Decision d) {
		this(q, m, p, d, null);
	}

	public QueryCandidatePair(DataAccessObject<T> q, DataAccessObject<T> m,
			float p, Decision d, String[] notes) {
		super(q, m, p, d, notes);
	}

	public DataAccessObject<T> getQueryRecord() {
		return getRecord1();
	}

	public DataAccessObject<T> getMatchCandidate() {
		return getRecord2();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((candidateId == null) ? 0 : candidateId.hashCode());
		result = prime * result + ((queryId == null) ? 0 : queryId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		QueryCandidatePair other = (QueryCandidatePair) obj;
		if (candidateId == null) {
			if (other.candidateId != null)
				return false;
		} else if (!candidateId.equals(other.candidateId))
			return false;
		if (queryId == null) {
			if (other.queryId != null)
				return false;
		} else if (!queryId.equals(other.queryId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "QueryCandidatePair [q="
				+ (getQueryRecord() == null ? null : getQueryRecord().getId())
				+ ", m="
				+ (getMatchCandidate() == null ? null
						: getMatchCandidate().getId())
				+ ", p=" + getMatchProbability() + ", d=" + getMatchDecision()
				+ ", notes='" + getNotesAsDelimitedString() + "']";
	}

}
