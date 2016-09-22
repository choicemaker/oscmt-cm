package com.choicemaker.cm.validation.eclipse.impl;

import java.util.Collections;
import java.util.Set;

import com.choicemaker.cm.validation.AbstractSetBasedValidator;
import com.choicemaker.util.Precondition;

public class ConcreteSetBasedValidator<T> extends AbstractSetBasedValidator<T> {

	protected final Set<T> set;
	protected final String setName;
	
	public ConcreteSetBasedValidator(Set<T> set) {
		Precondition.assertNonNullArgument("null set", set);
		this.set = set;
		this.setName = StringPatternValidator.generateSetName(set);
	}

	@Override
	public String getNamedSet() {
		return setName;
	}

	@Override
	public Set<T> getSetContents() {
		return Collections.unmodifiableSet(set);
	}

	@Override
	public boolean isValid(T object) {
		boolean retVal = set.contains(object);
		return retVal;
	}
	
	/**
	 * Not supported by this implementation
	 * @param setName unused
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public void setNamedSet(String setName) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("setNamedSet(String() is not supported");
	}

}
