/*******************************************************************************
 * Copyright (c) 2015, 2020 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.client.api;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Type-safe enum for decisions on record pairs. Decisions are also known as
 * futures.
 *
 * @author Martin Buechi
 */

public class Decision implements Serializable, Comparable<Decision> {

	static final long serialVersionUID = 271;

	/** The number of different decisions. */
	public final static int NUM_DECISIONS = 3;

	private static Decision[] vals = new Decision[NUM_DECISIONS];

	/** The decision differ. */
	public static final Decision DIFFER = new Decision("differ", "D", 0);

	/** The decision match. */
	public static final Decision MATCH = new Decision("match", "M", 1);

	/** The decision hold. */
	public static final Decision HOLD = new Decision("hold", "H", 2);

	private final int no;
	private final String name;
	private final String singleCharAsString;
	private final char singleChar;

	protected Decision(String name, String singleChar, int no) {
		assert name != null && name.equals(name.trim()) && !name.isEmpty();
		assert singleChar != null && singleChar.equals(singleChar.trim())
				&& singleChar.length() == 1;
		this.name = name.intern();
		this.singleCharAsString = singleChar;
		this.singleChar = this.singleCharAsString.charAt(0);
		this.no = no;
		if (0 <= no && no < NUM_DECISIONS)
			vals[no] = this;
	}

	public String getName() {
		return name;
	}

	/** Same as {@link #getName()} */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Returns the single character representation of the <code>Decision</code>.
	 * Returns "D", "M", or "H".
	 *
	 * @return the single character representation of the <code>Decision</code>.
	 */
	public String toSingleCharString() {
		return singleCharAsString;
	}

	public char toSingleChar() {
		return singleChar;
	}

	/**
	 * Returns the corresponding <code>Decision</code>
	 * 
	 * @param name
	 *            must be <code>match</code>, <code>hold</code> or
	 *            <code>differ</code> (case insensitive)
	 * @return a non-null <code>Decision</code>
	 * @throws IllegalArgumentException
	 *             if <code>name</code> is not a valid decision.
	 */
	public static Decision valueOf(String name) {
		name = name.toLowerCase().intern();
		if (DIFFER.name == name) {
			return DIFFER;
		} else if (HOLD.name == name) {
			return HOLD;
		} else if (MATCH.name == name) {
			return MATCH;
		} else {
			throw new IllegalArgumentException(
					name + " is not a valid Decision.");
		}
	}

	/**
	 * Returns the corresponding <code>Decision</code>.
	 * 
	 * @param name
	 *            must be <code>m</code>, <code>h</code> or <code>d</code> (case
	 *            insensitive)
	 * @return The corresponding <code>Decision</code>.
	 * @throws IllegalArgumentException
	 *             if <code>name</code> is not a valid decision.
	 */
	public static Decision valueOf(char name) {
		name = Character.toLowerCase(name);
		if (name == 'd') {
			return DIFFER;
		} else if (name == 'h') {
			return HOLD;
		} else if (name == 'm') {
			return MATCH;
		} else {
			throw new IllegalArgumentException(
					name + " is not a valid Decision.");
		}
	}

	/**
	 * Returns the corresponding <code>Decision</code>.
	 * 
	 * @param no
	 *            must be <code>0 (differ)</code>, <code>1 (match)</code> or
	 *            <code>2 (hold)</code> (case insensitive)
	 * @return The corresponding <code>Decision</code>.
	 * @throws IndexOutOfBoundsException
	 *             if <code>no</code> is out of the range
	 *             <code>(no &lt; 0 || no &gt;= NUM_DECISIONS)</code>.
	 */
	public static Decision valueOf(int no) {
		return vals[no];
	}

	/**
	 * Returns the <code>int</code> value corresponding to this decision.
	 *
	 * @return The <code>int</code> value corresponding to this decision.
	 */
	public int toInt() {
		return no;
	}

	private Object readResolve() throws ObjectStreamException {
		return valueOf(no);
	}

	@Override
	public boolean equals(Object o) {
		Decision d = (Decision) o;
		boolean retVal = this.no == d.no;
		return retVal;
	}

	@Override
	public int hashCode() {
		return this.no;
	}

	/**
	 * Compares this object with the specified object for order, where MATCH
	 * &lt; HOLD &lt; DIFFER.
	 *
	 * @return A negative integer, zero, or a positive integer as this object is
	 *         less than, equal to, or greater than the specified object.
	 */
	@Override
	public int compareTo(Decision o) {
		Decision d = o;
		if (this == d) {
			return 0;
		} else if (this == DIFFER || (this == HOLD && d == MATCH)) {
			return 1;
		} else {
			return -1;
		}
	}

}
