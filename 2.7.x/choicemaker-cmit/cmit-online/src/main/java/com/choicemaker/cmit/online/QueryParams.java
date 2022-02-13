/*******************************************************************************
 * Copyright (c) 2003, 2016 ChoiceMaker LLC and others.
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
 * Created on Aug 26, 2009
 */
package com.choicemaker.cmit.online;

import java.io.PrintWriter;

import com.choicemaker.cm.core.ImmutableProbabilityModel;

/**
 * A struct to hold functional parameters parsed from a query entry
 * in a ChoiceMaker report. The parameters held by this struct are
 * restricted to ones that are used as input to a URM
 * findMatches or findCompositeMatches operation.
 * @author rphall
 * @version $Revision$ $Date$
 */
public class QueryParams {
	
	public static float DEFAULT_DIFFER_THRESHOLD = 0.0f;
	public static float DEFAULT_MATCH_THRESHOLD = 1.0f;
	public static int DEFAULT_MAX_NUM_MATCHES = 100;

	private ImmutableProbabilityModel model;
	private Float differThreshold;
	private Float matchThreshold;
	private Integer maxNumMatches;
	private String externalId;
	
	/** Creates an uninitialized set of query parameters */	
	public QueryParams() {}
	
	public QueryParams(
	ImmutableProbabilityModel model,
	Float differThreshold,
	Float matchThreshold,
	Integer maxNumMatches,
	String externalId
	) {
		this.model = model;
		this.differThreshold = differThreshold;
		this.matchThreshold = matchThreshold;
		this.maxNumMatches = maxNumMatches;
		this.externalId = externalId;
	}

	public void setModel(ImmutableProbabilityModel model) {
		this.model = model;
	}

	public ImmutableProbabilityModel getModel() {
		return model;
	}

	public void setDifferThreshold(Float differThreshold) {
		this.differThreshold = differThreshold;
	}

	public Float getDifferThreshold() {
		return differThreshold;
	}

	public void setMatchThreshold(Float matchThreshold) {
		this.matchThreshold = matchThreshold;
	}

	public Float getMatchThreshold() {
		return matchThreshold;
	}

	public void setMaxNumMatches(Integer maxNumMatches) {
		this.maxNumMatches = maxNumMatches;
	}

	public Integer getMaxNumMatches() {
		return maxNumMatches;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getExternalId() {
		return externalId;
	}
	
	public void dumpDebugInfo(PrintWriter pw) {
		pw.println("model: " + model.getModelName());
		pw.println("differThreshold: " + differThreshold);
		pw.println("matchThreshold: " + matchThreshold);
		pw.println("maxNumMatches: " + maxNumMatches);
		pw.println("externalId: " + externalId);
	}

}

