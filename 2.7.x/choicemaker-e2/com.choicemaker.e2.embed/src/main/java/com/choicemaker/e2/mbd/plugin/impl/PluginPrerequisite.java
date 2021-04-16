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
@Override
public PluginVersionIdentifier getResolvedVersionIdentifier() {
	String version = getResolvedVersion();
	return version == null ? null : new PluginVersionIdentifier(version);
}
/**
 * @see IPluginPrerequisite
 */
@Override
public String getUniqueIdentifier() {
	return getPlugin();
}
/**
 * @see IPluginPrerequisite
 */
@Override
public PluginVersionIdentifier getVersionIdentifier() {
	String version = getVersion();
	return version == null ? null : new PluginVersionIdentifier(version);
}
/**
 * @see IPluginPrerequisite
 */
@Override
public boolean isExported() {
	return getExport();
}
/**
 * @see IPluginPrerequisite
 */
@Override
public boolean isMatchedAsGreaterOrEqual() {
	return getMatchByte() == PREREQ_MATCH_GREATER_OR_EQUAL;
}
/**
 * @see IPluginPrerequisite
 */
@Override
public boolean isMatchedAsCompatible() {
	return (getMatchByte() == PREREQ_MATCH_COMPATIBLE) ||
	        ((getVersionIdentifier() != null) && (getMatchByte() == PREREQ_MATCH_UNSPECIFIED));
}
/**
 * @see IPluginPrerequisite
 */
@Override
public boolean isMatchedAsEquivalent() {
	return getMatchByte() == PREREQ_MATCH_EQUIVALENT;
}
/**
 * @see IPluginPrerequisite
 */
@Override
public boolean isMatchedAsPerfect() {
	return getMatchByte() == PREREQ_MATCH_PERFECT;
}
/**
 * @see IPluginPrerequisite
 */
@Override
public boolean isMatchedAsExact() {
	return isMatchedAsEquivalent();
}
/**
 * @see IPluginPrerequisite
 */
@Override
public boolean isOptional() {
	return getOptional();
}
}
