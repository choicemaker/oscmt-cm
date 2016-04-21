/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;

import com.choicemaker.cm.core.base.DynamicDispatcher;

/**
 * A dispatch handler can handle object of a specific type and its
 * subtypes. The method used for performing some operation on the
 * handled objects is application specific.
 *
 * @see       DynamicDispatcher
 * @author    Martin Buechi
 */
public interface DynamicDispatchHandler {
	/**
	 * Returns the handler.
	 *
	 * @return  The handler.
	 */
	Object getHandler();

	/**
	 * Returns the handled type.
	 *
	 * @return  The handled type.
	 */
	Class getHandledType();
}
