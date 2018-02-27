package com.choicemaker.client.api;

import java.io.Serializable;

/**
 * Essentially the same as {@link EvaluatedPair}, but with a naming convention
 * in which record1 is reguarded as a query record and record2 as a match
 * candidate. In some contexts, these names can add clarity about how record1
 * and record2 are being used, but there's no way to enforce this use.
 * 
 * @author rphall
 *
 * @param <T>
 */
public final class QueryCandidatePair<T extends Comparable<T> & Serializable>
		extends EvaluatedPair<T> {

	private static final long serialVersionUID = 271L;

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
