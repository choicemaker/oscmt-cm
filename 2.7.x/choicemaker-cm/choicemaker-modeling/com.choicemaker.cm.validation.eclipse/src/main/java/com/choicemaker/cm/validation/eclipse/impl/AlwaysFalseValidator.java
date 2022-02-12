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
 * A validator that always return true for the <code>isValid</code>
 * method. This can be useful in comparing the effect of other
 * validators on a matching model.
 * @author rphall
 */
public class AlwaysFalseValidator extends AbstractValidator<Object> {


	/**
	 * Partially constructs an aggregate validator. The
	 * <code>setValidators(Map)</code> or <code>addValidator(String,IValidator)</code>
	 * methods must be called to finish construction.
	 */
	public AlwaysFalseValidator() {
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
	@Override
	public boolean isValid(Object object) {
		return false;
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

