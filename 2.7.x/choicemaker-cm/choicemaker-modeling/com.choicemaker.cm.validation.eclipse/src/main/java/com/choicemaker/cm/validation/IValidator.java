/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.validation;

/**
 * Validation method for MCI names.
 *
 * @author rphall
 */
public interface IValidator<T> {

	/**
	 * Checks if two validators are functionally identical; i.e.
	 * if they are implemented by the same class and
	 * perform the validation checks.
	 * @see #hashCode()
	 */
	public boolean equals(IValidator<?> validator);

	/**
	 * To work as expected, implementors must override
	 * <code>Object.equals(Object)</code>. The recommended
	 * method is to check whether <code>o</code> is an instance of IValidator,
	 * and if so, then invoke <code>equals(IValidator)</code>; otherwise, the
	 * method should invoke <code>super.equals(Object)</code>.
	 * @see AbstractValidator#equals(Object)
	 * @see AbstractValidator#validatorEquals(IValidator,Object)
	 */
	public boolean equals(Object o);

	/**
	 * Returns the a hashcode that should be the same for
	 * two instances of the same implementation class that
	 * perform the same validation checks. The hashcode should
	 * be unique by implementation class, and within an implementation
	 * class, unique by the set of validation checks that are performed.
	 * @see #equals(IValidator)
	 */
	public int hashCode();

	/**
	 * Validates an object.
	 */
	public boolean isValid(T object);

}

