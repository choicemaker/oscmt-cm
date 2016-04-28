/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.modelmaker.gui.listeners;

import java.util.EventListener;

/**
 *
 * @author  Martin Buechi
 * @author  S. Yoakum-Stover
 */
public interface EvaluationListener extends EventListener {
	void evaluated(EvaluationEvent evt);
}
