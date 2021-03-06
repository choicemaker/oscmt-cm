/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.e2.mbd.runtime.model;

import com.choicemaker.e2.mbd.plugin.impl.PluginRegistry;
import com.choicemaker.e2.mbd.runtime.Platform;

/**
 * An object which represents the user-defined properties in a configuration
 * element of a plug-in manifest.  Properties are <code>String</code>-based
 * key/value pairs.
 * <p>
 * This class may be instantiated, or further subclassed.
 * </p>
 */
public class ConfigurationPropertyModel extends PluginModelObject {

	// DTD properties (included in plug-in manifest)
	private String value = null;
	
/**
 * Creates a new configuration property model in which all fields
 * are <code>null</code>.
 */
public ConfigurationPropertyModel() {
}
/**
 * Returns the value of this property.
 * 
 * @return the value of this property
 *  or <code>null</code>
 */
public String getValue() {
	return value;
}
/**
 * Optimization to replace a non-localized key with its localized value.  Avoids having
 * to access resource bundles for further lookups.
 */
public void setLocalizedValue(String value) {
	this.value = value;
	((PluginRegistry)Platform.getPluginRegistry()).setCacheDirty();
}
/**
 * Sets the value of this property.
 * This object must not be read-only.
 * 
 * @param value the new value of this property.  May be <code>null</code>.
 */
public void setValue(String value) {
	assertIsWriteable();
	this.value = value;
}
}