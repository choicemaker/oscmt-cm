/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.module.console;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;

import com.choicemaker.cm.module.IUserMessages;

/**
 * @author rphall
 */
public class SystemStatusMessages implements IUserMessages {

	@Override
	public Writer getWriter() {
		return new OutputStreamWriter(System.err);
	}

	@Override
	public OutputStream getOutputStream() {
		return System.err;
	}

	@Override
	public PrintStream getPrintStream() {
		return new PrintStream(System.err);
	}

	@Override
	public void postMessage(final String s) {
		System.err.println(s);
	}

	@Override
	public void clearMessages() {
	}

	/** Displays a message to the user */
	@Override
	public void postInfo(String s) {
		postMessage(s);
	}

}

