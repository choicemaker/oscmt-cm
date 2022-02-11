/*******************************************************************************
 * Copyright (c) 2003, 2021 ChoiceMaker LLC and others.
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
package com.choicemaker.util;

public final class TypedAttribute<T> {

	public final String name;
	public final TypedValue<T> typedValue;

	public TypedAttribute(String name, TypedValue<T> typedValue) {
		Precondition.assertNonEmptyString("name must be non-empty", name);
		Precondition.assertNonNullArgument("typedValue must be non-empty", typedValue);
		this.name = name;
		this.typedValue = typedValue;
	}

	public String getName() {
		return name;
	}

	public TypedValue<T> getTypedValue() {
		return typedValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result =
			prime * result + ((typedValue == null) ? 0 : typedValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
		TypedAttribute<T> other = (TypedAttribute<T>) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (typedValue == null) {
			if (other.typedValue != null)
				return false;
		} else if (!typedValue.equals(other.typedValue))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TypedAttribute [name=" + name + ", typedValue=" + typedValue
				+ "]";
	}

}
