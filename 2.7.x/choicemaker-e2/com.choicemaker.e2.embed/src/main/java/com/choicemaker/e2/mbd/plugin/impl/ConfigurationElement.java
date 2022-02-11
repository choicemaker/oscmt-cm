/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.choicemaker.e2.mbd.plugin.impl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Logger;

import com.choicemaker.e2.mbd.runtime.CoreException;
import com.choicemaker.e2.mbd.runtime.IConfigurationElement;
import com.choicemaker.e2.mbd.runtime.IExtension;
import com.choicemaker.e2.mbd.runtime.IPluginDescriptor;
import com.choicemaker.e2.mbd.runtime.IStatus;
import com.choicemaker.e2.mbd.runtime.Platform;
import com.choicemaker.e2.mbd.runtime.Status;
import com.choicemaker.e2.mbd.runtime.impl.Policy;
import com.choicemaker.e2.mbd.runtime.model.ConfigurationElementModel;
import com.choicemaker.e2.mbd.runtime.model.ConfigurationPropertyModel;

public class ConfigurationElement extends ConfigurationElementModel implements IConfigurationElement {

	private static final Logger logger =
		Logger.getLogger(ConfigurationElementModel.class.getName());

  public ConfigurationElement()
  {
	super();
  }  
@Override
public Object createExecutableExtension(String attributeName) throws CoreException {
	String prop = null;
	String executable;
	String pluginName = null;
	String className = null;
	Object initData = null;
	int i;

	if (attributeName != null)
		prop = getAttribute(attributeName);
	else {
		// property not specified, try as element value
		prop = getValue();
		if (prop != null) {
			prop = prop.trim();
			if (prop.equals("")) //$NON-NLS-1$
				prop = null;
		}
	}

	if (prop == null) {
		// property not defined, try as a child element
		IConfigurationElement[] exec;
		IConfigurationElement[] parms;
		IConfigurationElement element;
		Hashtable<String,String> initParms;
		String pname;

		exec = getChildren(attributeName);
		if (exec.length != 0) {
			element = exec[0]; // assumes single definition
			pluginName = element.getAttribute("plugin"); //$NON-NLS-1$
			className = element.getAttribute("class"); //$NON-NLS-1$
			parms = element.getChildren("parameter"); //$NON-NLS-1$
			if (parms != null) {
				initParms = new Hashtable<>(parms.length + 1);
				for (i = 0; i < parms.length; i++) {
					pname = parms[i].getAttribute("name"); //$NON-NLS-1$
					if (pname != null)
						initParms.put(pname, parms[i].getAttribute("value")); //$NON-NLS-1$
				}
				if (!initParms.isEmpty())
					initData = initParms;
			}
		}

		// specified name is not a simple attribute nor child element
		else {
			String message = Policy.bind("plugin.extDefNotFound", attributeName); //$NON-NLS-1$
			IStatus status = new Status(IStatus.ERROR, Platform.PI_RUNTIME, Platform.PLUGIN_ERROR, message, null);
			logError(status);
			throw new CoreException(status);
		}
	} else {
		// simple property or element value, parse it into its components
		i = prop.indexOf(':');
		if (i != -1) {
			executable = prop.substring(0, i).trim();
			initData = prop.substring(i + 1).trim();
		} else
			executable = prop;

		i = executable.indexOf('/');
		if (i != -1) {
			pluginName = executable.substring(0, i).trim();
			className = executable.substring(i + 1).trim();
		} else
			className = executable;
	}

	if (className == null || className.equals("")) { //$NON-NLS-1$
		String message = Policy.bind("plugin.extDefNoClass", attributeName ); //$NON-NLS-1$
		IStatus status = new Status(IStatus.ERROR, Platform.PI_RUNTIME, Platform.PLUGIN_ERROR, message, null);
		logError(status);
		throw new CoreException(status);
	}

	IPluginDescriptor plugin = getDeclaringExtension().getDeclaringPluginDescriptor();
	return ((PluginDescriptor) plugin).createExecutableExtension(pluginName, className, initData, this, attributeName);
}
@Override
public String getAttribute(String name) {
	ConfigurationPropertyModel[] list = getProperties();
	if (list == null)
		return null;
		
	ConfigurationPropertyModel found = null;	
	for (int i = 0; i < list.length; i++)
		if (name.equals(list[i].getName())) {
			found = list[i];
			break;
		}
	String s;
	if (found == null || (s = found.getValue()) == null)
		return null;
	//replace the key with its localized value
	String localized = getDeclaringExtension().getDeclaringPluginDescriptor().getResourceString(s);
	if (localized != s)
		found.setLocalizedValue(localized);
	return localized;
}
@Override
public String getAttributeAsIs(String name) {
	ConfigurationPropertyModel[] list = getProperties();
	if (list == null)
		return null;
	for (int i = 0; i < list.length; i++)
		if (name.equals(list[i].getName()))
			return list[i].getValue();
	return null;
}
@Override
public String[] getAttributeNames() {
	ConfigurationPropertyModel[] list = getProperties();
	if (list == null)
		return new String[0];
	String[] result = new String[list.length];
	for (int i = 0; i < list.length; i++)
		result[i] = list[i].getName();
	return result;
}
@Override
public IConfigurationElement[] getChildren() {
	ConfigurationElementModel[] list = getSubElements();
	if (list == null)
		return new IConfigurationElement[0];
	IConfigurationElement[] newValues = new IConfigurationElement[list.length];
	System.arraycopy(list, 0, newValues, 0, list.length);
	return newValues;
}
@Override
public IConfigurationElement[] getChildren(String name) {
	ConfigurationElementModel[] list = getSubElements();
	if (list == null)
		return new IConfigurationElement[0];
	ArrayList<ConfigurationElementModel> children = new ArrayList<>();
	for (int i = 0; i < list.length; i++) {
		ConfigurationElementModel	element = list[i];
		if (name.equals(element.getName()))
			children.add(list[i]);
	}
	return children.toArray(new IConfigurationElement[children.size()]);
}
@Override
public IExtension getDeclaringExtension() {
	return (IExtension) getParentExtension();
}
@Override
public String getValue() {
	String s = getValueAsIs();
	if (s == null)
		return null;
	String localized = getDeclaringExtension().getDeclaringPluginDescriptor().getResourceString(s);
	if (localized != s)
		setLocalizedValue(localized);
	return localized;
}
private void logError(IStatus status) {
	logger.severe(status.getMessage());
}
}
