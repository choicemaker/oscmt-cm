package com.choicemaker.cms.exp;

import java.io.Serializable;

import com.choicemaker.cms.exp.SafeIndex;

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
			final SafeIndex<T> that = (SafeIndex<T>) o;
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