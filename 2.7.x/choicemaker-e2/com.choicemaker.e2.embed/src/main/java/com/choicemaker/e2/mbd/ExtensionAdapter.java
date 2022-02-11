/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.e2.mbd;

import com.choicemaker.e2.CMConfigurationElement;
import com.choicemaker.e2.CMExtension;
import com.choicemaker.e2.CMPluginDescriptor;
import com.choicemaker.e2.mbd.runtime.IConfigurationElement;
import com.choicemaker.e2.mbd.runtime.IExtension;
import com.choicemaker.e2.mbd.runtime.IPluginDescriptor;

public class ExtensionAdapter {

	public static IExtension convert(CMExtension o) {
		IExtension retVal = null;
		if (o != null) {
			retVal = new CMtoStd(o);
		}
		return retVal;
	}

	public static IExtension[] convert(CMExtension[] o) {
		IExtension[] retVal = null;
		if (o != null) {
			retVal = new IExtension[o.length];
			for (int i=0; i<o.length; i++) {
				retVal[i] = convert(o[i]);
			}
		}
		return retVal;
	}
	
	public static CMExtension convert(IExtension o) {
		CMExtension retVal = null;
		if (o != null) {
			retVal = new StdToCM(o);
		}
		return retVal;
	}

	public static CMExtension[] convert(IExtension[] o) {
		CMExtension[] retVal = null;
		if (o != null) {
			retVal = new CMExtension[o.length];
			for (int i=0; i<o.length; i++) {
					retVal[i] = convert(o[i]);
			}
		}
		return retVal;
	}
	
	protected static class StdToCM implements CMExtension {
		
		private final IExtension delegate;

		public StdToCM(IExtension o) {
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
		public String getExtensionPointUniqueIdentifier() {
			return delegate.getExtensionPointUniqueIdentifier();
		}

		@Override
		public String getLabel() {
			return delegate.getLabel();
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

	protected static class CMtoStd implements IExtension {
		
		private final CMExtension delegate;

		public CMtoStd(CMExtension o) {
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
		public String getExtensionPointUniqueIdentifier() {
			return delegate.getExtensionPointUniqueIdentifier();
		}

		@Override
		public String getLabel() {
			return delegate.getLabel();
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
