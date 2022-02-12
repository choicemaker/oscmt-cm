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
 * Jaro distance metric, as extended by Winkler.  
 */
public class JaroWinkler extends WinklerRescorer
{
	private static final long serialVersionUID = 1L;
	public JaroWinkler() { super(new Jaro()); }
	static public void main(String[] argv) {	doMain(new JaroWinkler(), argv);	}
}
