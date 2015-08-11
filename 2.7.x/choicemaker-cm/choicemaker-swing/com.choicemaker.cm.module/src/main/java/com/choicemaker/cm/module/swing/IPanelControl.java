/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.module.swing;

import javax.swing.JPanel;

import com.choicemaker.cm.module.IModuleController.IUserInterface;

/**
 * @author rphall
 * @version $Revision: 1.1 $ $Date: 2010/03/27 19:27:57 $
 */
public interface IPanelControl extends IUserInterface {
	
	JPanel getManagedPanel();
	void setManagedPanel(JPanel panel);

}

