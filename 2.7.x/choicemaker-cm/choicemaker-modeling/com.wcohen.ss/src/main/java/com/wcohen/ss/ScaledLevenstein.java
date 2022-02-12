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
 * Levenstein string distance. Levenstein distance is basically
 * NeedlemanWunsch with unit costs for all operations.
 */

public class ScaledLevenstein extends Levenstein
{
	
	private static final long serialVersionUID = 1L;

	public double score(StringWrapper s,StringWrapper t){
		double d = super.score(s,t);
		double n = Math.max((double)s.length(),(double)t.length());
		return (1 + (d/n));
	}
	
	public String toString() { return "[ScaledLevenstein]"; }

	static public void main(String[] argv) {
		doMain(new ScaledLevenstein(), argv);
	}
}
