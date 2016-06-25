package com.choicemaker.demo.simple_person_matching.fake;

import com.choicemaker.cm.core.Decision;
import com.choicemaker.cm.core.ImmutableRecordPair;
import com.choicemaker.cm.core.Record;
import com.choicemaker.demo.simple_person_matching.gendata.gend.Person.PersonBase;

public class PersonMrpComparator {
	
	public static final float FLOATING_POINT_PRECISION = 0.000001f;

	private final DeclaredAccessorComparator<Record> recordComparator = new DeclaredAccessorComparator<>(
			Record.class, true);
	private final DeclaredAccessorComparator<PersonBase> personComparator = new DeclaredAccessorComparator<>(
			PersonBase.class, true);

	public boolean areEqual(ImmutableRecordPair mrp1, ImmutableRecordPair mrp2)
			throws NoSuchMethodException {

		boolean retVal = mrp1 != null && mrp2 != null;
		DetailedComparision: if (retVal) {

			Decision d1 = mrp1.getCmDecision();
			Decision d2 = mrp2.getCmDecision();
			if (d1 == null) {
				retVal = d2 == null;
			} else {
				retVal = d1.equals(d2);
			}
			if (!retVal)
				break DetailedComparision;

			float p1 = mrp1.getProbability();
			float p2 = mrp2.getProbability();
			float diff = Math.abs(p1 - p2);
			retVal = diff < FLOATING_POINT_PRECISION;
			if (!retVal)
				break DetailedComparision;

			Record q1 = mrp1.getQueryRecord();
			Record q2 = mrp2.getQueryRecord();
			retVal = recordComparator.haveEqualAccessorValues(q1, q2);
			if (!retVal)
				break DetailedComparision;
			retVal = personComparator.haveEqualAccessorValues(q1, q2);
			if (!retVal)
				break DetailedComparision;

			Record m1 = mrp1.getMatchRecord();
			Record m2 = mrp2.getMatchRecord();
			retVal = recordComparator.haveEqualAccessorValues(m1, m2);
			if (!retVal)
				break DetailedComparision;
			retVal = personComparator.haveEqualAccessorValues(m1, m2);

		}

		return retVal;
	}

}
