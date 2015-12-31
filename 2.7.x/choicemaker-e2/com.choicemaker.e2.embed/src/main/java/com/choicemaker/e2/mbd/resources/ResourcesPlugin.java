/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.choicemaker.e2.mbd.resources;

import com.choicemaker.e2.mbd.runtime.IPluginDescriptor;
import com.choicemaker.e2.mbd.runtime.Plugin;

/**
 * Comment
 *
 * @author   Martin Buechi
 */
public class ResourcesPlugin extends Plugin {
	public ResourcesPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
	}
	public static IWorkspace getWorkspace() {
		throw new UnsupportedOperationException("Workspace not supported in single-jar runtime.");
	}
}
