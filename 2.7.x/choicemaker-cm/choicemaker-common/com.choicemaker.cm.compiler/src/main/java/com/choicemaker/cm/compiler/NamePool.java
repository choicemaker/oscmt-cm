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
package com.choicemaker.cm.compiler;


public class NamePool {
	private int n = 0;
    
	public String newName() {
		return "$" + (n++);
	}
    
	public String newName(String prefix) {
		return prefix + (n++);
	}
}
