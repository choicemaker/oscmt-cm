/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.module.swing;

import javax.swing.JPanel;

/**
 * @author rphall
 */
public class DefaultPanelControl implements IPanelControl {
	
	private JPanel managedPanel = new JPanel();

	/** Manages a single panel */
	public DefaultPanelControl() {
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.module.swing.IPanelControl#getManagedPanel()
	 */
	@Override
	public JPanel getManagedPanel() {
		return this.managedPanel;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.module.swing.IPanelControl#setManagedPanel(javax.swing.JPanel)
	 */
	@Override
	public void setManagedPanel(JPanel panel) {
		// Precondition: the panel should not be null, otherwise
		// this module would become disconnected from any visible
		// UI. If you really want a disconnected module, you are on your own.
		if (panel == null) {
			throw new IllegalArgumentException("null panel");
		}
		this.managedPanel = panel;
	}

}

