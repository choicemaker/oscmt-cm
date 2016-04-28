/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.abstraction;

import javax.swing.JPanel;
import javax.swing.text.Document;

/**
 * The panel which displays status messages to the user.
 * 
 * @author S. Yoakum-Stover
 */
public abstract class AbstractMessagePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public AbstractMessagePanel() {
		super();
		buildPanel();
	}
	
	/** Provides the document used to display messages */
	public abstract Document getDocument();

	/** Callback from within constructor */
	protected abstract void buildPanel();

}

