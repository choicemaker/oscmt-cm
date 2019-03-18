/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.compiler.impl;

import java.util.Properties;
import java.util.logging.Logger;

import com.choicemaker.cm.compiler.CompilationEnv;
import com.choicemaker.cm.compiler.ICompilationUnit;
import com.choicemaker.cm.compiler.Sourcecode;
import com.choicemaker.cm.core.compiler.ICompilerFeatureNames;

/**
 * ClueMaker compiler.
 *
 * @author rphall
 */
public class Compiler26 extends CMCompiler {

	private static final Logger logger = Logger.getLogger(Compiler26.class.getName());
	private static final String SRC = Compiler26.class.getSimpleName();

	public final static String VERSION_VALUE = "2.6.0";

	public final static String OPTIMIZING_VALUE = "true";

	@Override
	protected ICompilationUnit getCompilationUnit(
		CompilationEnv env,
		Sourcecode source) {
		final String METHOD = "getCompilationUnit(CompilationEnv,Sourcecode)";
		logger.entering(SRC, METHOD, new Object[] { env, source });
		// Preconditions
		if (env == null) {
			throw new IllegalArgumentException("null compilation environment");
		}
		if (source == null) {
			throw new IllegalArgumentException("null source code");
		}

		return new CompilationUnit25b(env, source);
	}

	@Override
	public Properties getFeatures() {
		Properties retVal = new Properties();
		retVal.setProperty(ICompilerFeatureNames.VERSION,VERSION_VALUE);
		retVal.setProperty(ICompilerFeatureNames.OPTIMIZING,OPTIMIZING_VALUE);
		return retVal;
	}

}

