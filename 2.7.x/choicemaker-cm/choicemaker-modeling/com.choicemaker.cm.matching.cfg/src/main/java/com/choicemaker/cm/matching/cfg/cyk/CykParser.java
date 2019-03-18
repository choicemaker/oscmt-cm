/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.cfg.cyk;

import java.util.List;

import com.choicemaker.cm.matching.cfg.AbstractParser;
import com.choicemaker.cm.matching.cfg.ContextFreeGrammar;
import com.choicemaker.cm.matching.cfg.ParseTreeNode;
import com.choicemaker.cm.matching.cfg.ParseTreeNodeStandardizer;
import com.choicemaker.cm.matching.cfg.ParsedData;
import com.choicemaker.cm.matching.cfg.Token;
import com.choicemaker.cm.matching.cfg.Tokenizer;
import com.choicemaker.cm.matching.cfg.cnf.NearlyCnfGrammar;

/**
 * .
 * 
 * @author Adam Winkel
 */
public class CykParser extends AbstractParser {

	private CykParserChart cykParserChart;

	public CykParser() {
	}

	public CykParser(Tokenizer t, ContextFreeGrammar g,
			ParseTreeNodeStandardizer s) {
		super(t, g, s);
	}

	public CykParser(Tokenizer[] t, ContextFreeGrammar g,
			ParseTreeNodeStandardizer s) {
		super(t, g, s);
	}

	public CykParser(Tokenizer t, ContextFreeGrammar g,
			ParseTreeNodeStandardizer s, Class<? extends ParsedData> c) {
		super(t, g, s, c);
	}

	public CykParser(Tokenizer[] t, ContextFreeGrammar g,
			ParseTreeNodeStandardizer s, Class<? extends ParsedData> c) {
		super(t, g, s, c);
	}

	@Override
	public void setGrammar(ContextFreeGrammar g) {
		super.setGrammar(g);
		cykParserChart = new CykParserChart(new NearlyCnfGrammar(g));
	}

	@Override
	protected ParseTreeNode getBestParseTreeFromParser(List<Token> tokens) {
		return cykParserChart.getBestParseTree(tokens);
	}

	@Override
	protected ParseTreeNode[] getAllParseTreesFromParser(List<Token> tokens) {
		ParseTreeNode ptn = getBestParseTreeFromParser(tokens);
		if (ptn == null) {
			return new ParseTreeNode[0];
		} else {
			return new ParseTreeNode[] {
					ptn };
		}
	}

}
