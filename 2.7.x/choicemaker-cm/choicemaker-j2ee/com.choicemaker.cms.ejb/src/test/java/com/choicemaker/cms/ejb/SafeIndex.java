/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.cms.ejb;

import java.io.Serializable;

/** Safe index instances of the same type with null index values are equal */
public class SafeIndex<T extends Comparable<T> & Serializable>
		implements Comparable<SafeIndex<T>>, Serializable {

	private static final long serialVersionUID = 271L;

	private final T index;

	public SafeIndex(T index) {
		this.index = index;
	}

	public T getIndex() {
		return index;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((index == null) ? 0 : index.hashCode());
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
		@SuppressWarnings("rawtypes")
		SafeIndex other = (SafeIndex) obj;
		if (index == null) {
			if (other.index != null)
				return false;
		} else if (!index.equals(other.index))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SafeIndex [index=" + index + "]";
	}

	@Override
	public int compareTo(SafeIndex<T> o) {
		int retVal;
		if (this == o) {
			retVal = 0;
		} else if (o == null) {
			retVal = 1;
		} else {
			final SafeIndex<T> that = o;
			final T thatIndex = that.getIndex();
			final T thisIndex = this.getIndex();
			if (thisIndex == null && thatIndex == null) {
				retVal = 0;
			} else if (thisIndex == null) {
				retVal = -1;
			} else {
				retVal = thisIndex.compareTo(thatIndex);
			}
		}
		return retVal;
	}
}