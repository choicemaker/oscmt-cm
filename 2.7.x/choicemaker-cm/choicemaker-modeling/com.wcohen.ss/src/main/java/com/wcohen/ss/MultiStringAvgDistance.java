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

import com.wcohen.ss.api.StringDistance;

/**
 * StringDistance defined over Strings that are broken into fields,
 * with distance defined as the average distance between any field.
 */

public class MultiStringAvgDistance extends MultiStringDistance
{
	private static final long serialVersionUID = 1L;
	private StringDistance innerDistance;

	public MultiStringAvgDistance(StringDistance distance, String delim) { 
		super(delim);
		this.innerDistance = distance; 
	}

	/** Combine the scores for each primitive distance function on each field. */
	protected double scoreCombination(double[] multiScore) {
		double sum = 0.0;
		for (int i=0; i<multiScore.length; i++) {
			sum += multiScore[i];
		}
		return sum/multiScore.length;
	}

	/** Explain how to combine the scores for each primitive distance
	 * function on each field. */
	protected String explainScoreCombination(double[] multiScore) {
		StringBuffer buf = new StringBuffer("");
		PrintfFormat fmt = new PrintfFormat(" %.3f");
		buf.append("field-level scores [");
		for (int i=0; i<multiScore.length; i++) {
			buf.append(fmt.sprintf(multiScore[i]));
		}
		buf.append("] Average score:");
		buf.append(fmt.sprintf( scoreCombination(multiScore) ));
		return buf.toString();
	}

	protected StringDistance getDistance(int i) {
		return innerDistance;
	}

	static public void main(String[] argv) {
		doMain(new MultiStringAvgDistance( new JaroWinkler(), ":" ), argv);
	}
}
