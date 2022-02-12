/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.ml.me.gui;

import java.util.logging.Logger;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import com.choicemaker.cm.core.MachineLearner;
import com.choicemaker.cm.core.util.ChoiceMakerCoreMessages;
import com.choicemaker.cm.ml.me.base.MaximumEntropy;
import com.choicemaker.cm.modelmaker.gui.hooks.TrainDialogPlugin;
import com.choicemaker.cm.modelmaker.gui.ml.MlGuiFactory;
import com.choicemaker.cm.modelmaker.gui.tables.ActiveClueTableModelPlugin;
import com.choicemaker.cm.modelmaker.gui.tables.ClueTableModelPlugin;
import com.choicemaker.cm.modelmaker.gui.utils.NullFloat;

/**
 * Description
 * 
 * @author  Martin Buechi
 */
public class MeGuiFactory extends MlGuiFactory {
	private static final Logger logger =
		Logger.getLogger(MeGuiFactory.class.getName());

	private ClueTableModelPlugin clueTableModelPlugin;
	private ActiveClueTableModelPlugin activeClueTableModelPlugin;

	@Override
	public ClueTableModelPlugin getClueTableModelPlugin() {
		if (clueTableModelPlugin == null) {
			clueTableModelPlugin = new ClueTableModelPlugin() {
				private static final long serialVersionUID = 1L;
				TableColumn weightColumn;
				@Override
				public TableColumn getColumn(int column) {
					if (weightColumn == null) {
						DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
						renderer.setHorizontalAlignment(SwingConstants.RIGHT);
						TableCellEditor editor = new DefaultCellEditor(new JTextField());
						weightColumn = new TableColumn(startColumn, 150, renderer, editor);
					}
					return weightColumn;
				}
				@Override
				public String getColumnName(int column) {
					return ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.table.common.weight");
				}
				@Override
				public boolean isCellEditable(int row, int column) {
					return !model.getClueSet().getClueDesc()[row].rule;
				}
				@Override
				public Object getValueAt(int row, int column) {
					float[] weights = ((MaximumEntropy) model.getMachineLearner()).getWeights();
					return weights == null
						|| model.getClueSet().getClueDesc()[row].rule
							? NullFloat.getNullInstance()
							: new NullFloat(weights[row]);
				}
				@Override
				public void setValueAt(Object value, int row, int col) {
					try {
						float we = Float.parseFloat(value.toString());
						((MaximumEntropy) model.getMachineLearner()).getWeights()[row] = we;
					} catch (NumberFormatException ex) {
						String msg =
							"Maximum Entropy ClueTableModelPlugin.setValue"
									+ "(value: '" + value.toString()
									+ "', row: " + row + ", col: " + col + "): "
									+ ex.toString();
						logger.warning(msg);
					}
				}
				@Override
				public int getColumnCount() {
					return 1;
				}
				@Override
				public int getRowCount() {
					throw new UnsupportedOperationException();
				}
			};
		}
		return clueTableModelPlugin;
	}

	@Override
	public ActiveClueTableModelPlugin getActiveClueTableModelPlugin() {
		if (activeClueTableModelPlugin == null) {
			activeClueTableModelPlugin = new ActiveClueTableModelPlugin() {
				private static final long serialVersionUID = 1L;
				TableColumn weightColumn;
				@Override
				public TableColumn getColumn(int column) {
					if (weightColumn == null) {
						DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
						renderer.setHorizontalAlignment(SwingConstants.RIGHT);
						TableCellEditor editor = new DefaultCellEditor(new JTextField());
						weightColumn = new TableColumn(startColumn, 150, renderer, editor);
					}
					return weightColumn;
				}
				@Override
				public String getColumnName(int column) {
					return ChoiceMakerCoreMessages.m.formatMessage("train.gui.modelmaker.table.common.weight");
				}
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
				@Override
				public Object getValueAt(int row, int column) {
					if (model.getClueSet().getClueDesc()[row].rule) {
						return NullFloat.getNullInstance();
					} else {
						return new NullFloat(((MaximumEntropy) model.getMachineLearner()).getWeights()[row]);
					}
				}
				@Override
				public void setValueAt(Object value, int row, int col) {
					throw new UnsupportedOperationException();
				}
				@Override
				public int getColumnCount() {
					return 1;
				}
				@Override
				public int getRowCount() {
					throw new UnsupportedOperationException();
				}

				@Override
				public boolean isSortAscending() {
					return false;
				}

				@Override
				public int getSortColumn() {
					return startColumn;
				}
			};
		}
		return activeClueTableModelPlugin;
	}
	/**
	 * @see com.choicemaker.cm.ml.gui.MlGuiFactory#getTrainDialogPlugin(com.choicemaker.cm.core.MachineLearner)
	 */
	@Override
	public TrainDialogPlugin getTrainDialogPlugin(MachineLearner learner) {
		return new MeTrainDialogPlugin((MaximumEntropy) learner);
	}
	/**
	 * @see com.choicemaker.cm.core.base.DynamicDispatchHandler#getHandler()
	 */
	@Override
	public Object getHandler() {
		return this;
	}
	/**
	 * @see com.choicemaker.cm.core.base.DynamicDispatchHandler#getHandledType()
	 */
	@Override
	public Class<?> getHandledType() {
		return MaximumEntropy.class;
	}

	@Override
	public String toString() {
		return ChoiceMakerCoreMessages.m.formatMessage("ml.me.label");
	}
	/**
	 * @see com.choicemaker.cm.ml.gui.MlGuiFactory#getMlInstance()
	 */
	@Override
	public MachineLearner getMlInstance() {
		return new MaximumEntropy();
	}
}
