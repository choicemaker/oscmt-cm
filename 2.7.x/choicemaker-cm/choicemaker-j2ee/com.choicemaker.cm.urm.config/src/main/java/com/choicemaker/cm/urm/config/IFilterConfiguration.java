/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Dec 19, 2006
 *
 */
package com.choicemaker.cm.urm.config;

import java.io.Serializable;

/**
 * @author emoussikaev
 */
public interface IFilterConfiguration extends Serializable {

	public abstract Integer getBatchSize();

	public abstract Float getDefaultPrefilterFromPercentage();

	public abstract Float getDefaultPrefilterToPercentage();

	public abstract Float getDefaultPostfilterFromPercentage();

	public abstract Float getDefaultPostfilterToPercentage();

	public abstract Integer getDefaultPairSamplerSize();

	public abstract Boolean getUseDefaultPrefilter();

	public abstract Boolean getUseDefaultPostfilter();

	public abstract Boolean getUseDefaultPairSampler();

	public abstract void setBatchSize(Integer integer);

	public abstract void setDefaultPrefilterFromPercentage(Float float1);

	public abstract void setDefaultPrefilterToPercentage(Float float1);

	public abstract void setDefaultPostfilterFromPercentage(Float float1);

	public abstract void setDefaultPostfilterToPercentage(Float float1);

	public abstract void setDefaultPairSamplerSize(Integer integer);

	public abstract void setUseDefaultPrefilter(Boolean boolean1);

	public abstract void setUseDefaultPostfilter(Boolean boolean1);

	public abstract void setUseDefaultPairSampler(Boolean boolean1);
}
