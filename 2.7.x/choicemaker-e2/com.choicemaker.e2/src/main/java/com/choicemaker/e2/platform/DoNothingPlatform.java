/*******************************************************************************
 * Copyright (c) 2015, 2016 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.e2.platform;

import java.net.URL;

import com.choicemaker.e2.CMPlatform;
import com.choicemaker.e2.CMPlatformRunnable;
import com.choicemaker.e2.CMPluginRegistry;

public final class DoNothingPlatform implements CMPlatform {

	@Override
	public CMPluginRegistry getPluginRegistry() {
		return DoNothingRegistry.getInstance();
	}

	@Override
	public CMPlatformRunnable loaderGetRunnable(String applicationName) {
		return null;
	}

	@Override
	public String getPluginDirectory(String id, String version) {
		return null;
	}

	@Override
	public URL getPluginDescriptorUrl(String id, String version,
			String descriptorFile) {
		return null;
	}

}
