/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.e2.std;

import org.eclipse.core.runtime.ISafeRunnable;

import com.choicemaker.e2.CMSafeRunnable;

public class SafeRunnableAdapter {

	public static ISafeRunnable convert(CMSafeRunnable o) {
		ISafeRunnable retVal = null;
		if (o != null) {
			retVal = new CMtoStd(o);
		}
		return retVal;
	}

	public static ISafeRunnable[] convert(CMSafeRunnable[] o) {
		ISafeRunnable[] retVal = null;
		if (o != null) {
			retVal = new ISafeRunnable[o.length];
			for (int i=0; i<o.length; i++) {
				retVal[i] = convert(o[i]);
			}
		}
		return retVal;
	}
	
	public static CMSafeRunnable convert(ISafeRunnable o) {
		CMSafeRunnable retVal = null;
		if (o != null) {
			retVal = new StdToCM(o);
		}
		return retVal;
	}

	public static CMSafeRunnable[] convert(ISafeRunnable[] o) {
		CMSafeRunnable[] retVal = null;
		if (o != null) {
			retVal = new CMSafeRunnable[o.length];
			for (int i=0; i<o.length; i++) {
					retVal[i] = convert(o[i]);
			}
		}
		return retVal;
	}
	
	protected static class StdToCM implements CMSafeRunnable {
		
		private final ISafeRunnable delegate;

		public StdToCM(ISafeRunnable o) {
			if (o == null) {
				throw new IllegalArgumentException("null delegate");
			}
			this.delegate = o;
		}

		public void handleException(Throwable exception) {
			delegate.handleException(exception);
		}

		public void run() throws Exception {
			delegate.run();
		}

	}

	protected static class CMtoStd implements ISafeRunnable {
		
		private final CMSafeRunnable delegate;

		public CMtoStd(CMSafeRunnable o) {
			if (o == null) {
				throw new IllegalArgumentException("null delegate");
			}
			this.delegate = o;
		}

		public void handleException(Throwable exception) {
			delegate.handleException(exception);
		}

		public void run() throws Exception {
			delegate.run();
		}

	}

}
