/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.choicemaker.e2.mbd.plugin.impl;

import com.choicemaker.e2.mbd.runtime.IConfigurationElement;
import com.choicemaker.e2.mbd.runtime.IExtension;
import com.choicemaker.e2.mbd.runtime.IPluginDescriptor;
import com.choicemaker.e2.mbd.runtime.model.ConfigurationElementModel;
import com.choicemaker.e2.mbd.runtime.model.ExtensionModel;

public class Extension extends ExtensionModel implements IExtension {
	// this extension's elements data offset in the registry cache
	private int subElementsCacheOffset;
	// is this extension already fully loaded?
	private boolean fullyLoaded = true;

@Override
public IConfigurationElement[] getConfigurationElements() {
	ConfigurationElementModel[] list = getSubElements();
	if (list == null)
		return new IConfigurationElement[0];
	IConfigurationElement[] newValues = new IConfigurationElement[list.length];
	System.arraycopy(list, 0, newValues, 0, list.length);
	return newValues;
}
@Override
public IPluginDescriptor getDeclaringPluginDescriptor() {
	return (IPluginDescriptor) getParentPluginDescriptor();
}
@Override
public String getExtensionPointUniqueIdentifier() {
	return getExtensionPoint();
}
@Override
public String getLabel() {
	String s = getName();
	if (s == null)
		return ""; //$NON-NLS-1$
	String localized =  ((PluginDescriptor) getDeclaringPluginDescriptor()).getResourceString(s);
	if (localized != s)
		setLocalizedName(localized);
	return localized;
}
@Override
public String getSimpleIdentifier() {
	return getId();
}
@Override
public String getUniqueIdentifier() {
	String simple = getSimpleIdentifier();
	if (simple == null)
		return null;
	return getParentPluginDescriptor().getId() + "." + simple; //$NON-NLS-1$
}
@Override
public String toString() {
	return getParent().getPluginId() + "." + getSimpleIdentifier(); //$NON-NLS-1$
}
int getSubElementsCacheOffset() {
	return subElementsCacheOffset;
}
void setSubElementsCacheOffset(int subElementsCacheOffset) {
	this.subElementsCacheOffset = subElementsCacheOffset;
}
public boolean isFullyLoaded() {
	return fullyLoaded;
}
public void setFullyLoaded(boolean fullyLoaded) {
	this.fullyLoaded = fullyLoaded;
}
//public ConfigurationElementModel[] getSubElements() {
//	// synchronization is needed to avoid two threads trying to load the same 
//	// extension at the same time (see bug 36659) 
//	synchronized (this) {
//	// maybe it was lazily loaded
//	if (!fullyLoaded)
//		((PluginRegistry)this.getParent().getRegistry()).loadConfigurationElements(this);
//		// fullyLoaded should be true and elements available now
//	}
//	return super.getSubElements();
//}
/**
 * Overridden to relax read-only contraints and allow lazy loading of read-only
 * extensions.
 * @see com.choicemaker.e2.mbd.runtime.model.PluginModelObject#assertIsWriteable
 */
@Override
protected void assertIsWriteable() {
	if (fullyLoaded)
		super.assertIsWriteable();
}
}
