/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;



/**
 * An unsafe, historical definition of a matching record. Wherever
 * possible, use the ImmutableMatch interface
 * or -- better yet -- an extending interface that conveys information
 * about the query record, matching model and thresholds used to
 * make a match determination.</p>
 * <p>
 * UnsafeMatch instances are useful in only contexts where the query record,
 * the matching model and the thresholds used for matching are
 * all known, and <em>the matches to query record need to be compared
 * <strong>only in term of match probability</strong></em>.</p>
 * <p>
 * The {@link #equals(Object) equality test} for this interface can be
 * particularly misleading since it checks only the type of the Object
 * being compared (to see if it is an ImmutableMatch and if the
 * value of the probability decisions are equal to within the required
 * precision.
 * </p>
 * <p>
 * <em><strong>This interface should be avoided</strong></em>, but its
 * peculiar method definitions are actually required. Please
 * refactor any occurences to the ImmutableMatch interface
 * and its extensions where appropriate.</p>
 * @author rphall
 */
public interface ImmutableUnsafeMatch {

	/**
	 * <strong><em>Overrides the <code>ImmutableMatch.equals(Object)</code>
	 * check of ImmutableMatch in an unexpected manner</em></strong>
	 * @return {@link #equalProbabilitiesOnly(Object o) equalProbabilitiesOnly(Object)}
	 */
	public abstract boolean equals(Object o);

	/**
	 * Returns true if:<ul>
	 * <li/> <code>o</code> is an instance of Match
	 * <li/> the match probabilities differ by less than PRECISION
	 * <li/> and the ids of the matching records test {@link java.lang.Object#equals(Object) equal}.
	 * </ul>
	 * <em><strong>Note:</strong> the ids, decisions and active clues of the matching records are
	 * <strong>not</strong> tested for equality.</em>
	 */
	public boolean equalProbabilitiesOnly(Object o);

	/**
	 * Returns true if:<ul>
	 * <li/> <code>o</code> is an instance of ImmutableMatch
	 * <li/> the decisions test equal (they have the same
	 * {@link Decision#toInt() toInt()} values).
	 * <li/> the match probabilities differ by less than PRECISION
	 * <li/> and the clue firings test {@link java.lang.Object#equals(Object) equal}.
	 * </ul>
	 * <em><strong>Note:</strong> the ids of the matching records are
	 * <strong>not</strong> tested for equality.</em>
	 */
	public abstract boolean equalDecisionProbablityAndClueFirings(Object o);

	/**
	 * Returns true if:<ul>
	 * <li/> <code>o</code> is an instance of ImmutableMatch
	 * <li/> the decisions test equal (they have the same
	 * {@link Decision#toInt() toInt()} values).
	 * <li/> the match probabilities differ by less than PRECISION
	 * <li/> the ids of the matching records test {@link java.lang.Object#equals(Object) equal}.
	 * <li/> and the clue firings test {@link java.lang.Object#equals(Object) equal}.
	 * </ul>
	 */
	public abstract boolean equalAllFields(Object o);

	/**
	 * <strong><em>Overrides the <code>compareTo()</code>
	 * method of ImmutableMatch in an unexpected manner</em></strong>.
	 * Consistent with the equality method of this interface, but different from the
	 * equality method of ImmutableMatch, orders by descending match probability.
	 */
	public abstract int compareTo(Object o);

	/**
	 * <strong><em>Overrides the ImmutableMatch hashCode()
	 * method of in an unexpected manner</em></strong>
	 * Consistent with <code>equals(Object)</code>, this method hashes on just
	 * the probability score.
	 */
	public abstract int hashCode();

}
