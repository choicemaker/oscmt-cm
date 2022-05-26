/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.core;

/**
 * This determines if a block is valid.
 * 
 * @author pcheung
 *
 */
public interface IValidatorBase {

	/** This returns true if this is a valid BlockSet. */
	public boolean validBlockSet(BlockSet bs);

}