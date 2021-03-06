/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.module;



/**
 * Reacts to state changes of a module. For example,
 * a module might send out notifications when the module
 * starts or finishes an operation.
 * @author rphall
 */
public interface INamedStateListener {
	
	void stateChanged(INamedEvent evt);

}

