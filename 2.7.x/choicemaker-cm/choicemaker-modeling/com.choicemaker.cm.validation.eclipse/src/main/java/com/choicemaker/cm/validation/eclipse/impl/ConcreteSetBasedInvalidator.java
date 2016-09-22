package com.choicemaker.cm.validation.eclipse.impl;

import java.util.Set;

public class ConcreteSetBasedInvalidator<T> extends ConcreteSetBasedValidator<T> {

	public ConcreteSetBasedInvalidator(Set<T> set) {
		super(set);
	}

	@Override
	public boolean isValid(T object) {
		boolean retVal = !set.contains(object);
		return retVal;
	}
	
}
