/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.stats;

import com.choicemaker.cm.core.Thresholds;

/**
 * @author mchen
 *
 */
public interface IStatistics {
	public abstract int[][] getConfusionMatrix();
	public abstract StatPoint getCurrentStatPoint();
	public abstract void computeStatPoint(StatPoint pt);
	public abstract float[][] getThresholdVsAccuracy();
	public abstract float[][] getHoldPercentageVsAccuracy(float[] errorRates);
	public abstract int[][] getHistogram(int numBins);
	public abstract void setThresholds(Thresholds t);
}
