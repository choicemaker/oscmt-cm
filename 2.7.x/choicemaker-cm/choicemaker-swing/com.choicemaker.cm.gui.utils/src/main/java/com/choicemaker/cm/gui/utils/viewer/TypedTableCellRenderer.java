/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.gui.utils.viewer;

import java.awt.Color;
import java.awt.Font;

import javax.swing.table.DefaultTableCellRenderer;


/**
 * Rendered used to give different colors to the data in the RecordTables.
 * 
 * @author S. Yoakum-Stover
 */
public class TypedTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	private Color black = new Color(0f, 0f, 0f);
	private Color red = new Color(1f, 0f, 0f);
//	private Color invalidDerived = Color.lightGray;
	private Color defaultBackground;

	public TypedTableCellRenderer(Color backgroundColor) {
		defaultBackground = backgroundColor;
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof TypedValue) {
			TypedValue v = (TypedValue) value;

			if (v.isUnique) {
				setForeground(red);
			} else if (!v.isUnique) {
				setForeground(black);
			}

			if (v.isValid) {
				setBackground(defaultBackground);
			} else {
				setBackground(Color.lightGray);
			}
			if (v.isDerived) {
				setFont(getFont().deriveFont(Font.ITALIC));
			} else {
				setFont(getFont().deriveFont(0));
			}
			setText(v.toString());

		} else {
			setForeground(black);
			setBackground(defaultBackground);
			super.setValue(value);
		}
	}

}
