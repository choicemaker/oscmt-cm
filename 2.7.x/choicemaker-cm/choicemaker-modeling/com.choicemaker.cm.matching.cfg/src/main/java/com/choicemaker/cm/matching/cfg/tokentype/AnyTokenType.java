/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.cfg.tokentype;

import com.choicemaker.cm.matching.cfg.TokenType;

/** 
 * @author   Adam Winkel
 */
public class AnyTokenType extends TokenType {
	
	protected double probability;
	
	public AnyTokenType(String tokenName) {
		this(tokenName, 0.0);
	}	
	
	public AnyTokenType(String tokenName, double defaultProb) {
		super(tokenName);
		setDefaultProbability(defaultProb);
	}
	
	/**
	 * Sets the probability returned for all tokens.
	 */
	public void setDefaultProbability(double probability) {
		this.probability = probability;
	}
	
	/**
	 * Returns true for all arguments.
	 */
	@Override
	public boolean canHaveToken(String token) {
		return true;
	}

	/**
	 * Since this class is meant to be a last resort thing, we 
	 * don't do anything...
	 */
	@Override
	protected double getTokenProbability(String token) {
		return probability;
	}

}
