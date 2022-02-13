/*******************************************************************************
 * Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
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
package com.choicemaker.cm.core.gen;

import java.util.logging.Logger;

public class FakeGeneratorPlugin implements GeneratorPlugin {

	private static final Logger logger = Logger.getLogger(FakeGeneratorPlugin.class.getName());

	@Override
	public void generate(IGenerator g) throws GenException {
		logger.info("generate invoked with argument: '" + g + "'");
	}

}
