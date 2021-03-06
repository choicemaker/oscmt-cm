/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.module.swing;



/**
 * An object that knows about JMenu functionality. To reduce dependencies
 * between plugins, object references are used in place of type-safe
 * references to JMenu. It should be possible to replace this interface
 * with a type-safe one by restricting it to Swing-specific package.
 * @author rphall
 */
public interface IMenuAware {

	Object getMenuObject();
	void setMenuObject(Object mo);	

}

