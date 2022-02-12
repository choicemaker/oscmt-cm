/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.tables;

import com.choicemaker.cm.core.MutableMarkedRecordPair;

/**
 * Description
 * 
 * @author  Martin Buechi
 */
public abstract class ActiveClueTableModelPlugin extends ClueTableModelPlugin {
	private static final long serialVersionUID = 1L;
	protected MutableMarkedRecordPair markedRecordPair;
	
	public void setMarkedRecordPair(MutableMarkedRecordPair markedRecordPair) {
		this.markedRecordPair = markedRecordPair;
	}

	/** By default, the initial sort is ascending */
	public boolean isSortAscending() {
		return true;
	}

	/** By default, the sort column is the first column in the table */
	public int getSortColumn() {
		return 0;
	}
}
