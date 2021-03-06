/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.e2.mbd.runtime;

import com.choicemaker.e2.mbd.runtime.impl.Assert;

/**
 * A concrete status implementation, suitable either for 
 * instantiating or subclassing.
 */
public class Status implements IStatus {
	/**
	 * The severity. One of
	 * <ul>
	 * <li><code>ERROR</code></li>
	 * <li><code>WARNING</code></li>
	 * <li><code>INFO</code></li>
	 * <li>or <code>OK</code> (0)</li>
	 * </ul>
	 */
	private int severity = OK;
	
	/** Unique identifier of plug-in.
	 */
	private String pluginId;
	
	/** Plug-in-specific status code.
	 */
	private int code;

	/** Message, localized to the current locale.
	 */
	private String message;

	/** Wrapped exception, or <code>null</code> if none.
	 */
	private Throwable exception = null;

	/** Constant to avoid generating garbage.
	 */
	private static final IStatus[] theEmptyStatusArray = new IStatus[0];
/**
 * Creates a new status object.  The created status has no children.
 *
 * @param severity the severity; one of <code>OK</code>,
 *   <code>ERROR</code>, <code>INFO</code>, or <code>WARNING</code>
 * @param pluginId the unique identifier of the relevant plug-in
 * @param code the plug-in-specific status code, or <code>OK</code>
 * @param message a human-readable message, localized to the
 *    current locale
 * @param exception a low-level exception, or <code>null</code> if not
 *    applicable 
 */
public Status(int severity, String pluginId, int code, String message, Throwable exception) {
	setSeverity(severity);
	setPlugin(pluginId);
	setCode(code);
	setMessage(message);
	setException(exception);
}
/* (Intentionally not javadoc'd)
 * Implements the corresponding method on <code>IStatus</code>.
 */
@Override
public IStatus[] getChildren() {
	return theEmptyStatusArray;
}
/* (Intentionally not javadoc'd)
 * Implements the corresponding method on <code>IStatus</code>.
 */
@Override
public int getCode() {
	return code;
}
/* (Intentionally not javadoc'd)
 * Implements the corresponding method on <code>IStatus</code>.
 */
@Override
public Throwable getException() {
	return exception;
}
/* (Intentionally not javadoc'd)
 * Implements the corresponding method on <code>IStatus</code>.
 */
@Override
public String getMessage() {
	return message;
}
/* (Intentionally not javadoc'd)
 * Implements the corresponding method on <code>IStatus</code>.
 */
@Override
public String getPlugin() {
	return pluginId;
}
/* (Intentionally not javadoc'd)
 * Implements the corresponding method on <code>IStatus</code>.
 */
@Override
public int getSeverity() {
	return severity;
}
/* (Intentionally not javadoc'd)
 * Implements the corresponding method on <code>IStatus</code>.
 */
@Override
public boolean isMultiStatus() {
	return false;
}
/* (Intentionally not javadoc'd)
 * Implements the corresponding method on <code>IStatus</code>.
 */
@Override
public boolean isOK() {
	return severity == OK;
}
/* (Intentionally not javadoc'd)
 * Implements the corresponding method on <code>IStatus</code>.
 */
@Override
public boolean matches(int severityMask) {
	return (severity & severityMask) != 0;
}
/**
 * Sets the status code.
 *
 * @param code the plug-in-specific status code, or <code>OK</code>
 */
protected void setCode(int code) {
	this.code = code;
}
/**
 * Sets the exception.
 *
 * @param exception a low-level exception, or <code>null</code> if not
 *    applicable 
 */
protected void setException(Throwable exception) {
	this.exception = exception;
}
/**
 * Sets the message.
 *
 * @param message a human-readable message, localized to the
 *    current locale
 */
protected void setMessage(String message) {
	Assert.isLegal(message != null);
	this.message = message;
}
/**
 * Sets the plug-in id.
 *
 * @param pluginId the unique identifier of the relevant plug-in
 */
protected void setPlugin(String pluginId) {
	Assert.isLegal(pluginId != null && pluginId.length() > 0);
	this.pluginId = pluginId;
}
/**
 * Sets the severity.
 *
 * @param severity the severity; one of <code>OK</code>,
 *   <code>ERROR</code>, <code>INFO</code>, or <code>WARNING</code>
 */
protected void setSeverity(int severity) {
	Assert.isLegal(severity == OK || severity == ERROR || severity == WARNING || severity == INFO);
	this.severity = severity;
}
/**
 * Returns a string representation of the status, suitable 
 * for debugging purposes only.
 */
@Override
public String toString() {
	StringBuffer buf = new StringBuffer();
	buf.append("Status "); //$NON-NLS-1$
	if (severity == OK) {
		buf.append("OK"); //$NON-NLS-1$
	} else if (severity == ERROR) {
		buf.append("ERROR"); //$NON-NLS-1$
	} else if (severity == WARNING) {
		buf.append("WARNING"); //$NON-NLS-1$
	} else if (severity == INFO) {
		buf.append("INFO"); //$NON-NLS-1$
	} else {
		buf.append("severity="); //$NON-NLS-1$
		buf.append(severity);
	}
	buf.append(pluginId);
	buf.append(" code="); //$NON-NLS-1$
	buf.append(code);
	buf.append(' ');
	buf.append(message);
	buf.append(' ');
	buf.append(exception);
	return buf.toString();
}
}
