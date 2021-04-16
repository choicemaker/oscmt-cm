/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.args;

/**
 * This is the exception thrown by the Transitivity Engine.
 * 
 * @author pcheung
 *
 *         ChoiceMaker Technologies Inc.
 */
public class TransitivityException extends Exception {

	private static final long serialVersionUID = 1L;

	public TransitivityException() {
		super();
	}

	public TransitivityException(String message) {
		super(message);
	}

}
