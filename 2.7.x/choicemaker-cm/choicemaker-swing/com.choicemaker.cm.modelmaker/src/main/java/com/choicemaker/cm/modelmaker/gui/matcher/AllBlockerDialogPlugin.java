/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.matcher;

import java.awt.FlowLayout;

import javax.swing.JLabel;

import com.choicemaker.cm.analyzer.matcher.AllBlocker;
import com.choicemaker.cm.core.blocking.InMemoryBlocker;

/**
 * Description
 *
 * @author  Martin Buechi
 */
public class AllBlockerDialogPlugin extends MatchDialogBlockerPlugin {
	private static final long serialVersionUID = 1L;

	AllBlockerDialogPlugin() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(new JLabel("Returns all records from small source."));
	}

	/**
	 * @see com.choicemaker.cm.train.matcher.MatchDialogBlockerPlugin#getBlocker()
	 */
	public InMemoryBlocker getBlocker() {
		return new AllBlocker();
	}

}
