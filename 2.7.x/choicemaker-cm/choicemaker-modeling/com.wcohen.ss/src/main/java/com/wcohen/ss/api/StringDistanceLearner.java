/*******************************************************************************
 * Copyright (c) 2003 Carnegie Mellon University
 *
 * This program and the accompanying materials are made available under the
 * terms of an instance of the University of Illinois/NCSA Open Source
 * license which accompanies this distribution.
 *
 * Authors: William W. Cohen, Pradeep Ravikumar, Stephen E. Fienberg, and others
 * https://sourceforge.net/projects/secondstring
 *******************************************************************************/
package com.wcohen.ss.api;

import java.io.Serializable;

/**
 * Learn a StringDistance.
 *
 */
public interface StringDistanceLearner extends Serializable {

	/** Preprocess  a StringWrapperIterator for unsupervised training. */
	public StringWrapperIterator prepare(StringWrapperIterator i);

	/** Preprocess a DistanceInstanceIterator for supervised training. */
	public DistanceInstanceIterator prepare(DistanceInstanceIterator i);

	/** Unsupervised learning method that observes strings for which
	 * distance will be computed.  This examines a number of unlabeled
	 * StringWrapper's and uses that information to tune the distance
	 * function being learned.  An example use of this method would be a
	 * TFIDF-based distance function, which accumulated token-frequency
	 * statistics over a corpus.
	 */
	public void setStringWrapperPool(StringWrapperIterator i);

	/** Accept a set of unlabeled DistanceInstance, to use in making
	 * distance instance queries. Queries are made with the methods
	 * hasNextQuery(), nextQuery(), and setAnswer().
	 */
	public void setDistanceInstancePool(DistanceInstanceIterator i);
	
	/** Returns true if the learner has more queries to answer. */
	public boolean hasNextQuery(); 

	/** Returns a DistanceInstance for which the learner would like a
	 * label.  */
	public DistanceInstance nextQuery(); 

	/** Accept the answer to the last query. An 'answer' is a
	 * DistanceInstance with a known score or correctness. 
	 */
	public void addExample(DistanceInstance answeredQuery);

	/** Return the learned distance.
	 */
	public StringDistance getDistance();
}
