/*
 * Copyright (c) 2001, 2015 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.compiler.backend;

import com.choicemaker.cm.compiler.ICompilationUnit;
import com.choicemaker.cm.core.compiler.CompilerException;

/**
 * Printing of Java source code.
 * @author rphall
 */
public interface ITargetPrinter {
	public abstract void printUnit(ICompilationUnit unit) throws CompilerException;
}
