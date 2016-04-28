/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.utils;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class EnablednessGuard implements DocumentListener {
	private Enable target;

	public EnablednessGuard(Enable target) {
		this.target = target;
	}

	public void changedUpdate(DocumentEvent e) {
		target.setEnabledness();
	}

	public void insertUpdate(DocumentEvent e) {
		target.setEnabledness();
	}

	public void removeUpdate(DocumentEvent e) {
		target.setEnabledness();
	}
}
