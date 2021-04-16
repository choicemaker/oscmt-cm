/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.e2.std;

import org.eclipse.core.runtime.IStatus;

import com.choicemaker.e2.CMStatus;

public class StatusAdapter {

	
	public static CMStatus convert(IStatus ice) {
		CMStatus retVal = null;
		if (ice != null) {
			retVal = new StdToCM(ice);
		}
		return retVal;
	}

	public static CMStatus[] convert(IStatus[] ice) {
		CMStatus[] retVal = null;
		if (ice != null) {
			retVal = new CMStatus[ice.length];
			for (int i=0; i<ice.length; i++) {
				retVal[i] = convert(ice[i]);
			}
		}
		return retVal;
	}

	public static IStatus convert(CMStatus ice) {
		IStatus retVal = null;
		if (ice != null) {
			retVal = new CMtoStd(ice);
		}
		return retVal;
	}

	public static IStatus[] convert(CMStatus[] ice) {
		IStatus[] retVal = null;
		if (ice != null) {
			retVal = new IStatus[ice.length];
			for (int i=0; i<ice.length; i++) {
				retVal[i] = convert(ice[i]);
			}
		}
		return retVal;
	}

	protected static class StdToCM implements CMStatus {
		
		private final IStatus delegate;

		public StdToCM(IStatus o) {
			if (o == null) {
				throw new IllegalArgumentException("null delegate");
			}
			this.delegate = o;
		}

		@Override
		public CMStatus[] getChildren() {
			return convert(delegate.getChildren());
		}

		@Override
		public int getCode() {
			return delegate.getCode();
		}

		@Override
		public Throwable getException() {
			return delegate.getException();
		}

		@Override
		public String getMessage() {
			return delegate.getMessage();
		}

		@Override
		public String getPlugin() {
			return delegate.getPlugin();
		}

		@Override
		public int getSeverity() {
			return delegate.getSeverity();
		}

		@Override
		public boolean isMultiStatus() {
			return delegate.isMultiStatus();
		}

		@Override
		public boolean isOK() {
			return delegate.isOK();
		}

		@Override
		public boolean matches(int severityMask) {
			return delegate.matches(severityMask);
		}

	}

	protected static class CMtoStd implements IStatus {
		
		private final CMStatus delegate;

		public CMtoStd(CMStatus o) {
			if (o == null) {
				throw new IllegalArgumentException("null delegate");
			}
			this.delegate = o;
		}

		@Override
		public IStatus[] getChildren() {
			return convert(delegate.getChildren());
		}

		@Override
		public int getCode() {
			return delegate.getCode();
		}

		@Override
		public Throwable getException() {
			return delegate.getException();
		}

		@Override
		public String getMessage() {
			return delegate.getMessage();
		}

		@Override
		public String getPlugin() {
			return delegate.getPlugin();
		}

		@Override
		public int getSeverity() {
			return delegate.getSeverity();
		}

		@Override
		public boolean isMultiStatus() {
			return delegate.isMultiStatus();
		}

		@Override
		public boolean isOK() {
			return delegate.isOK();
		}

		@Override
		public boolean matches(int severityMask) {
			return delegate.matches(severityMask);
		}

	}

}
