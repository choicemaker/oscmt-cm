/*
 * Copyright (c) 2001, 2022 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.core;

/**
 * Mutable match and differ thresholds.
 * 
 * @author Martin Buechi
 * @author rphall
 */
public class Thresholds extends ImmutableThresholds implements Cloneable {

	public Thresholds(float differThreshold, float matchThreshold) {
		this((double) differThreshold, (double) matchThreshold);
	}

	protected Thresholds(double dt, double mt) {
		super(MIN_VALUE, MAX_VALUE);
		this.setDifferThreshold(dt);
		this.setMatchThreshold(mt);
	}

	public Thresholds(ImmutableThresholds imt) {
		super(imt);
	}

	@Override
	public Object clone() {
		Object retVal = super.clone();
		return retVal;
	}

	/**
	 * Sets the value of differThreshold in a forgivng manner.
	 * <em>This method is different than the method for <code>IThresholds.setDifferThreshold(float)</code></em>
	 * <ul>
	 * <li/>If the specified value is less than <code>IThresholds.MIN_VALUE</code>
	 * then the specified threshold is set to <code>MIN_VALUE</code>.
	 * <li/>If the specified value is greater than <code>IThresholds.MAX_VALUE</code>
	 * then the specified threshold is set to <code>MAX_VALUE</code>.
	 * <li/>If the specified value is greater than
	 * <code>IThresholds.getMatchThreshold()</code> then the
	 * match threshold is <code>set</code> to the specified value.
	 * <li/>The differ threshold is <code>set</code> to the specified value.
	 * </ul>
	 * 
	 * @param v
	 *            Value to assign to differThreshold.
	 */
	public void setDifferThreshold(float v) {
		setDifferThreshold((double) v);
	}

	@Override
	protected void setDifferThreshold(double v) {
		super.invariant();
		if (v < ImmutableThresholds.MIN_VALUE) {
			v = ImmutableThresholds.MIN_VALUE;
		} else if (v > ImmutableThresholds.MAX_VALUE) {
			v = ImmutableThresholds.MAX_VALUE;
		}
		if (getMatchThreshold() < v) {
			super.setMatchThreshold(v);
		}
		super.setDifferThreshold(v);
		super.invariant();
	}

	/**
	 * Sets the value of matchThreshold.
	 * <em>This method is different than the method for <code>IThresholdssetMatchThreshold(float)}</code>:</em>
	 * 
	 * @param v
	 *            Value to assign to matchThreshold.
	 */
	public void setMatchThreshold(float v) {
		setMatchThreshold((double) v);
	}

	@Override
	protected void setMatchThreshold(double v) {
		super.invariant();
		if (v < ImmutableThresholds.MIN_VALUE) {
			v = ImmutableThresholds.MIN_VALUE;
		} else if (v > ImmutableThresholds.MAX_VALUE) {
			v = ImmutableThresholds.MAX_VALUE;
		}
		if (getDifferThreshold() > v) {
			this.setDifferThreshold(v);
		}
		super.setMatchThreshold(v);
		super.invariant();

	}

}
