/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.gui.utils.viewer.event;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.choicemaker.cm.core.util.ChoiceMakerCoreMessages;
import com.choicemaker.cm.gui.utils.viewer.RecordTable;
import com.choicemaker.cm.gui.utils.viewer.RecordTableModel;

/**
 * This is the thing that listens for the mouse
 * clicks on a table cell.
 * 
 * @author S. Yoakum-Stover
 */
public class RecordTableMouseListener extends MouseAdapter {

	private boolean enableEditing;
	private JPopupMenu popup;
	private RecordTable recordTable;
	private static final String INSERT_ABOVE =
		ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.listener.recordtable.insert.row.above");
	private static final String INSERT_BELOW =
		ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.listener.recordtable.insert.row.below");
	private static final String DELETE =
		ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.listener.recordtable.delete.row");
	private int rowIndex;

	public RecordTableMouseListener() {
		buildMenu();
	}

	private void buildMenu() {
		popup = new JPopupMenu();
		ActionListener rowListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				JMenuItem item = (JMenuItem) (ev.getSource());
				String selection = item.getText();
				if (selection.equals(INSERT_ABOVE)) {
					((RecordTableModel)recordTable.getModel()).addRow(rowIndex, true);
				}
				if (selection.equals(INSERT_BELOW)) {
					((RecordTableModel)recordTable.getModel()).addRow(rowIndex, false);
				} else if (selection.equals(DELETE)) {
					//                 logger.debug("Delete row at position : " + theRow);
					((RecordTableModel)recordTable.getModel()).deleteRow(rowIndex);
				}
			}
		};
		JMenuItem insertRowAbove = new JMenuItem(INSERT_ABOVE);
		insertRowAbove.addActionListener(rowListener);
		JMenuItem insertRowBelow = new JMenuItem(INSERT_BELOW);
		insertRowBelow.addActionListener(rowListener);
		JMenuItem deleteRow = new JMenuItem(DELETE);
		deleteRow.addActionListener(rowListener);
		popup.add(insertRowAbove);
		popup.add(insertRowBelow);
		popup.add(deleteRow);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger()) {
			displayPopup(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()) {
			displayPopup(e);
		}
	}

	private void displayPopup(MouseEvent e) {
		if (enableEditing){
			recordTable = (RecordTable) e.getSource();
			popup.show(e.getComponent(), e.getX(), e.getY());
			Point origin = e.getPoint();
			rowIndex = recordTable.rowAtPoint(origin);
		}
	}
	
	public void setEnableEditing(boolean enableEditing){
		this.enableEditing = enableEditing;
	}
}
