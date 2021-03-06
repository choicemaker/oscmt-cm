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
package com.wcohen.ss;

import java.util.Iterator;

import com.wcohen.ss.api.DistanceInstance;
import com.wcohen.ss.api.StringDistance;
import com.wcohen.ss.api.StringDistanceLearner;
import com.wcohen.ss.api.StringWrapper;

/**
 * Abstract StringDistanceLearner class which averages results of a number of
 * inner distance metrics, learned by a number of inner distance learners.
 */

public class AveragedStringDistanceLearner extends CombinedStringDistanceLearner
{
	private static final long serialVersionUID = 1L;
	MultiStringWrapper prototype = null;
	
	public AveragedStringDistanceLearner() { super(); }

	public AveragedStringDistanceLearner(StringDistanceLearner[] innerLearners, String delim) {	super(innerLearners,delim); }

	protected void comboSetStringWrapperPool(Iterator it) {
		if (it.hasNext()) prototype = asMultiStringWrapper( (StringWrapper) it.next() );
	}
	protected boolean comboHasNextQuery() { return false; }
	protected DistanceInstance comboNextQuery() { return null; }
	protected void comboAddExample(DistanceInstance di) { ; }
	protected void comboSetDistanceInstancePool(Iterator i) {
		// use the first example to build a prototype
	}

	public StringDistance getDistance() {
		if (prototype==null) throw new IllegalStateException("need to be trained first");
		return new AveragedStringDistance(getInnerDistances(), prototype);
	}

	//
	// average of some string distances
	//

	private class AveragedStringDistance extends CombinedStringDistance
	{
		private static final long serialVersionUID = 1L;

		public AveragedStringDistance(StringDistance[] innerDistances, MultiStringWrapper prototype) 
		{
			super(innerDistances,prototype);
		}
		protected double doScore(MultiStringWrapper ms,MultiStringWrapper mt)
		{
			double totScore = 0.0;
			for (int i=0; i<ms.size(); i++) {
				StringDistance d = innerDistances[ ms.getDistanceLearnerIndex(i) ];
				totScore += d.score( ms.get(i), mt.get(i) );
			}
			return totScore/ms.size();
		}
		protected String explainCombination(MultiStringWrapper ms,MultiStringWrapper mt)
		{ 
			return "Final score is the average\n";
		}

		public String toString() 
		{
			return "[Average of:"+innerDistanceString()+"]";
		}
	}

}
