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
package com.choicemaker.cm.compiler.gen.ant;

public class Project extends org.apache.tools.ant.Project {
	public void fireBuildStarted() {
		super.fireBuildStarted();
	}

	public void fireBuildFinished(Throwable exception) {
		super.fireBuildFinished(exception);
	}
}