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
 * A connection between two single records based on the match or hold evaluation of those two records.
 * <p>
 *
 * @author emoussikaev
 * @see
 */
public class RecordConnection implements Serializable, IRecordConnection {

	/* As of 2010-03-10 */
	private static final long serialVersionUID = -5535427933058906963L;

	protected IMatchScore 	matchScore;
	protected int			recordIndex1;
	protected int			recordIndex2;

	public RecordConnection() {
		super();
	}

	public RecordConnection(IMatchScore score,int i1, int i2) {
		matchScore = score;
		recordIndex1 = i1;
		recordIndex2 = i2;
	}

	@Override
	public IMatchScore getMatchScore() {
		return matchScore;
	}

	@Override
	public int getRecordIndex1() {
		return recordIndex1;
	}

	@Override
	public int getRecordIndex2() {
		return recordIndex2;
	}

	@Override
	public void setMatchScore(IMatchScore score) {
		matchScore = score;
	}

	@Override
	public void setRecordIndex1(int i) {
		recordIndex1 = i;
	}

	@Override
	public void setRecordIndex2(int i) {
		recordIndex2 = i;
	}

}
