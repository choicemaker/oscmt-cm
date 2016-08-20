/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.cfg;

import java.util.ArrayList;
import java.util.List;

/**
 * Comment
 *
 * @author   Adam Winkel
 */
public class CascadedParser implements Parser {
	
	public static final String PARSER_NAME = "ParseName";

	protected String name;
	protected List<Parser> parsers;
	protected int size;

	public CascadedParser() {
		parsers = new ArrayList<>();
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public void addParser(Parser parser) {
		parsers.add(parser);
		size = parsers.size();
	}

	public int size() {
		return size;
	}

	@Override
	public ParsedData getBestParse(String s) {
		for (int i = 0; i < size; i++) {
			Parser parser = parsers.get(i);
			ParsedData pd = parser.getBestParse(s);
			if (pd != null) {
				pd.put(PARSER_NAME, parser.getName());
				return pd;
			}
		}
		
		return null;
	}

	@Override
	public ParsedData getBestParse(String[] s) {
		for (int i = 0; i < size; i++) {
			Parser parser = parsers.get(i);
			ParsedData pd = parser.getBestParse(s);
			if (pd != null) {
				pd.put(PARSER_NAME, parser.getName());
				return pd;
			}
		}
		
		return null;
	}

	//
	// Method stubs because of Parser's specification.
	//

	@Override
	public void setSymbolFactory(SymbolFactory sf) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setTokenizer(Tokenizer t) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addTokenizer(Tokenizer t) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setGrammar(ContextFreeGrammar g) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setStandardizer(ParseTreeNodeStandardizer s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setParsedDataClass(Class<? extends ParsedData> cls) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SymbolFactory getSymbolFactory() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Tokenizer[] getTokenizers() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ContextFreeGrammar getGrammar() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ParseTreeNodeStandardizer getStandardizer() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Class<? extends ParsedData> getParsedDataClass() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public ParsedData[] getAllParses(String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ParsedData[] getAllParses(String[] s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ParseTreeNode getBestParseTree(String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ParseTreeNode getBestParseTree(String[] s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ParseTreeNode[] getAllParseTrees(String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ParseTreeNode[] getAllParseTrees(String[] s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Token>[] getAllTokenizations(String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Token>[] getAllTokenizations(String[] s) {
		throw new UnsupportedOperationException();
	}	

}
