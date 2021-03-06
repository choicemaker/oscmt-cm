/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.module.swing;



/**
 * An object that knows about ModelMaker. To reduce dependencies
 * between plugins, object references are used in place of type-safe
 * references to ModelMaker. It should be possible to replace this interface
 * with a type-safe one by refactoring ModelMaker (quite) a bit.
 * @author rphall
 */
public interface IModelMakerAware {

	Object getModelMakerObject();
	void setModelMakerObject(Object mm);	

}

