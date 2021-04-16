/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.matcher;

import com.choicemaker.cm.core.ImmutableProbabilityModel;

/**
 * Description
 * 
 * @author  Martin Buechi
 */
public interface BlockerToolkit {
	@Override
	String toString();
	
	MatchDialogBlockerPlugin getDialogPlugin(ImmutableProbabilityModel model);
}
