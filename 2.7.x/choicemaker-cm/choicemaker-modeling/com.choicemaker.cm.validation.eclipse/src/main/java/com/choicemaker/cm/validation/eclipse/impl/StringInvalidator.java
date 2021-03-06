/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.validation.eclipse.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.choicemaker.cm.matching.gen.Sets;
import com.choicemaker.cm.validation.AbstractSetBasedValidator;
import com.choicemaker.util.StringUtils;

/**
 * Validates a non-null value by checking that it does <em>NOT</em> match
 * any string in some set of strings. Null values are considered invalid.
 *
 * @author rphall
 */
public class StringInvalidator extends AbstractSetBasedValidator<String> {

	/** The hash character (#) */
	public static final String COMMENT_FLAG = "#";

	private static Logger logger = Logger.getLogger(StringInvalidator.class.getName());

	private Set<String> strings;

	private String setName;

	/**
	 * Partially constructs a validator. The
	 * {@link #setNamedSet(String)} method must
	 * be called to finish construction.
	 */
	public StringInvalidator() {
	}

	/**
	 * Constructs a validitor from a named set of strings.
	 * @param setName a named set in the collection
	 * com.choicemaker.cm.matching.gen.Sets;
	 */
	public StringInvalidator(String setName) {
		initializeSetNameAndContents(setName);
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.validation.eclipse.ISetBasedValidator#getNamedSet()
	 */
	@Override
	public String getNamedSet() {
		if (this.setName == null) {
			throw new IllegalStateException("set name not initialized");
		}
		return this.setName;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.validation.eclipse.ISetBasedValidator#getSetContents()
	 */
	@Override
	public Set<String> getSetContents() {
		if (this.strings == null) {
			throw new IllegalStateException("strings not initialized");
		}
		Set<String> retVal = Collections.unmodifiableSet(strings);
		return retVal;
	}

	public Class<?>[] getValidationTypes() {
		return new Class[] { String.class };
	}

	private void initializeSetNameAndContents(String _setName) {

		// Preconditions
		if (!StringUtils.nonEmptyString(_setName)) {
			throw new IllegalArgumentException("null or blank set name");
		}
		Collection<String> c = Sets.getCollection(_setName);
		if (c == null) {
			String msg = "No set named '" + _setName + "'";
			logger.severe(msg);
			throw new IllegalArgumentException(msg);
		}

		this.setName = _setName;
		this.strings = new HashSet<>();
		for (String s : c) {
			boolean nonEmpty = StringUtils.nonEmptyString(s);
			if (nonEmpty && !s.startsWith(COMMENT_FLAG)) {
				logger.fine(this.setName + ": adding '" + s + "'");
				this.strings.add(s);
			} else if (nonEmpty) {
				logger.fine(this.setName + ": skipping '" + s + "'");
			} else {
				logger.fine(this.setName + ": skipping blank line");
			}
		}

	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.validation.eclipse.IValidator#isValid(java.lang.String)
	 */
	@Override
	public boolean isValid(String object) {
		// Preconditions
		if (this.strings == null) {
			throw new IllegalStateException("strings not initialized");
		}

		boolean retVal = false;
		if (object != null && object instanceof String) {
			String value = object;
			retVal = !this.strings.contains(value);
		}

		return retVal;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.validation.eclipse.ISetBasedValidator#setNamedSet(String)
	 */
	@Override
	public void setNamedSet(String setName) {
		initializeSetNameAndContents(setName);
	}

}

