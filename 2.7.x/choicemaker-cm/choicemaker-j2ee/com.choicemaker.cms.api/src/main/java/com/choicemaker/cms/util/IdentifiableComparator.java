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
package com.choicemaker.cms.util;

import java.io.Serializable;
import java.util.Comparator;

import com.choicemaker.client.api.Identifiable;

/**
 * Defines consistent equality and comparison methods for Identifiable instances
 * where the instances or their identifiers may be null.
 * <ul>
 * <li>Null instances are considered equal.
 * <li>Instances of different classes can not be compared and are not considered
 * equal.
 * <li>Instances with null identifiers are considered equal.
 * <li>A null instance is not equal to, and compares less than, an instance with
 * a null identifier.
 * <li>Instances of the same class with non-null identifiers that are equal are
 * considered equal.
 * <li>For non-null instances, the results
 * <code>equals(Identifiable<T> o1, Identifiable<T> o2)</code> are asserted to
 * agree with <code>o1.equals(o2)</code> and <code>o2.equals(o1)</code>, unless
 * this behavior is turned off during construction.
 * </ul>
 * 
 * @author rphall
 *
 * @param <T>
 *            The identifier type
 */
public class IdentifiableComparator<T extends Comparable<T>>
		implements Comparator<Identifiable<T>>, Serializable {

	private static final long serialVersionUID = 271L;

	private final boolean assertionsEnabled;

	public IdentifiableComparator() {
		this(true);
	}

	public IdentifiableComparator(boolean enableAssertions) {
		boolean b = false;
		assert b = true;
		this.assertionsEnabled = b && enableAssertions;
	}

	public boolean equals(Identifiable<T> o1, Identifiable<T> o2) {
		boolean retVal = (o1 == o2);
		if (retVal == false && o1 != null && o2 != null
				&& o1.getClass() == o2.getClass()) {
			T id1 = o1.getId();
			T id2 = o2.getId();
			retVal = (id1 == id2);
			if (retVal == false && id1 != null && id2 != null) {
				retVal = id1.equals(id2);
				assert !retVal || id2.equals(id1); // required: equals contract
			}
		}
		if (assertionsEnabled) {
			if (retVal) {
				assert o1 == null || (o1.equals(o2) && o2.equals(o1));
			} else {
				assert (o1 == null && o2 != null) || (o1 != null && o2 == null)
						|| (o1 != null && !o1.equals(o2))
						|| (o2 != null && !o2.equals(o1));
			}
		}
		return retVal;
	}

	@Override
	public int compare(Identifiable<T> o1, Identifiable<T> o2) {
		int retVal;
		if (equals(o1, o2)) {
			retVal = 0;
		} else if (o1 == null) {
			assert o2 != null;
			retVal = -1;
		} else if (o2 == null) {
			assert o1 == null;
			retVal = +1;
		} else {
			assert o1 != null && o2 != null;
			assert o1.getId() != null && o2.getId() != null;
			if (o1.getClass() != o2.getClass()) {
				String msg = "Class o1: " + o1.getClass() + ", class o2: "
						+ o2.getClass();
				throw new ClassCastException(msg);
			}
			retVal = o1.getId().compareTo(o2.getId());
		}
		return retVal;
	}

}
