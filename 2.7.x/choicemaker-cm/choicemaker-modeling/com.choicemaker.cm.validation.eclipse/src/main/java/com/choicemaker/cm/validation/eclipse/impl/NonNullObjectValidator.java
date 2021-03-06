/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.validation.eclipse.impl;

import com.choicemaker.cm.validation.AbstractValidator;
import com.choicemaker.cm.validation.IValidator;

/**
 * A simple validator for checking that an object is not null. Instances are
 * thread safe since they have no instance data.
 * 
 * @author rphall
 */
public class NonNullObjectValidator extends AbstractValidator<Object> {

	private static final NonNullObjectValidator instance =
		new NonNullObjectValidator();

	public static NonNullObjectValidator getThreadSafeInstance() {
		return instance;
	}

	/**
	 * Constructs an NonNullObjectValidator instance. The
	 * {@link #getThreadSafeInstance()} method is preferred to constructing a
	 * new instance, since it avoids allocating and then garbage collecting
	 * additional instances.
	 */
	public NonNullObjectValidator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.validation.eclipse.IValidator#getValidationTypes()
	 */
	public Class<?>[] getValidationTypes() {
		Class<?>[] retVal = new Class[] {
				Object.class };
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.choicemaker.cm.validation.eclipse.IValidator#isValid(java.lang.
	 * Object)
	 */
	@Override
	public boolean isValid(Object object) {
		boolean retVal = object != null;
		return retVal;
	}

	@Override
	public boolean equals(IValidator<?> validator) {
		boolean retVal = false;
		if (validator != null && validator.getClass().equals(this.getClass())) {
			retVal = true;
		}
		return retVal;
	}

	@Override
	public int hashCode() {
		return this.getClass().hashCode();
	}

}
