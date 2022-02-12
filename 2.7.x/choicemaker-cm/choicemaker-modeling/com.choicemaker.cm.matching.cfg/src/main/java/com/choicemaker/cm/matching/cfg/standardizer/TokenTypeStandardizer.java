/*******************************************************************************
 * Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.cfg.standardizer;

import com.choicemaker.cm.matching.cfg.ParseTreeNode;
import com.choicemaker.cm.matching.cfg.ParsedData;
import com.choicemaker.cm.matching.cfg.Token;
import com.choicemaker.cm.matching.cfg.TokenType;

/**
 * The TokenTypeStandardizer is basically just a wrapper around
 * <code>TokenType</code>'s <code>getStandardToken(Token)</code>
 * method.
 *
 * @author   Adam Winkel
 */
public class TokenTypeStandardizer extends DefaultStandardizer {

	/**
	 * Create a new TokenTypeStandardizer which puts its 
	 * standardized Token in the <code>fieldName</code>
	 * field.
	 * 
	 * @param fieldName the field in which to store the standardized
	 * token.
	 */
	public TokenTypeStandardizer(String fieldName) {
		super(fieldName);
	}

	/**
	 * Standardize the content of <code>node</code>, which is taken
	 * to be a leaf node, and put the standardized token in
	 * the <code>fieldName</code> (passed in the constructor) field
	 * of <code>holder</code>.
	 */
	@Override
	public void standardize(ParseTreeNode node, ParsedData holder) {
		TokenType type = (TokenType)node.getRule().getLhs();
		Token tok = (Token)node.getRule().getRhsSymbol(0);
		holder.append(fieldName, type.getStandardToken(tok));
	}

}
