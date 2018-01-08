package com.choicemaker.cms.args;

import java.io.Serializable;
import java.util.Arrays;

import com.choicemaker.cm.core.Decision;
import com.choicemaker.util.Precondition;

public final class EvaluatedPair<T extends Comparable<T> & Serializable>
		implements Serializable {

	private static final long serialVersionUID = 271L;

	private final RemoteRecord<T> q;
	private final RemoteRecord<T> m;
	private final float p;
	private final Decision d;
	private final String[] notes;

	/** Valid probabilities are in the range 0.0f to 1.0f, inclusive */
	public static boolean isValidProbability(float p) {
		boolean retVal = (p >= 0.0f) && (p <= 1.0f);
		return retVal;
	}

	public EvaluatedPair(RemoteRecord<T> q, RemoteRecord<T> m, float p,
			Decision d) {
		this(q, m, p, d, null);
	}

	public EvaluatedPair(RemoteRecord<T> q, RemoteRecord<T> m, float p,
			Decision d, String[] notes) {
		Precondition.assertNonNullArgument("null query", q);
		Precondition.assertNonNullArgument("null candidate", m);
		Precondition.assertBoolean("invalid probability: " + p,
				isValidProbability(p));
		Precondition.assertNonNullArgument("null decision", d);
		this.q = q;
		this.m = m;
		this.p = p;
		this.d = d;
		if (notes == null) {
			this.notes = new String[0];
		} else {
			this.notes = new String[notes.length];
			System.arraycopy(notes, 0, this.notes, 0, notes.length);
		}
	}

	RemoteRecord<T> getQueryRecord() {
		return q;
	}

	RemoteRecord<T> getMatchCandidate() {
		return m;
	}

	float getMatchProbability() {
		return p;
	}

	Decision getMatchDecision() {
		return d;
	}

	String[] getNotes() {
		String[] retVal = new String[notes.length];
		System.arraycopy(this.notes, 0, retVal, 0, this.notes.length);
		return retVal;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((d == null) ? 0 : d.hashCode());
		result = prime * result + ((m == null) ? 0 : m.hashCode());
		result = prime * result + Arrays.hashCode(notes);
		result = prime * result + Float.floatToIntBits(p);
		result = prime * result + ((q == null) ? 0 : q.hashCode());
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
		@SuppressWarnings("unchecked")
		EvaluatedPair<T> other = (EvaluatedPair<T>) obj;
		if (d == null) {
			if (other.d != null)
				return false;
		} else if (!d.equals(other.d))
			return false;
		if (m == null) {
			if (other.m != null)
				return false;
		} else if (!m.equals(other.m))
			return false;
		if (!Arrays.equals(notes, other.notes))
			return false;
		if (Float.floatToIntBits(p) != Float.floatToIntBits(other.p))
			return false;
		if (q == null) {
			if (other.q != null)
				return false;
		} else if (!q.equals(other.q))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EvaluatedPair [q=" + q.getId() + ", m=" + m.getId() + ", p=" + p
				+ ", d=" + d + "]";
	}

}
