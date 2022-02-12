/*******************************************************************************
 * Copyright (c) 2014, 2016 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker LLC - initial API and implementation
 *******************************************************************************/
package com.choicemaker.cm.validation.eclipse.impl;

import java.util.Set;

public class ConcreteSetBasedInvalidator<T>
		extends ConcreteSetBasedValidator<T> {

	// /** A default constructor required by the plugin registry */
	// public ConcreteSetBasedInvalidator() {
	// }

	public ConcreteSetBasedInvalidator(Set<T> set) {
		super(set);
	}

	@Override
	public boolean isValid(T object) {
		boolean retVal = !set.contains(object);
		return retVal;
	}

}
