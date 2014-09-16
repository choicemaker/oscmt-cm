/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.internal.plugins;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.internal.boot.*;
import org.eclipse.core.internal.runtime.Policy;
import org.eclipse.core.runtime.*;

/**
 * Plugin class loader.
 *
 * Handle loading of classes from a plugin. Configures load path based
 * on the plugin <runtime> specification. Configures delegate loaders
 * based on the <requires> specification. Configures
 * the parent loader to be the default instance of 
 * PlatformClassLoader (assumes to be initialized by this point).
 *
 */

public final class PluginClassLoader extends DelegatingURLClassLoader {
	private PluginDescriptor descriptor;
	private boolean pluginActivationInProgress = false;
//	private boolean loadInProgress = false;
	public static boolean usePackagePrefixes = true;

public PluginClassLoader(URL[] codePath, URLContentFilter[] codeFilters, URL[] resourcePath, URLContentFilter[] resourceFilters, ClassLoader parent, PluginDescriptor descriptor) {
	// create a class loader with the given classpath and filters.  Also, the parent
	// should be the parent of the platform class loader.  This allows us to decouple standard
	// parent loading from platform loading.
	super(codePath, codeFilters, resourcePath, resourceFilters, parent);
	this.descriptor = descriptor;
	this.base = descriptor.getInstallURL();
	this.prefixes = initializePrefixes(descriptor, getPrefixId());
	debugConstruction(); // must have initialized loader

	//	Note: initializeImportedLoaders() is called by PluginDescriptor.getPluginClassLoader().
	//	The split between construction and initialization is needed
	//	to correctly handle the case where the user defined loops in 
	//	the prerequisite definitions.
}
public static String[] initializePrefixes(IPluginDescriptor descriptor, String prefixId) {
	// use the classloader.properties file as an over-ride to what
	// appears in the plugin.xml
	if (InternalBootLoader.useClassLoaderProperties()) {
		String list = (String) prefixTable.get(prefixId);
		if (list == null)
			return null;
		// if there is an entry in the file but no value then treat that as
		// a "don't use any package prefixes"
		if (list.trim().length() == 0) {
			if (DEBUG_PROPERTIES)
				System.out.println("Clearing prefixes for: " + descriptor.getUniqueIdentifier()); //$NON-NLS-1$
			return null;
		}
		if (DEBUG_PROPERTIES)
			System.out.println("Using prefixes for " + descriptor.getUniqueIdentifier() + " from classloader.properties: " + list); //$NON-NLS-1$ //$NON-NLS-2$
		return getArrayFromList(list);
	}

	// setup the package prefixes to use
	if (usePackagePrefixes) {
		if (DEBUG_PACKAGE_PREFIXES)
			System.out.println("Reading package prefixes for plug-in: " + descriptor.getUniqueIdentifier()); //$NON-NLS-1$
		// collect all of the package prefixes for all of the runtime entries in the plugin.xml
		Set set = new HashSet(5);
		ILibrary[] libraries = descriptor.getRuntimeLibraries();
		for (int i = 0; libraries != null && i < libraries.length; i++) {
			String[] entries = libraries[i].getPackagePrefixes();
			for (int j=0; entries != null && j < entries.length; j++)
				set.add(entries[j]);
		}
		String[] prefixes = null;
		if (set.size() != 0)
			prefixes = (String[]) set.toArray(new String[set.size()]);
		if (DEBUG_PACKAGE_PREFIXES) {
			String list = arrayToString(prefixes);
			System.out.println("Using the following prefixes: " + list); //$NON-NLS-1$
		}
		return prefixes;
	}
	return null;
}
static String arrayToString(String[] array) {
	if (array == null)
		return null;
	StringBuffer buffer = new StringBuffer();
	for (int i=0; i<array.length; i++) {
		buffer.append(array[i]);
		if (i != array.length - 1)
			buffer.append(',');
	}
	return buffer.toString();
}
protected void activatePlugin(String name) {
	try {
		// pluginActivationInProgress = true;
		// the in-progress flag is set when we detect that activation will be required.
		// be sure to unset it here.
		if (DEBUG && DEBUG_SHOW_ACTIVATE && debugLoader())
			debug("Attempting to activate " + descriptor.getUniqueIdentifier()); //$NON-NLS-1$
		descriptor.doPluginActivation();
	} catch (CoreException e) {
		if (DEBUG && DEBUG_SHOW_ACTIVATE && debugLoader())
			debug("Activation failed for " + descriptor.getUniqueIdentifier()); //$NON-NLS-1$
		throw new DelegatingLoaderException(Policy.bind("plugin.delegatingLoaderTrouble", descriptor.getUniqueIdentifier(), name), e); //$NON-NLS-1$
	} finally {
		if (DEBUG && DEBUG_SHOW_ACTIVATE && debugLoader())
			debug("Exit activation for " + descriptor.getUniqueIdentifier()); //$NON-NLS-1$
		pluginActivationInProgress = false;
	}
}
public String debugId() {
	return descriptor.toString();
}
/**
 * Finds and loads the class with the specified name from the URL search
 * path. Any URLs referring to JAR files are loaded and opened as needed
 * until the class is found.   Search on the parent chain and then self.
 *
 * Subclasses should implement this method.
 *
 * @param name the name of the class
 * @param resolve whether or not to resolve the class if found
 * @param requestor class loader originating the request
 * @param checkParents whether the parent of this loader should be consulted
 * @return the resulting class
 */
protected Class internalFindClassParentsSelf(final String name, boolean resolve, DelegatingURLClassLoader requestor, boolean checkParents) {
	Class result = null;
	synchronized (this) {
		// check the cache.  If we find something, check to see if its visible.
		// If it is, return it.  If not, return null if we are not checking parents.  There is
		// no point in looking in self as the class was already in the cache.
		result = findLoadedClass(name);
		if (result != null) {
			result = checkClassVisibility(result, requestor, true);
			if (result != null || !checkParents)
				return result;
		}

		// if it wasn't in the cache or was not visible, check the parents (if requested)
		if (checkParents) {
			result = findClassParents(name, resolve);
			if (result != null)
				return result;
		}

		// if activation is not going to be required, try the load here.  This is
		// a short circuit so we don't fall through to the other sync block and do
		// more work.  Note that the order of the tests is important, since 
		//descriptor.isPluginActivated() blocks while activation in progress,
		//thus creating a potential deadlock situation.
		if (pluginActivationInProgress || descriptor.isPluginActivated()) {
			try {
				result = super.findClass(name);
			} catch (ClassNotFoundException e) {
				return null;
			}
			return checkClassVisibility(result, requestor, false);
		}
		// Check to see if we would find the class if we looked.  If so,
		// activation is required.  If not, don't bother, just return null
		if (shouldLookForClass(name))
			// leave a dropping to discourage others from trying to do activation.
			// This flag will be cleared once activation is complete.
			pluginActivationInProgress = true;
		else
			return null;
	}

	// If we will find the class and the plugin is not yet activated, go ahead and do it now.
	// Note that this MUST be done outside the sync block to avoid deadlock if
	// plugin activation forks threads etc.
	activatePlugin(name);

	// By now the plugin is activated and we need to sycn and retry the
	// class load.
	synchronized (this) {
		result = findLoadedClass(name);
		if (result != null)
			return checkClassVisibility(result, requestor, true);

		// do search/load in this class loader
		try {
			result = super.findClass(name);
			// If the class is loaded in this classloader register it with
			// the hot swap support.  Need to do this regardless of visibility
			// because the class was actually loaded.
			if (result == null)
				return null;
			return checkClassVisibility(result, requestor, false);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
}
public PluginDescriptor getPluginDescriptor() {
	return descriptor;
}
/**
 * Returns the id to use to lookup class prefixes for this loader
 */
public String getPrefixId() {
	return descriptor.getUniqueIdentifier();
 }
 /**
  * Initializes imported classloaders with the classloaders of all resolved pre-
  * requisites.
  */
public void initializeImportedLoaders() {
	PluginDescriptor desc = getPluginDescriptor();
	// prereqs will contain all *resolved* pre-requisites (except boot and runtime)
	IPluginPrerequisite[] prereqs = desc.getPluginResolvedPrerequisites();
	if (prereqs.length == 0)
		return;
	PluginRegistry registry = desc.getPluginRegistry();
	DelegateLoader[] importedLoaders = new DelegateLoader[prereqs.length];
	for (int i = 0; i < prereqs.length; i++) {
		String prereqId = prereqs[i].getUniqueIdentifier();
		desc = (PluginDescriptor) registry.getPluginDescriptor(prereqId, prereqs[i].getResolvedVersionIdentifier());
		importedLoaders[i] = new DelegateLoader((DelegatingURLClassLoader) desc.getPluginClassLoader(true), prereqs[i].isExported());
	}
	setImportedLoaders(importedLoaders);
}
public void setPluginDescriptor(PluginDescriptor value) {
	descriptor = value;
}
protected boolean shouldLookForClass(String name) {
	// check if requested class is in loader search path
	// Note: this check is suboptimal. It results in additional
	// loader overhead until the plugin is activated. The reason
	// the check is performed here is because
	// (1) plug-in activation needs to be performed prior to
	//     the requested class load (done in findClass(String))
	// (2) the check cannot be added to the "right" spot inside private
	//     implementation of URLClassLoader
	String resource = name.replace('.', '/');
	if (findClassResource(resource + ".class") == null) //$NON-NLS-1$
		return false;

	// check if plugin is permanently deactivated
	if (descriptor.isPluginDeactivated()) {
		String message = Policy.bind("plugin.deactivatedLoad", name, descriptor.getUniqueIdentifier()); //$NON-NLS-1$
		throw new DelegatingLoaderException(message);
	}
	return true;
}
protected String getClassloaderId() {
	return descriptor.getId();
}
}
