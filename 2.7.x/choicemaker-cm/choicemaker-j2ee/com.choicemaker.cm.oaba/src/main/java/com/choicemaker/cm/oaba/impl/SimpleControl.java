/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.impl;

import com.choicemaker.cm.core.IControl;

/**
 * @author pcheung
 *
 */
public class SimpleControl implements IControl {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.choicemaker.cm.oaba.core.IControl#shouldStop
	 * ()
	 */
	@Override
	public boolean shouldStop() {
		return false;
	}

}
