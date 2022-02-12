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
 * Abstract distance between characters.
 *
 */

abstract public class CharMatchScore 
{
	abstract public double matchScore(char c,char d);
	
	/** Scores match as 0, mismatch as -1. */
	static public CharMatchScore DIST_01 = 
	new CharMatchScore() {
		public double matchScore(char c,char d) {
			return Character.toLowerCase(c)==Character.toLowerCase(d) ? 0 : -1;
		}
	};
	
	/** Scores match as +2, mismatch as -1. */
	static public CharMatchScore DIST_21 = 
	new CharMatchScore() {
		public double matchScore(char c,char d) {
			return Character.toLowerCase(c)==Character.toLowerCase(d) ? 2 : -1;
		}
	};
	
}
