/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;

import com.choicemaker.client.api.Decision;

/**
 * Computes the probability that two records match each other,
 * and based on specified thresholds for certainty, classifies
 * the pair as a match, hold or differ.
 *
 * @author rphall
 *
 */
public interface PairEvaluator {

	/**
	 * Compute an evaluation result, regardless of the decision.
	 *
	 * @param   q  The query record.
	 * @param   m  The match record.
	 * @return  The <code>PairEvaluation</code> containing the ID of <code>m</code>;
	 * never null.
	 */
	PairEvaluation evaluate(Record q, Record m, float lt, float ut);

	/**
	 * Compute an evaluation result, but
	 * return null for cases where the decision is differ and no
	 * reporting criteria is triggered.
	 *
	 * @param q
	 *            The query record.
	 * @param m
	 *            The match record.
	 * @return If q and m evaluate to a differ decision, this method will return
	 *         null unless a report modifier is active on some clue or rule.
	 *         Otherwise, this method is the same as invoking
	 *         <code>evaluate(q,m,lt,ut)</code>.
	 */
	PairEvaluation evaluateAndFilter(Record q, Record m, float lt, float ut);

	Firings computeActiveClues(Record q, Record m);
	/** @deprecated */
	@Deprecated
	Firings getActiveClues(Record q, Record m);

	/**
	 * Computes the decision based on the active clues and rules, the match probability,
	 * and the thresholds.
	 *
	 * @param  a  The active clues.
	 * @param  lt  The lower threshold.
	 * @param  ut  The upper threshold.
	 * @return   The decision.
	 */
	Decision computeDecision(Firings a, float lt, float ut);
	/** @deprecated */
	@Deprecated
	Decision computeDecision(Firings a, float p, float lt, float ut);
	/** @deprecated */
	@Deprecated
	Decision getDecision(Firings a, float lt, float ut);
	/** @deprecated */
	@Deprecated
	Decision getDecision(Firings a, float p, float lt, float ut);

	/**
	 * Returns the probability that two records match.
	 *
	 * @param   q  The query record.
	 * @param   m  The match record.
	 * @return  The probability that <code>m</code> and <code>q</code>
	 *            are the same entity.
	 */
	float computeMatchProbability(Record q, Record m);
	/** @deprecated */
	@Deprecated
	float getProbability(Record q, Record m);

	/**
	 * Returns the match probability based on the active clues.
	 *
	 * @param   a  The active clues.
	 * @return  The match probability.
	 */
	float computeProbability(Firings a);
	/** @deprecated */
	@Deprecated
	float getProbability(Firings a);

	/**
	 * Returns details used in calculating a ChoiceMaker probability score
	 * that are specific to a particular machine learning technique.
	 *
	 * @param   a  The active clues.
	 * @return  A string representing pertinent details.  For example,
	 * a Maximum Entropy classifier might return a string describing the
	 * classification probabiltiesfor match, hold and differ; whereas a
	 * Support Vector Machine might return a string representing a
	 * real-valued, type-dependent prediction for a match classification.
	 * <p>
	 * Subclasses should override the default, do-nothing method provided
	 * by this class.
	 * @since 2010-08-11
	 */
	String computeProbabilityDetails(Firings a);
	/** @deprecated */
	@Deprecated
	String getProbabilityDetails(Firings a);

}
