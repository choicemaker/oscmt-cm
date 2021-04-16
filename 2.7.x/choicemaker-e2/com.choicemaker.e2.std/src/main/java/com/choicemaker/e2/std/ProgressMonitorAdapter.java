/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.e2.std;

import org.eclipse.core.runtime.IProgressMonitor;

import com.choicemaker.e2.CMProgressMonitor;

public class ProgressMonitorAdapter {

	public static IProgressMonitor convert(CMProgressMonitor o) {
		IProgressMonitor retVal = null;
		if (o != null) {
			retVal = new CMtoStd(o);
		}
		return retVal;
	}

	public static IProgressMonitor[] convert(CMProgressMonitor[] o) {
		IProgressMonitor[] retVal = null;
		if (o != null) {
			retVal = new IProgressMonitor[o.length];
			for (int i=0; i<o.length; i++) {
				retVal[i] = convert(o[i]);
			}
		}
		return retVal;
	}
	
	public static CMProgressMonitor convert(IProgressMonitor o) {
		CMProgressMonitor retVal = null;
		if (o != null) {
			retVal = new StdToCM(o);
		}
		return retVal;
	}

	public static CMProgressMonitor[] convert(IProgressMonitor[] o) {
		CMProgressMonitor[] retVal = null;
		if (o != null) {
			retVal = new CMProgressMonitor[o.length];
			for (int i=0; i<o.length; i++) {
					retVal[i] = convert(o[i]);
			}
		}
		return retVal;
	}
	
	protected static class StdToCM implements CMProgressMonitor {
		
		private final IProgressMonitor delegate;

		public StdToCM(IProgressMonitor o) {
			if (o == null) {
				throw new IllegalArgumentException("null delegate");
			}
			this.delegate = o;
		}

		@Override
		public void beginTask(String name, int totalWork) {
			delegate.beginTask(name, totalWork);
		}

		@Override
		public void done() {
			delegate.done();
		}

		@Override
		public void internalWorked(double work) {
			delegate.internalWorked(work);
		}

		@Override
		public boolean isCanceled() {
			return delegate.isCanceled();
		}

		@Override
		public void setCanceled(boolean value) {
			delegate.setCanceled(value);
		}

		@Override
		public void setTaskName(String name) {
			delegate.setTaskName(name);
		}

		@Override
		public void subTask(String name) {
			delegate.subTask(name);
		}

		@Override
		public void worked(int work) {
			delegate.worked(work);
		}

	}

	protected static class CMtoStd implements IProgressMonitor {
		
		private final CMProgressMonitor delegate;

		public CMtoStd(CMProgressMonitor o) {
			if (o == null) {
				throw new IllegalArgumentException("null delegate");
			}
			this.delegate = o;
		}

		@Override
		public void beginTask(String name, int totalWork) {
			delegate.beginTask(name, totalWork);
		}

		@Override
		public void done() {
			delegate.done();
		}

		@Override
		public void internalWorked(double work) {
			delegate.internalWorked(work);
		}

		@Override
		public boolean isCanceled() {
			return delegate.isCanceled();
		}

		@Override
		public void setCanceled(boolean value) {
			delegate.setCanceled(value);
		}

		@Override
		public void setTaskName(String name) {
			delegate.setTaskName(name);
		}

		@Override
		public void subTask(String name) {
			delegate.subTask(name);
		}

		@Override
		public void worked(int work) {
			delegate.worked(work);
		}

	}

}
