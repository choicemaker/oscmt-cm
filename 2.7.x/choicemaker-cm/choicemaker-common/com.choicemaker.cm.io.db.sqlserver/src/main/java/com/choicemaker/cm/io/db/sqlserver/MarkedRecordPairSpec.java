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
package com.choicemaker.cm.io.db.sqlserver;

import com.choicemaker.cm.core.Decision;

public class MarkedRecordPairSpec {
	
	private Comparable qId, mId;
	private Decision d;
	
	public MarkedRecordPairSpec(Comparable qId, Comparable mId, Decision d) {
		this.qId = qId;
		this.mId = mId;
		this.d = d;
	}
	
	public Comparable getQId() {
		return qId;
	}
	
	public Comparable getMId() {
		return mId;
	}
	
	public Decision getDecision() {
		return d;
	}

	public String toString() {
		return "MarkedRecordPairSpec [qId=" + qId + ", mId=" + mId + ", d=" + d
				+ "]";
	}
	
}
