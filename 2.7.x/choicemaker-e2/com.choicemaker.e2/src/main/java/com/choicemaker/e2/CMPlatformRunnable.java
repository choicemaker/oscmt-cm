/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.e2;

/**
 * Bootstrap type for the platform. Platform runnables represent executable 
 * entry points into plug-ins.  Runnables can be configured into the Platform's
 * <code>org.eclipse.core.runtime.applications</code> extension-point 
 * or be made available through code or extensions on other plug-in's extension-points.
 *
 * <p>
 * Clients may implement this interface.
 * </p>
 */
public interface CMPlatformRunnable {
	
///**
// * Exit object indicating normal termination
// */
//public static final Integer EXIT_OK = new Integer(0);
//
///**
// * Exit object requesting platform restart
// */
//public static final Integer EXIT_RESTART = new Integer(23);
	
/**
 * Runs this runnable with the given args and returns a result.
 * The content of the args is unchecked and should conform to the expectations of
 * the runnable being invoked.  Typically this is a <code>String<code> array.
 * 
 * @exception Exception if there is a problem running this runnable.
 */
public Object run(Object args) throws Exception;
}
