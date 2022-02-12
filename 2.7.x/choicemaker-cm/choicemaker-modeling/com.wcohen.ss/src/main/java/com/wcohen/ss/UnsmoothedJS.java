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

import com.wcohen.ss.api.Token;

/**
 * Jensen-Shannon distance of two unsmoothed unigram language models.
 */

public class UnsmoothedJS extends JensenShannonDistance
{
	private static final long serialVersionUID = 1L;

	public String toString() { return "[UnsmoothedJS]"; }

	/** Unsmoothed probability of the token */
	protected double smoothedProbability(Token tok, double freq, double totalWeight) 
	{
		return freq/totalWeight;
	}

	static public void main(String[] argv) {
		doMain(new UnsmoothedJS(), argv);
	}
}
