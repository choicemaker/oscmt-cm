/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.choicemaker.e2.mbd.plugin.impl;

import com.choicemaker.e2.mbd.runtime.ILibrary;
import com.choicemaker.e2.mbd.runtime.IPath;
import com.choicemaker.e2.mbd.runtime.Path;
import com.choicemaker.e2.mbd.runtime.model.LibraryModel;

public class Library extends LibraryModel implements ILibrary {
  public Library()
  {
	super();
  }  
@Override
public String[] getContentFilters() {
	if (!isExported() || isFullyExported())
		return null;
	return getExports();
}
@Override
public IPath getPath() {
	return new Path(getName());
}
}
