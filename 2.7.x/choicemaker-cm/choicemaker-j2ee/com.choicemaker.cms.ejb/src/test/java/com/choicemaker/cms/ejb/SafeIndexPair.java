package com.choicemaker.cms.ejb;

import java.io.Serializable;

import com.choicemaker.util.Precondition;

public final class SafeIndexPair<T extends Comparable<T> & Serializable>
		implements Serializable {

	private static final long serialVersionUID = 271L;

	private final SafeIndex<T> id1;
	private final SafeIndex<T> id2;

	public SafeIndexPair(SafeIndex<T> idx1, SafeIndex<T> idx2) {
		Precondition.assertNonNullArgument(idx1);
		Precondition.assertNonNullArgument(idx2);
		this.id1 = idx1;
		this.id2 = idx2;
	}

	public SafeIndex<T> getIndex1() {
		return this.id1;
	}

	public SafeIndex<T> getIndex2() {
		return this.id2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id1 == null) ? 0 : id1.hashCode());
		result = prime * result + ((id2 == null) ? 0 : id2.hashCode());
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
		SafeIndexPair<T> other = (SafeIndexPair<T>) obj;
		if (id1 == null) {
			if (other.id1 != null)
				return false;
		} else if (!id1.equals(other.id1))
			return false;
		if (id2 == null) {
			if (other.id2 != null)
				return false;
		} else if (!id2.equals(other.id2))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SafeIndexPair [id1=" + id1 + ", id2=" + id2 + "]";
	}

}
