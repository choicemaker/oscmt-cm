/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.e2.mbd.runtime.model;

import com.choicemaker.e2.mbd.plugin.impl.PluginRegistry;
import com.choicemaker.e2.mbd.runtime.Platform;
import com.choicemaker.e2.mbd.runtime.PluginVersionIdentifier;

/**
 * An object which represents the user-defined contents of a plug-in model
 * (either a descriptor or a fragment) in a plug-in manifest.
 * <p>
 * This class may not be instantiated, but may be further subclassed.
 * </p>
 */
public abstract class PluginModel extends PluginModelObject {

	// DTD properties (included in plug-in manifest)
	private String id = null;
	private String providerName = null;
	private String version = null;
	private LibraryModel[] runtime = null;
	private ExtensionPointModel[] extensionPoints = null;
	private ExtensionModel[] extensions = null;
	private PluginPrerequisiteModel[] requires = null;

	// transient properties (not included in plug-in manifest)
	private PluginRegistryModel registry = null;
	private String location = null;
/**
 * Creates a new plug-in descriptor model in which all fields
 * are <code>null</code>.
 */
public PluginModel() {
	super();
}
/**
 * Returns the extension points in this plug-in descriptor.
 *
 * @return the extension points in this plug-in descriptor or <code>null</code>
 */
public ExtensionPointModel[] getDeclaredExtensionPoints() {
	return extensionPoints;
}
/**
 * Returns the extensions in this plug-in descriptor.
 *
 * @return the extensions in this plug-in descriptor or <code>null</code>
 */
public ExtensionModel[] getDeclaredExtensions() {
	return extensions;
}
/**
 * Returns the unique identifier of this plug-in model
 * or <code>null</code>.
 * This identifier is a non-empty string and is unique 
 * within the plug-in registry.
 *
 * @return the unique identifier of this plugin model
 *		(e.g. <code>"com.example"</code>) or <code>null</code>. 
 */
public String getId() {
	return id;
}
/**
 * Returns the location of the plug-in manifest file (e.g., <code>plugin.xml</code>)
 * which corresponds to this plug-in descriptor.  The location is in the
 * form of a URL.
 *
 * @return the location of this plug-in descriptor or <code>null</code>.
 */
public String getLocation() {
	return location;
}
/**
 * Returns the unique identifier of the plug-in related to this model
 * or <code>null</code>.  
 * This identifier is a non-empty string and is unique 
 * within the plug-in registry.
 *
 * @return the unique identifier of the plug-in related to this model
 *		(e.g. <code>"com.example"</code>) or <code>null</code>. 
 */
public abstract String getPluginId();
/**
 * Returns the name of the provider who authored this plug-in.
 *
 * @return name of the provider who authored this plug-in or <code>null</code>
 */
public String getProviderName() {
	return providerName;
}
/**
 * Returns the plug-in registry of which this plug-in descriptor is a member.
 *
 * @return the registry in which this descriptor has been installed or 
 *		<code>null</code> if none.
 */
public PluginRegistryModel getRegistry() {
	return registry;
}

/**
 * Returns the prerequisites of this plug-in.
 *
 * @return the prerequisites of this plug-in or <code>null</code>
 */
public PluginPrerequisiteModel[] getRequires() {
	return requires;
}

/**
 * Returns the libraries configured for this plug-in.
 *
 * @return the libraries configured for this plug-in or <code>null</code>
 */
public LibraryModel[] getRuntime() {
	return runtime;
}
/**
 * Returns the version name of this plug-in.
 *
 * @return the version name of this plug-in or <code>null</code>
 */
public String getVersion() {
	return version;
}
/**
 * Sets this model object and all of its descendents to be read-only.
 * Subclasses may extend this implementation.
 *
 * @see #isReadOnly
 */
@Override
public void markReadOnly() {
	super.markReadOnly();
	if (runtime != null)
		for (int i = 0; i < runtime.length; i++)
			runtime[i].markReadOnly();
	if (extensionPoints != null)
		for (int i = 0; i < extensionPoints.length; i++)
			extensionPoints[i].markReadOnly();
	if (extensions != null)
		for (int i = 0; i < extensions.length; i++)
			extensions[i].markReadOnly();
	if (requires != null)
		for (int i = 0; i < requires.length; i++)
			requires[i].markReadOnly();
}
/**
 * Sets the extension points in this plug-in descriptor.
 * This object must not be read-only.
 *
 * @param value the extension points in this plug-in descriptor.
 *		May be <code>null</code>.
 */
public void setDeclaredExtensionPoints(ExtensionPointModel[] value) {
	assertIsWriteable();
	extensionPoints = value;
}
/**
 * Sets the extensions in this plug-in descriptor.
 * This object must not be read-only.
 *
 * @param value the extensions in this plug-in descriptor.
 *		May be <code>null</code>.
 */
public void setDeclaredExtensions(ExtensionModel[] value) {
	assertIsWriteable();
	extensions = value;
}
/**
 * Sets the unique identifier of this plug-in model.
 * The identifier is a non-empty string and is unique 
 * within the plug-in registry.
 * This object must not be read-only.
 *
 * @param value the unique identifier of the plug-in model (e.g. <code>"com.example"</code>).
 *		May be <code>null</code>.
 */
public void setId(String value) {
	assertIsWriteable();
	id = value;
}
/**
 * Optimization to replace a non-localized key with its localized value.  Avoids having
 * to access resource bundles for further lookups.
 */
public void setLocalizedProviderName(String value) {
	providerName = value;
	((PluginRegistry)Platform.getPluginRegistry()).setCacheDirty();
}
/**
 * Sets the location of the plug-in manifest file (e.g., <code>plugin.xml</code>)
 * which corresponds to this plug-in descriptor.  The location is in the
 * form of a URL.
 * This object must not be read-only.
 *
 * @param value the location of this plug-in descriptor.  May be <code>null</code>.
 */
public void setLocation(String value) {
	assertIsWriteable();
	location = value;
}
/**
 * Sets the name of the provider who authored this plug-in.
 * This object must not be read-only.
 *
 * @param value name of the provider who authored this plug-in.
 *		May be <code>null</code>.
 */
public void setProviderName(String value) {
	assertIsWriteable();
	providerName = value;
}
/**
 * Sets the registry with which this plug-in descriptor is associated.
 * This object must not be read-only.
 *
 * @param value the registry with which this plug-in is associated.
 *		May be <code>null</code>.
 */
public void setRegistry(PluginRegistryModel value) {
	assertIsWriteable();
	registry = value;
}

/**
 * Sets the prerequisites of this plug-in.
 * This object must not be read-only.
 *
 * @param value the prerequisites of this plug-in.  May be <code>null</code>.
 */
public void setRequires(PluginPrerequisiteModel[] value) {
	assertIsWriteable();
	requires = value;
}

/**
 * Sets the libraries configured for this plug-in.
 * This object must not be read-only.
 *
 * @param value the libraries configured for this plug-in.  May be <code>null</code>.
 */
public void setRuntime(LibraryModel[] value) {
	assertIsWriteable();
	runtime = value;
}
/**
 * Sets the version name of this plug-in.  The version number
 * is canonicalized.
 * This object must not be read-only.
 *
 * @param value the version name of this plug-in.
 *		May be <code>null</code>.
 */
public void setVersion(String value) {
	assertIsWriteable();
	version = new PluginVersionIdentifier(value).toString();
}
}
