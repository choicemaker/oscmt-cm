/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.e2.std;

import org.eclipse.core.boot.IPlatformRunnable;

import com.choicemaker.e2.CMPlatformRunnable;

public class PlatformRunnableAdapter {

	public static IPlatformRunnable convert(CMPlatformRunnable o) {
		IPlatformRunnable retVal = null;
		if (o != null) {
			retVal = new CMtoStd(o);
		}
		return retVal;
	}

	public static IPlatformRunnable[] convert(CMPlatformRunnable[] o) {
		IPlatformRunnable[] retVal = null;
		if (o != null) {
			retVal = new IPlatformRunnable[o.length];
			for (int i=0; i<o.length; i++) {
				retVal[i] = convert(o[i]);
			}
		}
		return retVal;
	}
	
	public static CMPlatformRunnable convert(IPlatformRunnable o) {
		CMPlatformRunnable retVal = null;
		if (o != null) {
			retVal = new StdToCM(o);
		}
		return retVal;
	}

	public static CMPlatformRunnable[] convert(IPlatformRunnable[] o) {
		CMPlatformRunnable[] retVal = null;
		if (o != null) {
			retVal = new CMPlatformRunnable[o.length];
			for (int i=0; i<o.length; i++) {
					retVal[i] = convert(o[i]);
			}
		}
		return retVal;
	}
	
	protected static class StdToCM implements CMPlatformRunnable {
		
		private final IPlatformRunnable delegate;

		public StdToCM(IPlatformRunnable o) {
			if (o == null) {
				throw new IllegalArgumentException("null delegate");
			}
			this.delegate = o;
		}

		@Override
		public Object run(Object args) throws Exception {
			return delegate.run(args);
		}

	}

	protected static class CMtoStd implements IPlatformRunnable {
		
		private final CMPlatformRunnable delegate;

		public CMtoStd(CMPlatformRunnable o) {
			if (o == null) {
				throw new IllegalArgumentException("null delegate");
			}
			this.delegate = o;
		}

		@Override
		public Object run(Object args) throws Exception {
			return delegate.run(args);
		}

	}

}
