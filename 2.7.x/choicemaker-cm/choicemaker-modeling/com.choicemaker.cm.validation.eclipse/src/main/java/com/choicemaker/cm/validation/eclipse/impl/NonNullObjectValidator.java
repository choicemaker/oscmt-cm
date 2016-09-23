/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.validation.eclipse.impl;

import com.choicemaker.cm.validation.AbstractValidator;
import com.choicemaker.cm.validation.IValidator;

/**
 * A simple validator for checking that an object is not null.
 * @author rphall
 */
public class NonNullObjectValidator extends AbstractValidator<Object> {

	/** A thread-safe instance */
	private static final NonNullObjectValidator instance = new NonNullObjectValidator();

	/** Returns a thread-safe instance */
	public static NonNullObjectValidator getThreadSafeInstance() {
		return instance;
	}

	/**
	 * Constructs an NonNullObjectValidator instance.
	 */
	public NonNullObjectValidator() {
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.validation.eclipse.IValidator#getValidationTypes()
	 */
	public Class<?>[] getValidationTypes() {
		Class<?>[] retVal = new Class[] { Object.class };
		return retVal;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.validation.eclipse.IValidator#isValid(java.lang.Object)
	 */
	public boolean isValid(Object object) {
		boolean retVal = object != null;
		return retVal;
	}

	public boolean equals(IValidator<?> validator) {
		boolean retVal = false;
		if (validator != null
			&& validator.getClass().equals(this.getClass())) {
			retVal = true;
		}
		return retVal;
	}

	public int hashCode() {
		return this.getClass().hashCode();
	}

}

