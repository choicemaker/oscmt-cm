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

import java.util.Iterator;

import com.wcohen.ss.api.StringWrapper;
import com.wcohen.ss.api.Token;
import com.wcohen.ss.api.Tokenizer;
import com.wcohen.ss.tokens.SimpleTokenizer;

/**
 * Highly simplified model of Felligi-Sunter's method 1,
 * applied to tokens.
 */

public class TokenFelligiSunter extends AbstractStatisticalTokenDistance
{
	private static final long serialVersionUID = 1L;
	private Tokenizer _tokenizer;
	private double _mismatchFactor = 0.5;
	private boolean _oversimplified = false;
	
	public TokenFelligiSunter(Tokenizer tokenizer,double mismatchFactor) {
		this._tokenizer = tokenizer;
		this._mismatchFactor = mismatchFactor;
	}
	public TokenFelligiSunter() {
		this(SimpleTokenizer.DEFAULT_TOKENIZER, 0.5);
	}
	public void setMismatchFactor(double d) { _mismatchFactor=d; }
	public void setMismatchFactor(Double d) { _mismatchFactor=d.doubleValue(); }

	public double score(StringWrapper s,StringWrapper t) {
		BagOfTokens sBag = (BagOfTokens)s;
		BagOfTokens tBag = (BagOfTokens)t;
		double sim = 0.0;
		//for (Iterator i = sBag.tokenIterator(); i.hasNext(); ) {
		for (Iterator i=sBag.getDistinctTokens().iterator(); i.hasNext(); ) {
			Token tok = (Token) i.next();
			if (tBag.contains(tok)) {
				if (_oversimplified) {
					sim += tBag.getWeight(tok);
				} else {
					double p = Math.exp( -tBag.getWeight(tok) ); 
					//sim += Math.log( p );
					sim -= Math.log( 1.0 - Math.exp( sBag.size() * tBag.size() * Math.log(1.0 - p*p) ) );
				}
			} else {
				if (_oversimplified) {
					sim -= sBag.getWeight(tok)*_mismatchFactor;
				}
			}
		}
		//System.out.println("common="+numCommon+" |s| = "+sBag.size()+" |t| = "+tBag.size());
		return sim;
	}
	
	/** Preprocess a string by finding tokens and giving them appropriate weights */ 
	public StringWrapper prepare(String s) {
		BagOfTokens bag = new BagOfTokens(s, _tokenizer.tokenize(s));
		// reweight by -log( freq/collectionSize )
		for (Iterator i=bag.getDistinctTokens().iterator(); i.hasNext(); ) {
			Token tok = (Token) i.next();
			if (collectionSize>0) {
				Integer dfInteger = (Integer)documentFrequency.get(tok);
				// set previously unknown words to df==1, which gives them a high value
				double df = dfInteger==null ? 1.0 : dfInteger.intValue();
				double w = -Math.log( df/collectionSize );
				bag.setWeight( tok, w );
			} else {
				bag.setWeight( tok, Math.log(10) );
			}
		}
		return bag;
	}
	
	/** Explain how the distance was computed. 
	 * In the output, the tokens in S and T are listed, and the
	 * common tokens are marked with an asterisk.
	 */
	public String explainScore(StringWrapper s, StringWrapper t) 
	{
		BagOfTokens sBag = (BagOfTokens)s;
		BagOfTokens tBag = (BagOfTokens)t;
		StringBuffer buf = new StringBuffer("");
		PrintfFormat fmt = new PrintfFormat("%.3f");
		buf.append("Common tokens: ");
		for (Iterator i=sBag.getDistinctTokens().iterator(); i.hasNext(); ) {
			Token tok = (Token) i.next();
			if (tBag.contains(tok)) {
				buf.append(" "+tok.getValue()+": ");
				buf.append(fmt.sprintf(tBag.getWeight(tok)));
			}
		}
		buf.append("\nscore = "+score(s,t));
		return buf.toString(); 
	}
	public String toString() { return "[TokenFelligiSunter]"; }
	
	static public void main(String[] argv) {
		doMain(new TokenFelligiSunter(), argv);
	}

}
