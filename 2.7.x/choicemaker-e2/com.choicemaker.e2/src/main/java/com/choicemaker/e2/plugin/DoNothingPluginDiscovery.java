/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.e2.plugin;

import java.net.URL;
import java.util.Collections;
import java.util.Set;

import com.choicemaker.e2.PluginDiscovery;

public class DoNothingPluginDiscovery implements PluginDiscovery {

	@Override
	public Set<URL> getPluginUrls() {
		return Collections.emptySet();
	}

}
