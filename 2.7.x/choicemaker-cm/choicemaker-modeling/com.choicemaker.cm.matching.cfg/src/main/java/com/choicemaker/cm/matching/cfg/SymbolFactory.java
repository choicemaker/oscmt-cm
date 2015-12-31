/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.cfg;

/**
 * Utility for storing Variables and TokenTypes.
 *
 * @author   Adam Winkel
 */
public interface SymbolFactory {

	/**
	 * Returns true iff this SymbolFactory has a Variable named
	 * <code>name</code>.
	 */
	public abstract boolean hasVariable(String name);

	/**
	 * Returns the Variable with the specified name.
	 * 
	 * @throws IllegalArgumentException if this SymbolFactory
	 * doesn't contain a Variable with the specified name.
	 */
	public abstract Variable getVariable(String name);
	
	/**
	 * Adds the specified Variable to this SymbolFactory.
	 * 
	 * @throws IllegalArgumentException if this SymbolFactory
	 * already contains a Variable with the same name.
	 */
	public abstract void addVariable(Variable v);
	
}
