/*******************************************************************************
 * Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.cfg;

import java.util.HashMap;
import java.util.Map;

/**
 * Minimal implementation of the SymbolFactory interface.
 *
 * @author   Adam Winkel
 */
public class SimpleSymbolFactory implements SymbolFactory {

	/** 
	 * Mapping from variable names to VARIABLE objects.
	 */
	Map<String, Variable> variables = new HashMap<>();
		
	/**
	 * Returns true if this SymbolFactory has a variable with the specified
	 * display String.
	 */	
	@Override
	public boolean hasVariable(String display) {
		return variables.containsKey(display);
	}
	
	/**
	 * Fetches the variable specified by <code>display</code> from this
	 * SymbolFactory's list of variables.
	 * 
	 * @throws IllegalArgumentException if there is no variable for the
	 * specified display String.
	 */
	@Override
	public Variable getVariable(String display) {
		Variable v = variables.get(display);
		if (v == null) {
			throw new IllegalArgumentException("No variable for string: " + display);
		}
		return v;
	}
		
	/**
	 * Adds <code>variable</code> to this SymbolFactory's list
	 * of variables.
	 * 
	 * For any given application, the set of variables should be closed; once
	 * this SymbolFactory is initialized, the user/application shouldn't need 
	 * to add new Variables during runtime. 
	 */
	@Override
	public void addVariable(Variable v) {
		if (hasVariable(v.toString())) {
			throw new IllegalArgumentException("Variable " + v.toString() + 
				" already exists in this SymbolFactory");	
		}
		variables.put(v.toString(), v);
	}

}
