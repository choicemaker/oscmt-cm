/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.e2.mbd.runtime.impl;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class Policy {

	private static final Logger logger =
		Logger.getLogger(Policy.class.getName());

	private static String bundleName = "com.choicemaker.e2.mbd.runtime.impl.messages";//$NON-NLS-1$
	private static ResourceBundle bundle = ResourceBundle.getBundle(bundleName, Locale.getDefault());

/**
 * Lookup the message with the given ID in this catalog 
 */
public static String bind(String id) {
	return bind(id, (String[])null);
}
/**
 * Lookup the message with the given ID in this catalog and bind its
 * substitution locations with the given string.
 */
public static String bind(String id, String binding) {
	return bind(id, new String[] {binding});
}
/**
 * Lookup the message with the given ID in this catalog and bind its
 * substitution locations with the given strings.
 */
public static String bind(String id, String binding1, String binding2) {
	return bind(id, new String[] {binding1, binding2});
}

/**
 * Lookup the message with the given ID in this catalog and bind its
 * substitution locations with the given string values.
 */
public static String bind(String id, String[] bindings) {
	if (id == null)
		return "No message available";//$NON-NLS-1$
	String message = null;
	try {
		message = bundle.getString(id);
	} catch (MissingResourceException e) {
		// If we got an exception looking for the message, fail gracefully by just returning
		// the id we were looking for.  In most cases this is semi-informative so is not too bad.
		return "Missing message: " + id + " in: " + bundleName;//$NON-NLS-1$ //$NON-NLS-2$
	}
	if (bindings == null)
		return message;
	return MessageFormat.format(message, (Object[])bindings);
}
/**
 * Print a debug message to the console. If the given boolean is <code>true</code> then
 * pre-pend the message with the current date.
 */
public static void debug(boolean includeDate, String message) {
	if (includeDate) 
		message = new Date(System.currentTimeMillis()).toString() + " - "+ message; //$NON-NLS-1$
	logger.fine(message);
}
}
