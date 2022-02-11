/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.e2.std;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IPluginRegistry;
import org.eclipse.core.runtime.PluginVersionIdentifier;

import com.choicemaker.e2.CMConfigurationElement;
import com.choicemaker.e2.CMExtension;
import com.choicemaker.e2.CMExtensionPoint;
import com.choicemaker.e2.CMPluginDescriptor;
import com.choicemaker.e2.CMPluginRegistry;

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

		@Override
		public CMConfigurationElement[] getConfigurationElementsFor(
				String extensionPointId) {
			return ConfigurationElementAdapter.convert(delegate.getConfigurationElementsFor(extensionPointId));
		}

		@Override
		public CMConfigurationElement[] getConfigurationElementsFor(
				String pluginId, String extensionPointName) {
			return ConfigurationElementAdapter.convert(delegate.getConfigurationElementsFor(pluginId,
					extensionPointName));
		}

		@Override
		public CMConfigurationElement[] getConfigurationElementsFor(
				String pluginId, String extensionPointName, String extensionId) {
			return ConfigurationElementAdapter.convert(delegate.getConfigurationElementsFor(pluginId,
					extensionPointName, extensionId));
		}

		@Override
		public CMExtension getExtension(String extensionPointId,
				String extensionId) {
			return ExtensionAdapter.convert(delegate.getExtension(extensionPointId, extensionId));
		}

		@Override
		public CMExtension getExtension(String pluginId,
				String extensionPointName, String extensionId) {
			return ExtensionAdapter.convert(delegate.getExtension(pluginId, extensionPointName,
					extensionId));
		}

		@Override
		public CMExtensionPoint getExtensionPoint(String extensionPointId) {
			return ExtensionPointAdapter.convert(delegate.getExtensionPoint(extensionPointId));
		}

		@Override
		public CMExtensionPoint getExtensionPoint(String pluginId,
				String extensionPointName) {
			return ExtensionPointAdapter.convert(delegate.getExtensionPoint(pluginId, extensionPointName));
		}

		@Override
		public CMExtensionPoint[] getExtensionPoints() {
			return ExtensionPointAdapter.convert(delegate.getExtensionPoints());
		}

		@Override
		public CMPluginDescriptor getPluginDescriptor(String pluginId) {
			return PluginDescriptorAdapter.convert(delegate.getPluginDescriptor(pluginId));
		}

		public CMPluginDescriptor getPluginDescriptor(String pluginId,
				PluginVersionIdentifier version) {
			return PluginDescriptorAdapter.convert(delegate.getPluginDescriptor(pluginId, version));
		}

		@Override
		public CMPluginDescriptor[] getPluginDescriptors() {
			return PluginDescriptorAdapter.convert(delegate.getPluginDescriptors());
		}

		@Override
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

		@Override
		public IConfigurationElement[] getConfigurationElementsFor(
				String extensionPointId) {
			return ConfigurationElementAdapter.convert(delegate.getConfigurationElementsFor(extensionPointId));
		}

		@Override
		public IConfigurationElement[] getConfigurationElementsFor(
				String pluginId, String extensionPointName) {
			return ConfigurationElementAdapter.convert(delegate.getConfigurationElementsFor(pluginId,
					extensionPointName));
		}

		@Override
		public IConfigurationElement[] getConfigurationElementsFor(
				String pluginId, String extensionPointName, String extensionId) {
			return ConfigurationElementAdapter.convert(delegate.getConfigurationElementsFor(pluginId,
					extensionPointName, extensionId));
		}

		@Override
		public IExtension getExtension(String extensionPointId,
				String extensionId) {
			return ExtensionAdapter.convert(delegate.getExtension(extensionPointId, extensionId));
		}

		@Override
		public IExtension getExtension(String pluginId,
				String extensionPointName, String extensionId) {
			return ExtensionAdapter.convert(delegate.getExtension(pluginId, extensionPointName,
					extensionId));
		}

		@Override
		public IExtensionPoint getExtensionPoint(String extensionPointId) {
			return ExtensionPointAdapter.convert(delegate.getExtensionPoint(extensionPointId));
		}

		@Override
		public IExtensionPoint getExtensionPoint(String pluginId,
				String extensionPointName) {
			return ExtensionPointAdapter.convert(delegate.getExtensionPoint(pluginId, extensionPointName));
		}

		@Override
		public IExtensionPoint[] getExtensionPoints() {
			return ExtensionPointAdapter.convert(delegate.getExtensionPoints());
		}

		@Override
		public IPluginDescriptor getPluginDescriptor(String pluginId) {
			return PluginDescriptorAdapter.convert(delegate.getPluginDescriptor(pluginId));
		}

		@Override
		public IPluginDescriptor getPluginDescriptor(String pluginId,
				PluginVersionIdentifier version) {
			throw new Error("not implemented");
		}

		@Override
		public IPluginDescriptor[] getPluginDescriptors() {
			return PluginDescriptorAdapter.convert(delegate.getPluginDescriptors());
		}

		@Override
		public IPluginDescriptor[] getPluginDescriptors(String pluginId) {
			return PluginDescriptorAdapter.convert(delegate.getPluginDescriptors(pluginId));
		}

	}

}
