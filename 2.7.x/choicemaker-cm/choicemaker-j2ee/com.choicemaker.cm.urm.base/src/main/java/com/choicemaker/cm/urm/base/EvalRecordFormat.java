/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.base;

import java.io.Serializable;

/**
 * A format of an evaluated record. It consists of the format of the record and the format of the match score.
 * <p>  
 *
 * @author emoussikaev
 * @see
 */
public class EvalRecordFormat implements  Serializable {

	/* As of 2010-03-10 */
	static final long serialVersionUID = 4029012672168307509L;

	ScoreType	scoreType;
	RecordType		recordType;

	public EvalRecordFormat(ScoreType scoreType, RecordType recordType) {
		super();
		this.scoreType = scoreType;
		this.recordType = recordType; 
	}

	public RecordType getRecordType() {
		return recordType;
	}

	public ScoreType getScoreType() {
		return scoreType;
	}

	public void setRecordType(RecordType type) {
		recordType = type;
	}

	/**
	 * @param type
	 */
	public void setScoreType(ScoreType type) {
		scoreType = type;
	}

}
