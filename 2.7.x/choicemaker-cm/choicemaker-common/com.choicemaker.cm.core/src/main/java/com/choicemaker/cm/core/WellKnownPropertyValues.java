/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.core;

/**
 * Commonly used values of the system-wide properties that are used to configure
 * ChoiceMaker. These values should never be changed, but may be deprecated in
 * favor of updated values that are defined as additional manifest constants in
 * this interface.
 */
public interface WellKnownPropertyValues {
	
	/**
	 * Specifies an embedded version of the ChoiceMaker E2 platform.
	 */
	public static final String E2_EMBEDDED_PLATFORM = "com.choicemaker.e2.embed.EmbeddedPlatform";

	/**
	 * Specifies a basic version of the ChoiceMaker compiler appropriate for
	 * embedding into JAR files, where the classpath used to compile is
	 * specified by the application that invokes the compiler.
	 * @see PropertyNames#INSTALLABLE_COMPILER
	 */
	public static final String BASIC_COMPILER = "com.choicemaker.cm.compiler.impl.Compiler26";

	/**
	 * Specifies an Eclipse 2 backed version of the ChoiceMaker compiler, where
	 * the classpath used to compile ClueMaker files is augmented by the plugin
	 * descriptors of a Eclipse-2 registry.
	 * @see PropertyNames#INSTALLABLE_COMPILER
	 */
	public static final String ECLIPSE2_COMPILER = BASIC_COMPILER;

	/**
	 * Specifies an Eclipse 2 backed version of a factory for generator plugins.
	 * @see PropertyNames#INSTALLABLE_GENERATOR_PLUGIN_FACTORY
	 */
	public static final String ECLIPSE2_GENERATOR_PLUGIN_FACTORY = "com.choicemaker.cm.core.gen.Eclipse2GeneratorPluginFactory";

	/**
	 * Specifies a list-backed version of a factory for generator plugins.
	 * @see PropertyNames#INSTALLABLE_GENERATOR_PLUGIN_FACTORY
	 */
	public static final String LIST_BACKED_GENERATOR_PLUGIN_FACTORY = "com.choicemaker.cm.core.gen.ListBackedGeneratorPluginFactory";

	/**
	 * Specifies an Eclipse 2 backed version of a ChoiceMaker configurator, suitable for desktop
	 * applications like CM Analyzer.
	 * @see PropertyNames#INSTALLABLE_CHOICEMAKER_CONFIGURATOR
	 */
	public static final String ECLIPSE2_CONFIGURATOR = "com.choicemaker.cm.core.xmlconf.XmlConfigurator";

	/**
	 * Specifies a list backed version of a ChoiceMaker configurator, suitable for plain-old Java
	 * applications.
	 * @see PropertyNames#INSTALLABLE_CHOICEMAKER_CONFIGURATOR
	 */
	public static final String LIST_BACKED_CONFIGURATOR = "com.choicemaker.cm.core.configure.ListBackedConfigurator";

}
