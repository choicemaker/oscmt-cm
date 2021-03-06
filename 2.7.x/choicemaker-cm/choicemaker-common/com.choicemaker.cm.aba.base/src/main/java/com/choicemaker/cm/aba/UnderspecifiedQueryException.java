/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.aba;

import java.io.IOException;

/**
 * Comment
 *
 * @author Martin Buechi
 */
public class UnderspecifiedQueryException extends IOException {
	private static final long serialVersionUID = 1L;

	public UnderspecifiedQueryException() {
		super();
	}

	public UnderspecifiedQueryException(String message) {
		super(message);
	}
}
