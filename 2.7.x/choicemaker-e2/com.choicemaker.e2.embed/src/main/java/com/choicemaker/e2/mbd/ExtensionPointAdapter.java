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
import com.choicemaker.e2.mbd.runtime.IConfigurationElement;
import com.choicemaker.e2.mbd.runtime.IExtension;
import com.choicemaker.e2.mbd.runtime.IExtensionPoint;
import com.choicemaker.e2.mbd.runtime.IPluginDescriptor;

public class ExtensionPointAdapter {

	public static IExtensionPoint convert(CMExtensionPoint o) {
		IExtensionPoint retVal = null;
		if (o != null) {
			retVal = new CMtoStd(o);
		}
		return retVal;
	}

	public static IExtensionPoint[] convert(CMExtensionPoint[] o) {
		IExtensionPoint[] retVal = null;
		if (o != null) {
			retVal = new IExtensionPoint[o.length];
			for (int i=0; i<o.length; i++) {
				retVal[i] = convert(o[i]);
			}
		}
		return retVal;
	}
	
	public static CMExtensionPoint convert(IExtensionPoint o) {
		CMExtensionPoint retVal = null;
		if (o != null) {
			retVal = new StdToCM(o);
		}
		return retVal;
	}

	public static CMExtensionPoint[] convert(IExtensionPoint[] o) {
		CMExtensionPoint[] retVal = null;
		if (o != null) {
			retVal = new CMExtensionPoint[o.length];
			for (int i=0; i<o.length; i++) {
					retVal[i] = convert(o[i]);
			}
		}
		return retVal;
	}
	
	protected static class StdToCM implements CMExtensionPoint {
		
		private final IExtensionPoint delegate;

		public StdToCM(IExtensionPoint o) {
			if (o == null) {
				throw new IllegalArgumentException("null delegate");
			}
			this.delegate = o;
		}

		@Override
		public CMConfigurationElement[] getConfigurationElements() {
			return ConfigurationElementAdapter.convert(delegate.getConfigurationElements());
		}

		@Override
		public CMPluginDescriptor getDeclaringPluginDescriptor() {
			return PluginDescriptorAdapter.convert(delegate.getDeclaringPluginDescriptor());
		}

		@Override
		public CMExtension getExtension(String extensionId) {
			return ExtensionAdapter.convert(delegate.getExtension(extensionId));
		}

		@Override
		public CMExtension[] getExtensions() {
			return ExtensionAdapter.convert(delegate.getExtensions());
		}

		@Override
		public String getLabel() {
			return delegate.getLabel();
		}

		@Override
		public String getSchemaReference() {
			return delegate.getSchemaReference();
		}

		@Override
		public String getSimpleIdentifier() {
			return delegate.getSimpleIdentifier();
		}

		@Override
		public String getUniqueIdentifier() {
			return delegate.getUniqueIdentifier();
		}

	}

	protected static class CMtoStd implements IExtensionPoint {
		
		private final CMExtensionPoint delegate;

		public CMtoStd(CMExtensionPoint o) {
			if (o == null) {
				throw new IllegalArgumentException("null delegate");
			}
			this.delegate = o;
		}

		@Override
		public IConfigurationElement[] getConfigurationElements() {
			return ConfigurationElementAdapter.convert(delegate.getConfigurationElements());
		}

		@Override
		public IPluginDescriptor getDeclaringPluginDescriptor() {
			return PluginDescriptorAdapter.convert(delegate.getDeclaringPluginDescriptor());
		}

		@Override
		public IExtension getExtension(String extensionId) {
			return ExtensionAdapter.convert(delegate.getExtension(extensionId));
		}

		@Override
		public IExtension[] getExtensions() {
			return ExtensionAdapter.convert(delegate.getExtensions());
		}

		@Override
		public String getLabel() {
			return delegate.getLabel();
		}

		@Override
		public String getSchemaReference() {
			return delegate.getSchemaReference();
		}

		@Override
		public String getSimpleIdentifier() {
			return delegate.getSimpleIdentifier();
		}

		@Override
		public String getUniqueIdentifier() {
			return delegate.getUniqueIdentifier();
		}

	}

}
