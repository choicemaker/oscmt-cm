/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.module;



/**
 * Reacts to the events within a module. For example,
 * a module might send out notifications when the module
 * starts or finishes an operation. Clients would listen
 * for events from a module, rather than for state changes
 * of a module, if they needed to implement a state model
 * that was completely independent of the module state
 * model.
 * @author rphall
 */
public interface INamedEventListener {
	
	void eventOccurred(INamedEvent evt);

}

