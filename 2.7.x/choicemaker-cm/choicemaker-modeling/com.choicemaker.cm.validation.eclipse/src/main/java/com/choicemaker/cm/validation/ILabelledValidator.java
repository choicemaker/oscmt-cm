/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.validation;

/**
 * Validation method for some object type <code>T</code> classified by some
 * application label and an optional qualifier.
 * <p>
 * For example, town names might be validated according to state and county, in
 * which an application would define instances of
 * ILabelledValidator&lt;String&gt; that would validate town names based
 * on state (and county, if available).
 * 
 * <pre>
 * ILabelledValidator<String > TownNameValidator =
 *     new ILabelledValidator<String> { ... };
 * boolean b1 = TownNameValidator.isValid("NJ", "Mercer", "Princeton");
 * boolean b2 = TownNameValidator.isValid("NY", null, "New York City");
 * </pre>
 * 
 * @author rphall
 */
public interface ILabelledValidator<T> {

	/**
	 * Checks if two validators are functionally identical; i.e. if they are
	 * implemented by the same class and perform the validation checks.
	 * 
	 * @see #hashCode()
	 */
	public boolean equals(ILabelledValidator<?> validator);

	/**
	 * To work as expected, implementors must override
	 * <code>Object.equals(Object)</code>. The recommended method is to check
	 * whether <code>o</code> is an instance of IValidator, and if so, then
	 * invoke <code>equals(IValidator)</code>; otherwise, the method should
	 * invoke <code>super.equals(Object)</code>.
	 * 
	 * @see AbstractValidator#equals(Object)
	 * @see AbstractValidator#validatorEquals(ILabelledValidator,Object)
	 */
	@Override
	public boolean equals(Object o);

	/**
	 * Returns the a hashcode that should be the same for two instances of the
	 * same implementation class that perform the same validation checks. The
	 * hashcode should be unique by implementation class, and within an
	 * implementation class, unique by the set of validation checks that are
	 * performed.
	 * 
	 * @see #equals(ILabelledValidator)
	 */
	@Override
	public int hashCode();

	/**
	 * Validates an object of a given label and qualifier.
	 */
	public boolean isValid(String label, String qualifier, T object);

}
