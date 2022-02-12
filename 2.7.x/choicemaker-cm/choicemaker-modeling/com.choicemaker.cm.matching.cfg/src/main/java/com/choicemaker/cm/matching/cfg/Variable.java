/*******************************************************************************
 * Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.cfg;

/**
 * The Variable class represents a variable in a context-free grammar (CFG).
 * Variables are one of two subclasses of Symbol (the other is Token).
 * 
 * Variables are symbols that can actually appear in the rules of a
 * CFG.  The Variable class is further subclasses by the TokenType subclass.
 * Token types are variables which only appear on the right-hand side of
 * rules in the grammar.  However, the CFG implicitly contains a rule of the 
 * form
 * 
 *   TokenTypeX --> TokenY
 * 
 * for every Token that can take on TokenType X.
 * 
 * Variables that are not TokenTypes can appear on the left-hand side of 
 * one or more rules.  Note, however, that if such a variable appears on the
 * right-hand side of a rule, but the grammar does not have
 * at least one rule with the variable on the left-hand side, the first rule
 * will never be instantiated, e.g.
 * 
 * STREET_ADDR --> STREET_NAME STREET_SUFFIX
 *   without something like
 * STREET_NAME --> WORD | WORD WORD | NUMBER ORDINAL_EXTENSION
 *
 * If a CFG were to have the first rule without a rule of the form of the 
 * second rule (with STREET_NAME on the LHS), the first rule would never
 * be included in a parse tree.
 *
 * @author   Adam Winkel
 * @see Symbol
 * @see TokenType
 * @see Token
 * @see Rule
 * @see ContextFreeGrammar
 */
public class Variable extends Symbol {

	/**
	 * Creates a new <code>Variable</code> with the specified name.
	 * 
	 * Although creating two <code>Variable</code>s with
	 * the same name is not expressly forbidden, it is discouraged.
	 * Instead of creating <code>Variable</code>s directly using this
	 * constructor, use a <code>SymbolFactory</code> instead.  
	 * 
	 * @param name the <code>String</code> representation of this
	 * Variable
	 */
	public Variable(String name) {
		super(name);
	}
	
	/**
	 * Returns true iff <code>obj</code> is a <code>Variable</code> and its 
	 * <code>toString()</code> returns the same value as this <code>Variable</code>'s.
	 * 
	 * @return whether or not this Variable is equal to <code>obj</code>
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Variable) {
			Variable v = (Variable)obj;
			return this == v || name.equals(v.name);	
		}
		return false;
	}

}
