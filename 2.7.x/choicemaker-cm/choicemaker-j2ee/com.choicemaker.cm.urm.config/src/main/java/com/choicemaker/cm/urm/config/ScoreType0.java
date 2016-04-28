/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.config;

import java.io.ObjectStreamException;

/**
 * The 2.5 implementation of {@link ScoreType} as a pseudo enum.
 * @author emoussikaev
 */
public class ScoreType0 implements java.io.Serializable {

	private static final long serialVersionUID = 271L;

	public static final ScoreType0 NO_NOTE = new ScoreType0("NO_NOTE");
	public static final ScoreType0 RULE_LIST_NOTE = new ScoreType0(
			"RULE_LIST_NOTE");

	public static ScoreType0 valueOf(String name) {
		name = name.intern();
		if (NO_NOTE.toString().intern() == name) {
			return NO_NOTE;
		} else if (RULE_LIST_NOTE.toString().intern() == name) {
			return RULE_LIST_NOTE;
		} else {
			throw new IllegalArgumentException(name
					+ " is not a valid Record Score.");
		}
	}

	private static int nextOrdinal = 0;
	private final int ordinal = nextOrdinal++;
	private static final ScoreType0[] VALUES = {
			NO_NOTE, RULE_LIST_NOTE };

	private String value;

	private ScoreType0(String value) {
		this.value = value;
	}

	public String toString() {
		return value;
	}

	Object readResolve() throws ObjectStreamException {
		return VALUES[ordinal];
	}

}
