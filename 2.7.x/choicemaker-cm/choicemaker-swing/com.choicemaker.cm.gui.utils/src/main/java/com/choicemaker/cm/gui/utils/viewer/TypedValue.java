/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.gui.utils.viewer;


/**
 * Class that wraps a String in order to tag it so
 * that we can give it a different color when we go to display it in a table.
 * 
 * @author S. Yoakum-Stover
 */
public class TypedValue {

	private String myValue;
	public boolean isValid;
	public boolean isUnique;
	public boolean isDerived;


	public TypedValue(String value, boolean isValid, boolean isUnique, boolean isDerived) {
		myValue = value;
		this.isValid = isValid;
		this.isUnique = isUnique;
		this.isDerived = isDerived;
	}

	@Override
	public String toString() {
		return myValue;
	}
}
