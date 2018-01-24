/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.api;

public class ServerConfigurationException extends Exception {

	private static final long serialVersionUID = 271L;

	public ServerConfigurationException() {
	}

	public ServerConfigurationException(String message) {
		super(message);
	}

	public ServerConfigurationException(Throwable cause) {
		super(cause);
	}

	public ServerConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServerConfigurationException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
