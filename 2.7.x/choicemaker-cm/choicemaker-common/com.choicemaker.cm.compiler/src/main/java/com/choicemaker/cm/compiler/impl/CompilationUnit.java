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
package com.choicemaker.cm.compiler.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.choicemaker.cm.compiler.CompilationEnv;
import com.choicemaker.cm.compiler.ICompilationUnit;
import com.choicemaker.cm.compiler.Location;
import com.choicemaker.cm.compiler.Modifiers;
import com.choicemaker.cm.compiler.Printer;
import com.choicemaker.cm.compiler.Scope;
import com.choicemaker.cm.compiler.ScopeEntry;
import com.choicemaker.cm.compiler.Sourcecode;
import com.choicemaker.cm.compiler.Symbol;
import com.choicemaker.cm.compiler.Symbol.ClassSymbol;
import com.choicemaker.cm.compiler.Symbol.PackageSymbol;
import com.choicemaker.cm.compiler.Symbol.VarSymbol;
import com.choicemaker.cm.compiler.Tags;
import com.choicemaker.cm.compiler.Tree;
import com.choicemaker.cm.compiler.Type;
import com.choicemaker.cm.compiler.parser.Parser;
import com.choicemaker.cm.compiler.parser.Scanner;
import com.choicemaker.cm.compiler.typechecker.DeriveType;
import com.choicemaker.cm.compiler.typechecker.EnterClues;
import com.choicemaker.cm.compiler.typechecker.TypeChecker;
import com.choicemaker.cm.core.Constants;
import com.choicemaker.cm.core.compiler.CompilerException;
import com.choicemaker.cm.core.util.ChoiceMakerCoreMessages;

/**
 * Objects of that class represent a single compilation unit; The current
 * version of ClueMaker compiles only a single compilation unit.
 *
 * @author Matthias Zenger
 * @author Martin Buechi
 */
abstract class CompilationUnit implements Tags, ICompilationUnit {

	private static final Logger logger = Logger.getLogger(CompilationUnit.class.getName());

	/**
	 * the compilation environment
	 */
	private CompilationEnv env;

	/**
	 * the corresponding sourcecode
	 */
	private Sourcecode source;

	/**
	 * the package of this compilation unit
	 */
	private PackageSymbol pckage;

	/**
	 * the base class (of q and m)
	 */
	private Symbol baseClass;

	/**
	 * the base class (of r)
	 */
	// public Symbol sameBaseClass;
	private Symbol existsBaseClass;
	/** base class of r in simple shorthands */
	private Symbol doubleIndexBaseClass;

	/**
	 * the content of the compilation unit in tree form
	 */
	private Tree[] decls;

	/**
	 * the content of the translation in tree form
	 */
	private Tree[] target;

	/**
	 * the named import scope
	 */
	private Scope namedImports;

	/**
	 * the star import scope
	 */
	private Scope starImports;

	/**
	 * the ambiguous imports
	 */
	private Scope ambiguousImports;

	/**
	 * the number of errors in this compilation unit
	 */
	private int errors;

	/**
	 * the number of warnings in this compilation unit
	 */
	private int warnings;

	/**
	 * the list of generated files
	 */
	private List generatedJavaSourceFiles;

	/** the file name of the clue set */
	private String clueSetFileName;

	/** The name of the accessor class */
	private String accessorClass;

	/** The package in which the clue set will be placed */
	private String packageName;

	/** Strings of fields are intern()d */
	private boolean intern;

	private String schemaName;

	/**
	 * create a new compilation unit
	 */
	public CompilationUnit(CompilationEnv env, Sourcecode source) {
		// Preconditions
		if (env == null) {
			throw new IllegalArgumentException("null compilation environment");
		}
		if (source == null) {
			throw new IllegalArgumentException("null source code");
		}

		// initialize compilation unit
		setCompilationEnv(env);
		setSource(source);
		setNamedImports(new Scope(null));
		setStarImports(new Scope(null));
		setAmbiguousImports(new Scope(null));
		setBaseClass(Symbol.NONE);
		setGeneratedJavaSourceFiles(new ArrayList());
		// include java.lang.*
		PackageSymbol p = env.repository.definePackage("java.lang");
		ScopeEntry e = p.members().elems;
		while (e != ScopeEntry.NONE) {
			this.getStarImports().enter(e.sym);
			e = e.next;
		}
	}

	@Override
	public void compile() throws CompilerException {
		syntacticAnalysis();
		semanticAnalysis();
		codeGeneration();
	}

	@Override
	public void syntacticAnalysis() throws CompilerException {
		long start = System.currentTimeMillis();
		setDecls(new Parser(new Scanner(this)).compilationUnit());
		if (getCompilationEnv().debug) {
			new Printer().printUnit(this);
		}
		getCompilationEnv().message(
				"[parsed " + getSource() + " in "
						+ (System.currentTimeMillis() - start) + "ms]");
	}

	@Override
	public void semanticAnalysis() throws CompilerException {
		if (getErrors() == 0 && getCompilationEnv().errors == 0)
			// get a symbol for the package of this compilation unit
			setPackage(getCompilationEnv().repository
					.definePackage(getPackageName()));
		// setup symbol table
		new EnterClues(this).enter();
		// typecheck clue sets
		if (getErrors() == 0) {
			new TypeChecker(this).typecheck();
		}
	}

	@Override
	public abstract void codeGeneration() throws CompilerException;

	/**
	 * issue an error for this compilation context
	 */
	@Override
	public void error(String message) throws CompilerException {
		if (getSource().printMessageIfNew(Location.NOPOS, message)) {
			setErrors(getErrors() + 1);
			if (getCompilationEnv().prompt) {
				throw new CompilerException(
						ChoiceMakerCoreMessages.m
								.formatMessage("compiler.unit.abort"));
			}
		}
	}

	/**
	 * issue a warning for this compilation context
	 */
	@Override
	public void warning(String message) {
		if (getSource().printMessageIfNew(Location.NOPOS, message)) {
			setWarnings(getWarnings() + 1);
		}
	}

	/**
	 * issue an error for a specific line of this compilation context
	 */
	@Override
	public void error(int pos, String message) throws CompilerException {
		String msg = getSource().getShortName() + ": " + pos + ": " + message;
		logger.severe(msg);
		if (getSource().printMessageIfNew(pos, message)) {
			setErrors(getErrors() + 1);
			if (getCompilationEnv().prompt) {
				String msg0 =
					ChoiceMakerCoreMessages.m
							.formatMessage("compiler.unit.abort");
				throw new CompilerException(msg0);
			}
		}
	}

	/**
	 * issue a warning for a specific source code file
	 */
	@Override
	public void warning(Sourcecode source, String message) {
		if (source.printMessageIfNew(Location.NOPOS, ChoiceMakerCoreMessages.m
				.formatMessage("compiler.unit.warning", message)))
			;
		setWarnings(getWarnings() + 1);
	}

	/**
	 * issue a warning for a specific line of a specific source code file
	 */
	@Override
	public void warning(int pos, String message) {
		if (getSource().printMessageIfNew(
				pos,
				ChoiceMakerCoreMessages.m.formatMessage(
						"compiler.unit.warning", message)))
			;
		setWarnings(getWarnings() + 1);
	}

	@Override
	public void conclusion(Writer statusOutput) {
		setErrors(getErrors() + getCompilationEnv().errors);
		setWarnings(getWarnings() + getCompilationEnv().warnings);
		try {
			if ((getErrors() + getWarnings()) > 0) {
				statusOutput.write(ChoiceMakerCoreMessages.m.formatMessage(
						"compiler.unit.conclusion", new Integer(getErrors()),
						new Integer(getWarnings()))
						+ Constants.LINE_SEPARATOR);
			} else {
				statusOutput.write(ChoiceMakerCoreMessages.m
						.formatMessage("compiler.unit.compilation.complete")
						+ Constants.LINE_SEPARATOR);
			}
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}

	/**
	 * Call-back method for the Generator to populate the symbol table with the
	 * holder classes.
	 *
	 * All classes are in the same package as the clue set file generated by the
	 * ClueMaker compiler.
	 *
	 * Currently, the non-qualified name is passed, e.g., Sample__patient. I can
	 * easily change this to pass the fully qualified name instead.
	 *
	 * @param className
	 *            The name of the class.
	 */
	@Override
	public void addClassType(String className) {
		// define a new class
		ClassSymbol c =
			getCompilationEnv().repository.defineClass(getPackage().fullname()
					+ "." + className);
		// initialize the new class
		c.init(Modifiers.PUBLIC | Modifiers.SCHEMA,
				getCompilationEnv().repository.defineClass("java.lang.Object"),
				new ClassSymbol[0]);
		// enter it in the current package
		getPackage().members().enter(c);
	}

	/**
	 * Adds a field to a class type created with addClassType.
	 *
	 * @param className
	 *            The name of the class.
	 * @param typeName
	 *            The name of the type.
	 * @param fieldName
	 *            The name of the field.
	 */
	@Override
	public void addField(String className, String typeName, String fieldName)
			throws CompilerException {
		// Do actual addition to symbol table.
		// Once the compiler offers functionality to resolve type names,
		// only fully qualified types (e.g., java.lang.String rather than
		// String)
		// will be passed. Also, all types will be verified to exist in the
		// classpath.
		// Because the schema has its own set of import's, it is not guaranteed
		// that
		// typeName exists in the import's of the clue set. (We could change it
		// to
		// have only one set of import's if absolutely necessary.)

		// The following implementation assumes that className and fieldName
		// are simple names; typeName does not have to be a fully qualified name
		// either, but different import statements might yield different types
		// in such a case.
		// Another assumption is, that class consistency gets checked by the
		// caller of addField; e.g. it does not get checked that a field is not
		// entered twice etc.

		// get the class symbol
		ClassSymbol c =
			getCompilationEnv().repository.defineClass(getPackage().fullname()
					+ "." + className);
		// create a field symbol
		VarSymbol v =
			new VarSymbol(fieldName.intern(),
					new DeriveType(this).typeOf(typeName), Modifiers.PUBLIC, c);
		// enter the field symbol into the member table of c
		c.members().enter(v);
		// System.out.println("Added field " + typeName + " " + fieldName +
		// " to holder class " + className);
	}

	/**
	 * Adds a field to a class type created with addClassType.
	 *
	 * The typeName must be added via addClassType prior to calling this method.
	 *
	 * @param className
	 *            The name of the class.
	 * @param typeName
	 *            The name of the type.
	 * @param recordName
	 *            The name of the record.
	 */
	@Override
	public void addNestedRecord(String className, String typeName,
			String recordName) throws CompilerException {
		// Do actual addition to symbol table.

		// get the class symbol
		ClassSymbol c =
			getCompilationEnv().repository.defineClass(getPackage().fullname()
					+ "." + className);
		// create a field symbol for the nested record
		VarSymbol v =
			new VarSymbol(recordName.intern(), new Type.ArrayType(
					new DeriveType(this).typeOf(typeName)), Modifiers.PUBLIC, c);
		// enter the field symbol into the member table of c
		c.members().enter(v);

		// System.out.println("Added nested record " + typeName + " " +
		// recordName +
		// " to holder class " + className);
	}

	/**
	 * Defines the type for Q and M.
	 *
	 * @param name
	 *            The name of the type. Must be created with addClassType prior
	 *            to calling this method.
	 */
	@Override
	public void setBaseType(String name) {
		setBaseClass(getCompilationEnv().repository.defineClass(getPackage()
				.fullname() + "." + name));
	}

	@Override
	public void addGeneratedJavaSourceFile(String fileName) {
		getGeneratedJavaSourceFiles().add(fileName);
	}

	@Override
	public ClassSymbol createSameBaseClass(ClassSymbol c) {
		ClassSymbol res =
			new ClassSymbol(c.getName(), c.getOwner(),
					getCompilationEnv().repository);
		res.init(c.modifiers(), c.superclass(), c.interfaces());
		Scope s = res.members();
		ScopeEntry e = c.members().elems;
		while (e != ScopeEntry.NONE) {
			if (!e.sym.getType().isArray()) {
				VarSymbol v =
					new VarSymbol(e.sym.getName(), e.sym.getType(),
							Modifiers.PUBLIC, res);
				s.enter(v);
			}
			e = e.next;
		}
		return res;
	}

	@Override
	public ClassSymbol createExistsBaseClass(ClassSymbol c) {
		ClassSymbol res =
			new ClassSymbol(c.getName(), c.getOwner(),
					getCompilationEnv().repository);
		res.init(c.modifiers(), c.superclass(), c.interfaces());
		Scope s = res.members();
		ScopeEntry e = c.members().elems;
		while (e != ScopeEntry.NONE) {
			if (e.sym.getType().isArray()) {
				if ((e.sym.getType().elemtype().sym().modifiers() & Modifiers.SCHEMA) != 0) {
					VarSymbol v =
						new VarSymbol(e.sym.getName(), createExistsBaseClass(
								(ClassSymbol) e.sym.getType().elemtype().sym())
								.getType(), Modifiers.PUBLIC, e.sym.getOwner());
					s.enter(v);
				} else {
					VarSymbol v =
						new VarSymbol(e.sym.getName(), e.sym.getType(),
								Modifiers.PUBLIC, res);
					s.enter(v);
				}
			} else
				s.enter(e.sym);
			e = e.next;
		}
		return res;
	}

	@Override
	public ClassSymbol createDoubleIndexBaseClass(ClassSymbol c) {
		ClassSymbol res =
			new ClassSymbol(c.getName(), c.getOwner(),
					getCompilationEnv().repository);
		res.init(c.modifiers(), c.superclass(), c.interfaces());
		Scope s = res.members();
		ScopeEntry e = c.members().elems;
		while (e != ScopeEntry.NONE) {
			if (e.sym.getType().isArray()) {
				if ((e.sym.getType().elemtype().sym().modifiers() & Modifiers.SCHEMA) != 0) {
					VarSymbol v =
						new VarSymbol(e.sym.getName(), new Type.ArrayType(
								createDoubleIndexBaseClass(
										(ClassSymbol) e.sym.getType()
												.elemtype().sym()).getType(),
								true), Modifiers.PUBLIC, e.sym.getOwner());
					s.enter(v);
				} else {
					VarSymbol v =
						new VarSymbol(e.sym.getName(), e.sym.getType(),
								Modifiers.PUBLIC, res);
					s.enter(v);
				}
			} else
				s.enter(e.sym);
			e = e.next;
		}
		return res;
	}

	/**
	 * Get the value of packageName.
	 *
	 * @return value of packageName.
	 */
	@Override
	public String getPackageName() {
		return packageName;
	}

	/**
	 * Set the value of packageName.
	 *
	 * @param v
	 *            Value to assign to packageName.
	 */
	@Override
	public void setPackageName(String v) {
		this.packageName = v;
		getCompilationEnv().sourcePackages.add(v);
	}

	@Override
	public void setAccessorClass(String accessorClass) {
		this.accessorClass = accessorClass;
	}

	@Override
	public String getAccessorClass() {
		return accessorClass;
	}

	public void setAmbiguousImports(Scope ambiguousImports) {
		this.ambiguousImports = ambiguousImports;
	}

	@Override
	public Scope getAmbiguousImports() {
		return ambiguousImports;
	}

	public void setBaseClass(Symbol baseClass) {
		this.baseClass = baseClass;
	}

	@Override
	public Symbol getBaseClass() {
		return baseClass;
	}

	@Override
	public void setClueSetFileName(String clueSetFileName) {
		this.clueSetFileName = clueSetFileName;
	}

	@Override
	public String getClueSetFileName() {
		return clueSetFileName;
	}

	public void setDecls(Tree[] decls) {
		this.decls = decls;
	}

	@Override
	public Tree[] getDecls() {
		return decls;
	}

	@Override
	public void setDoubleIndexBaseClass(Symbol doubleIndexBaseClass) {
		this.doubleIndexBaseClass = doubleIndexBaseClass;
	}

	@Override
	public Symbol getDoubleIndexBaseClass() {
		return doubleIndexBaseClass;
	}

	public void setCompilationEnv(CompilationEnv env) {
		this.env = env;
	}

	@Override
	public CompilationEnv getCompilationEnv() {
		return env;
	}

	public void setErrors(int errors) {
		this.errors = errors;
	}

	@Override
	public int getErrors() {
		return errors;
	}

	@Override
	public void setExistsBaseClass(Symbol existsBaseClass) {
		this.existsBaseClass = existsBaseClass;
	}

	@Override
	public Symbol getExistsBaseClass() {
		return existsBaseClass;
	}

	public void setGeneratedJavaSourceFiles(List generatedJavaSourceFiles) {
		this.generatedJavaSourceFiles = generatedJavaSourceFiles;
	}

	@Override
	public List getGeneratedJavaSourceFiles() {
		return generatedJavaSourceFiles;
	}

	@Override
	public void setIntern(boolean intern) {
		this.intern = intern;
	}

	@Override
	public boolean isIntern() {
		return intern;
	}

	public void setNamedImports(Scope namedImports) {
		this.namedImports = namedImports;
	}

	@Override
	public Scope getNamedImports() {
		return namedImports;
	}

	public void setPackage(PackageSymbol pckage) {
		this.pckage = pckage;
	}

	@Override
	public PackageSymbol getPackage() {
		return pckage;
	}

	@Override
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	@Override
	public String getSchemaName() {
		return schemaName;
	}

	public void setSource(Sourcecode source) {
		this.source = source;
	}

	@Override
	public Sourcecode getSource() {
		return source;
	}

	public void setStarImports(Scope starImports) {
		this.starImports = starImports;
	}

	@Override
	public Scope getStarImports() {
		return starImports;
	}

	@Override
	public void setTarget(Tree[] target) {
		this.target = target;
	}

	@Override
	public Tree[] getTarget() {
		return target;
	}

	public void setWarnings(int warnings) {
		this.warnings = warnings;
	}

	@Override
	public int getWarnings() {
		return warnings;
	}

	@Override
	public String toString() {
		return "CompilationUnit [source=" + source + ", errors=" + errors
				+ ", warnings=" + warnings + ", clueSetFileName="
				+ clueSetFileName + ", packageName=" + packageName
				+ ", schemaName=" + schemaName + "]";
	}

}
