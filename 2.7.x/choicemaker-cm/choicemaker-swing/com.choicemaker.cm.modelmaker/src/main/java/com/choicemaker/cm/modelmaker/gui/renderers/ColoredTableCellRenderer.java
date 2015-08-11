/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.renderers;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ColoredTableCellRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1L;
	private Color[][] backgroundColor;
    private Color[] rowBackgroundColor;
    private Color[] columnBackgroundColor;

    public ColoredTableCellRenderer(Color[][] backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public static ColoredTableCellRenderer getColoredRowsInstance(Color[] rowBackgroundColor) {
        ColoredTableCellRenderer instance = new ColoredTableCellRenderer(null);
        instance.rowBackgroundColor = rowBackgroundColor;
        return instance;
    }

    public static ColoredTableCellRenderer getColoredColumnsInstance(Color[] columnBackgroundColor) {
        ColoredTableCellRenderer instance = new ColoredTableCellRenderer(null);
        instance.columnBackgroundColor = columnBackgroundColor;
        return instance;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int row, int column) {
        setEnabled(table == null || table.isEnabled()); // see question above
        if (backgroundColor != null) {
            setBackground(backgroundColor[row][column]);
        } else if (rowBackgroundColor != null) {
            setBackground(rowBackgroundColor[row]);
        } else if (columnBackgroundColor != null) {
            setBackground(columnBackgroundColor[column]);
        } else {
            setBackground(null);
        }
        super.getTableCellRendererComponent(table, value, selected, focused, row, column);
        return this;
    }
}
