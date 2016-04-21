/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;

public class ModelConfigurationException extends Exception {

	private static final long serialVersionUID = 1L;

	public ModelConfigurationException() {
	}

	public ModelConfigurationException(String message) {
		super(message);
	}

	public ModelConfigurationException(Throwable cause) {
		super(cause);
	}

	public ModelConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ModelConfigurationException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
