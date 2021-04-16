/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.e2.std;

import java.net.URL;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.ILibrary;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IPluginPrerequisite;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.PluginVersionIdentifier;

import com.choicemaker.e2.CMExtension;
import com.choicemaker.e2.CMExtensionPoint;
import com.choicemaker.e2.CMLibrary;
import com.choicemaker.e2.CMPath;
import com.choicemaker.e2.CMPlugin;
import com.choicemaker.e2.CMPluginDescriptor;
import com.choicemaker.e2.CMPluginPrerequisite;
import com.choicemaker.e2.CMPluginVersionIdentifier;
import com.choicemaker.e2.E2Exception;

public class PluginDescriptorAdapter {

	public static IPluginDescriptor convert(CMPluginDescriptor o) {
		IPluginDescriptor retVal = null;
		if (o != null) {
			retVal = new CMtoStd(o);
		}
		return retVal;
	}

	public static IPluginDescriptor[] convert(CMPluginDescriptor[] o) {
		IPluginDescriptor[] retVal = null;
		if (o != null) {
			retVal = new IPluginDescriptor[o.length];
			for (int i=0; i<o.length; i++) {
				retVal[i] = convert(o[i]);
			}
		}
		return retVal;
	}
	
	public static CMPluginDescriptor convert(IPluginDescriptor o) {
		CMPluginDescriptor retVal = null;
		if (o != null) {
			retVal = new StdToCM(o);
		}
		return retVal;
	}

	public static CMPluginDescriptor[] convert(IPluginDescriptor[] o) {
		CMPluginDescriptor[] retVal = null;
		if (o != null) {
			retVal = new CMPluginDescriptor[o.length];
			for (int i=0; i<o.length; i++) {
					retVal[i] = convert(o[i]);
			}
		}
		return retVal;
	}
	
	protected static class StdToCM implements CMPluginDescriptor {
		
		private final IPluginDescriptor delegate;

		public StdToCM(IPluginDescriptor o) {
			if (o == null) {
				throw new IllegalArgumentException("null delegate");
			}
			this.delegate = o;
		}

		@Override
		public CMExtension getExtension(String extensionName) {
			return ExtensionAdapter.convert(delegate.getExtension(extensionName));
		}

		@Override
		public CMExtensionPoint getExtensionPoint(String extensionPointId) {
			return ExtensionPointAdapter.convert(delegate.getExtensionPoint(extensionPointId));
		}

		@Override
		public CMExtensionPoint[] getExtensionPoints() {
			return ExtensionPointAdapter.convert(delegate.getExtensionPoints());
		}

		@Override
		public CMExtension[] getExtensions() {
			return ExtensionAdapter.convert(delegate.getExtensions());
		}

		@Override
		public URL getInstallURL() {
			return delegate.getInstallURL();
		}

		@Override
		public String getLabel() {
			return delegate.getLabel();
		}

		@Override
		public CMPlugin getPlugin() throws E2Exception {
			try {
				return PluginAdapter.convert(delegate.getPlugin());
			} catch (CoreException e) {
				E2Exception ce = CoreExceptionAdapter.convert(e);
				throw ce;
			}
		}

		@Override
		public ClassLoader getPluginClassLoader() {
			return delegate.getPluginClassLoader();
		}

		@Override
		public CMPluginPrerequisite[] getPluginPrerequisites() {
			return PluginPrerequisiteAdapter.convert(delegate.getPluginPrerequisites());
		}

		@Override
		public String getProviderName() {
			return delegate.getProviderName();
		}

		@Override
		public ResourceBundle getResourceBundle()
				throws MissingResourceException {
			return delegate.getResourceBundle();
		}

		@Override
		public String getResourceString(String value) {
			return delegate.getResourceString(value);
		}

		@Override
		public String getResourceString(String value, ResourceBundle bundle) {
			return delegate.getResourceString(value, bundle);
		}

		public CMLibrary[] getRuntimeLibraries() {
			return LibraryAdapter.convert(delegate.getRuntimeLibraries());
		}

		@Override
		public String getUniqueIdentifier() {
			return delegate.getUniqueIdentifier();
		}

		@Override
		public CMPluginVersionIdentifier getVersionIdentifier() {
			return PluginVersionIdentifierAdapter.convert(delegate.getVersionIdentifier());
		}

		@Override
		public boolean isPluginActivated() {
			return delegate.isPluginActivated();
		}

		public URL find(CMPath path) {
			return delegate.find(PathAdapter.convert(path));
		}

		public URL find(CMPath path, @SuppressWarnings("rawtypes") Map override) {
			return delegate.find(PathAdapter.convert(path), override);
		}

	}

	protected static class CMtoStd implements IPluginDescriptor {
		
		private final CMPluginDescriptor delegate;

		public CMtoStd(CMPluginDescriptor o) {
			if (o == null) {
				throw new IllegalArgumentException("null delegate");
			}
			this.delegate = o;
		}

		@Override
		public IExtension getExtension(String extensionName) {
			return ExtensionAdapter.convert(delegate.getExtension(extensionName));
		}

		@Override
		public IExtensionPoint getExtensionPoint(String extensionPointId) {
			return ExtensionPointAdapter.convert(delegate.getExtensionPoint(extensionPointId));
		}

		@Override
		public IExtensionPoint[] getExtensionPoints() {
			return ExtensionPointAdapter.convert(delegate.getExtensionPoints());
		}

		@Override
		public IExtension[] getExtensions() {
			return ExtensionAdapter.convert(delegate.getExtensions());
		}

		@Override
		public URL getInstallURL() {
			return delegate.getInstallURL();
		}

		@Override
		public String getLabel() {
			return delegate.getLabel();
		}

		@Override
		public Plugin getPlugin() throws CoreException {
			try {
				return PluginAdapter.convert(delegate.getPlugin());
			} catch (E2Exception e) {
				CoreException ce = CoreExceptionAdapter.convert(e);
				throw ce;
			}
		}

		@Override
		public ClassLoader getPluginClassLoader() {
			return delegate.getPluginClassLoader();
		}

		@Override
		public IPluginPrerequisite[] getPluginPrerequisites() {
			return PluginPrerequisiteAdapter.convert(delegate.getPluginPrerequisites());
		}

		@Override
		public String getProviderName() {
			return delegate.getProviderName();
		}

		@Override
		public ResourceBundle getResourceBundle()
				throws MissingResourceException {
			return delegate.getResourceBundle();
		}

		@Override
		public String getResourceString(String value) {
			return delegate.getResourceString(value);
		}

		@Override
		public String getResourceString(String value, ResourceBundle bundle) {
			return delegate.getResourceString(value, bundle);
		}

		@Override
		public ILibrary[] getRuntimeLibraries() {
			throw new Error("not implemented");
		}

		@Override
		public String getUniqueIdentifier() {
			return delegate.getUniqueIdentifier();
		}

		@Override
		public PluginVersionIdentifier getVersionIdentifier() {
			return PluginVersionIdentifierAdapter.convert(delegate.getVersionIdentifier());
		}

		@Override
		public boolean isPluginActivated() {
			return delegate.isPluginActivated();
		}

		@Override
		public URL find(IPath path) {
			throw new Error("not implemented");
		}

		@Override
		public URL find(IPath path, @SuppressWarnings("rawtypes") Map override) {
			throw new Error("not implemented");
		}

	}

}
