/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.tables;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class AccuracyTable extends JTable {
	private static final long serialVersionUID = 1L;
	private AccuracyTableModel myModel;

	public AccuracyTable(boolean firstColAcc, float[] firstColumn) {
		myModel = new AccuracyTableModel(firstColAcc, firstColumn);
		setModel(myModel);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setCellSelectionEnabled(true);
		DefaultTableCellRenderer tcr = (DefaultTableCellRenderer) getDefaultRenderer(Object.class);
		tcr.setHorizontalAlignment(SwingConstants.RIGHT);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				//                 Point origin = e.getPoint();
				//                 int row = rowAtPoint(origin);
				//                 int col = columnAtPoint(origin);
				//                 if((row < 0) || (col < 1) || (row > 3) || (col > 4)) {
				//                     return;
				//                 }
				// 		MarkedRecordPairFilter filter = parent.parent.filter;
				// 		filter.reset();
				// 		if(row < 3) {
				// 		    boolean[] b = new boolean[Decision.NUM_DECISIONS];
				// 		    b[row] = true;
				// 		    filter.setHumanDecision(b);
				// 		}
				// 		if(col < 4) {
				// 		    boolean[] b = new boolean[Decision.NUM_DECISIONS];
				// 		    b[col - 1] = true;
				// 		    filter.setChoiceMakerDecision(b);
				// 		}
				// 		parent.parent.filterMarkedRecordPairList();
			}
		});
	}

	public void reset() {
		myModel.reset();
	}

	public void refresh(float[][] data) {
		myModel.refresh(data);
	}
}
