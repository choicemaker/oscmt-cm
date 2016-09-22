/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.validation;

import java.util.Set;

/**
 * A validator that uses a set of data values to validate
 * an object.
 *
 * @author rphall
 */
public interface ISetBasedValidator<T> extends IValidator<T> {

	/**
	 * Gets the name of the Sets instance
	 * that holds the data used by this validator.
	 * FIXME: this operation is Eclipse-specific. Move it to an interface
	 * in com.choicemaker.cm.validation.eclipse
	 * FIXME: add a <code>Set getSet()</code> operation
	 * <p>see com.choicemaker.cm.match.gen.Sets</p>
	 * @return the plugin name of the set of tokens or Patterns used by this validator.
	 */
	public String getNamedSet();

	/**
	 * Returns the set of data used by this validator.
	 * @return a non-null Set
	 */
	public Set<?> getSetContents();

}

