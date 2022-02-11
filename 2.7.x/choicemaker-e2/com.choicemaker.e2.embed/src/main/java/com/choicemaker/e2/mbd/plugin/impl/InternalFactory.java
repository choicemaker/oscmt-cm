/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.choicemaker.e2.mbd.plugin.impl;

import com.choicemaker.e2.mbd.runtime.MultiStatus;
import com.choicemaker.e2.mbd.runtime.model.ConfigurationElementModel;
import com.choicemaker.e2.mbd.runtime.model.ConfigurationPropertyModel;
import com.choicemaker.e2.mbd.runtime.model.ExtensionModel;
import com.choicemaker.e2.mbd.runtime.model.ExtensionPointModel;
import com.choicemaker.e2.mbd.runtime.model.Factory;
import com.choicemaker.e2.mbd.runtime.model.LibraryModel;
import com.choicemaker.e2.mbd.runtime.model.PluginDescriptorModel;
import com.choicemaker.e2.mbd.runtime.model.PluginFragmentModel;
import com.choicemaker.e2.mbd.runtime.model.PluginPrerequisiteModel;
import com.choicemaker.e2.mbd.runtime.model.PluginRegistryModel;

public class InternalFactory extends Factory {
public InternalFactory(MultiStatus status) {
	super(status);
}
@Override
public ConfigurationElementModel createConfigurationElement() {
	return new ConfigurationElement();
}
@Override
public ConfigurationPropertyModel createConfigurationProperty() {
	return new ConfigurationProperty();
}
@Override
public ExtensionModel createExtension() {
	return new Extension();
}
@Override
public ExtensionPointModel createExtensionPoint() {
	return new ExtensionPoint();
}



@Override
public LibraryModel createLibrary() {
	return new Library();
}
@Override
public PluginDescriptorModel createPluginDescriptor() {
	return new PluginDescriptor();
}

@Override
public PluginFragmentModel createPluginFragment() {
	return new FragmentDescriptor();
}

@Override
public PluginPrerequisiteModel createPluginPrerequisite() {
	return new PluginPrerequisite();
}
@Override
public PluginRegistryModel createPluginRegistry() {
	return new PluginRegistry();
}
}
