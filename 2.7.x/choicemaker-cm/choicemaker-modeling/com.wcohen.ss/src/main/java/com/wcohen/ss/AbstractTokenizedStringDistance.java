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

import java.util.ArrayList;
import java.util.List;

import com.wcohen.ss.api.StringWrapper;
import com.wcohen.ss.api.StringWrapperIterator;
import com.wcohen.ss.api.Token;
import com.wcohen.ss.api.Tokenizer;
import com.wcohen.ss.tokens.SimpleTokenizer;


/**
 * Abstract distance metric for tokenized strings.
 */

abstract public class AbstractTokenizedStringDistance extends AbstractStringDistance
{
	private static final long serialVersionUID = 1L;
	protected Tokenizer tokenizer;
	// cached, tokenized version of wrappers
	private List tokenizedWrappers; 

	public AbstractTokenizedStringDistance(Tokenizer tokenizer) { this.tokenizer = tokenizer; }
	public AbstractTokenizedStringDistance() { this(SimpleTokenizer.DEFAULT_TOKENIZER); }
	
	final public void setStringWrapperPool(StringWrapperIterator i) { 
		train(i);
	}

	abstract public void train(StringWrapperIterator i); 

	final public StringWrapperIterator prepare(StringWrapperIterator i) {
		tokenizedWrappers = new ArrayList();
		while (i.hasNext()) {
			tokenizedWrappers.add( asBagOfTokens(i.nextStringWrapper()) );
		}
		return new BasicStringWrapperIterator(tokenizedWrappers.iterator());
	}

	// convert to a bag of tokens
	final protected BagOfTokens asBagOfTokens(StringWrapper w) 
	{
		if (w instanceof BagOfTokens) return (BagOfTokens)w;
		else {
			String s = w.unwrap();
			Token[] toks = tokenizer.tokenize(s);
			return new BagOfTokens(s,toks);
		}
	}
}
