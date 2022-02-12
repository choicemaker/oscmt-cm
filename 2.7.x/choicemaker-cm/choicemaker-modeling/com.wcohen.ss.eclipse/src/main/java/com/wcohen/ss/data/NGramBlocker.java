/*******************************************************************************
 * Copyright (c) 2007, 2014 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.wcohen.ss.data;

import com.wcohen.ss.api.Tokenizer;
import com.wcohen.ss.tokens.NGramTokenizer;
import com.wcohen.ss.tokens.SimpleTokenizer;

/**
 * Finds all pairs that share a not-too-common character n-gram.
 */

public class NGramBlocker extends TokenBlocker 
{
	private int maxN=4, minN=4;

	public NGramBlocker() { super(); tokenizer=initTokenizer(); }

	public int getMaxNGramSize() { return maxN; }
	public int getMinNGramSize() { return minN; }
	public void setMaxNGramSize(int n) { maxN=n; tokenizer=initTokenizer(); }
	public void setMinNGramSize(int n) { minN=n; tokenizer=initTokenizer(); }

	private Tokenizer initTokenizer() 
	{
		return new NGramTokenizer(minN,maxN,false,SimpleTokenizer.DEFAULT_TOKENIZER);
	}


	public String toString() { return "[NGramBlocker: N="+minN+"-"+maxN+"]"; }
}
