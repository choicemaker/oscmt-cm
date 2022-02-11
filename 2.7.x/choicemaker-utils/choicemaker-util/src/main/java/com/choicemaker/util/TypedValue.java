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

public final class TypedValue<T> {
	public final Class<T> type;
	public final T value;
	public final String strValue;

	public TypedValue(Class<T> type, T value) {
		this(type,value,String.valueOf(value));
	}

	public TypedValue(Class<T> type, T value, String strValue) {
		Precondition.assertNonNullArgument("type must be non-null", type);
		this.type = type;
		this.value = value;
		this.strValue = strValue;
	}

	public Class<T> getType() {
		return type;
	}

	public T getValue() {
		return value;
	}

	public String getStrValue() {
		return strValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result =
			prime * result + ((strValue == null) ? 0 : strValue.hashCode());
		result = prime * result + type.hashCode();
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		TypedValue<T> other = (TypedValue<T>) obj;
		if (strValue == null) {
			if (other.strValue != null)
				return false;
		} else if (!strValue.equals(other.strValue))
			return false;
		if (!type.equals(other.type))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TypedValue [type=" + type.getSimpleName() + ", value=" + strValue
				+ "]";
	}
}
