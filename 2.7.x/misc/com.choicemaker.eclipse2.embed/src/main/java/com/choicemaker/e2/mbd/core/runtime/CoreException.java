/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.choicemaker.e2.mbd.core.runtime;

import java.io.PrintStream;
import java.io.PrintWriter;
/**
 * A checked exception representing a failure.
 * <p>
 * Core exceptions contain a status object describing the 
 * cause of the exception.
 * </p>
 *
 * @see IStatus
 */
public class CoreException extends Exception {

	/** Status object. */
	private IStatus status;
/**
 * Creates a new exception with the given status object.  The message
 * of the given status is used as the exception message.
 *
 * @param status the status object to be associated with this exception
 */
public CoreException(IStatus status) {
	super(status.getMessage());
	this.status = status;
}
/**
 * Returns the status object for this exception.
 *
 * @return a status object
 */
public final IStatus getStatus() {
	return status;
}
/**
 * Prints a stack trace out for the exception, and
 * any nested exception that it may have embedded in
 * its Status object.
 */
public void printStackTrace() {
	printStackTrace(System.err);
}
/**
 * Prints a stack trace out for the exception, and
 * any nested exception that it may have embedded in
 * its Status object.
 */
public void printStackTrace(PrintStream output) {
	synchronized (output) {
		if (status.getException() != null) {
			output.print(getClass().getName() + "[" + status.getCode() + "]: "); //$NON-NLS-1$ //$NON-NLS-2$
			status.getException().printStackTrace(output);
		} else
			super.printStackTrace(output);
	}
}
/**
 * Prints a stack trace out for the exception, and
 * any nested exception that it may have embedded in
 * its Status object.
 */
public void printStackTrace(PrintWriter output) {
	synchronized (output) {
		if (status.getException() != null) {
			output.print(getClass().getName() + "[" + status.getCode() + "]: "); //$NON-NLS-1$ //$NON-NLS-2$
			status.getException().printStackTrace(output);
		} else
			super.printStackTrace(output);
	}
}

}
