/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.module;

/**
 * @author rphall
 */
public interface IMessageSupport {
	public abstract String formatMessage(String messageKey);
	public abstract String formatMessage(String messageKey, Object[] args);
	public abstract String formatMessage(String messageKey, Object arg0);
	public abstract String formatMessage(
		String messageKey,
		Object arg0,
		Object arg1);
	public abstract String formatMessage(
		String messageKey,
		Object arg0,
		Object arg1,
		Object arg2);
	public abstract String formatMessage(
		String messageKey,
		Object arg0,
		Object arg1,
		Object arg2,
		Object arg3);
}
