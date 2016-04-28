/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.choicemaker.e2.mbd.plugin.impl;

import com.choicemaker.e2.mbd.runtime.IPluginPrerequisite;
import com.choicemaker.e2.mbd.runtime.PluginVersionIdentifier;
import com.choicemaker.e2.mbd.runtime.model.PluginPrerequisiteModel;

public class PluginPrerequisite extends PluginPrerequisiteModel implements IPluginPrerequisite {
/**
 * @see IPluginPrerequisite
 */
public PluginVersionIdentifier getResolvedVersionIdentifier() {
	String version = getResolvedVersion();
	return version == null ? null : new PluginVersionIdentifier(version);
}
/**
 * @see IPluginPrerequisite
 */
public String getUniqueIdentifier() {
	return getPlugin();
}
/**
 * @see IPluginPrerequisite
 */
public PluginVersionIdentifier getVersionIdentifier() {
	String version = getVersion();
	return version == null ? null : new PluginVersionIdentifier(version);
}
/**
 * @see IPluginPrerequisite
 */
public boolean isExported() {
	return getExport();
}
/**
 * @see IPluginPrerequisite
 */
public boolean isMatchedAsGreaterOrEqual() {
	return getMatchByte() == PREREQ_MATCH_GREATER_OR_EQUAL;
}
/**
 * @see IPluginPrerequisite
 */
public boolean isMatchedAsCompatible() {
	return (getMatchByte() == PREREQ_MATCH_COMPATIBLE) ||
	        ((getVersionIdentifier() != null) && (getMatchByte() == PREREQ_MATCH_UNSPECIFIED));
}
/**
 * @see IPluginPrerequisite
 */
public boolean isMatchedAsEquivalent() {
	return getMatchByte() == PREREQ_MATCH_EQUIVALENT;
}
/**
 * @see IPluginPrerequisite
 */
public boolean isMatchedAsPerfect() {
	return getMatchByte() == PREREQ_MATCH_PERFECT;
}
/**
 * @see IPluginPrerequisite
 */
public boolean isMatchedAsExact() {
	return isMatchedAsEquivalent();
}
/**
 * @see IPluginPrerequisite
 */
public boolean isOptional() {
	return getOptional();
}
}
