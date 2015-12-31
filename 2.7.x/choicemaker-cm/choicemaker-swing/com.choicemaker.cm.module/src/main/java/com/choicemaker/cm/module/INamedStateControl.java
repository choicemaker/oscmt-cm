/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.module;

import com.choicemaker.cm.module.IModule.IStateModel;

/**
 * @author rphall
 */
public interface INamedStateControl extends IStateModel {
	
	INamedState getCurrentState();
	void setCurrentState(INamedState state);
	void addStateListener(INamedStateListener l);
	void removeStateListener(INamedStateListener l);

}

