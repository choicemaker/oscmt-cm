package com.choicemaker.client.api;

import java.io.Serializable;

import com.choicemaker.util.Precondition;

public class EvaluatedPair<T extends Comparable<T> & Serializable>
		implements Serializable {

	private static final long serialVersionUID = 271L;

	/** Valid probabilities are in the range 0.0f to 1.0f, inclusive */
	public static boolean isValidProbability(float p) {
		boolean retVal = (p >= 0.0f) && (p <= 1.0f);
		return retVal;
	}

	private final DataAccessObject<T> q;
	private final DataAccessObject<T> m;
	private final float p;
	private final Decision d;
	private final String[] notes;

	public EvaluatedPair(DataAccessObject<T> q, DataAccessObject<T> m, float p,
			Decision d) {
		this(q, m, p, d, null);
	}

	public EvaluatedPair(DataAccessObject<T> q, DataAccessObject<T> m, float p,
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

	public EvaluatedPair(EvaluatedPair<T> p) {
		this(p.getRecord1(), p.getRecord2(), p.getMatchProbability(),
				p.getMatchDecision(), p.getNotes());
	}

	public DataAccessObject<T> getRecord1() {
		return q;
	}

	public DataAccessObject<T> getRecord2() {
		return m;
	}

	public float getMatchProbability() {
		return p;
	}

	public Decision getMatchDecision() {
		return d;
	}

	public String[] getNotes() {
		String[] retVal = new String[notes.length];
		System.arraycopy(this.notes, 0, retVal, 0, this.notes.length);
		return retVal;
	}

	@Override
	public String toString() {
		return "EvaluatedPair [q=" + (q == null ? null : q.getId()) + ", m="
				+ (m == null ? null : m.getId()) + ", p=" + p + ", d=" + d
				+ "]";
	}

}
