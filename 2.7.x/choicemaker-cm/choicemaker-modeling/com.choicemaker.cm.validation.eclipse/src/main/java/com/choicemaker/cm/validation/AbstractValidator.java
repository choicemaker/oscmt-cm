/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.validation;

/**
 * Base class for validators. This class implements the <code>equals</code>
 * methods in the manner recommendedc by the IValidator documentation.
 *
 * @author rphall
 */
public abstract class AbstractValidator implements IValidator {

	/**
	 * If <code>o</code> is an instance of IValidator, this method
	 * invokes <code>v.equals(IValidator)</code>; otherwise, it
	 * invokes <code>v.equals(Object)</code>. A null validator never
	 * equals another validator, even another null one.
	 */
	public static boolean validatorEquals(IValidator v, Object o) {
		boolean retVal = false;
		if (v != null && o instanceof IValidator) {
			retVal = v.equals((IValidator)o);
		} else if (v != null) {
			retVal = v.equals(o);
		} else {
			retVal = false;
		}
		return retVal;
	}

 	/**
	 * Code to keep lint happy. Subclasses should override if they
	 * override {@link #equals(IValidator)}.
	 */
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * If <code>o</code> is an instance of IValidator, this method
	 * invokes <code>equals(IValidator)</code>; otherwise, it
	 * invokes <code>super.equals(Object)</code>.
	 */
	public boolean equals(Object o) {
		boolean retVal;
		if (o instanceof IValidator) {
			retVal = this.equals((IValidator)o);
		} else {
			retVal = super.equals(o);
		}
		return retVal;
	}

}
