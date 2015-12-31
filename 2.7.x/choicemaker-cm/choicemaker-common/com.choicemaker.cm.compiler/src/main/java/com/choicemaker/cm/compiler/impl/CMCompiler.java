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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import com.choicemaker.cm.compiler.CompilationEnv;
import com.choicemaker.cm.compiler.ICompilationUnit;
import com.choicemaker.cm.compiler.Sourcecode;
import com.choicemaker.cm.core.Accessor;
import com.choicemaker.cm.core.IProbabilityModel;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.ProbabilityModelSpecification;
import com.choicemaker.cm.core.base.MutableProbabilityModel;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.core.compiler.CompilationArguments;
import com.choicemaker.cm.core.compiler.CompilerException;
import com.choicemaker.cm.core.compiler.ICompiler;
import com.choicemaker.cm.core.configure.ConfigurationManager;
import com.choicemaker.cm.core.util.ChoiceMakerCoreMessages;
import com.choicemaker.cm.core.xmlconf.XmlConfigurator;

/**
 * ClueMaker compiler.
 *
 * @author    Matthias Zenger
 * @author    Martin Buechi
 */
public abstract class CMCompiler implements ICompiler {

	public static final ClassLoader getJavacClassLoader() {
		String tools =
			new File(System.getProperty("java.home"))
				.getAbsoluteFile()
				.getParent()
				+ File.separator
				+ "lib"
				+ File.separator
				+ "tools.jar";
		try
		{
			URLClassLoader URLcl =
				new URLClassLoader(
					new URL[] { new File(tools).toURL() },
					CMCompiler.class.getClassLoader()
				);
			//logger.severe("Returning URLClassLoader URLcl");
			return URLcl;
		}
		catch (MalformedURLException ex)
		{
			//logger.severe("Returning ICompiler ClassLoader");
			return ICompiler.class.getClassLoader();
		}
	}

	protected static Logger logger = Logger.getLogger(CMCompiler.class.getName());
	private static final String SRC = CMCompiler.class.getSimpleName();

	protected static void usage() {
		System.out.println(
			ChoiceMakerCoreMessages.m.formatMessage(
				"compiler.comp.usage",
				"2.7.1"));
	}

	protected String getClassPath() {
		return ConfigurationManager.getInstance().getClassPath();
	}

	protected abstract ICompilationUnit getCompilationUnit(
		CompilationEnv env,
		Sourcecode source);

	public abstract Properties getFeatures();

	public int generateJavaCode(CompilationArguments arguments,
			Writer statusOutput) throws CompilerException {
		final String METHOD = "generateJavaCode(CompilationArguments,Writer)";
		ICompilationUnit unit = generateJavaCodeInternal(arguments,
			statusOutput);
		int retVal = unit.getErrors();
		logger.exiting(SRC, METHOD, Integer.valueOf(retVal));
		return retVal;
	}

	/**
	 * Returns the number of ClueMaker errors
	 * @throws CompilerException
	 */
	public ICompilationUnit generateJavaCodeInternal(CompilationArguments arguments,
			Writer statusOutput) throws CompilerException {
		final String METHOD = "generateJavaCodeInternal(CompilationArguments,Writer)";
		logger.entering(SRC, METHOD, new Object[] {arguments,statusOutput});

		String file = arguments.files()[0];
		String defaultPath = getClassPath();
		CompilationEnv env =
			new CompilationEnv(arguments, defaultPath, statusOutput);
		Sourcecode source;
		ICompilationUnit retVal = null;
		try {
			source = new Sourcecode(file, arguments.argumentVal(CompilationArguments.ENCODING),
					statusOutput);
			ICompilationUnit unit = getCompilationUnit(env, source);
			unit.compile();
			unit.conclusion(statusOutput);
			retVal = unit;
		} catch (IOException e) {
			e.fillInStackTrace();
			String msg = e.toString();
			logger.severe(msg);
			throw new CompilerException(msg,e);
		}

		logger.exiting(SRC, METHOD, retVal);
		return retVal;
	}

	public String compile(CompilationArguments arguments,
			final Writer statusOutput) throws CompilerException {
		final String METHOD = "compile(CompilationArguments,Writer)";
		logger.entering(SRC, METHOD, new Object[] {arguments,statusOutput});
		ICompilationUnit unit =
			generateJavaCodeInternal(arguments, statusOutput);
		if (unit.getErrors() == 0) {
			// Create the output directory
			File targetDir =
				new File(ConfigurationManager.getInstance().getCompiledCodeRoot());
			targetDir.getAbsoluteFile().mkdirs();

			// Create the compilation arguments
			String targetDirPath = targetDir.getAbsolutePath();
			String classPath = getClassPath();
			List generatedFiles = unit.getGeneratedJavaSourceFiles();
			final int numStdArgs = 5;
			Object[] args = new String[generatedFiles.size() + numStdArgs];
			args[0] = CompilationArguments.CLASSPATH;
			args[1] = classPath;
			args[2] = CompilationArguments.OUTPUT_DIRECTORY;
			args[3] = targetDirPath;
			args[4] = "-O";
			for (int i = 0; i < generatedFiles.size(); ++i) {
				args[i + numStdArgs] =
					new File((String) generatedFiles.get(i)).getAbsolutePath();
			}

			// result will store a return code from the compile function
			int result = -1;

			// save the location of System.out and System.err
			PrintStream out = System.out;
			PrintStream err = System.err;
			ClassLoader cl = getJavacClassLoader();
			PrintStream ps = null;
			try {

				// Change the location of System.out and System.err
				ps = new PrintStream(new OutputStream() {
					public void write(int c) throws IOException {
						statusOutput.write(c);
					}
				});
				System.setErr(ps);
				System.setOut(ps);

				// Get a handle on Sun's compiler object
				Class c = Class.forName("com.sun.tools.javac.Main", true, cl);
				Object compiler = c.newInstance();

				// Use reflection to call the compile method with the args setup
				// earlier
				Method compile =
					c.getMethod("compile",
							new Class[] { (new String[0]).getClass() });
				Integer returncode =
					(Integer) compile.invoke(compiler, new Object[] { args });

				// save the return code
				result = returncode.intValue();
			}

			catch (Exception ex) {
				logger.severe("Compiler.compile(): " + ex.toString());
				return null;
			}

			finally {
				if (ps != null) {
					ps.flush();
					ps.close();
					ps = null;
				}
				System.setErr(err);
				System.setOut(out);
			}

			if (result == MODERN_COMPILER_SUCCESS) {
				return unit.getAccessorClass();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public boolean compile(IProbabilityModel model, Writer statusOutput)
		throws CompilerException {
		final String METHOD = "compile(IProbabilityModel,Writer)";
		logger.entering(SRC, METHOD, new Object[] {model,statusOutput});
		CompilationArguments arguments = new CompilationArguments();
		String[] compilerArgs = new String[1];
		compilerArgs[0] = model.getClueFilePath();
		arguments.enter(compilerArgs);
		String accessorClass = compile(arguments, statusOutput);
		if (accessorClass != null) {
			try {
				model.setAccessor(
					PMManager.createAccessor(
						accessorClass,
						XmlConfigurator.getInstance().reload()));
			} catch (Exception ex) {
				logger.severe(ex.toString());
				return false;
			}
		} else {
			return false;
		}
		return true;
	}

	public ImmutableProbabilityModel compile(
			ProbabilityModelSpecification spec, Writer statusOutput)
			throws CompilerException {
		final String METHOD = "compile(ProbabilityModelSpecification,Writer)";
		logger.entering(SRC, METHOD, new Object[] {spec,statusOutput});
		CompilationArguments arguments = new CompilationArguments();
		String[] compilerArgs = new String[1];
		compilerArgs[0] = spec.getClueFilePath();
		arguments.enter(compilerArgs);
		String accessorFQCN = compile(arguments, statusOutput);
		ImmutableProbabilityModel retVal = null;
		if (accessorFQCN == null) {
			String status = statusOutput.toString();
			assert status != null && !status.trim().isEmpty();
			throw new CompilerException("Compilation failed: " + status);
		} else {
			assert !accessorFQCN.trim().isEmpty();
			try {
				/* FIXME Review this design */
//				Accessor acc = InstallableModelManager.getInstance().createAccessor(accessorClass,
//						XmlConfigurator.getInstance().reload());
//				retVal = InstallableModelManager.getInstance().createModelInstance(spec,acc);
//				ChoiceMakerConfiguration cmc = InstalledConfiguration
//						.getInstance();
//				ChoiceMakerConfigurator configurator = InstallableConfigurator
//						.getInstance();
//				cmc = configurator.reloadClasses(cmc);
//				ClassLoader cl = cmc.getClassLoader();
				ClassLoader cl = ConfigurationManager.getInstance()
						.getClassLoader();
//				ProbabilityModelManager pmm = InstallableModelManager
//						.getInstance();
//				Accessor acc = pmm.createAccessor(accessorClass, cl);
//				retVal = pmm.createModelInstance(spec, acc);
				Class accessorClass = Class.forName(accessorFQCN, true, cl);
				Accessor acc = (Accessor) accessorClass.newInstance();
				retVal = new MutableProbabilityModel(spec, acc);
				/* END */
			} catch (Exception ex) {
				String msg = "Compilation failed: " + ex.toString();
				logger.severe(msg);
				throw new CompilerException(msg);
			}
		}
		assert retVal != null;

		return retVal;
	}

} // Compiler

