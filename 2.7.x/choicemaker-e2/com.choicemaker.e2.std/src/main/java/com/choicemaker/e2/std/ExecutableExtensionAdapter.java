/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.e2.std;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;

import com.choicemaker.e2.CMConfigurationElement;
import com.choicemaker.e2.CMExecutableExtension;
import com.choicemaker.e2.E2Exception;

public class ExecutableExtensionAdapter {
	
	public static IExecutableExtension convert(CMExecutableExtension cmce) {
		IExecutableExtension retVal = null;
		if (cmce != null) {
			retVal = new CMtoStd(cmce);
		}
		return retVal;
	}

	public static IExecutableExtension[] convert(CMExecutableExtension[] cmce) {
		IExecutableExtension[] retVal = null;
		if (cmce != null) {
			retVal = new IExecutableExtension[cmce.length];
			for (int i=0; i<cmce.length; i++) {
				retVal[i] = convert(cmce[i]);
			}
		}
		return retVal;
	}
	
	public static CMExecutableExtension convert(IExecutableExtension ice) {
		CMExecutableExtension retVal = null;
		if (ice != null) {
			retVal = new StdToCM(ice);
		}
		return retVal;
	}

	public static CMExecutableExtension[] convert(IExecutableExtension[] ice) {
		CMExecutableExtension[] retVal = null;
		if (ice != null) {
			retVal = new CMExecutableExtension[ice.length];
			for (int i=0; i<ice.length; i++) {
					retVal[i] = convert(ice[i]);
			}
		}
		return retVal;
	}
	
	protected static class StdToCM implements CMExecutableExtension {
		
		private final IExecutableExtension delegate;

		
		public StdToCM(IExecutableExtension o) {
			if (o == null) {
				throw new IllegalArgumentException("null delegate");
			}
			this.delegate = o;
		}

		@Override
		public void setInitializationData(CMConfigurationElement config,
				String propertyName, Object data) throws E2Exception {
			try {
				delegate.setInitializationData(ConfigurationElementAdapter.convert(config), propertyName, data);
			} catch (CoreException e) {
				E2Exception cmce = CoreExceptionAdapter.convert(e);
				throw cmce;
			}
		}

	}
	
	protected static class CMtoStd implements IExecutableExtension {
		
		private final CMExecutableExtension delegate;

		
		public CMtoStd(CMExecutableExtension o) {
			if (o == null) {
				throw new IllegalArgumentException("null delegate");
			}
			this.delegate = o;
		}

		@Override
		public void setInitializationData(IConfigurationElement config,
				String propertyName, Object data) throws CoreException {
			try {
				delegate.setInitializationData(ConfigurationElementAdapter.convert(config), propertyName, data);
			} catch (E2Exception e) {
				CoreException cmce = CoreExceptionAdapter.convert(e);
				throw cmce;
			}
		}

	}

}
