/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.config;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * The 2.5 implementation of {@link RecordType} as a pseudo enum.
 * @author emoussikaev
 */
public class RecordType0 implements Serializable {

	private static final long serialVersionUID = 271L;

	public static final RecordType0 NONE = new RecordType0("NONE");
	public static final RecordType0 HOLDER = new RecordType0("HOLDER");
	public static final RecordType0 REF = new RecordType0("REF");
	public static final RecordType0 GLOBAL_REF = new RecordType0("GLOBAL_REF");

	public static RecordType0 valueOf(String name) {
		name = name.intern();
		if (NONE.toString().intern() == name) {
			return NONE;
		} else if (HOLDER.toString().intern() == name) {
			return HOLDER;
		} else if (REF.toString().intern() == name) {
			return REF;
		} else if (GLOBAL_REF.toString().intern() == name) {
			return GLOBAL_REF;
		} else {
			throw new IllegalArgumentException(name
					+ " is not a valid RecordType.");
		}
	}

	private static int nextOrdinal = 0;
	private final int ordinal = nextOrdinal++;
	private static final RecordType0[] VALUES = {
			NONE, HOLDER, REF, GLOBAL_REF };

	private final String name;

	private RecordType0(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return getName();
	}

	Object readResolve() throws ObjectStreamException {
		return VALUES[ordinal];
	}

}
