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
