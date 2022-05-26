/*
 * Copyright (c) 2001, 2022 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.core.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.choicemaker.client.api.Decision;
import com.choicemaker.cm.core.ActiveClues;
import com.choicemaker.cm.core.ClueSet;
import com.choicemaker.cm.core.Evaluator;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.Match;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.RecordSource;

/**
 * Evaluation of matches.
 *
 * @author Martin Buechi
 */
public class RecordDecisionMaker {

	private static Logger logger =
		Logger.getLogger(RecordDecisionMaker.class.getName());
	private static Logger profiler =
		Logger.getLogger("profile." + RecordDecisionMaker.class.getName());

	/**
	 * Returns the sorted set of all records from source <code>src</code> that
	 * match the query record <code>q</code> with a probability of at least
	 * <code>lowerThreshold</code>.
	 *
	 * @param q
	 *            The query record.
	 * @param src
	 *            The record source of match records.
	 * @param model
	 *            The probability model used for the matching.
	 * @param lt
	 *            The differ threshold (minimum match probability for a match
	 *            record to be returned).
	 * @param ut
	 *            The match threshold.
	 * @return The sorted (probability descending) set of <code>Match<code>es.
	 */
	@SuppressWarnings("unchecked")
	public List<Match> getMatches(Record q, RecordSource src,
			ImmutableProbabilityModel model, float lt, float ut)
			throws java.io.IOException {
		int numMatched = 0;
		int numAdded = 0;
		List<Match> matches = new ArrayList<>();
		try {
			long t = System.currentTimeMillis();
			src.open();
			t = System.currentTimeMillis() - t;
			profiler.fine("Time in Blocker.open() " + t);

			t = System.currentTimeMillis();
			Evaluator eval = model.getEvaluator();
			t = System.currentTimeMillis() - t;
			profiler.fine("Time in model.getEvaluator() " + t);

			t = System.currentTimeMillis();
			while (src.hasNext()) {
				++numMatched;
				Match m = eval.getMatch(q, src.getNext(), lt, ut);
				if (m != null) {
					++numAdded;
					matches.add(m);
				}
			}
			Collections.sort(matches);
			t = System.currentTimeMillis() - t;
			profiler.fine("Time in matching " + t);

		} finally {
			src.close();
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Number matched: " + numMatched
					+ ", number above lower threshold: " + numAdded);
		}
		return matches;
	}

	@SuppressWarnings("unchecked")
	public static List<Match> getPairs(Record q, RecordSource src,
			ImmutableProbabilityModel model, float lt, float ut)
			throws IOException {
		int numMatched = 0;
		List<Match> matches = new ArrayList<>();
		try {
			src.open();
			final Evaluator eval = model.getEvaluator();
			final boolean[] toEval = model.getCluesToEvaluate();
			while (src.hasNext()) {
				++numMatched;
				Record m = src.getNext();
				ClueSet cs = model.getClueSet();
				ActiveClues a = cs.getActiveClues(q, m, toEval);
				float p = eval.getProbability(a);
				Decision d = eval.getDecision(a, p, lt, ut);
				matches.add(new Match(d, p, m.getId(), m, a));
			}
			Collections.sort(matches);
		} finally {
			src.close();
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Number matched: " + numMatched);
		}
		return matches;
	}
}
