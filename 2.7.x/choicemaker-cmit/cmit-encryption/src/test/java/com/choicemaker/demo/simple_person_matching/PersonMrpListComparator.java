package com.choicemaker.demo.simple_person_matching;

import java.util.Iterator;
import java.util.List;

import com.choicemaker.cm.core.ImmutableRecordPair;

public class PersonMrpListComparator {

	public boolean areEqual(List<ImmutableRecordPair> mrps,
			List<ImmutableRecordPair> emrps) throws NoSuchMethodException {

		// Are the lists the same size
		boolean retVal = mrps != null && emrps != null
				&& mrps.size() == emrps.size();

		DetailedComparison: if (retVal) {
			PersonMrpComparator mrpComparator = new PersonMrpComparator();
			Iterator<ImmutableRecordPair> it_emrps = emrps.iterator();
			for (ImmutableRecordPair mrp : mrps) {
				assert it_emrps.hasNext();
				ImmutableRecordPair emrp = it_emrps.next();
				retVal = mrpComparator.areEqual(mrp, emrp);
				if (!retVal) {
					break DetailedComparison;
				}
			}

		}

		return retVal;
	}

}
