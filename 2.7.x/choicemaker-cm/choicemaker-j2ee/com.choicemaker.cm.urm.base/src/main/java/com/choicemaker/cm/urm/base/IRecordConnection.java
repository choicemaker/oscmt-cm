/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.base;


/**
 * A connection between two single records based on the match or hold evaluation of those two records.
 * <p>  
 *
 * @author emoussikaev
 * @see
 */
public interface IRecordConnection {
	/**
	 * @return
	 */
	public abstract IMatchScore getMatchScore();
	/**
	 * @return
	 */
	public abstract int getRecordIndex1();
	/**
	 * @return
	 */
	public abstract int getRecordIndex2();
	/**
	 * @param score
	 */
	public abstract void setMatchScore(IMatchScore score);
	/**
	 * @param i
	 */
	public abstract void setRecordIndex1(int i);
	/**
	 * @param i
	 */
	public abstract void setRecordIndex2(int i);
}
