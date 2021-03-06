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
package com.choicemaker.cm.core.compiler;

import java.io.Writer;
import java.util.Properties;

import com.choicemaker.cm.core.IProbabilityModel;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.ProbabilityModelSpecification;

/**
 * A placeholder for objects that require, but don't use, a compiler.
 * (There's a lot of these objects in the J2EE projects.) Most
 * methods throw a <code>CompilerException</code>.
 *
 * @author rphall
 */
public final class DoNothingCompiler implements ICompiler {

	static final ICompiler instance = new DoNothingCompiler();

	public DoNothingCompiler() {}

	@Override
	public int generateJavaCode(CompilationArguments arguments,
			Writer statusOutput) throws CompilerException {
		throw new CompilerException("DoNothingCompiler method 'compile' not implemented");
	}

	@Override
	public String compile(CompilationArguments arguments, Writer statusOutput)
		throws CompilerException {
		throw new CompilerException("DoNothingCompiler method 'generateJavaCode' not implemented");
	}

	@Override
	public ImmutableProbabilityModel compile(ProbabilityModelSpecification model, Writer statusOutput)
		throws CompilerException {
			throw new CompilerException("DoNothingCompiler method 'compile' not implemented");
	}

	@Override
	public boolean compile(IProbabilityModel model, Writer statusOutput)
			throws CompilerException {
		throw new CompilerException("DoNothingCompiler method 'compile' not implemented");
	}

	@Override
	public Properties getFeatures() {
		return new Properties();
	}

}
