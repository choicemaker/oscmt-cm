/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.utils;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.core.IControl;

/**
 * Utility class for checking periodically whether a process should be stopped.
 * 
 * @author pcheung
 */
public class ControlChecker {

	public static final int CONTROL_INTERVAL = 10000;

	/**
	 * This method check to see if the process should be stopped for every
	 * CONTROL_INTERVAL number of c.
	 * 
	 * @param c
	 *            - int counter
	 * @return boolean - true if the process should be stopped.
	 * @throws BlockingException
	 */
	public static boolean checkStop(IControl control, int c)
			throws BlockingException {
		return checkStop(control, c, CONTROL_INTERVAL);
	}

	/**
	 * This method allows the user to specify the interval. Control is checked
	 * when c % interval == 0.
	 * 
	 * @param control
	 * @param c
	 * @param interval
	 */
	public static boolean checkStop(IControl control, int c, int interval)
			throws BlockingException {

		boolean ret = false;
		if (c % interval == 0)
			try {
				ret = control.shouldStop();
			} catch (Exception e) {
				throw new BlockingException(e.toString());
			}
		return ret;
	}

}
