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
import com.wcohen.ss.tokens.SimpleTokenizer;

/**
 * "Level 2" recursive field matching algorithm, based on Jaro
 * distance.
 */

public class Level2JaroWinkler extends Level2
{
	private static final long serialVersionUID = 1L;
	private static final StringDistance MY_JARO_WINKLER = new JaroWinkler();

	public Level2JaroWinkler() { super( SimpleTokenizer.DEFAULT_TOKENIZER, MY_JARO_WINKLER) ; }
	public String toString() { return "[Level2JaroWinkler]"; }
	
	static public void main(String[] argv) {
		doMain(new Level2JaroWinkler(), argv);
	}
}
