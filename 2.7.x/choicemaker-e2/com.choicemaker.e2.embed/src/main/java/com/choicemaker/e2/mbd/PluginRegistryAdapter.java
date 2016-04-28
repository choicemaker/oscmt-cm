/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.e2.mbd;

import com.choicemaker.e2.CMConfigurationElement;
import com.choicemaker.e2.CMExtension;
import com.choicemaker.e2.CMExtensionPoint;
import com.choicemaker.e2.CMPluginDescriptor;
import com.choicemaker.e2.CMPluginRegistry;
import com.choicemaker.e2.mbd.runtime.IConfigurationElement;
import com.choicemaker.e2.mbd.runtime.IExtension;
import com.choicemaker.e2.mbd.runtime.IExtensionPoint;
import com.choicemaker.e2.mbd.runtime.IPluginDescriptor;
import com.choicemaker.e2.mbd.runtime.IPluginRegistry;
import com.choicemaker.e2.mbd.runtime.PluginVersionIdentifier;

public class PluginRegistryAdapter {
	
	public static IPluginRegistry convert(CMPluginRegistry cmce) {
		IPluginRegistry retVal = null;
		if (cmce != null) {
			retVal = new CMtoStd(cmce);
		}
		return retVal;
	}

	public static IPluginRegistry[] convert(CMPluginRegistry[] cmce) {
		IPluginRegistry[] retVal = null;
		if (cmce != null) {
			retVal = new IPluginRegistry[cmce.length];
			for (int i = 0; i < cmce.length; i++) {
				retVal[i] = convert(cmce[i]);
			}
		}
		return retVal;
	}

	public static CMPluginRegistry convert(IPluginRegistry ice) {
		CMPluginRegistry retVal = null;
		if (ice != null) {
			retVal = new StdToCM(ice);
		}
		return retVal;
	}

	public static CMPluginRegistry[] convert(IPluginRegistry[] ice) {
		CMPluginRegistry[] retVal = null;
		if (ice != null) {
			retVal = new CMPluginRegistry[ice.length];
			for (int i = 0; i < ice.length; i++) {
				retVal[i] = convert(ice[i]);
			}
		}
		return retVal;
	}

	protected static class StdToCM implements CMPluginRegistry {
		
		private final IPluginRegistry delegate;

		public StdToCM(IPluginRegistry o) {
			if (o == null) {
				throw new IllegalArgumentException("null delegate");
			}
			this.delegate = o;
		}

		public CMConfigurationElement[] getConfigurationElementsFor(
				String extensionPointId) {
			return ConfigurationElementAdapter.convert(delegate.getConfigurationElementsFor(extensionPointId));
		}

		public CMConfigurationElement[] getConfigurationElementsFor(
				String pluginId, String extensionPointName) {
			return ConfigurationElementAdapter.convert(delegate.getConfigurationElementsFor(pluginId,
					extensionPointName));
		}

		public CMConfigurationElement[] getConfigurationElementsFor(
				String pluginId, String extensionPointName, String extensionId) {
			return ConfigurationElementAdapter.convert(delegate.getConfigurationElementsFor(pluginId,
					extensionPointName, extensionId));
		}

		public CMExtension getExtension(String extensionPointId,
				String extensionId) {
			return ExtensionAdapter.convert(delegate.getExtension(extensionPointId, extensionId));
		}

		public CMExtension getExtension(String pluginId,
				String extensionPointName, String extensionId) {
			return ExtensionAdapter.convert(delegate.getExtension(pluginId, extensionPointName,
					extensionId));
		}

		public CMExtensionPoint getExtensionPoint(String extensionPointId) {
			return ExtensionPointAdapter.convert(delegate.getExtensionPoint(extensionPointId));
		}

		public CMExtensionPoint getExtensionPoint(String pluginId,
				String extensionPointName) {
			return ExtensionPointAdapter.convert(delegate.getExtensionPoint(pluginId, extensionPointName));
		}

		public CMExtensionPoint[] getExtensionPoints() {
			return ExtensionPointAdapter.convert(delegate.getExtensionPoints());
		}

		public CMPluginDescriptor getPluginDescriptor(String pluginId) {
			return PluginDescriptorAdapter.convert(delegate.getPluginDescriptor(pluginId));
		}

		public CMPluginDescriptor getPluginDescriptor(String pluginId,
				PluginVersionIdentifier version) {
			throw new Error("not implemented");
		}

		public CMPluginDescriptor[] getPluginDescriptors() {
			return PluginDescriptorAdapter.convert(delegate.getPluginDescriptors());
		}

		public CMPluginDescriptor[] getPluginDescriptors(String pluginId) {
			return PluginDescriptorAdapter.convert(delegate.getPluginDescriptors(pluginId));
		}

	}

	protected static class CMtoStd implements IPluginRegistry {
		
		private final CMPluginRegistry delegate;

		public CMtoStd(CMPluginRegistry o) {
			if (o == null) {
				throw new IllegalArgumentException("null delegate");
			}
			this.delegate = o;
		}

		public IConfigurationElement[] getConfigurationElementsFor(
				String extensionPointId) {
			return ConfigurationElementAdapter.convert(delegate.getConfigurationElementsFor(extensionPointId));
		}

		public IConfigurationElement[] getConfigurationElementsFor(
				String pluginId, String extensionPointName) {
			return ConfigurationElementAdapter.convert(delegate.getConfigurationElementsFor(pluginId,
					extensionPointName));
		}

		public IConfigurationElement[] getConfigurationElementsFor(
				String pluginId, String extensionPointName, String extensionId) {
			return ConfigurationElementAdapter.convert(delegate.getConfigurationElementsFor(pluginId,
					extensionPointName, extensionId));
		}

		public IExtension getExtension(String extensionPointId,
				String extensionId) {
			return ExtensionAdapter.convert(delegate.getExtension(extensionPointId, extensionId));
		}

		public IExtension getExtension(String pluginId,
				String extensionPointName, String extensionId) {
			return ExtensionAdapter.convert(delegate.getExtension(pluginId, extensionPointName,
					extensionId));
		}

		public IExtensionPoint getExtensionPoint(String extensionPointId) {
			return ExtensionPointAdapter.convert(delegate.getExtensionPoint(extensionPointId));
		}

		public IExtensionPoint getExtensionPoint(String pluginId,
				String extensionPointName) {
			return ExtensionPointAdapter.convert(delegate.getExtensionPoint(pluginId, extensionPointName));
		}

		public IExtensionPoint[] getExtensionPoints() {
			return ExtensionPointAdapter.convert(delegate.getExtensionPoints());
		}

		public IPluginDescriptor getPluginDescriptor(String pluginId) {
			return PluginDescriptorAdapter.convert(delegate.getPluginDescriptor(pluginId));
		}

		public IPluginDescriptor getPluginDescriptor(String pluginId,
				PluginVersionIdentifier version) {
			throw new Error("not implemented");
		}

		public IPluginDescriptor[] getPluginDescriptors() {
			return PluginDescriptorAdapter.convert(delegate.getPluginDescriptors());
		}

		public IPluginDescriptor[] getPluginDescriptors(String pluginId) {
			return PluginDescriptorAdapter.convert(delegate.getPluginDescriptors(pluginId));
		}

	}

}
