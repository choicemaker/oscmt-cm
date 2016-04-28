/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.tables;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import com.choicemaker.cm.core.ImmutableProbabilityModel;

/**
 * Description
 * 
 * @author  Martin Buechi
 */
public abstract class ClueTableModelPlugin extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	protected ImmutableProbabilityModel model;
	protected int startColumn;
	
	public abstract TableColumn getColumn(int column);
	
	public void setModel(ImmutableProbabilityModel model) {
		this.model = model;
	}
	
	public void setStartColumn(int startColumn) {
		this.startColumn = startColumn;
	}
}
