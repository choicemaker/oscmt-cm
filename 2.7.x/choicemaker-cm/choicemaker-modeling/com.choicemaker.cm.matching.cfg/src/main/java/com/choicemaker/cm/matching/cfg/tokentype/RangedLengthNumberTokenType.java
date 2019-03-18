/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.cfg.tokentype;

/**
 *
 * @author    Adam Winkel
 */
public class RangedLengthNumberTokenType extends NumberTokenType {

	protected int minLength;
	protected int maxLength;

	public RangedLengthNumberTokenType(String name) {
		super(name);
	}
	
	public RangedLengthNumberTokenType(String name, int min, int max) {
		super(name);
		setLengthRange(min, max);
	}

	public void setLengthRange(int min, int max) {
		if (min <= 0) {
			throw new IllegalArgumentException("min must be greater than 0: " + min);
		} else if (min > max) {
			throw new IllegalArgumentException("min cannot be greater than max: " + min + ", " + max);
		}
		
		minLength = min;
		maxLength = max;
	}
	
	@Override
	public boolean canHaveToken(String token) {
		return super.canHaveToken(token) && token.length() >= minLength && token.length() <= maxLength;
	}

}
