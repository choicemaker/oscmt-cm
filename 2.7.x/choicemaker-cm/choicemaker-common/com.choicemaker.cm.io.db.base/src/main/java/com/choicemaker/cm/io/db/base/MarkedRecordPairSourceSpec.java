/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Jun 2, 2004
 *
 */
package com.choicemaker.cm.io.db.base;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.choicemaker.client.api.Decision;
import com.choicemaker.cm.core.MutableMarkedRecordPair;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.RecordSource;
import com.choicemaker.util.Precondition;

public class MarkedRecordPairSourceSpec<T extends Comparable<T> & Serializable> {

	private static final Logger logger =
		Logger.getLogger(MarkedRecordPairSourceSpec.class.getName());

	private List<MarkedRecordPairSpec<T>> spec;

	public MarkedRecordPairSourceSpec() {
		this.spec = new ArrayList<>();
	}

	public void addMarkedPair(T qId, T mId, Decision d) {
		spec.add(new MarkedRecordPairSpec<T>(qId, mId, d));
	}

	/**
	 * Returns a list of entries, one for each previous call to addMarkedPair().
	 * Each entry is a pair of full records (not just ids) and an associated
	 * decision. If either record in some pairs can not be retrieved, a warning
	 * about each problematic pair will be logged, and an exception will be
	 * thrown after all pairs have been attempted.
	 * <p>
	 * If a record id occurs more than once in record source, a warning will be
	 * logged, but no exception will be thrown.
	 * 
	 * @param rs
	 *            a non-null record source
	 * @exception RecordPairRetrievalException
	 *                information about the first problematic pair that could
	 *                not be created.
	 * 
	 */
	public List<MutableMarkedRecordPair<T>> createPairs(RecordSource rs)
			throws IOException {

		Precondition.assertNonNullArgument("record source must be non-null",
				rs);

		HashMap<T, Record<T>> recordMap = new HashMap<>();
		int count = -1;
		try {
			rs.open();
			while (rs.hasNext()) {
				++count;
				@SuppressWarnings("unchecked")
				Record<T> r = rs.getNext();
				assert r != null;
				T id = r.getId();
				assert id != null;
				Record<T> previous = recordMap.put(id, r);
				if (previous != null) {
					logDuplicateRecords(count, previous, r);
				}
			}
		} finally {
			rs.close();
		}

		RecordPairRetrievalException firstException = null;
		List<MutableMarkedRecordPair<T>> pairs = new ArrayList<>(spec.size());
		for (int i = 0; i < spec.size(); i++) {
			MarkedRecordPairSpec<T> s = spec.get(i);
			Record<T> q = recordMap.get(s.getQId());
			Record<T> m = recordMap.get(s.getMId());

			if (q == null && m != null) {
				RecordPairRetrievalException rpre =
					new RecordPairRetrievalException(s.getQId(), s.getMId(),
							RecordPairRetrievalException.Q_RECORD);
				if (firstException == null) {
					firstException = rpre;
				}
				logRecordPairRetrievalException(rpre);

			} else if (q != null && m == null) {
				RecordPairRetrievalException rpre =
					new RecordPairRetrievalException(s.getQId(), s.getMId(),
							RecordPairRetrievalException.M_RECORD);
				if (firstException == null) {
					firstException = rpre;
				}
				logRecordPairRetrievalException(rpre);

			} else if (q == null && m == null) {
				RecordPairRetrievalException rpre =
					new RecordPairRetrievalException(s.getQId(), s.getMId(),
							RecordPairRetrievalException.BOTH);
				if (firstException == null) {
					firstException = rpre;
				}
				logRecordPairRetrievalException(rpre);

			} else {
				MutableMarkedRecordPair<T> mrp =
					new MutableMarkedRecordPair<>();
				mrp.setQueryRecord(q);
				mrp.setMatchRecord(m);
				mrp.setMarkedDecision(s.getDecision());
				mrp.setComment("");
				mrp.setDateMarked(new Date());
				mrp.setSource("");
				mrp.setUser("");

				pairs.add(mrp);
			}
		}
		if (firstException != null) {
			throw firstException;
		}

		return pairs;
	}

	private String idAsString(Record<T> r) {
		T id = r.getId();
		String retVal = id == null ? null : id.toString();
		return retVal;
	}

	private void logDuplicateRecords(int count, Record<T> previous,
			Record<T> current) {
		String pId = idAsString(previous);
		String cId = idAsString(current);
		String msg0 =
			"Duplicate record at index %d: previousId: %s, currentId: %s";
		String msg = String.format(msg0, count, pId, cId);
		logger.warning(msg);
	}

	private void logRecordPairRetrievalException(
			RecordPairRetrievalException rpre) {
		logger.warning(rpre.toString());

	}

	@Override
	public String toString() {
		return "MarkedRecordPairSourceSpec [spec=" + spec + "]";
	}

}
