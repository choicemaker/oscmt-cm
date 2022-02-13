/*
 * Copyright (c) 2001, 2016 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.core.util;

import java.util.Arrays;

import com.choicemaker.util.MessageUtil;

/**
 * A LoggingObject is a logging message that can take parameters.
 * 
 * @author Adam Winkel (initial version)
 * @author rphall (added Throwable argument to constructors)
 */
public class LoggingObject {

	private final String message;
	private final Object[] params;
	private final Throwable throwable;

	public LoggingObject(String msg) {
		this(msg, new Object[0], (Throwable) null);
	}

	public LoggingObject(String msg, Throwable t) {
		this(msg, new Object[0], t);
	}

	public LoggingObject(String msg, Object[] params) {
		this(msg, params, (Throwable) null);
	}

	public LoggingObject(String msg, Object[] params, Throwable t) {
		this.message = msg;
		this.throwable = t;
		if (params != null) {
			this.params = params;
		} else {
			this.params = new Object[0];
		}
	}

	public LoggingObject(String msg, Object p1) {
		this(msg, new Object[] { p1 }, (Throwable) null);
	}

	public LoggingObject(String msg, Object p1, Exception t) {
		this(msg, new Object[] { p1 }, (Throwable) t);
	}

	public LoggingObject(String msg, Object p1, Throwable t) {
		this(msg, new Object[] { p1 }, t);
	}

	public LoggingObject(String msg, Object p1, Object p2) {
		this(msg, new Object[] {
				p1, p2 }, (Throwable) null);
	}

	public LoggingObject(String msg, Object p1, Object p2, Throwable t) {
		this(msg, new Object[] {
				p1, p2 }, t);
	}

	public LoggingObject(String msg, Object p1, Object p2, Object p3) {
		this(msg, new Object[] {
				p1, p2, p3 }, (Throwable) null);
	}

	public LoggingObject(String msg, Object p1, Object p2, Object p3,
			Throwable t) {
		this(msg, new Object[] {
				p1, p2, p3 }, t);
	}

	@Override
	public String toString() {
		final int maxLen = 3;
		return "LoggingObject [message="
				+ getMessage()
				+ ", params="
				+ (getParams() != null ? Arrays.asList(getParams()).subList(0,
						Math.min(getParams().length, maxLen)) : null)
				+ ", throwable=" + getThrowable() + "]";
	}

	protected String getMessage() {
		return message;
	}

	protected Object[] getParams() {
		return params;
	}

	protected Throwable getThrowable() {
		return throwable;
	}

	public String getFormattedMessage() {
		return getFormattedMessage(ChoiceMakerCoreMessages.m);
	}

	public String getFormattedMessage(MessageUtil m) {
		return m.formatMessage(getMessage(), getParams(), getThrowable());
	}

}
