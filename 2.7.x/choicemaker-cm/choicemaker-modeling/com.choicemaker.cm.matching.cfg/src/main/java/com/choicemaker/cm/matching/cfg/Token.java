/*******************************************************************************
 * Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.cfg;

/**
 * A Token represents an actual piece of a String or String array to 
 * be parsed using context-free grammar methods.
 * 
 * Token is the second subclass of Symbol (the first is Variable).
 * Unlike Variables, Tokens may only appear in "leaf" or "unit" rules
 * of the form 
 * 
 *    TokenTypeX --> TokenY
 * 
 * and may not appear in a rule that is added to a context-free grammar.
 * Note that the Token must be the only symbol on the RHS of the rule.
 * 
 * Each TokenType has the responsibility of keeping track of all its 
 * implied rules.
 * 
 * @author   Adam Winkel
 * @see Symbol
 * @see TokenType
 * @see Rule
 * @see ContextFreeGrammar
 */
public class Token extends Symbol {

	/**
	 * Create a new Token representing the specified String.
	 * 
	 * As opposed to Variables, the Token constructor may be called directly.
	 * Tokens are immutable, and SymbolFactory may be extended to store the 
	 * Tokens that have already been created, possibly saving some memory, but
	 * the default CFG, and parsing implementations do not do this.
	 * 
	 * @param value the <code>String</code> this Token will represent
	 * in the parsing process.
	 */
	public Token(String value) {
		super(value);	
	}
	
	/**
	 * Compare this Token to <code>obj</code> for equality.
	 * 
	 * @param obj the Object for comparison
	 * @return true if obj is non-null, is a Token, and has a String representation 
	 * equal to that of this Token's
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Token) {
			Token t = (Token) obj;
			return this == t || name.equals(t.name);
		}
		return false;
	}
	
}
