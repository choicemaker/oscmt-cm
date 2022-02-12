/*******************************************************************************
 * Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.gen.tfidf;

import java.util.StringTokenizer;

/**
 * Comment
 *
 * @author   Adam Winkel
 */
public class DefaultTokenizer implements Tokenizer {

	private static Tokenizer instance;
	
	public static Tokenizer instance() {
		if (instance == null) {
			instance = new DefaultTokenizer();
		}
		
		return instance;
	}

	@Override
	public String[] tokenize(String s) {
		return defaultTokenize(s);
	}
	
	/**
	 * Tokenizes the input string by spaces, and interns the resulting tokens.
	 */
	public static String[] defaultTokenize(String s) {
		StringTokenizer toks = new StringTokenizer(s);
		String[] tokens = new String[toks.countTokens()];
		
		int i = 0;
		while (toks.hasMoreTokens()) {
			tokens[i++] = toks.nextToken().intern();
		}
		
		return tokens;
	}
		
	private DefaultTokenizer() { }

}
