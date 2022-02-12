/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.gui.utils.swing;

import java.awt.Toolkit;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class IntegerField extends JTextField {
	private static final long serialVersionUID = 1L;

	public static final int DEFAULT = Integer.MIN_VALUE;

	private Toolkit toolkit;
	private NumberFormat integerFormatter;

	public IntegerField(int columns) {
		this(DEFAULT, columns);
	}

	public IntegerField(int value, int columns) {
		super(columns);
		setMinimumSize(getPreferredSize()); // this prevents GridbagLayout from collapsing it.
		toolkit = Toolkit.getDefaultToolkit();
		integerFormatter = NumberFormat.getNumberInstance(Locale.US);
		integerFormatter.setParseIntegerOnly(true);
		setValue(value);
	}

	public void clear() {
		setValue(DEFAULT);
	}

	public int getValue() {
		int retVal = DEFAULT;
		try {
			retVal = integerFormatter.parse(getText()).intValue();
		} catch (ParseException e) {
			// This can only happen if the user types (-) then inserts another (-) before it.
			toolkit.beep();
			System.err.println("invalid Number: " + getText());
		}
		return retVal;
	}

	public void setValue(int value) {
		//        setText(integerFormatter.format(value));
		if (value == DEFAULT) {
			setText("");
		} else {
			setText("" + value);
		}
	}

	@Override
	protected Document createDefaultModel() {
		return new IntegerDocument();
	}

	protected class IntegerDocument extends PlainDocument {
		private static final long serialVersionUID = 1L;

		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			char[] source = str.toCharArray();
			char[] result = new char[source.length];
			int j = 0;

			for (int i = 0; i < result.length; i++) {
				if (Character.isDigit(source[i]) || ('-' == source[i] && offs == 0))
					result[j++] = source[i];
				else {
					toolkit.beep();
					System.err.println("insertString: " + source[i]);
				}
			}
			super.insertString(offs, new String(result, 0, j), a);
		}
	}
}
