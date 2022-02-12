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

/**
 * Soft TFIDF-based distance metric, extended to use "soft" token-matching
 * with the JaroWinkler distance metric.
 */

public class JaroWinklerTFIDF extends SoftTFIDF
{
	private static final long serialVersionUID = 1L;

	public JaroWinklerTFIDF() { super(new JaroWinkler(), 0.9); }
	public String toString() { return "[JaroWinklerTFIDF:threshold="+getTokenMatchThreshold()+"]"; }
	
	static public void main(String[] argv) {
		doMain(new JaroWinklerTFIDF(), argv);
	}
}
