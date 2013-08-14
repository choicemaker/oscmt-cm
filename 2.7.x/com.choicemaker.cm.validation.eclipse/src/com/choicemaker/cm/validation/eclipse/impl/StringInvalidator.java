/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.validation.eclipse.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.choicemaker.cm.core.util.StringUtils;
import com.choicemaker.cm.matching.gen.Sets;
import com.choicemaker.cm.validation.AbstractSetBasedValidator;

/**
 * Validates a non-null value by checking that it does <em>NOT</em> match
 * any string in some set of strings. Null values are considered invalid.
 *
 * @author rphall
 * @version $Revision: 1.2 $ $Date: 2010/03/29 14:44:29 $
 */
public class StringInvalidator extends AbstractSetBasedValidator {

	/** The hash character (#) */
	public static final String COMMENT_FLAG = "#";

	private static Logger logger = Logger.getLogger(StringInvalidator.class);

	private Set strings;

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
	public String getNamedSet() {
		if (this.setName == null) {
			throw new IllegalStateException("set name not initialized");
		}
		return this.setName;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.validation.eclipse.ISetBasedValidator#getSetContents()
	 */
	public Set getSetContents() {
		if (this.strings == null) {
			throw new IllegalStateException("strings not initialized");
		}
		Set retVal = Collections.unmodifiableSet(strings);
		return retVal;
	}

	public Class[] getValidationTypes() {
		return new Class[] { String.class };
	}

	private void initializeSetNameAndContents(String _setName) {

		// Preconditions
		if (!StringUtils.nonEmptyString(_setName)) {
			throw new IllegalArgumentException("null or blank set name");
		}
		Collection c = Sets.getCollection(_setName);
		if (c == null) {
			String msg = "No set named '" + _setName + "'";
			logger.error(msg);
			throw new IllegalArgumentException(msg);
		}

		this.setName = _setName;
		this.strings = new HashSet();
		for (Iterator i = c.iterator(); i.hasNext();) {
			String s = (String) i.next();
			boolean nonEmpty = StringUtils.nonEmptyString(s);
			if (nonEmpty && !s.startsWith(COMMENT_FLAG)) {
				logger.debug(this.setName + ": adding '" + s + "'");
				this.strings.add(s);
			} else if (nonEmpty) {
				logger.debug(this.setName + ": skipping '" + s + "'");
			} else {
				logger.debug(this.setName + ": skipping blank line");
			}
		}

	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.validation.eclipse.IValidator#isValid(java.lang.String)
	 */
	public boolean isValid(Object object) {
		// Preconditions
		if (this.strings == null) {
			throw new IllegalStateException("strings not initialized");
		}

		boolean retVal = false;
		if (object != null && object instanceof String) {
			String value = (String) object;
			retVal = !this.strings.contains(value);
		}

		return retVal;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.validation.eclipse.ISetBasedValidator#setNamedSet(String)
	 */
	public void setNamedSet(String setName) {
		initializeSetNameAndContents(setName);
	}

}

