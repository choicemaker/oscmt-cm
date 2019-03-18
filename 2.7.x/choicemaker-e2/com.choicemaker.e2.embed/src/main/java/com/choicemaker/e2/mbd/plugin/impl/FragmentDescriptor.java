/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.choicemaker.e2.mbd.plugin.impl;

import java.net.URL;

import com.choicemaker.e2.mbd.runtime.model.PluginFragmentModel;

public class FragmentDescriptor extends PluginFragmentModel {

	// constants
//	static final String FRAGMENT_URL = PlatformURLHandler.PROTOCOL + PlatformURLHandler.PROTOCOL_SEPARATOR + "/" + PlatformURLFragmentConnection.FRAGMENT + "/"; //$NON-NLS-1$ //$NON-NLS-2$

@Override
public String toString() {
	return getId() + PluginDescriptor.VERSION_SEPARATOR + getVersion();
}
public URL getInstallURL() {
//	try {
		return null; // PlatformURLFactory.createURL(FRAGMENT_URL + toString() + "/"); 
//	} catch (MalformedURLException e) {
//		throw new IllegalStateException(); // unchecked
//	}
}
}
