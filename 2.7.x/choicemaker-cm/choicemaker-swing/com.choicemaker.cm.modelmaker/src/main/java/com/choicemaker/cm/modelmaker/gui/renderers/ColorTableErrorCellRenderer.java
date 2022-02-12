/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.renderers;

import java.awt.Color;

import javax.swing.table.DefaultTableCellRenderer;

import com.choicemaker.cm.modelmaker.gui.utils.ValueError;

/**
 * Renderer used to give the ConfusionTable display its different colored text.
 * 
 * @author S. Yoakum-Stover
 */
public class ColorTableErrorCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	@Override
	public void setValue(Object value) {
		if (value instanceof ValueError) {
			ValueError v = (ValueError) value;
			setForeground(v.color);
			setText(v.toString());
		} else if (value instanceof String) {
			String v = (String) value;
			setForeground(Color.black);
			setText(v);
		} else {
			super.setValue(value);
		}
	}

}
