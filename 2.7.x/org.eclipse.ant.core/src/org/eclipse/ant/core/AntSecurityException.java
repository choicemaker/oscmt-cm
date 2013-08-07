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
package org.eclipse.ant.core;


/**
 * A security exception that is thrown by the AntSecurityManager if
 * an Ant task in some way attempts to halt or exit the Java Virtual Machine.
 * 
 * @since 2.1
 */
public class AntSecurityException extends SecurityException {

	// 2013-08-07 rphall
	private static final long serialVersionUID = 1L;

}
