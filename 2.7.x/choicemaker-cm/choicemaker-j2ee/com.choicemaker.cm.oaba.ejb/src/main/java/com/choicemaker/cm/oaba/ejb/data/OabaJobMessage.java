/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb.data;

import java.io.Serializable;

import com.choicemaker.cm.oaba.impl.ValidatorBase;

/**
 * This is the data object that gets passed to the StartOabaMDB message bean.
 * 
 * @author pcheung
 *
 */
public class OabaJobMessage implements Serializable {

	static final long serialVersionUID = 271;

	public final long jobID;

	public ValidatorBase validator;

	/**
	 * An index used to split processing across a set of agents that are running
	 * in parallel.
	 */
	public int processingIndex;

	/**
	 * An index used to assign a Matcher to set of records within a chunk.
	 */
	public int treeIndex;

	// constructor
	public OabaJobMessage(long jobId) {
		this.jobID = jobId;
	}

	// copy constructor
	public OabaJobMessage(OabaJobMessage data) {
		this.jobID = data.jobID;
		this.processingIndex = data.processingIndex;
		this.treeIndex = data.treeIndex;
		this.validator = data.validator;
	}

	// create MatchWriterMessage from OabaJobMessage
	public OabaJobMessage(MatchWriterMessage data) {
		this.jobID = data.jobID;
		this.processingIndex = data.processingIndex;
		this.treeIndex = data.treeIndex;
	}

	@Override
	public String toString() {
		return "OabaJobMessage [jobID=" + jobID + "]";
	}

}
