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

import com.wcohen.ss.api.StringWrapper;

/**
 * Smith-Waterman string distance, following Durban et al. 
 * Sec 2.3.
 */

public class SmithWaterman extends AbstractStringDistance
{
	private static final long serialVersionUID = 1L;
	private CharMatchScore charMatchScore;
	private double gapCost;
	
	public SmithWaterman() {
		this(CharMatchScore.DIST_21, 1.0 );
	}
	public SmithWaterman(CharMatchScore charMatchScore,double gapCost) {
		this.charMatchScore = charMatchScore;
		this.gapCost = gapCost;
	}
	
	public double score(StringWrapper s,StringWrapper t) {
		MyMatrix mat = new MyMatrix( s, t );
		return score(s,t,mat);
	}
	
	private double score(StringWrapper s,StringWrapper t,MyMatrix mat) {
		double best = -Double.MAX_VALUE;
		for (int i=0; i<=s.length(); i++) {
	    for (int j=0; j<=t.length(); j++) {
				best = Math.max( best, mat.get(i,j) );
	    }
		}
		return best;
	}
	
	public String explainScore(StringWrapper s,StringWrapper t) {
		MyMatrix mat = new MyMatrix( s, t );
		double d = score(s,t,mat);
		return mat.toString() + "\nScore = "+d;
	}
	
	private class MyMatrix extends MemoMatrix {
		public MyMatrix(StringWrapper s,StringWrapper t) {
	    super(s,t);
		}
		public double compute(int i,int j) {
	    if (i==0) return 0;
	    if (j==0) return 0;
	    return max4( 0,
									 get(i-1,j-1) + charMatchScore.matchScore( sAt(i), tAt(j) ),
									 get(i-1, j) - gapCost,
									 get(i, j-1) - gapCost);
		}
	}
	
	public String toString() { return "[SmithWaterman]"; }
		 

	static public void main(String[] argv) {
		doMain(new SmithWaterman(), argv);
	}
}
