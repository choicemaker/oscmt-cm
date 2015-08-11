/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.offline.core;

import java.io.Serializable;

/**
 * This represents a pair for matching comparison.
 *
 * @author pcheung
 *
 */
public class ComparisonPair<T extends Comparable<T>> implements
		Comparable<ComparisonPair<T>>, Serializable {

	static final long serialVersionUID = 271L;

	/**
	 * Record id of the first record.
	 */
	private T id1;

	/**
	 * Record if of the second record.
	 */
	private T id2;

	/**
	 * This is true is id2 is a staging record.
	 */
	public boolean isStage;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result =
			prime * result + ((getId1() == null) ? 0 : getId1().hashCode());
		result =
			prime * result + ((getId2() == null) ? 0 : getId2().hashCode());
		result = prime * result + (isStage ? 1231 : 1237);
		return result;
	}

	/**
	 * This is true if this Object is a MatchRecord and has the same id pair as
	 * the input MatchRecord.
	 *
	 * @param o
	 * @return boolean - true if the ids from both MatchRecords match.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		ComparisonPair other = (ComparisonPair) obj;
		if (getId1() == null) {
			if (other.getId1() != null)
				return false;
		} else if (!getId1().equals(other.getId1()))
			return false;
		if (getId2() == null) {
			if (other.getId2() != null)
				return false;
		} else if (!getId2().equals(other.getId2()))
			return false;
		if (isStage != other.isStage)
			return false;
		return true;
	}

	/**
	 * Obsolete method for <code>equals(ComparisonPair)</code>. Used for testing
	 * only.
	 * 
	 * @deprecated
	 */
	@Deprecated
	public boolean equals_00(ComparisonPair<T> p) {
		boolean ret = false;
		if (this.getId1().equals(p.getId1())
				&& this.getId2().equals(p.getId2())
				&& this.isStage == p.isStage)
			ret = true;
		return ret;
	}

	/**
	 * This returns -1 if this object is less than input o, 0 is equals input 0,
	 * and 1 if it is greater than input o.
	 *
	 */
	@Override
	public int compareTo(ComparisonPair<T> p) {
		int ret = 0;

		if (getId1().compareTo(p.getId1()) < 0)
			ret = -1;
		else if (getId1().compareTo(p.getId1()) > 0)
			ret = 1;
		else if (getId1().compareTo(p.getId1()) == 0) {
			if (getId2().compareTo(p.getId2()) < 0)
				ret = -1;
			else if (getId2().compareTo(p.getId2()) > 0)
				ret = 1;
			else if (getId2().compareTo(p.getId2()) == 0) {
				if (isStage == p.isStage)
					ret = 0;
				else if (isStage == true)
					ret = -1;
				else if (isStage == false)
					ret = 1;
			}
		}
		return ret;
	}

	public T getId1() {
		return id1;
	}

	public void setId1(T id) {
		if (id == null) {
			throw new IllegalArgumentException("null argument");
		}
		this.id1 = id;
	}

	public T getId2() {
		return id2;
	}

	public void setId2(T id) {
		if (id == null) {
			throw new IllegalArgumentException("null argument");
		}
		this.id2 = id;
	}

	@Override
	public String toString() {
		return "ComparisonPair [id1=" + id1 + ", id2=" + id2 + ", isStage="
				+ isStage + "]";
	}

}
