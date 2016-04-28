/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.offline.core;

public enum EXTERNAL_DATA_FORMAT {
	STRING(1), BINARY(2);
	public final int symbol;

	public static final String LOG_SOURCE = "EXTERNAL_DATA_FORMAT";

	EXTERNAL_DATA_FORMAT(int i) {
		symbol = i;
	}

	public static EXTERNAL_DATA_FORMAT fromSymbol(char i) {
		return fromSymbol((int) i);
	}

	public static EXTERNAL_DATA_FORMAT fromSymbol(int i) {
		EXTERNAL_DATA_FORMAT retVal = null;
		switch (i) {
		case 1:
			retVal = STRING;
			break;
		case 2:
			retVal = BINARY;
			break;
		default:
			throw new IllegalArgumentException(
				LOG_SOURCE + ": invalid symbol: " + i);
		}
		assert retVal != null;
		return retVal;
	}

	public static EXTERNAL_DATA_FORMAT fromSymbol(String s) {
		if (s == null || !s.equals(s.trim()) || s.length() != 1) {
			throw new IllegalArgumentException(
				LOG_SOURCE + ": invalid String: " + s);
		}
		return fromSymbol(s.charAt(0));
	}

}
