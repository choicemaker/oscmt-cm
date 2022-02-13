/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.cms.ejb;

import java.io.Serializable;

import com.choicemaker.cm.core.ActiveClues;
import com.choicemaker.cm.core.Evaluator;

public class TestEvaluator extends Evaluator {

	public <T extends Comparable<T> & Serializable> TestEvaluator(
			TestModel<T> model) {
		super(model);
	}

	@Override
	public float getProbability(ActiveClues a) {
		return 0;
	}

	@Override
	public String getSignature() {
		return null;
	}

}
