/*******************************************************************************
 * Copyright (c) 2003, 2014 ChoiceMaker LLC and others.
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
/*
 * Created on Dec 19, 2006
 *
 */
package com.choicemaker.cm.args;

import java.io.Serializable;

/**
 * @author emoussikaev
 */
public interface IFilterConfiguration extends Serializable {

	int getBatchSize();

	float getDefaultPrefilterFromPercentage();

	float getDefaultPrefilterToPercentage();

	float getDefaultPostfilterFromPercentage();

	float getDefaultPostfilterToPercentage();

	int getDefaultPairSamplerSize();

	boolean getUseDefaultPrefilter();

	boolean getUseDefaultPostfilter();

	boolean getUseDefaultPairSampler();

}
