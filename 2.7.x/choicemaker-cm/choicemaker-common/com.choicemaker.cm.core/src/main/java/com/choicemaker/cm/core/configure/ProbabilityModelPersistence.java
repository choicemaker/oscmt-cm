/*******************************************************************************
 * Copyright (c) 2003, 2014 ChoiceMaker LLC and others.
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
package com.choicemaker.cm.core.configure;

import java.io.InputStream;
import java.io.Writer;

import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.XmlConfException;

public interface ProbabilityModelPersistence {

	void saveModel(ImmutableProbabilityModel model) throws XmlConfException;

	ImmutableProbabilityModel readModel(String fileName, Compiler compiler, Writer w)
			throws XmlConfException;

	ImmutableProbabilityModel readModel(String fileName, InputStream is,
			Compiler compiler, Writer statusOutput) throws XmlConfException;

	ImmutableProbabilityModel readModel(String fileName, InputStream is,
			Compiler compiler, Writer statusOutput,
			ClassLoader customClassLoader) throws XmlConfException;

	void loadProductionProbabilityModels(Compiler compiler, boolean fromResource)
			throws XmlConfException;

	void loadProductionProbabilityModels(Compiler compiler)
			throws XmlConfException;

}