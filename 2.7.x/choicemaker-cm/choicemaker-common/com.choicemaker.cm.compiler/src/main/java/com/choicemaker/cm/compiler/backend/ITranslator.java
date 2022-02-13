/*
 * Copyright (c) 2001, 2014 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.compiler.backend;

import com.choicemaker.cm.core.compiler.CompilerException;

/**
 * Interface of main translator class.
 * @author rphall
 */
public interface ITranslator {
	public abstract void translate() throws CompilerException;
	public abstract void toJava() throws CompilerException;
}
