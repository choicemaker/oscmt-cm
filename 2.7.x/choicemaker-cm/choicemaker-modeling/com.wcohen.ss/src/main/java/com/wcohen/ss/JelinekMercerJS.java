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
import com.wcohen.ss.api.Tokenizer;
import com.wcohen.ss.tokens.SimpleTokenizer;

/**
 * Jensen-Shannon distance of two unigram language models, smoothed
 * using Jelinek-Mercer mixture model.
 */

public class JelinekMercerJS extends JensenShannonDistance
{
	private static final long serialVersionUID = 1L;
	private double lambda = 0.5;

	public double getLambda() { return lambda; }
	public void setLambda(double lambda) { this.lambda = lambda; }
	public void setLambda(Double lambda) { this.lambda = lambda.doubleValue(); }
	
	public JelinekMercerJS(Tokenizer tokenizer,double lambda) { 
		super(tokenizer);
		setLambda(lambda);
	}
	public JelinekMercerJS() { 
		this(SimpleTokenizer.DEFAULT_TOKENIZER, 0.2); 
	}

	/** smoothed probability of the token */
	protected double smoothedProbability(Token tok, double freq, double totalWeight) 
	{
		return (1-lambda) * (freq/totalWeight) + lambda * backgroundProb(tok);
	}
	public String toString() { return "[JelinekMercerJS lambda="+lambda+"]"; }

	static public void main(String[] argv) {
		doMain(new JelinekMercerJS(), argv);
	}
}
