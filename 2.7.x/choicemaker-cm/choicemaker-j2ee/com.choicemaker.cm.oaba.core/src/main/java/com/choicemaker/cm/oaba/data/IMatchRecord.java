/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.data;

/**
 * A MatchRecord has a pair of id's and a match probability on this pair.
 * 
 * @author pcheung
 * @deprecated
 */
@Deprecated
public interface IMatchRecord extends Comparable<IMatchRecord> {

	public static final char MATCH = 'M';
	public static final char DIFFER = 'D';
	public static final char HOLD = 'H';

	long getRecordID1();

	long getRecordID2();

	float getProbability();

	char getMatchType();

	char getRecord2Source();

}