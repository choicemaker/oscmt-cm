/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.listeners;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;

import com.choicemaker.cm.modelmaker.gui.ModelMaker;
import com.choicemaker.cm.modelmaker.gui.tables.ClueTableModel;
import com.choicemaker.cm.modelmaker.gui.utils.NullInteger;

/**
 * This is the thing that listens for the mouse
 * clicks on a table cell.
 * 
 * @author S. Yoakum-Stover
 */
public class ClueTableCellListener extends MouseAdapter {

	private JTable table;
	private ModelMaker meTrainer;
	private int numPluginColumns;

	public ClueTableCellListener(ModelMaker met, JTable table, int numPluginColumns) {
		meTrainer = met;
		this.table = table;
		this.numPluginColumns = numPluginColumns;
	}

	/**
	 * Invoked when the mouse has been clicked on a component.
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		Point origin = e.getPoint();
		int row = table.rowAtPoint(origin);
		int col = table.columnAtPoint(origin);
		//logger.debug("Selected row: " + row + " column: " + col);
		//get the index of the column in the table model
		int modelIndex = table.getColumnModel().getColumn(col).getModelIndex();
		if(modelIndex > ClueTableModel.PRE_COLUMNS.length) {
			modelIndex -= numPluginColumns;
		}
		int clueID = ((NullInteger) table.getValueAt(row, 0)).value();
		meTrainer.updateRecordPairList(clueID, modelIndex);
	}

}
