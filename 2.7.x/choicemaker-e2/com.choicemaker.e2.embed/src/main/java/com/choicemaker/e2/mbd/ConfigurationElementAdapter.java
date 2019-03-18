/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.e2.mbd;

import com.choicemaker.e2.CMConfigurationElement;
import com.choicemaker.e2.E2Exception;
import com.choicemaker.e2.CMExtension;
import com.choicemaker.e2.mbd.runtime.CoreException;
import com.choicemaker.e2.mbd.runtime.IConfigurationElement;
import com.choicemaker.e2.mbd.runtime.IExtension;

public class ConfigurationElementAdapter {

	public static IConfigurationElement convert(CMConfigurationElement cmce) {
		IConfigurationElement retVal = null;
		if (cmce != null) {
			retVal = new CMtoStd(cmce);
		}
		return retVal;
	}

	public static IConfigurationElement[] convert(CMConfigurationElement[] cmce) {
		IConfigurationElement[] retVal = null;
		if (cmce != null) {
			retVal = new IConfigurationElement[cmce.length];
			for (int i = 0; i < cmce.length; i++) {
				retVal[i] = convert(cmce[i]);
			}
		}
		return retVal;
	}

	public static CMConfigurationElement convert(IConfigurationElement ice) {
		CMConfigurationElement retVal = null;
		if (ice != null) {
			retVal = new StdToCM(ice);
		}
		return retVal;
	}

	public static CMConfigurationElement[] convert(IConfigurationElement[] ice) {
		CMConfigurationElement[] retVal = null;
		if (ice != null) {
			retVal = new CMConfigurationElement[ice.length];
			for (int i = 0; i < ice.length; i++) {
				retVal[i] = convert(ice[i]);
			}
		}
		return retVal;
	}

	protected static class StdToCM implements CMConfigurationElement {

		private final IConfigurationElement delegate;

		public StdToCM(IConfigurationElement o) {
			if (o == null) {
				throw new IllegalArgumentException("null delegate");
			}
			this.delegate = o;
		}

		@Override
		public Object createExecutableExtension(String propertyName)
				throws E2Exception {
			try {
				return delegate.createExecutableExtension(propertyName);
			} catch (CoreException e) {
				E2Exception cmce = CoreExceptionAdapter.convert(e);
				throw cmce;
			}
		}

		@Override
		public String getAttribute(String name) {
			return delegate.getAttribute(name);
		}

		@Override
		public String getAttributeAsIs(String name) {
			return delegate.getAttributeAsIs(name);
		}

		@Override
		public String[] getAttributeNames() {
			return delegate.getAttributeNames();
		}

		@Override
		public CMConfigurationElement[] getChildren() {
			return convert(delegate.getChildren());
		}

		@Override
		public CMConfigurationElement[] getChildren(String name) {
			return convert(delegate.getChildren(name));
		}

		@Override
		public CMExtension getDeclaringExtension() {
			return ExtensionAdapter.convert(delegate.getDeclaringExtension());
		}

		@Override
		public String getName() {
			return delegate.getName();
		}

		@Override
		public String getValue() {
			return delegate.getValue();
		}

		@Override
		public String getValueAsIs() {
			return delegate.getValueAsIs();
		}

	}

	protected static class CMtoStd implements IConfigurationElement {

		private final CMConfigurationElement delegate;

		public CMtoStd(CMConfigurationElement o) {
			if (o == null) {
				throw new IllegalArgumentException("null delegate");
			}
			this.delegate = o;
		}

		@Override
		public Object createExecutableExtension(String propertyName)
				throws CoreException {
			try {
				return delegate.createExecutableExtension(propertyName);
			} catch (E2Exception e) {
				CoreException ce = CoreExceptionAdapter.convert(e);
				throw ce;
			}
		}

		@Override
		public String getAttribute(String name) {
			return delegate.getAttribute(name);
		}

		@Override
		public String getAttributeAsIs(String name) {
			return delegate.getAttributeAsIs(name);
		}

		@Override
		public String[] getAttributeNames() {
			return delegate.getAttributeNames();
		}

		@Override
		public IConfigurationElement[] getChildren() {
			return convert(delegate.getChildren());
		}

		@Override
		public IConfigurationElement[] getChildren(String name) {
			return convert(delegate.getChildren(name));
		}

		@Override
		public IExtension getDeclaringExtension() {
			return ExtensionAdapter.convert(delegate.getDeclaringExtension());
		}

		@Override
		public String getName() {
			return delegate.getName();
		}

		@Override
		public String getValue() {
			return delegate.getValue();
		}

		@Override
		public String getValueAsIs() {
			return delegate.getValueAsIs();
		}

	}

	private ConfigurationElementAdapter() {
	}

}
