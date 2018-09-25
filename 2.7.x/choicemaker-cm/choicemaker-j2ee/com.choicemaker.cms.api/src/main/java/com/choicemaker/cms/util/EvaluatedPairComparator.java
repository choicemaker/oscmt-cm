package com.choicemaker.cms.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

import com.choicemaker.client.api.DataAccessObject;
import com.choicemaker.client.api.Decision;
import com.choicemaker.client.api.EvaluatedPair;

public class EvaluatedPairComparator<T extends Comparable<T> & Serializable>
		implements Comparator<EvaluatedPair<T>>, Serializable {

	private static final long serialVersionUID = 271L;

	private final IdentifiableComparator<T> idComparator;

	public EvaluatedPairComparator() {
		this(true);
	}

	public EvaluatedPairComparator(boolean enableAssertions) {
		boolean b = false;
		assert b = enableAssertions;
		this.idComparator = new IdentifiableComparator<T>(b);
	}

	public boolean equals(EvaluatedPair<T> p1, EvaluatedPair<T> p2) {
		boolean retVal = (p1 == p2);

		NonNullComparison: if (retVal == false && p1 != null && p2 != null) {
			DataAccessObject<T> q1 = p1.getRecord1();
			DataAccessObject<T> q2 = p2.getRecord1();
			retVal = (this.idComparator.equals(q1, q2));
			if (!retVal)
				break NonNullComparison;

			DataAccessObject<T> c1 = p1.getRecord2();
			DataAccessObject<T> c2 = p2.getRecord2();
			retVal = (this.idComparator.equals(c1, c2));
			if (!retVal)
				break NonNullComparison;

			Decision d1 = p1.getMatchDecision();
			Decision d2 = p2.getMatchDecision();
			retVal = (d1 == d2 || d1.equals(d2));
			if (!retVal)
				break NonNullComparison;

			int b1 = Float.floatToIntBits(p1.getMatchProbability());
			int b2 = Float.floatToIntBits(p2.getMatchProbability());
			retVal = (b1 == b2);
			if (!retVal)
				break NonNullComparison;

			String[] n1 = p1.getNotes();
			String[] n2 = p2.getNotes();
			retVal = Arrays.equals(n1, n2);
		}
		return retVal;
	}

	@SuppressWarnings("unused")
	@Override
	public int compare(EvaluatedPair<T> p1, EvaluatedPair<T> p2) {
		int retVal;
		if (equals(p1, p2)) {
			retVal = 0;
		} else if (p1 == null) {
			assert p2 != null;
			retVal = -1;
		} else if (p2 == null) {
			assert p1 == null;
			retVal = +1;
		} else {
			assert p1 != null && p2 != null;
			NonNullComparision: {
				// TODO stub
				throw new Error("not yet implemented");
			}
		}
		return retVal;
	}

}
