/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.gui.utils.plaf;

import javax.swing.UIDefaults;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * Description
 * 
 * @author  Martin Buechi
 */
public class ChoiceMakerMetalTheme extends DefaultMetalTheme {
	public static void init() {		
		try {
			MetalLookAndFeel.setCurrentTheme(new ChoiceMakerMetalTheme());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * @see javax.swing.plaf.metal.MetalTheme#addCustomEntriesToTable(javax.swing.UIDefaults)
	 */
	@Override
	public void addCustomEntriesToTable(UIDefaults table) {
		super.addCustomEntriesToTable(table);
		table.put("InternalFrameUI", ChoiceMakerInternalFrameUI.class.getName());
	}

}
