/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.gui.utils.viewer.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import com.choicemaker.cm.core.util.ChoiceMakerCoreMessages;
import com.choicemaker.cm.gui.utils.viewer.RecordPairFrameModel;
import com.choicemaker.cm.gui.utils.viewer.RecordTable;
import com.choicemaker.cm.gui.utils.viewer.RecordTableModel;
import com.choicemaker.cm.gui.utils.viewer.dialog.FieldSelectorDialog;

/**
 * This is the thing that listens for the mouse
 * clicks on a table cell.
 * 
 * @author S. Yoakum-Stover
 */
public class RecordPairFramePopupManager extends MouseAdapter {
	private static final String EDIT_COL = "Edit Columns";
	private static final String INSERT =
		ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.listener.recordviewer.insert.row");


	private boolean enableEditing;
	private JPopupMenu popup;
	private RecordTable recordTable;
	private RecordPairFrameModel recordPairFrameModel;
	private JMenuItem insertRow;

	public RecordPairFramePopupManager(RecordTable recordTable, RecordPairFrameModel recordPairFrameModel) {
		this.recordPairFrameModel = recordPairFrameModel;
		this.recordTable = recordTable;
		buildMenu();
	}

	private void buildMenu() {
		popup = new JPopupMenu();
		insertRow = new JMenuItem(INSERT);
		insertRow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				JMenuItem item = (JMenuItem) (ev.getSource());
				String selection = item.getText();
				if (selection.equals(INSERT)) {
					((RecordTableModel) recordTable.getModel()).addRow(0, true);
				}
			}
		});
		popup.add(insertRow);
						
		JMenuItem editColumns = new JMenuItem(EDIT_COL);
		editColumns.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new FieldSelectorDialog(getParentFrame(), recordPairFrameModel);
			}
		});
		popup.add(editColumns);

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
			insertRow.setEnabled(  recordTable.isEnabled() 
							&& ((RecordTableModel)recordTable.getModel()).isRecordPairSet()
							&& ((RecordTableModel)recordTable.getModel()).isStackable());
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	private JFrame getParentFrame(){
		return (JFrame)SwingUtilities.getRoot(recordTable);
	}
	/**
	 * Sets the enableEditing.
	 * @param enableEditing The enableEditing to set
	 */
	public void setEnableEditing(boolean enableEditing) {
		this.enableEditing = enableEditing;
	}

}
