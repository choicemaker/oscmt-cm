/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.e2;

/**
 * The plug-in registry holds the master list of all discovered plug-ins,
 * extension points, and extensions.
 * <p>
 * The plug-in registry can be queried, by name, for plug-ins, extension points,
 * and extensions.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface CMPluginRegistry {
	/**
	 * Returns all configuration elements from all extensions configured into
	 * the identified extension point. Returns an empty array if the extension
	 * point does not exist, has no extensions configured, or none of the
	 * extensions contain configuration elements.
	 *
	 * @param extensionPointId
	 *            the unique identifier of the extension point (e.g.
	 *            <code>"org.eclipse.core.resources.builders"</code>)
	 * @return the configuration elements
	 */
	public CMConfigurationElement[] getConfigurationElementsFor(
			String extensionPointId);

	/**
	 * Returns all configuration elements from all extensions configured into
	 * the identified extension point. Returns an empty array if the extension
	 * point does not exist, has no extensions configured, or none of the
	 * extensions contain configuration elements.
	 *
	 * @param pluginId
	 *            the unique identifier of the plug-in (e.g.
	 *            <code>"org.eclipse.core.resources"</code>)
	 * @param extensionPointName
	 *            the simple identifier of the extension point (e.g.
	 *            <code>"builders"</code>)
	 * @return the configuration elements
	 */
	public CMConfigurationElement[] getConfigurationElementsFor(String pluginId,
			String extensionPointName);

	/**
	 * Returns all configuration elements from the identified extension. Returns
	 * an empty array if the extension does not exist or contains no
	 * configuration elements.
	 *
	 * @param pluginId
	 *            the unique identifier of the plug-in (e.g.
	 *            <code>"org.eclipse.core.resources"</code>)
	 * @param extensionPointName
	 *            the simple identifier of the extension point (e.g.
	 *            <code>"builders"</code>)
	 * @param extensionId
	 *            the unique identifier of the extension (e.g.
	 *            <code>"com.example.acme.coolbuilder</code>)
	 * @return the configuration elements
	 */
	public CMConfigurationElement[] getConfigurationElementsFor(String pluginId,
			String extensionPointName, String extensionId);

	/**
	 * Returns the specified extension in this plug-in registry, or
	 * <code>null</code> if there is no such extension. The first parameter
	 * identifies the extension point, and the second parameter identifies an
	 * extension plugged in to that extension point.
	 *
	 * @param extensionPointId
	 *            the unique identifier of the extension point (e.g.
	 *            <code>"org.eclipse.core.resources.builders"</code>)
	 * @param extensionId
	 *            the unique identifier of the extension (e.g.
	 *            <code>"com.example.acme.coolbuilder"</code>)
	 * @return the extension, or <code>null</code>
	 */
	public CMExtension getExtension(String extensionPointId,
			String extensionId);

	/**
	 * Returns the specified extension in this plug-in registry, or
	 * <code>null</code> if there is no such extension. The first two parameters
	 * identify the extension point, and the third parameter identifies an
	 * extension plugged in to that extension point.
	 *
	 * @param pluginId
	 *            the unique identifier of the plug-in (e.g.
	 *            <code>"org.eclipse.core.resources"</code>)
	 * @param extensionPointName
	 *            the simple identifier of the extension point (e.g.
	 *            <code>"builders"</code>)
	 * @param extensionId
	 *            the unique identifier of the extension (e.g.
	 *            <code>"com.example.acme.coolbuilder"</code>)
	 * @return the extension, or <code>null</code>
	 */
	public CMExtension getExtension(String pluginId, String extensionPointName,
			String extensionId);

	/**
	 * Returns the extension point with the given extension point identifier in
	 * this plug-in registry, or <code>null</code> if there is no such extension
	 * point.
	 *
	 * @param extensionPointId
	 *            the unique identifier of the extension point (e.g.,
	 *            <code>"org.eclipse.core.resources.builders"</code>)
	 * @return the extension point, or <code>null</code>
	 */
	public CMExtensionPoint getExtensionPoint(String extensionPointId);

	/**
	 * Returns the extension point in this plug-in registry with the given
	 * plug-in identifier and extension point simple identifier, or
	 * <code>null</code> if there is no such extension point.
	 *
	 * @param pluginId
	 *            the unique identifier of the plug-in (e.g.
	 *            <code>"org.eclipse.core.resources"</code>)
	 * @param extensionPointName
	 *            the simple identifier of the extension point (e.g.
	 *            <code>" builders"</code>)
	 * @return the extension point, or <code>null</code>
	 */
	public CMExtensionPoint getExtensionPoint(String pluginId,
			String extensionPointName);

	/**
	 * Returns all extension points known to this plug-in registry. Returns an
	 * empty array if there are no extension points.
	 *
	 * @return the extension points known to this plug-in registry
	 */
	public CMExtensionPoint[] getExtensionPoints();

	/**
	 * Returns the plug-in descriptor with the given plug-in identifier in this
	 * plug-in registry, or <code>null</code> if there is no such plug-in. If
	 * there are multiple versions of the identified plug-in, one will be
	 * non-deterministically chosen and returned.
	 *
	 * @param pluginId
	 *            the unique identifier of the plug-in (e.g.
	 *            <code>"com.example.acme"</code>).
	 * @return the plug-in descriptor, or <code>null</code>
	 */
	public CMPluginDescriptor getPluginDescriptor(String pluginId);

	// /**
	// * Returns the plug-in descriptor with the given plug-in identifier
	// * and version in this plug-in registry, or <code>null</code> if
	// * there is no such plug-in.
	// *
	// * @param pluginId the unique identifier of the plug-in
	// * (e.g. <code>"org.eclipse.core.resources"</code>)
	// * @param version plug-in version identifier. If <code>null</code> is
	// specified,
	// * a non-deterministically chosen version of the identified plug-in (if
	// any)
	// * will be returned
	// * @return the plug-in descriptor, or <code>null</code>
	// */
	// public CMPluginDescriptor getPluginDescriptor(String pluginId,
	// PluginVersionIdentifier version);
	/**
	 * Returns all plug-in descriptors known to this plug-in registry. Returns
	 * an empty array if there are no installed plug-ins.
	 *
	 * @return the plug-in descriptors known to this plug-in registry
	 */
	public CMPluginDescriptor[] getPluginDescriptors();

	/**
	 * Returns all versions of the identified plug-in descriptor known to this
	 * plug-in registry. Returns an empty array if there are no plug-ins with
	 * the specified identifier.
	 *
	 * @param pluginId
	 *            the unique identifier of the plug-in (e.g.
	 *            <code>"org.eclipse.core.resources"</code>).
	 * @return the plug-in descriptors known to this plug-in registry
	 */
	public CMPluginDescriptor[] getPluginDescriptors(String pluginId);
}
