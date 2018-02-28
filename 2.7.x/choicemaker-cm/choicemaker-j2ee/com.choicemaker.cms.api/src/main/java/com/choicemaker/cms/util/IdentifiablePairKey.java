package com.choicemaker.cms.util;

import java.io.Serializable;

import com.choicemaker.client.api.Identifiable;
import com.choicemaker.util.Precondition;

public class IdentifiablePairKey<T extends Comparable<T> & Serializable> {
	public final Identifiable<T> q;
	public final Identifiable<T> m;
	public IdentifiablePairKey(Identifiable<T> _q, Identifiable<T> _m) {
		Precondition.assertNonNullArgument("null query", _q);
		Precondition.assertNonNullArgument("null match candidate", _m);
		this.q = _q;
		this.m = _m;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((m == null) ? 0 : m.hashCode());
		result = prime * result + ((q == null) ? 0 : q.hashCode());
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
		IdentifiablePairKey<T> other = (IdentifiablePairKey<T>) obj;
		if (m == null) {
			if (other.m != null)
				return false;
		} else if (!m.equals(other.m))
			return false;
		if (q == null) {
			if (other.q != null)
				return false;
		} else if (!q.equals(other.q))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "QMKey [q=" + q + ", m=" + m + "]";
	}

	
}