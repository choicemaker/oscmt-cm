/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.aba.base;

import java.util.Arrays;

import com.choicemaker.cm.aba.IField;
import com.choicemaker.cm.aba.IQueryField;

/**
 * A field on a query record, which is compared against {@link DbField master}
 * records to find matches.
 * 
 * @author mbuechi
 */
public class QueryField extends Field implements IQueryField {

	private static final long serialVersionUID = 271;

	public QueryField() {
		this(NN_FIELD);
	}

	public QueryField(IField[][] illegalCombinations) {
		super(illegalCombinations);
	}

	@Override
	public String toString() {
		return "QueryField [illegalCombinations="
				+ Arrays.toString(getIllegalCombinations()) + "]";
	}

}
