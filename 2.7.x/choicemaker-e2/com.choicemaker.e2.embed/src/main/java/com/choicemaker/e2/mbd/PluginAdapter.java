/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.e2.mbd;

import com.choicemaker.e2.CMPlugin;
import com.choicemaker.e2.CMPluginDescriptor;
import com.choicemaker.e2.E2Exception;
import com.choicemaker.e2.mbd.runtime.CoreException;
import com.choicemaker.e2.mbd.runtime.Plugin;

public class PluginAdapter {

	public static Plugin convert(CMPlugin o) {
		Plugin retVal = null;
		if (o != null) {
			retVal = new CMtoStd(o);
		}
		return retVal;
	}

	public static Plugin[] convert(CMPlugin[] o) {
		Plugin[] retVal = null;
		if (o != null) {
			retVal = new Plugin[o.length];
			for (int i=0; i<o.length; i++) {
				retVal[i] = convert(o[i]);
			}
		}
		return retVal;
	}
	
	public static CMPlugin convert(Plugin o) {
		CMPlugin retVal = null;
		if (o != null) {
			retVal = new StdToCM(o);
		}
		return retVal;
	}

	public static CMPlugin[] convert(Plugin[] o) {
		CMPlugin[] retVal = null;
		if (o != null) {
			retVal = new CMPlugin[o.length];
			for (int i=0; i<o.length; i++) {
					retVal[i] = convert(o[i]);
			}
		}
		return retVal;
	}
	
	protected static class StdToCM implements CMPlugin {
		
		private final Plugin delegate;

		public StdToCM(Plugin o) {
			if (o == null) {
				throw new IllegalArgumentException("null delegate");
			}
			this.delegate = o;
		}

		@Override
		public CMPluginDescriptor getDescriptor() {
			return PluginDescriptorAdapter.convert(delegate.getDescriptor());
		}

		@Override
		public boolean isDebugging() {
			return delegate.isDebugging();
		}

		@Override
		public void setDebugging(boolean value) {
			delegate.setDebugging(value);
		}

		@Override
		public void shutdown() throws E2Exception {
			try {
				delegate.shutdown();
			} catch (CoreException e) {
				E2Exception cmce = CoreExceptionAdapter.convert(e);
				throw cmce;
			}
		}

		@Override
		public void startup() throws E2Exception {
			try {
				delegate.startup();
			} catch (CoreException e) {
				E2Exception cmce = CoreExceptionAdapter.convert(e);
				throw cmce;
			}
		}

	}

	protected static class CMtoStd extends Plugin {
		
		public CMtoStd(CMPlugin o) {
			super(PluginDescriptorAdapter.convert(o.getDescriptor()));
		}

	}
	
}
