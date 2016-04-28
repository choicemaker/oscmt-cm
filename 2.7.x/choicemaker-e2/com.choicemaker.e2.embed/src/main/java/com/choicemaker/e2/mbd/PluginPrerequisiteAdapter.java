/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.e2.mbd;

import com.choicemaker.e2.CMPluginPrerequisite;
import com.choicemaker.e2.CMPluginVersionIdentifier;
import com.choicemaker.e2.mbd.runtime.IPluginPrerequisite;
import com.choicemaker.e2.mbd.runtime.PluginVersionIdentifier;

public class PluginPrerequisiteAdapter {

	public static IPluginPrerequisite convert(CMPluginPrerequisite o) {
		IPluginPrerequisite retVal = null;
		if (o != null) {
			retVal = new CMtoStd(o);
		}
		return retVal;
	}

	public static IPluginPrerequisite[] convert(CMPluginPrerequisite[] o) {
		IPluginPrerequisite[] retVal = null;
		if (o != null) {
			retVal = new IPluginPrerequisite[o.length];
			for (int i=0; i<o.length; i++) {
				retVal[i] = convert(o[i]);
			}
		}
		return retVal;
	}
	
	public static CMPluginPrerequisite convert(IPluginPrerequisite o) {
		CMPluginPrerequisite retVal = null;
		if (o != null) {
			retVal = new StdToCM(o);
		}
		return retVal;
	}

	public static CMPluginPrerequisite[] convert(IPluginPrerequisite[] o) {
		CMPluginPrerequisite[] retVal = null;
		if (o != null) {
			retVal = new CMPluginPrerequisite[o.length];
			for (int i=0; i<o.length; i++) {
					retVal[i] = convert(o[i]);
			}
		}
		return retVal;
	}
	
	protected static class StdToCM implements CMPluginPrerequisite {
		
		private final IPluginPrerequisite delegate;

		public StdToCM(IPluginPrerequisite o) {
			if (o == null) {
				throw new IllegalArgumentException("null delegate");
			}
			this.delegate = o;
		}

		public CMPluginVersionIdentifier getResolvedVersionIdentifier() {
			return PluginVersionIdentifierAdapter.convert(delegate.getResolvedVersionIdentifier());
		}

		public String getUniqueIdentifier() {
			return delegate.getUniqueIdentifier();
		}

		public CMPluginVersionIdentifier getVersionIdentifier() {
			return PluginVersionIdentifierAdapter.convert(delegate.getVersionIdentifier());
		}

		public boolean isExported() {
			return delegate.isExported();
		}

		public boolean isMatchedAsGreaterOrEqual() {
			return delegate.isMatchedAsGreaterOrEqual();
		}

		public boolean isMatchedAsCompatible() {
			return delegate.isMatchedAsCompatible();
		}

		public boolean isMatchedAsEquivalent() {
			return delegate.isMatchedAsEquivalent();
		}

		public boolean isMatchedAsPerfect() {
			return delegate.isMatchedAsPerfect();
		}

		public boolean isMatchedAsExact() {
			return delegate.isMatchedAsExact();
		}

		public boolean isOptional() {
			return delegate.isOptional();
		}

	}

	protected static class CMtoStd implements IPluginPrerequisite {
		
		private final CMPluginPrerequisite delegate;

		public CMtoStd(CMPluginPrerequisite o) {
			if (o == null) {
				throw new IllegalArgumentException("null delegate");
			}
			this.delegate = o;
		}

		public PluginVersionIdentifier getResolvedVersionIdentifier() {
			return PluginVersionIdentifierAdapter.convert(delegate.getResolvedVersionIdentifier());
		}

		public String getUniqueIdentifier() {
			return delegate.getUniqueIdentifier();
		}

		public PluginVersionIdentifier getVersionIdentifier() {
			return PluginVersionIdentifierAdapter.convert(delegate.getVersionIdentifier());
		}

		public boolean isExported() {
			return delegate.isExported();
		}

		public boolean isMatchedAsGreaterOrEqual() {
			return delegate.isMatchedAsGreaterOrEqual();
		}

		public boolean isMatchedAsCompatible() {
			return delegate.isMatchedAsCompatible();
		}

		public boolean isMatchedAsEquivalent() {
			return delegate.isMatchedAsEquivalent();
		}

		public boolean isMatchedAsPerfect() {
			return delegate.isMatchedAsPerfect();
		}

		public boolean isMatchedAsExact() {
			return delegate.isMatchedAsExact();
		}

		public boolean isOptional() {
			return delegate.isOptional();
		}

	}

}
