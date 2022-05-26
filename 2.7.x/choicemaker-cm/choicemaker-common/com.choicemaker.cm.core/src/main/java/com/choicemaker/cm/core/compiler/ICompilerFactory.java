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

import java.util.Properties;

/**
 * @author rphall
 * @deprecated see InstallableCompiler
 */
@Deprecated
public interface ICompilerFactory {

	/** This method gets the compiler with the given set of properties.
	 *
	 * @param features - a Properties object specifying compiler information.
	 * @return The compiler matching the given features.
	 * @throws UnavailableCompilerFeaturesException
	 */
	ICompiler getCompiler(Properties features) throws UnavailableCompilerFeaturesException;


	/** Returns the compiler with the given name */
	ICompiler getCompiler(String name) throws UnavailableCompilerFeaturesException;


	/** Returns the default compiler */
	ICompiler getDefaultCompiler();

}
