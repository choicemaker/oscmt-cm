/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Jun 2, 2004
 *
 */
package com.choicemaker.cm.io.db.base;

import com.choicemaker.client.api.Decision;

public class MarkedRecordPairSpec {
	
	private String qId;
	private String mId;
	private Decision d;
	
	public MarkedRecordPairSpec(String qId, String mId, Decision d) {
		this.qId = qId;
		this.mId = mId;
		this.d = d;
	}
	
	public String getQId() {
		return qId;
	}
	
	public String getMId() {
		return mId;
	}
	
	public Decision getDecision() {
		return d;
	}

	@Override
	public String toString() {
		return "MarkedRecordPairSpec [qId=" + qId + ", mId=" + mId + ", d=" + d
				+ "]";
	}
	
}
