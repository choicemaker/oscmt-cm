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
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.util;

/**
 * Conjunction (and eventually, disjunction) utilities that can be useful in
 * equality tests.
 * 
 * @author rphall
 */
public class Equal {

	/**
	 * Conjunction of long (and int) values.
	 * 
	 * @param n1
	 *            any value
	 * @param n2
	 *            any value
	 * @return true if n1 == n2
	 */
	public static boolean and(long n1, long n2) {
		return and(true, n1, n2);
	}

	/**
	 * Conjunction of long (and int) values.
	 * 
	 * @param n1
	 *            any value
	 * @param n2
	 *            any value
	 * @param b
	 *            an additional condition
	 * @return true if b is true and n1 == n2
	 */
	public static boolean and(boolean b, long n1, long n2) {
		return b && (n1 == n2);
	}

	/**
	 * Conjunction of double (and float) values.
	 * 
	 * @param n1
	 *            any value
	 * @param n2
	 *            any value
	 * @return true if n1 == n2
	 */
	public static boolean and(double n1, double n2) {
		return and(true, n1, n2);
	}

	/**
	 * Conjunction of double (and float) values.
	 * 
	 * @param n1
	 *            any value
	 * @param n2
	 *            any value
	 * @param b
	 *            an additional condition
	 * @return true if b is true and n1 == n2
	 */
	public static boolean and(boolean b, double n1, double n2) {
		return b && (n1 == n2);
	}

	/**
	 * Conjunction of objects when nulls are treated as unequal
	 * 
	 * @param o1
	 *            any Object
	 * @param o2
	 *            any Object
	 * @return true o1 and o2 are not null and o1.equals(o2)
	 */
	public static boolean and(Object o1, Object o2) {
		return and(true, o1, o2, false);
	}

	/**
	 * Conjunction of objects when nulls are treated as unequal
	 * 
	 * @param o1
	 *            any Object
	 * @param o2
	 *            any Object
	 * @param b
	 *            an additional condition
	 * @return true if b is true and o1 and o2 are not null and o1.equals(o2)
	 */
	public static boolean and(boolean b, Object o1, Object o2) {
		return and(b, o1, o2, false);
	}

	/**
	 * Conjunction of objects that specifies whether null are treated as unequal
	 * 
	 * @param o1
	 *            any Object
	 * @param o2
	 *            any Object
	 * @param nullsAreEqual
	 *            if true, nulls are considered equal
	 * @return true if o1 and o2 are not both null and o1.equals(o2). If o1 and
	 *         o2 are both null, returns <code>nullsAreEqual</code>.
	 */
	public static boolean and(Object o1, Object o2, boolean nullsAreEqual) {
		return and(true, o1, o2, nullsAreEqual);
	}

	/**
	 * Conjunction of objects that specifies whether null are treated as unequal
	 * 
	 * @param b
	 *            an additional condition
	 * @param o1
	 *            any Object
	 * @param o2
	 *            any Object
	 * @param nullsAreEqual
	 *            if true, nulls are considered equal
	 * @return true if b is true and o1 and o2 are not both null and
	 *         o1.equals(o2). If o1 and o2 are both null, returns
	 *         <code>nullsAreEqual</code>.
	 */
	public static boolean and(boolean b, Object o1, Object o2,
			boolean nullsAreEqual) {
		boolean retVal = false;
		if (b) {
			if (o1 == null && o2 == null) {
				retVal = nullsAreEqual;
			} else if (o1 == null) {
				retVal = false;
			} else {
				retVal = o1.equals(o2);
			}
		}
		return retVal;
	}

	private Equal() {
	}

}
