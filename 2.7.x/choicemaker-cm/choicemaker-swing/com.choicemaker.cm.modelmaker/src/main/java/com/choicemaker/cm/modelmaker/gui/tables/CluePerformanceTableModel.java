/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.tables;

import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;

import com.choicemaker.client.api.Decision;
import com.choicemaker.cm.core.ClueSet;
import com.choicemaker.cm.core.ColumnDefinition;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.train.Trainer;
import com.choicemaker.cm.core.util.ChoiceMakerCoreMessages;

/**
 * 
 * @author S. Yoakum-Stover
 */
public class CluePerformanceTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;


	private boolean isEmpty;
	private CluePerformanceTableRow[] rows;

	public static final ColumnDefinition[] columns;
	static {
		columns = new ColumnDefinition[Decision.NUM_DECISIONS + 2];
		columns[0] = new ColumnDefinition("", 150, SwingConstants.CENTER);
		for (int i = 0; i < Decision.NUM_DECISIONS; ++i) {
			columns[i + 1] =
				new ColumnDefinition(ChoiceMakerCoreMessages.m.formatMessage(Decision.valueOf(i).toString()), 100, SwingConstants.RIGHT);
		}
		columns[Decision.NUM_DECISIONS + 1] =
			new ColumnDefinition(
				ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.table.performance.total"),
				100,
				SwingConstants.RIGHT);
	};

	public CluePerformanceTableModel() {
		isEmpty = true;
	}

	public ColumnDefinition[] getColumnDefinitions() {
		return columns;
	}

	public void refresh(ImmutableProbabilityModel pm, Trainer t) {
		buildRows(pm, t);
	}

	public void wipeOutTable() {
		isEmpty = true;
	}

	public static final String[] FIRST_COLS =
		{
			ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.table.performance.total.clues"),
			ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.table.performance.active.clues"),
			ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.table.performance.correct.firings"),
			ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.table.performance.incorrect.firings"),
			ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.table.performance.total.firings")};

	public void buildRows(ImmutableProbabilityModel pm, Trainer t) {
		rows = new CluePerformanceTableRow[FIRST_COLS.length];
		ClueSet cs = pm.getClueSet();
		int[] vals = new int[Decision.NUM_DECISIONS];
		for (int i = 0; i < Decision.NUM_DECISIONS; ++i) {
			vals[i] = cs.size(Decision.valueOf(i));
		}
		rows[0] = new CluePerformanceTableRow(FIRST_COLS[0], vals);
		for (int i = 0; i < Decision.NUM_DECISIONS; ++i) {
			vals[i] = pm.activeSize(Decision.valueOf(i));
		}
		rows[1] = new CluePerformanceTableRow(FIRST_COLS[1], vals);
		int[] correct = new int[Decision.NUM_DECISIONS];
		for (int i = 0; i < Decision.NUM_DECISIONS; ++i) {
			correct[i] = t.getNumCorrectFirings(Decision.valueOf(i));
		}
		rows[2] = new CluePerformanceTableRow(FIRST_COLS[2], correct);
		int[] incorrect = new int[Decision.NUM_DECISIONS];
		for (int i = 0; i < Decision.NUM_DECISIONS; ++i) {
			incorrect[i] = t.getNumIncorrectFirings(Decision.valueOf(i));
		}
		rows[3] = new CluePerformanceTableRow(FIRST_COLS[3], incorrect);
		for (int i = 0; i < Decision.NUM_DECISIONS; ++i) {
			vals[i] = correct[i] + incorrect[i];
		}
		rows[4] = new CluePerformanceTableRow(FIRST_COLS[4], vals);
		isEmpty = false;
	}

	public String getTitle() {
		return ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.table.performance.label");
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public int getRowCount() {
		if (rows == null) {
			return 0;
		}
		return rows.length;
	}

	@Override
	public String getColumnName(int col) {
		return columns[col].getName();
	}

	@Override
	public boolean isCellEditable(int nRow, int nCol) {
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (isEmpty || (rowIndex < 0) || (rowIndex >= getRowCount())) {
			return "";
		}
		return rows[rowIndex].getVal(columnIndex);
	}

}

class CluePerformanceTableRow {
	private String[] vals;

	public CluePerformanceTableRow(String label, int[] v) {
		vals = new String[v.length + 2];
		vals[0] = label;
		int total = 0;
		for (int i = 0; i < v.length; ++i) {
			vals[i + 1] = String.valueOf(v[i]);
			total += v[i];
		}
		vals[vals.length - 1] = String.valueOf(total);
	}

	public String getVal(int i) {
		return vals[i];
	}
}
