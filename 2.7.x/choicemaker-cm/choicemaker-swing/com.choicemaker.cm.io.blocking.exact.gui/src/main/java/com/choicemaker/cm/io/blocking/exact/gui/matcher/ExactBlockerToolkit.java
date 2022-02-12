/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.exact.gui.matcher;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.modelmaker.gui.matcher.BlockerToolkit;
import com.choicemaker.cm.modelmaker.gui.matcher.MatchDialogBlockerPlugin;

/**
 * Description
 * 
 * @author  Martin Buechi
 */
public class ExactBlockerToolkit implements BlockerToolkit {
	/**
	 * @see com.choicemaker.cm.train.matcher.BlockerToolkit#getDialogPlugin(com.choicemaker.cm.core.base.ProbabilityModel)
	 */
	@Override
	public MatchDialogBlockerPlugin getDialogPlugin(ImmutableProbabilityModel model) {
		return new ExactBlockerDialogPlugin(model);
	}

	@Override
	public String toString() {
		return "Exact Blocker";
	}
}
