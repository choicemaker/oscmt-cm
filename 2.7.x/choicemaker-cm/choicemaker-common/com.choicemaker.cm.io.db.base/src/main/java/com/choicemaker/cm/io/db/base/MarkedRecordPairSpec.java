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

import java.io.Serializable;

import com.choicemaker.client.api.Decision;

public class MarkedRecordPairSpec<T extends Comparable<T> & Serializable> {
	
	private T qId;
	private T mId;
	private Decision d;
	
	public MarkedRecordPairSpec(T qId, T mId, Decision d) {
		this.qId = qId;
		this.mId = mId;
		this.d = d;
	}
	
	public T getQId() {
		return qId;
	}
	
	public T getMId() {
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
