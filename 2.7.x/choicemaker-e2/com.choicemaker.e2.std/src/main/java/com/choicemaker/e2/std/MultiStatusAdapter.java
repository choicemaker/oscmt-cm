/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.e2.std;

import org.eclipse.core.runtime.MultiStatus;

import com.choicemaker.e2.CMMultiStatus;
import com.choicemaker.e2.CMStatus;

public class MultiStatusAdapter {

	public static MultiStatus convert(CMMultiStatus o) {
		MultiStatus retVal = null;
		if (o != null) {
			retVal =
				new MultiStatus(o.getPlugin(), o.getCode(),
						StatusAdapter.convert(o.getChildren()), o.getMessage(),
						o.getException());
		}
		return retVal;
	}

	public static MultiStatus[] convert(CMMultiStatus[] o) {
		MultiStatus[] retVal = null;
		if (o != null) {
			retVal = new MultiStatus[o.length];
			for (int i = 0; i < o.length; i++) {
				retVal[i] = convert(o[i]);
			}
		}
		return retVal;
	}

	public static CMMultiStatus convert(MultiStatus o) {
		CMMultiStatus retVal = null;
		if (o != null) {
			retVal = new StdToCM(o);
		}
		return retVal;
	}

	public static CMMultiStatus[] convert(MultiStatus[] o) {
		CMMultiStatus[] retVal = null;
		if (o != null) {
			retVal = new CMMultiStatus[o.length];
			for (int i = 0; i < o.length; i++) {
				retVal[i] = convert(o[i]);
			}
		}
		return retVal;
	}

	protected static class StdToCM implements CMMultiStatus {

		private final MultiStatus delegate;

		public StdToCM(MultiStatus o) {
			if (o == null) {
				throw new IllegalArgumentException("null delegate");
			}
			this.delegate = o;
		}

		@Override
		public int hashCode() {
			return delegate.hashCode();
		}

		@Override
		public void add(CMStatus status) {
			delegate.add(StatusAdapter.convert(status));
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
		public void addAll(CMStatus status) {
			delegate.addAll(StatusAdapter.convert(status));
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
		public CMStatus[] getChildren() {
			return StatusAdapter.convert(delegate.getChildren());
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
		public void merge(CMStatus status) {
			delegate.merge(StatusAdapter.convert(status));
		}

		@Override
		public boolean matches(int severityMask) {
			return delegate.matches(severityMask);
		}

		@Override
		public boolean equals(Object obj) {
			return delegate.equals(obj);
		}

		@Override
		public String toString() {
			return delegate.toString();
		}

	}

}
