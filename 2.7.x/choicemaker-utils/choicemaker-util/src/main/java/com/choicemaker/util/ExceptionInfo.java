/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.util;

/**
 * Gathers information from an exception
 */
public class ExceptionInfo {

	public final Throwable x;
	public final String className;
	public final String simpleClassName;
	public final String message;
	public final String causeClassName;
	public final String causeSimpleClassName;
	public final String causeMessage;

	public ExceptionInfo(Throwable x) {
		Precondition.assertNonNullArgument("null exception", x);
		this.x = x;
		className = x.getClass().getName();
		simpleClassName = x.getClass().getSimpleName();
		message = x.getMessage() == null ? "" : x.getMessage();
		final Throwable t = x.getCause();
		if (t != null) {
			causeClassName = t.getClass().getName();
			causeSimpleClassName = t.getClass().getSimpleName();
			causeMessage = t.getMessage() == null ? "" : t.getMessage();
		} else {
			causeClassName = "";
			causeSimpleClassName = "";
			causeMessage = "";
		}
	}

	public String toString(String context) {
		StringBuilder sb = new StringBuilder();
		if (context != null) {
			sb.append(context.trim());
		}
		if (sb.length() > 0) {
			sb.append(": ");
		}
		sb.append(this.toString());
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Exception " + simpleClassName);
		if (!message.isEmpty()) {
			sb.append(": " + message);
		}
		if (!causeSimpleClassName.isEmpty()) {
			sb.append(" (" + causeSimpleClassName);
			if (!causeMessage.isEmpty()) {
				sb.append(": " + causeMessage);
			}
			sb.append(")");
		}
		String retVal = sb.toString();
		return retVal;
	}

}
