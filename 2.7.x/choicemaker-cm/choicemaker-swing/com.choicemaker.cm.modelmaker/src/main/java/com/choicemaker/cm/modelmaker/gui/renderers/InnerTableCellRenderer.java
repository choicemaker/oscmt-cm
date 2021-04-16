/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.renderers;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

/**
 * Renderer to implement a table inside a table.
 * 
 * @author S. Yoakum-Stover
 */
public class InnerTableCellRenderer extends JTable implements TableCellRenderer {
	private static final long serialVersionUID = 1L;
	protected static Border noFocusBorder;
	public InnerTableCellRenderer() {
		super();
		noFocusBorder = new EmptyBorder(1, 2, 1, 2);
		setOpaque(true);
		setBorder(noFocusBorder);
	}

	@Override
	public Component getTableCellRendererComponent(
		JTable table,
		Object value,
		boolean isSelected,
		boolean hasFocus,
		int row,
		int column) {
		setBackground(isSelected && !hasFocus ? table.getSelectionBackground() : table.getBackground());
		setForeground(isSelected && !hasFocus ? table.getSelectionForeground() : table.getForeground());

		Border br = hasFocus ? UIManager.getBorder("Table.focusCellHighlightBorder") : noFocusBorder;
		setBorder(br);
		return this;
	}
}
