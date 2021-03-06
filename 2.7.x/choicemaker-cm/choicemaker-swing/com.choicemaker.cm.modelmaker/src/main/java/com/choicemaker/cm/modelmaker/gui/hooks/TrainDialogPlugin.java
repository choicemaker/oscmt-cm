/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.hooks;

import javax.swing.JPanel;

import com.choicemaker.cm.modelmaker.gui.dialogs.TrainDialog;

/**
 *
 * @author    
 */
public abstract class TrainDialogPlugin extends JPanel {
	private static final long serialVersionUID = 1L;

	public abstract boolean isParametersValid();

	public abstract void set();
	
	public abstract void setTrainDialog(TrainDialog d);
}
