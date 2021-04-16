package com.choicemaker.cms.util;

import com.choicemaker.client.api.Identifiable;
import com.choicemaker.util.Precondition;

/**
 * A class that defines equality, hashCode and comparison methods for
 * Identifiable classes using the IdentifiableComparator class.
 * 
 * @author rphall
 *
 */
public class IdentifiableWrapper<T extends Comparable<T>>
		implements Comparable<IdentifiableWrapper<T>> {

	private final Identifiable<T> wrapped;
	private final IdentifiableComparator<T> comparator =
		new IdentifiableComparator<>(false);

	public IdentifiableWrapper(Identifiable<T> identifiable) {
		Precondition.assertNonNullArgument("null identifiable", identifiable);
		this.wrapped = identifiable;
	}

	public Identifiable<T> getWrapped() {
		return wrapped;
	}

	@Override
	public int compareTo(IdentifiableWrapper<T> o) {
		int retVal;
		if (o == null) {
			retVal = +1;
		} else {
			retVal = this.comparator.compare(this.wrapped, o.wrapped);
		}
		return retVal;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		assert wrapped != null;
		result = prime * result
				+ ((wrapped.getId() == null) ? 0 : wrapped.getId().hashCode());
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
		IdentifiableWrapper<T> other = (IdentifiableWrapper<T>) obj;
		return comparator.compare(wrapped, other.wrapped) == 0;
	}

	@Override
	public String toString() {
		return "IdentifiableWrapper [wrapped=" + wrapped == null ? null
				: wrapped.getClass().getSimpleName() + ", id=" + wrapped == null
						? null : wrapped.getId() + "]";
	}

}
