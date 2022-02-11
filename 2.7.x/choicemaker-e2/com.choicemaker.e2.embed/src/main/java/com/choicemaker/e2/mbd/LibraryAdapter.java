/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.e2.mbd;

import com.choicemaker.e2.CMLibrary;
import com.choicemaker.e2.CMPath;
import com.choicemaker.e2.mbd.runtime.ILibrary;
import com.choicemaker.e2.mbd.runtime.IPath;

public class LibraryAdapter {

	public static ILibrary convert(CMLibrary o) {
		ILibrary retVal = null;
		if (o != null) {
			retVal = new CMtoStd(o);
		}
		return retVal;
	}

	public static ILibrary[] convert(CMLibrary[] o) {
		ILibrary[] retVal = null;
		if (o != null) {
			retVal = new ILibrary[o.length];
			for (int i=0; i<o.length; i++) {
				retVal[i] = convert(o[i]);
			}
		}
		return retVal;
	}
	
	public static CMLibrary convert(ILibrary o) {
		CMLibrary retVal = null;
		if (o != null) {
			retVal = new StdToCM(o);
		}
		return retVal;
	}

	public static CMLibrary[] convert(ILibrary[] o) {
		CMLibrary[] retVal = null;
		if (o != null) {
			retVal = new CMLibrary[o.length];
			for (int i=0; i<o.length; i++) {
					retVal[i] = convert(o[i]);
			}
		}
		return retVal;
	}
	
	protected static class StdToCM implements CMLibrary {
		
		private final ILibrary delegate;

		public StdToCM(ILibrary o) {
			if (o == null) {
				throw new IllegalArgumentException("null delegate");
			}
			this.delegate = o;
		}

		@Override
		public String[] getContentFilters() {
			return delegate.getContentFilters();
		}

		@Override
		public CMPath getPath() {
			return PathAdapter.convert(delegate.getPath());
		}

		@Override
		public String getType() {
			return delegate.getType();
		}

		@Override
		public boolean isExported() {
			return delegate.isExported();
		}

		@Override
		public boolean isFullyExported() {
			return delegate.isFullyExported();
		}

		@Override
		public String[] getPackagePrefixes() {
			return delegate.getPackagePrefixes();
		}

	}

	protected static class CMtoStd implements ILibrary {
		
		private final CMLibrary delegate;

		public CMtoStd(CMLibrary o) {
			if (o == null) {
				throw new IllegalArgumentException("null delegate");
			}
			this.delegate = o;
		}

		@Override
		public String[] getContentFilters() {
			return delegate.getContentFilters();
		}

		@Override
		public IPath getPath() {
			return PathAdapter.convert(delegate.getPath());
		}

		@Override
		public String getType() {
			return delegate.getType();
		}

		@Override
		public boolean isExported() {
			return delegate.isExported();
		}

		@Override
		public boolean isFullyExported() {
			return delegate.isFullyExported();
		}

		@Override
		public String[] getPackagePrefixes() {
			return delegate.getPackagePrefixes();
		}

	}

}
