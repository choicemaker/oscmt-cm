/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.core.internal.plugins;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.model.*;

public class ExtensionPoint extends ExtensionPointModel implements IExtensionPoint {
  public ExtensionPoint()
  {
	super();
  }  
public IConfigurationElement[] getConfigurationElements() {
	ExtensionModel[] list = getDeclaredExtensions();
	if (list == null)
		return new IConfigurationElement[0];
	ArrayList result = new ArrayList();
	for (int i = 0; i < list.length; i++) {
		ConfigurationElementModel[] configs = list[i].getSubElements();
		if (configs != null)
			for (int j = 0; j < configs.length; j++)
				result.add(configs[j]);
	}
	return (IConfigurationElement[]) result.toArray(new IConfigurationElement[result.size()]);
}
public IPluginDescriptor getDeclaringPluginDescriptor() {
	return (IPluginDescriptor) getParentPluginDescriptor();
}
public IExtension getExtension(String id) {
	if (id == null)
		return null;
	ExtensionModel[] list = getDeclaredExtensions();
	if (list == null)
		return null;
	for (int i = 0; i < list.length; i++) {
		if (id.equals(((Extension) list[i]).getUniqueIdentifier()))
			return (IExtension) list[i];
	}
	return null;
}
public IExtension[] getExtensions() {
	ExtensionModel[] list = getDeclaredExtensions();
	if (list == null)
		return new IExtension[0];
	IExtension[] newValues = new IExtension[list.length];
	System.arraycopy(list, 0, newValues, 0, list.length);
	return newValues;
}
public String getLabel() {
	String s = getName();
	if (s == null)
		return "";//$NON-NLS-1$
	String localized = ((PluginDescriptor) getDeclaringPluginDescriptor()).getResourceString(s);
	if (localized != s)
		setLocalizedName(localized);
	return localized;
}
public java.lang.String getSchemaReference() {
	String s = getSchema();
	return s == null ? "" : s.replace(File.separatorChar, '/'); //$NON-NLS-1$
}
public String getSimpleIdentifier() {
	return getId();
}
public String getUniqueIdentifier() {
	return getParentPluginDescriptor().getId() + "." + getSimpleIdentifier(); //$NON-NLS-1$
}
public String toString() {
	return getParent().getPluginId() + "." + getSimpleIdentifier(); //$NON-NLS-1$
}
}
