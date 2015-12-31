/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.base;

import java.io.Serializable;
import java.util.Arrays;

import com.choicemaker.cm.io.blocking.automated.IField;

/**
 *
 * @author    mbuechi
 */
public abstract class Field implements Serializable, IField {
	
	private static final long serialVersionUID = 271;

	protected static final Field[] N_FIELD = new Field[0];
	protected static final Field[][] NN_FIELD = new Field[0][0];

	private final IField[][] illegalCombinations;

	protected Field(IField[][] illegalCombos) {
		this.illegalCombinations = illegalCombos;
	}

	@Override
	public IField[][] getIllegalCombinations() {
		IField[][] retVal;
		if (illegalCombinations == null) {
			retVal = NN_FIELD;
		} else {
			retVal = illegalCombinations;
		}
		assert retVal != null;
		return retVal;
	}

	@Override
	public String toString() {
		return "Field [illegalCombinations="
				+ Arrays.toString(illegalCombinations) + "]";
	}

}
