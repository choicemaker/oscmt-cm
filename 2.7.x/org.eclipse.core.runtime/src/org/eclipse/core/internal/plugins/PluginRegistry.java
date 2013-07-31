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

import java.io.*;
import java.util.*;

import org.eclipse.core.internal.runtime.InternalPlatform;
import org.eclipse.core.internal.runtime.Policy;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.model.*;

public class PluginRegistry extends PluginRegistryModel implements IPluginRegistry {

	private static final String URL_PROTOCOL_FILE = "file"; //$NON-NLS-1$
	private static final String F_DEBUG_REGISTRY = ".debugregistry"; //$NON-NLS-1$

	// lifecycle events
	private static final int STARTUP = 0;
	private static final int SHUTDOWN = 1;
	
	// the registry keeps a reference to the cache reader for lazily loading extensions
	private RegistryCacheReader registryCacheReader;
	final private File registryCacheFile;
	
	//indicates if the registry cache needs to be rewritten
	private boolean cacheDirty = false;

public PluginRegistry() {
	this.registryCacheFile = InternalPlatform.getMetaArea().getRegistryPath().toFile();
}  
/**
 * Iterate over the plug-ins in this registry.  Plug-ins are visited in dependent order.  That is, 
 * a plug-in, A, which requires another plug-in, B, is visited before its dependents (i.e., A is 
 * visited before B).  
 */
public void accept(IPluginVisitor visitor, boolean activeOnly) {
	Map dependents = getDependentCounts(activeOnly);
	// keep iterating until all have been visited.
	while (!dependents.isEmpty()) {
		// loop over the dependents list.  For each entry, if there are no dependents, visit
		// the plugin and remove it from the list.  Make a copy of the keys so we don't end up
		// with concurrent accesses (since we are deleting the values as we go)
		PluginDescriptor[] keys = (PluginDescriptor[]) dependents.keySet().toArray(new PluginDescriptor[dependents.size()]);
		for (int i = 0; i < keys.length; i++) {
			PluginDescriptor descriptor = keys[i];
			Integer entry = (Integer) dependents.get(descriptor);
			if (entry != null && entry.intValue() <= 0) {
				visitor.visit(descriptor);
				dependents.remove(descriptor);
				// decrement the dependent count for all of the prerequisites.
				PluginPrerequisiteModel[] requires = descriptor.getRequires();
				int reqSize = (requires == null) ? 0 : requires.length;
				for (int j = 0; j < reqSize; j++) {
					String id = ((PluginPrerequisite) requires[j]).getUniqueIdentifier();
					PluginDescriptor prereq = (PluginDescriptor) getPlugin(id);
					Integer count = (Integer) dependents.get(prereq);
					if (count != null)
						dependents.put(prereq, new Integer(count.intValue() - 1));
				}
			}
		}
	}
}
public IConfigurationElement[] getConfigurationElementsFor(String uniqueId) {
	IExtensionPoint point = getExtensionPoint(uniqueId);
	if (point == null)
		return new IConfigurationElement[0];
	IConfigurationElement[] result = point.getConfigurationElements();
	return result == null ? new IConfigurationElement[0] : result;
}
public IConfigurationElement[] getConfigurationElementsFor(String pluginId, String pointId) {
	IExtensionPoint point = getExtensionPoint(pluginId, pointId);
	if (point == null)
		return new IConfigurationElement[0];
	IConfigurationElement[] result = point.getConfigurationElements();
	return result == null ? new IConfigurationElement[0] : result;
}
public IConfigurationElement[] getConfigurationElementsFor(String pluginId, String pointId, String extensionId) {
	IExtension extension = getExtension(pluginId, pointId, extensionId);
	if (extension == null)
		return new IConfigurationElement[0];
	IConfigurationElement[] result = extension.getConfigurationElements();
	return result == null ? new IConfigurationElement[0] : result;
}
/**
 * Returns a map of the dependent counts for all plug-ins.  The map's
 * keys are the plug-in descriptors and the values are an (<code>Integer</code>) count of
 * descriptors which depend on that plug-in.
 */
private Map getDependentCounts(boolean activeOnly) {
	IPluginDescriptor[] descriptors = getPluginDescriptors();
	int descSize = (descriptors == null) ? 0 : descriptors.length;
	Map dependents = new HashMap(5);
	// build a table of all dependent counts.  The table is keyed by descriptor and
	// the value the integer number of dependent plugins.
	for (int i = 0; i < descSize; i++) {
		if (activeOnly && !descriptors[i].isPluginActivated())
			continue;
		// ensure there is an entry for this descriptor (otherwise it will not be visited)
		Integer entry = (Integer) dependents.get(descriptors[i]);
		if (entry == null)
			dependents.put(descriptors[i], new Integer(0));
		PluginPrerequisiteModel[] requires = ((PluginDescriptor) descriptors[i]).getRequires();
		int reqSize = (requires == null ? 0 : requires.length);
		for (int j = 0; j < reqSize; j++) {
			String id = ((PluginPrerequisite) requires[j]).getUniqueIdentifier();
			PluginDescriptor prereq = (PluginDescriptor) getPlugin(id);
			if (prereq == null || activeOnly && !prereq.isPluginActivated())
				continue;
			entry = (Integer) dependents.get(prereq);
			entry = entry == null ? new Integer(1) : new Integer(entry.intValue() + 1);
			dependents.put(prereq, entry);
		}
	}
	return dependents;
}
public IExtension getExtension(String xptUniqueId, String extUniqueId) {

	int lastdot = xptUniqueId.lastIndexOf('.');
	if (lastdot == -1) return null;
	return getExtension(xptUniqueId.substring(0,lastdot), xptUniqueId.substring(lastdot+1), extUniqueId); 
	
}
public IExtension getExtension(String pluginId, String xptSimpleId, String extId) {

	IExtensionPoint xpt = getExtensionPoint(pluginId, xptSimpleId);
	if (xpt == null) return null;
	return xpt.getExtension(extId);
}
public IExtensionPoint getExtensionPoint(String  xptUniqueId) {

	int lastdot = xptUniqueId.lastIndexOf('.');
	if (lastdot == -1) return null;
	return getExtensionPoint(xptUniqueId.substring(0,lastdot), xptUniqueId.substring(lastdot+1)); 
}
public IExtensionPoint getExtensionPoint(String plugin, String xpt) {

	IPluginDescriptor pd = getPluginDescriptor(plugin);
	if (pd == null) return null;
	return pd.getExtensionPoint(xpt);
}
public IExtensionPoint[] getExtensionPoints() {
	PluginDescriptorModel[] list = getPlugins();
	if (list == null)
		return new IExtensionPoint[0];
	ArrayList result = new ArrayList();
	for (int i = 0; i < list.length; i++) {
		ExtensionPointModel[] pointList = list[i].getDeclaredExtensionPoints();
		if (pointList != null) {
			for (int j = 0; j < pointList.length; j++)
				result.add(pointList[j]);
		}
	}
	return (IExtensionPoint[]) result.toArray(new IExtensionPoint[result.size()]);
}
public IPluginDescriptor getPluginDescriptor(String plugin) {
	return (IPluginDescriptor) getPlugin(plugin);
}
public IPluginDescriptor getPluginDescriptor(String pluginId, PluginVersionIdentifier version) {
	PluginDescriptorModel[] plugins = getPlugins(pluginId);
	if (plugins == null || plugins.length == 0)
		return null;
	if (version == null)
		// Just return the first one in the list (random)
		return (IPluginDescriptor) plugins[0];
	for (int i = 0; i < plugins.length; i++) {
		IPluginDescriptor element = (IPluginDescriptor) plugins[i];
		if (element.getVersionIdentifier().equals(version))
			return element;
	}
	return null;
}
public IPluginDescriptor[] getPluginDescriptors() {
	PluginDescriptorModel[] plugins = getPlugins();
	if (plugins==null)
		return new IPluginDescriptor[0];
	IPluginDescriptor[] result = new IPluginDescriptor[plugins.length];
	for (int i = 0; i < plugins.length; i++)
		result[i] = (IPluginDescriptor) plugins[i];
	return result;
}
public IPluginDescriptor[] getPluginDescriptors(String plugin) {
	PluginDescriptorModel[] plugins = getPlugins(plugin);
	if (plugins==null)
		return new IPluginDescriptor[0];
	IPluginDescriptor[] result = new IPluginDescriptor[plugins.length];
	System.arraycopy(plugins, 0, result, 0, plugins.length);
	return result;
}
void logError(IStatus status) {
	InternalPlatform.getRuntimePlugin().getLog().log(status);
	if (InternalPlatform.DEBUG)
		System.out.println(status.getMessage());
}
public void saveRegistry() throws IOException {
	IPath path = InternalPlatform.getMetaArea().getRegistryPath();
	if (!cacheDirty && path.toFile().exists()) {
		// The registry cache file exists.  Assume it is fine and
		// we don't need to re-write it.
		return;
	}
	DataOutputStream output = null;
	//write to a temp file first, because the old registry cache file may still 
	//be in use for lazy loading of extensions
	java.io.File tempFile = new java.io.File(path.toOSString().concat(".tmp"));
	try {
		output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tempFile)));
	} catch (IOException ioe) {
		String message = Policy.bind("meta.unableToCreateCache"); //$NON-NLS-1$
		IStatus status = new Status(IStatus.ERROR, Platform.PI_RUNTIME, Platform.PLUGIN_ERROR, message, ioe);
		logError(status);
		return;
	}
	try {
		long start = System.currentTimeMillis();
		RegistryCacheWriter cacheWriter = new RegistryCacheWriter();
		cacheWriter.writePluginRegistry(this, output);
		output.close();
		//now move the temp file to the real location
		java.io.File realFile = path.toFile();
		realFile.delete();
		tempFile.renameTo(realFile);
		if (InternalPlatform.DEBUG)
			System.out.println("Wrote registry: " + (System.currentTimeMillis() - start) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$
	} finally {
		output.close();
	}
}
public void flushRegistry() {
	IPath path = InternalPlatform.getMetaArea().getRegistryPath();
	IPath tempPath = InternalPlatform.getMetaArea().getBackupFilePathFor(path);
	path.toFile().delete();
	tempPath.toFile().delete();
}
public void debugRegistry(String filename) {
	Path path = new Path(filename);
	path = (Path)path.makeAbsolute();
	if (!path.isValidPath(path.toOSString())) {
		String message = Policy.bind("meta.invalidRegDebug", path.toOSString()); //$NON-NLS-1$
		IStatus status = new Status(IStatus.ERROR, Platform.PI_RUNTIME, Platform.PLUGIN_ERROR, message, null);
		logError(status);
		return;
	}
		
	try {
		FileOutputStream fs = new FileOutputStream(path.toOSString());
		PrintWriter w = new PrintWriter(fs);
		try {
			RegistryWriter regWriter = new RegistryWriter();
			System.out.println(Policy.bind("meta.infoRegDebug", path.toOSString())); //$NON-NLS-1$
			regWriter.writePluginRegistry(this, w, 0);
			w.flush();
		} finally {
			w.close();
		}
	} catch (IOException ioe) {
		String message = Policy.bind("meta.unableToCreateRegDebug", path.toOSString()); //$NON-NLS-1$
		IStatus status = new Status(IStatus.ERROR, Platform.PI_RUNTIME, Platform.PLUGIN_ERROR, message, ioe);
		logError(status);
	}
}
public void flushDebugRegistry() {
	IPath path = InternalPlatform.getMetaArea().getLocation().append(F_DEBUG_REGISTRY);
	path.toFile().delete();
}
public void shutdown(IProgressMonitor progress) {
	shutdownPlugins();
	if (progress != null)
		progress.worked(1);
}
private void shutdownPlugins() {
	IPluginVisitor visitor = new IPluginVisitor() {
		public void visit(final IPluginDescriptor descriptor) {
			ISafeRunnable code = new ISafeRunnable() {
				public void run() throws Exception {
					if (!descriptor.isPluginActivated())
						return;
					try {
						Plugin plugin = descriptor.getPlugin();
						long time = 0L;
						if (InternalPlatform.DEBUG_SHUTDOWN) {
							time = System.currentTimeMillis();
							System.out.println("Shutting down plugin: " + plugin.getDescriptor().getUniqueIdentifier()); //$NON-NLS-1$
						}
						plugin.shutdown();
						if (InternalPlatform.DEBUG_SHUTDOWN) {
							time = System.currentTimeMillis() - time;
							System.out.println("Finished plugin shutdown for " + plugin.getDescriptor().getUniqueIdentifier() + " time: " + time + "ms"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						}
					} finally {
						((PluginDescriptor) descriptor).doPluginDeactivation();
					}
				}
				public void handleException(Throwable e) {
					// do nothing as the exception has already been logged.
				}
			};
			InternalPlatform.run(code);
		}
	};
	accept(visitor, true);
}
public void startup(IProgressMonitor progress) {}
/**
 * Loads an extension model's sub-elements.
 */
final void loadConfigurationElements(Extension extension) {
	DataInputStream input = null;	
	try {
		input = new DataInputStream(new BufferedInputStream(new FileInputStream(registryCacheFile)));
		input.skipBytes(extension.getSubElementsCacheOffset());					
		ConfigurationElementModel[] subElements = this.registryCacheReader.readSubElements(input, extension, InternalPlatform.DEBUG);
		extension.setSubElements(subElements);
		extension.setFullyLoaded(true);
		// to force the just loaded sub-elements to be read-only as their parent
		if (extension.isReadOnly())
			extension.markReadOnly();
	} catch (IOException e) {
		// an I/O failure would keep the extension elements unloaded
		//TODO: log this exception?
		if (InternalPlatform.DEBUG)
			e.printStackTrace();
	} catch (RegistryCacheReader.InvalidRegistryCacheException e) {
		// it has already been checked by RegistryCacheLazyReader - shouldn't happen
		if (InternalPlatform.DEBUG)
			e.printStackTrace();		
	} finally {
		try {
			if (input != null)
				input.close();
		} catch (IOException ioe) {
		}
	}
}
/**
 * Indicates that the registry cache is dirty and should be rewritten on shutdown.
 */
public void setCacheDirty() {
	cacheDirty = true;
}
void setCacheReader(RegistryCacheReader registryCacheReader) {
	this.registryCacheReader = registryCacheReader;
}
}