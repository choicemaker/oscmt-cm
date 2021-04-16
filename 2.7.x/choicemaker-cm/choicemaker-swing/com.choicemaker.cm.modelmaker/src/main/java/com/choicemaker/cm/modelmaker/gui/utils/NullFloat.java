/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.utils;

import java.text.DecimalFormat;

public class NullFloat implements Comparable {
	private final static NullFloat nullInstance = new NullFloat();
	private static DecimalFormat df = new DecimalFormat("##0.00");

	private final float val;
	private final boolean nul;
	private final String rPadding;

	public static NullFloat getNullInstance() {
		return nullInstance;
	}

	public NullFloat(float val) {
		this(val, "");
	}

	public NullFloat(float val, String rPadding) {
		this.val = val;
		this.nul = false;
		this.rPadding = rPadding;
	}

	private NullFloat() {
		this.val = 0;
		this.nul = true;
		this.rPadding = "";
	}

	public float value() {
		if (nul) {
			throw new IllegalStateException("Value of nul instance");
		} else {
			return val;
		}
	}

	@Override
	public int compareTo(Object o) {
		NullFloat other = (NullFloat) o;
		if (nul) {
			if (other.nul) {
				return 0;
			} else {
				return -1;
			}
		} else if (other.nul) {
			return 1;
		} else {
			float thisVal = this.val;
			float anotherVal = other.val;
			return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(val);
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
		NullFloat other = (NullFloat) obj;
		if (Float.floatToIntBits(val) != Float.floatToIntBits(other.val))
			return false;
		return true;
	}
	
	/**
	 * Obsolete method for {@link #equals(Object)}. Used for testing only.
	 * @deprecated
	 */
	@Deprecated
	public boolean equals_00(Object o) {
		NullFloat other = (NullFloat) o;
		return nul && other.nul || val == other.val;
	}

	@Override
	public String toString() {
		return nul ? "" : df.format(val) + rPadding;
	}
}
