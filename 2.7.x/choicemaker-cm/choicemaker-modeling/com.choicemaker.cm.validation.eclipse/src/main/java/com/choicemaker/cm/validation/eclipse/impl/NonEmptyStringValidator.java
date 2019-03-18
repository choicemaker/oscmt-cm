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
import com.choicemaker.util.StringUtils;

/**
 * A simple validator for checking that a String is neither null or blank.
 * @author rphall
 */
public class NonEmptyStringValidator extends AbstractValidator<String> {


	/**
	 * Partially constructs an aggregate validator. The
	 * <code>setValidators(Map)</code> or <code>addValidator(String,IValidator)</code>
	 * methods must be called to finish construction.
	 */
	public NonEmptyStringValidator() {
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.validation.eclipse.IValidator#getValidationTypes()
	 */
	public Class<?>[] getValidationTypes() {
		Class<?>[] retVal = new Class[] { String.class };
		return retVal;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.validation.eclipse.IValidator#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(String object) {
		boolean retVal = false;
		if (object != null && (object instanceof String)) {
			String value = object;
			retVal = StringUtils.nonEmptyString(value);
		}
		return retVal;
	}
	
	@Override
	public boolean equals(IValidator<?> validator) {
		boolean retVal = false;
		if (validator != null
			&& validator.getClass().equals(this.getClass())) {
			retVal = true;
		}
		return retVal;
	}

	@Override
	public int hashCode() {
		return this.getClass().hashCode();
	}

}

