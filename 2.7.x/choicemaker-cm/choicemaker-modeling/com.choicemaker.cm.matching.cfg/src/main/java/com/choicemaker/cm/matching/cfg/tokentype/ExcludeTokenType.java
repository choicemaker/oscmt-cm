/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.matching.cfg.tokentype;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author    Adam Winkel
 */
public class ExcludeTokenType extends WordTokenType {

	protected Set<String> exclude;

	public ExcludeTokenType(String name) {
		this(name, null);
	}

	public ExcludeTokenType(String name, Set<String> exclude) {
		super(name);
		setExcludes(exclude);
	}

	public void setExcludes(Set<String> e) {
		if (e == null || e.size() == 0) {
			this.exclude = null;
		} else {
			this.exclude = new HashSet<>(e);
		}
	}
	
	public void addExcludes(Set<String> e) {
		if (e == null || e.size() == 0) {
			return;
		} 
		
		if (this.exclude == null) {
			this.exclude = new HashSet<>(e);
		} else {
			this.exclude.addAll(e);
		}
	}
	
	@Override
	public boolean canHaveToken(String token) {
		if (exclude != null) {
			return super.canHaveToken(token) && !exclude.contains(token);
		}
		
		return true;
	}
	
}
