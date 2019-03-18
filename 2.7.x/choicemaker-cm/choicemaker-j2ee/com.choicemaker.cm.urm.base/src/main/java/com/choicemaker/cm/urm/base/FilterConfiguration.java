/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Dec 1, 2006
 *
 */
package com.choicemaker.cm.urm.base;

import java.util.Hashtable;

import com.choicemaker.cm.args.IFilterConfiguration;

/**
 * @author emoussikaev
 *
 */
public class FilterConfiguration implements IFilterConfiguration {

	static final long serialVersionUID = 271L;

	@SuppressWarnings("rawtypes")
	protected Hashtable props = new Hashtable();

	/**
	 * Name of the property for batch size
	 * 
	 * @since 2.5.22.5
	 */
	protected static final String PN_BATCH_SIZE = "BatchSize";

	/**
	 * Name of the property that controls whether to use a
	 * DefaultMatchRecord2Filter
	 * 
	 * @since 2.5.22.5
	 * @see com.choicemaker.cm.analyzer.filter.DefaultMatchRecord2Filter
	 */
	protected static final String PN_USE_DEFAULT_PREFILTER =
		"UseDefaultPrefilter";

	/**
	 * Name of the property for the default filter "fromPercentage"
	 * 
	 * @since 2.5.22.5
	 */
	protected static final String PN_DEFAULT_PREFILTER_FROM_PERCENTAGE =
		"DefaultPrefilterFromPercentage";

	/**
	 * Name of the property for the default filter "toPercentage"
	 * 
	 * @since 2.5.22.5
	 */
	protected static final String PN_DEFAULT_PREFILTER_TO_PERCENTAGE =
		"DefaultPrefilterToPercentage";

	/**
	 * Name of the property that controls whether to use a DefaultPairFilter
	 * 
	 * @since 2.5.22.5
	 * @see com.choicemaker.cm.analyzer.filter.DefaultPairFilter
	 */
	protected static final String PN_USE_DEFAULT_POSTFILTER =
		"UseDefaultPostfilter";

	/**
	 * Name of the property for the default filter "fromPercentage"
	 * 
	 * @since 2.5.22.5
	 */
	protected static final String PN_DEFAULT_POSTFILTER_FROM_PERCENTAGE =
		"DefaultPostfilterFromPercentage";

	/**
	 * Name of the property for the default filter "toPercentage"
	 * 
	 * @since 2.5.22.5
	 */
	protected static final String PN_DEFAULT_POSTFILTER_TO_PERCENTAGE =
		"DefaultPostfilterToPercentage";

	/**
	 * Name of the property that controls whether to use a DefaultPairSampler
	 * 
	 * @since 2.5.22.5
	 * @see com.choicemaker.cm.analyzer.sampler.DefaultPairSampler
	 */
	protected static final String PN_USE_DEFAULT_PAIR_SAMPLER =
		"UseDefaultPairSampler";

	/**
	 * Name of the property for sample size
	 * 
	 * @since 2.5.22.5
	 */
	protected static final String PN_DEFAULT_PAIR_SAMPLER_SIZE =
		"DefaultPairSamplerSize";

	public FilterConfiguration() {
	}

	@SuppressWarnings("rawtypes")
	public FilterConfiguration(FilterConfiguration cmConf1) {
		if (cmConf1 != null) {
			this.props = (Hashtable) cmConf1.props.clone();
		}
	}

	@Override
	public int getBatchSize() {
		return (Integer) props.get(PN_BATCH_SIZE);
	}

	@Override
	public float getDefaultPrefilterFromPercentage() {
		return (Float) props.get(PN_DEFAULT_PREFILTER_FROM_PERCENTAGE);
	}

	@Override
	public float getDefaultPrefilterToPercentage() {
		return (Float) props.get(PN_DEFAULT_PREFILTER_TO_PERCENTAGE);
	}

	@Override
	public float getDefaultPostfilterFromPercentage() {
		return (Float) props.get(PN_DEFAULT_POSTFILTER_FROM_PERCENTAGE);
	}

	@Override
	public float getDefaultPostfilterToPercentage() {
		return (Float) props.get(PN_DEFAULT_POSTFILTER_TO_PERCENTAGE);
	}

	@Override
	public int getDefaultPairSamplerSize() {
		return (Integer) props.get(PN_DEFAULT_PAIR_SAMPLER_SIZE);
	}

	@Override
	public boolean getUseDefaultPrefilter() {
		return (Boolean) props.get(PN_USE_DEFAULT_PREFILTER);
	}

	@Override
	public boolean getUseDefaultPostfilter() {
		return (Boolean) props.get(PN_USE_DEFAULT_POSTFILTER);
	}

	@Override
	public boolean getUseDefaultPairSampler() {
		return (Boolean) props.get(PN_USE_DEFAULT_PAIR_SAMPLER);
	}

	@SuppressWarnings("unchecked")
	public void setBatchSize(Integer integer) {
		props.put(PN_BATCH_SIZE, integer);
	}

	@SuppressWarnings("unchecked")
	public void setDefaultPrefilterFromPercentage(Float integer) {
		props.put(PN_DEFAULT_PREFILTER_FROM_PERCENTAGE, integer);
	}

	@SuppressWarnings("unchecked")
	public void setDefaultPrefilterToPercentage(Float integer) {
		props.put(PN_DEFAULT_PREFILTER_TO_PERCENTAGE, integer);
	}

	@SuppressWarnings("unchecked")
	public void setDefaultPostfilterFromPercentage(Float integer) {
		props.put(PN_DEFAULT_POSTFILTER_FROM_PERCENTAGE, integer);
	}

	@SuppressWarnings("unchecked")
	public void setDefaultPostfilterToPercentage(Float integer) {
		props.put(PN_DEFAULT_POSTFILTER_TO_PERCENTAGE, integer);
	}

	@SuppressWarnings("unchecked")
	public void setDefaultPairSamplerSize(Integer integer) {
		props.put(PN_DEFAULT_PAIR_SAMPLER_SIZE, integer);
	}

	@SuppressWarnings("unchecked")
	public void setUseDefaultPrefilter(Boolean boolean1) {
		props.put(PN_USE_DEFAULT_PREFILTER, boolean1);
	}

	@SuppressWarnings("unchecked")
	public void setUseDefaultPostfilter(Boolean boolean1) {
		props.put(PN_USE_DEFAULT_POSTFILTER, boolean1);
	}

	@SuppressWarnings("unchecked")
	public void setUseDefaultPairSampler(Boolean boolean1) {
		props.put(PN_USE_DEFAULT_PAIR_SAMPLER, boolean1);
	}

}
