/*
 * Copyright (c) 2001, 2022 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.core.compiler;

/**
 * Error in compiler.
 *
 * @author   Matthias Zenger
 */
public class CompilerException extends Exception {
	private static final long serialVersionUID = 1L;

	public CompilerException() {
		super();
	}

	public CompilerException(String s) {
		super(s);
	}

	public CompilerException(String message, Throwable cause) {
		super(message, cause);
	}

	public CompilerException(Throwable cause) {
		super(cause);
	}

} // CompilerException


