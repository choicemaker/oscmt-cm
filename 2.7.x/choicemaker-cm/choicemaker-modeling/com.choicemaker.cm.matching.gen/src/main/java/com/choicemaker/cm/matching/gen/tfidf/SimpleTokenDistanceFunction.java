/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.gen.tfidf;

/**
 * Comment
 *
 * @author   Adam Winkel
 */
public class SimpleTokenDistanceFunction implements TokenDistanceFunction {

	public SimpleTokenDistanceFunction(StringDistanceFunction sd) {
		
	}

	@Override
	public float distance(String s1, String s2) {
		return 0;
	}

	@Override
	public float distance(TokenBag b1, TokenBag b2) {
		return 0;
	}

}
