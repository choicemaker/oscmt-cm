/*
 * Copyright (c) 2001, 2022 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.core.gen;

/**
 * The interface of an extension that know how to generate a clue
 * for field in a ChoiceMaker record schema.
 * @author    rphall
 */
public interface IClueGeneratorPlugin {
	void generate(IClueSetGenerator g) throws GenException;
}
