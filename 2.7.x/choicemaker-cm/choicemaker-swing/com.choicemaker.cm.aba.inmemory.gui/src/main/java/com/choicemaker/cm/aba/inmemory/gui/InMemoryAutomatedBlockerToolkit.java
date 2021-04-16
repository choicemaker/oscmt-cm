/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.aba.inmemory.gui;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.modelmaker.gui.matcher.BlockerToolkit;
import com.choicemaker.cm.modelmaker.gui.matcher.MatchDialogBlockerPlugin;

/*
 * Created on Jan 21, 2004
 *
 */

/**
 * @author ajwinkel
 *
 */
public class InMemoryAutomatedBlockerToolkit implements BlockerToolkit {

	@Override
	public MatchDialogBlockerPlugin getDialogPlugin(ImmutableProbabilityModel model) {
		return new InMemoryAutomatedBlockerDialogPlugin(model);
	}

}
