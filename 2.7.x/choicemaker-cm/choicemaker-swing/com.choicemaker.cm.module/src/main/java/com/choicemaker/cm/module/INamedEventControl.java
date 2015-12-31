/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.module;

import com.choicemaker.cm.module.IModule.IEventModel;

/**
 * @author rphall
 */
public interface INamedEventControl extends IEventModel {
	
	void addEventListener(INamedEventListener l);
	void removeEventListener(INamedEventListener l);

}

