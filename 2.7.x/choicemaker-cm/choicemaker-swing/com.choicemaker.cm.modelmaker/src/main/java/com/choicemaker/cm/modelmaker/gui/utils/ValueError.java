/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.utils;

import java.awt.Color;

/**
 * Class that wraps a String in order to tag it so
 * that we can give it a different color when we go to display it in a table.
 * 
 * @author S. Yoakum-Stover
 */
public class ValueError {

	private String myValue;
	public Color color = new Color(0.8f, 0f, 0f);


	public ValueError(String value) {
		myValue = value;
	}

	@Override
	public String toString() {
		return myValue;
	}
}
