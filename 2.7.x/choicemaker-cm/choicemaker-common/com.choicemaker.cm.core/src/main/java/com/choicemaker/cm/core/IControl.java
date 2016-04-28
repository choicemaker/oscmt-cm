/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;


/**
 * This interface tells long-running objects if a loop should be interrupted.
 * 
 * @author pcheung
 *
 */
public interface IControl {
	
	/** This method returns true if the long running loop should be stopped.
	 * 
	 * @return boolean
	 */
	public boolean shouldStop ();

}
