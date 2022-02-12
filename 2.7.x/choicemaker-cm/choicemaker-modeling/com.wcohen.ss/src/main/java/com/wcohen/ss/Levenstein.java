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
 * Levenstein string distance. Levenstein distance is basically
 * NeedlemanWunsch with unit costs for all operations.
 */

public class Levenstein extends NeedlemanWunsch
{
	private static final long serialVersionUID = 1L;

	public Levenstein() {
		super(CharMatchScore.DIST_01, 1.0 );
	}
	public String toString() { return "[Levenstein]"; }

	static public void main(String[] argv) {
		doMain(new Levenstein(), argv);
	}
}
