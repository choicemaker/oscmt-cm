/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.validation.eclipse.impl;

import java.util.Collection;

/**
 * Validates a non-null name by checking that it does <em>NOT</em> match any
 * pattern in some set of patterns. Null names are considered invalid.
 * 
 * @see StringPatternValidator
 *
 * @author rphall
 */
public class StringPatternInvalidator extends StringPatternValidator {

	/** A default constructor is required by the plugin registry */
	public StringPatternInvalidator() {
	}

	/**
	 * Constructs an invalidator from an in-memory set of patterns. A generated
	 * name is assigned to this invalidator.
	 * 
	 * @param A
	 *            non-null collection of regex expressions.
	 */
	public StringPatternInvalidator(Collection<String> regexCollection) {
		super(regexCollection);
	}

	/**
	 * Constructs an invalidator from a named set of patterns.
	 * 
	 * @param setName
	 *            a named set in the collection
	 *            com.choicemaker.cm.matching.gen.Sets;
	 */
	public StringPatternInvalidator(String setName) {
		super(setName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.validation.eclipse.IValidator#isValid(java.lang.
	 * String)
	 */
	@Override
	public boolean isValid(String object) {
		boolean retVal = true;
		if (object != null && object instanceof String) {
			retVal = !isMatch(object);
		}
		return retVal;
	}

}
