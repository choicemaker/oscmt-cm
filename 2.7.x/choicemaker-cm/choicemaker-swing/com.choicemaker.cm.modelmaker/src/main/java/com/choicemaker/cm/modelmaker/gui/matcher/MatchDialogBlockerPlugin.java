/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.matcher;

import javax.swing.JPanel;

import com.choicemaker.cm.core.blocking.InMemoryBlocker;

/**
 * Description
 * 
 * @author  Martin Buechi
 */
public abstract class MatchDialogBlockerPlugin extends JPanel {
	private static final long serialVersionUID = 1L;

	public abstract InMemoryBlocker getBlocker();
}
