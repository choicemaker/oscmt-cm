/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.ml;

import javax.swing.table.TableColumn;

import com.choicemaker.cm.core.DynamicDispatchHandler;
import com.choicemaker.cm.core.MachineLearner;
import com.choicemaker.cm.modelmaker.gui.hooks.TrainDialogPlugin;
import com.choicemaker.cm.modelmaker.gui.tables.ActiveClueTableModelPlugin;
import com.choicemaker.cm.modelmaker.gui.tables.ClueTableModelPlugin;

/**
 * Description
 * 
 * @author  Martin Buechi
 */
public abstract class MlGuiFactory implements DynamicDispatchHandler {
	private static ClueTableModelPlugin clueTableModelPlugin;
	private static ActiveClueTableModelPlugin activeClueTableModelPlugin;

	/**
	 * Returns the TrainDialogPlugin to show as part of AbstractApplication's Train dialog.
	 * This is used to specify parameters specific to this machine learning technique.
	 *
	 * @return   The TrainDialogPlugin to show as part of AbstractApplication's Train dialog.
	 */
	public abstract TrainDialogPlugin getTrainDialogPlugin(MachineLearner learner);

	public synchronized ClueTableModelPlugin getClueTableModelPlugin() {
		if(clueTableModelPlugin == null) {
			clueTableModelPlugin = new ClueTableModelPlugin() {
				private static final long serialVersionUID = 1L;
				@Override
				public TableColumn getColumn(int column) {
					throw new UnsupportedOperationException();
				}
				@Override
				public int getRowCount() {
					throw new UnsupportedOperationException();
				}
				@Override
				public int getColumnCount() {
					return 0;
				}
				@Override
				public Object getValueAt(int rowIndex, int columnIndex) {
					throw new UnsupportedOperationException();
				}
			};
		}
		return clueTableModelPlugin;
	}
	
	public synchronized ActiveClueTableModelPlugin getActiveClueTableModelPlugin() {
		if(activeClueTableModelPlugin == null) {
			activeClueTableModelPlugin = new ActiveClueTableModelPlugin() {
				private static final long serialVersionUID = 1L;
				@Override
				public TableColumn getColumn(int column) {
					throw new UnsupportedOperationException();
				}
				@Override
				public int getRowCount() {
					throw new UnsupportedOperationException();
				}
				@Override
				public int getColumnCount() {
					return 0;
				}
				@Override
				public Object getValueAt(int rowIndex, int columnIndex) {
					throw new UnsupportedOperationException();
				}				
			};
		}
		return activeClueTableModelPlugin;
	}
	
	public abstract MachineLearner getMlInstance();
}
