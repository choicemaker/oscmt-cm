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
package org.eclipse.ant.core;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;

import org.eclipse.ant.internal.core.IAntCoreConstants;
import org.eclipse.ant.internal.core.InternalCoreAntMessages;
import org.eclipse.core.boot.BootLoader;
import org.eclipse.core.internal.plugins.PluginClassLoader;
import org.eclipse.core.runtime.*;


/**
 * Represents the Ant Core plug-in's preferences providing utilities for
 * extracting, changing and updating the underlying preferences.
 * @since 2.1
 */
public class AntCorePreferences implements org.eclipse.core.runtime.Preferences.IPropertyChangeListener {

	protected List defaultTasks;
	protected List defaultTypes;
	protected List extraClasspathURLs;
	protected URL[] defaultAntURLs;
	
	protected Task[] customTasks;
	protected Type[] customTypes;
	protected URL[] antURLs;
	protected URL[] customURLs;
	protected Property[] customProperties;
	protected String[] customPropertyFiles;
	
	protected List pluginClassLoaders;
	
	private ClassLoader[] orderedPluginClassLoaders;
	
	private String antHome;
	
	private boolean runningHeadless= false;

	protected AntCorePreferences(List defaultTasks, List defaultExtraClasspath, List defaultTypes, boolean headless) {
		runningHeadless= headless;
		initializePluginClassLoaders();
		extraClasspathURLs = new ArrayList(20);
		this.defaultTasks = computeDefaultTasks(defaultTasks);
		this.defaultTypes = computeDefaultTypes(defaultTypes);
		computeDefaultExtraClasspathEntries(defaultExtraClasspath);
		restoreCustomObjects();
	}
	
	/**
	 * When a preference changes, update the in-memory cache of the preference.
	 * @see org.eclipse.core.runtime.Preferences.IPropertyChangeListener#propertyChange(org.eclipse.core.runtime.Preferences.PropertyChangeEvent)
	 */
	public void propertyChange(Preferences.PropertyChangeEvent event) {
		Preferences prefs = AntCorePlugin.getPlugin().getPluginPreferences();
		String property= event.getProperty();
		if (property.equals(IAntCoreConstants.PREFERENCE_TASKS) || property.startsWith(IAntCoreConstants.PREFIX_TASK)) {
			restoreTasks(prefs);
		} else if (property.equals(IAntCoreConstants.PREFERENCE_TYPES) || property.startsWith(IAntCoreConstants.PREFIX_TYPE)) {
			restoreTypes(prefs);
		} else if (property.equals(IAntCoreConstants.PREFERENCE_ANT_URLS)) {
			restoreAntURLs(prefs);
		} else if (property.equals(IAntCoreConstants.PREFERENCE_URLS)) {
			restoreCustomURLs(prefs);
		} else if (property.equals(IAntCoreConstants.PREFERENCE_ANT_HOME)) {
			restoreAntHome(prefs);
		} else if (property.equals(IAntCoreConstants.PREFERENCE_PROPERTIES) || property.startsWith(IAntCoreConstants.PREFIX_PROPERTY)) {
			restoreCustomProperties(prefs);
		} else if (property.equals(IAntCoreConstants.PREFERENCE_PROPERTY_FILES)) {
			restoreCustomPropertyFiles(prefs);
		}
	}

	/**
	 * Restores the in-memory model of the preferences from the preference store
	 */
	private void restoreCustomObjects() {
		Preferences prefs = AntCorePlugin.getPlugin().getPluginPreferences();
		restoreTasks(prefs);
		restoreTypes(prefs);
		restoreAntURLs(prefs);
		restoreCustomURLs(prefs);
		restoreAntHome(prefs);
		restoreCustomProperties(prefs);
		restoreCustomPropertyFiles(prefs);
		prefs.addPropertyChangeListener(this);
	}
	
	private void restoreTasks(Preferences prefs) {
		 String tasks = prefs.getString(IAntCoreConstants.PREFERENCE_TASKS);
		 if (tasks.equals("")) { //$NON-NLS-1$
			 customTasks = new Task[0];
		 } else {
			 customTasks = extractTasks(prefs, getArrayFromString(tasks));
		 }
	}
	
	private void restoreTypes(Preferences prefs) {
		String types = prefs.getString(IAntCoreConstants.PREFERENCE_TYPES);
		if (types.equals("")) {//$NON-NLS-1$
			customTypes = new Type[0];
		} else {
			customTypes = extractTypes(prefs, getArrayFromString(types));
		}
	}
	
	private void restoreAntURLs(Preferences prefs) {
		String urls = prefs.getString(IAntCoreConstants.PREFERENCE_ANT_URLS);
		if (urls.equals("")) {//$NON-NLS-1$
			antURLs = getDefaultAntURLs();
		} else {
			antURLs = extractURLs(getArrayFromString(urls));
		}
	}
	
	private void restoreCustomURLs(Preferences prefs) {
		String urls = prefs.getString(IAntCoreConstants.PREFERENCE_URLS);
		if (urls.equals("")) {//$NON-NLS-1$
			customURLs = new URL[0];
		} else {
			customURLs = extractURLs(getArrayFromString(urls));
		}
	}
	
	private void restoreAntHome(Preferences prefs) {
		antHome= prefs.getString(IAntCoreConstants.PREFERENCE_ANT_HOME);
	}
	
	private void restoreCustomProperties(Preferences prefs) {
		String properties = prefs.getString(IAntCoreConstants.PREFERENCE_PROPERTIES);
		if (properties.equals("")) {//$NON-NLS-1$
			customProperties = new Property[0];
		} else {
			customProperties = extractProperties(prefs, getArrayFromString(properties));
		}
	}
	
	private void restoreCustomPropertyFiles(Preferences prefs) {
		String propertyFiles= prefs.getString(IAntCoreConstants.PREFERENCE_PROPERTY_FILES);
		if (propertyFiles.equals("")) { //$NON-NLS-1$
			customPropertyFiles= new String[0];
		} else {
			customPropertyFiles= getArrayFromString(propertyFiles);
		}
	}

	protected Task[] extractTasks(Preferences prefs, String[] tasks) {
		List result = new ArrayList(tasks.length);
		for (int i = 0; i < tasks.length; i++) {
			try {
				String taskName = tasks[i];
				String[] values = getArrayFromString(prefs.getString(IAntCoreConstants.PREFIX_TASK + taskName));
				if (values.length < 2) {
					continue;
				}
				Task task = new Task();
				task.setTaskName(taskName);
				task.setClassName(values[0]);
				task.setLibrary(new URL(values[1]));
				result.add(task);
			} catch (MalformedURLException e) {
				// if the URL does not have a valid format, just log and ignore the exception
				IStatus status = new Status(IStatus.ERROR, AntCorePlugin.PI_ANTCORE, AntCorePlugin.ERROR_MALFORMED_URL, InternalCoreAntMessages.getString("AntCorePreferences.Malformed_URL._1"), e); //$NON-NLS-1$
				AntCorePlugin.getPlugin().getLog().log(status);
			}
		}
		return (Task[]) result.toArray(new Task[result.size()]);
	}

	protected Type[] extractTypes(Preferences prefs, String[] types) {
		List result = new ArrayList(types.length);
		for (int i = 0; i < types.length; i++) {
			try {
				String typeName = types[i];
				String[] values = getArrayFromString(prefs.getString(IAntCoreConstants.PREFIX_TYPE + typeName));
				if (values.length < 2) {
					continue;
				}
				Type type = new Type();
				type.setTypeName(typeName);
				type.setClassName(values[0]);
				type.setLibrary(new URL(values[1]));
				result.add(type);
			} catch (MalformedURLException e) {
				// if the URL does not have a valid format, just log and ignore the exception
				IStatus status = new Status(IStatus.ERROR, AntCorePlugin.PI_ANTCORE, AntCorePlugin.ERROR_MALFORMED_URL, InternalCoreAntMessages.getString("AntCorePreferences.Malformed_URL._1"), e);  //$NON-NLS-1$
				AntCorePlugin.getPlugin().getLog().log(status);
			}
		}
		return (Type[]) result.toArray(new Type[result.size()]);
	}
	
	protected Property[] extractProperties(Preferences prefs, String[] properties) {
		Property[] result = new Property[properties.length];
		for (int i = 0; i < properties.length; i++) {
			String propertyName = properties[i];
			String[] values = getArrayFromString(prefs.getString(IAntCoreConstants.PREFIX_PROPERTY + propertyName));
			if (values.length < 1) {
				continue;
			}
			Property property = new Property();
			property.setName(propertyName);
			property.setValue(values[0]);
			result[i]= property;
		}
		return result;
	}

	protected URL[] extractURLs(String[] urls) {
		List result = new ArrayList(urls.length);
		for (int i = 0; i < urls.length; i++) {
			try {
				result.add(new URL(urls[i]));
			} catch (MalformedURLException e) {
				// if the URL does not have a valid format, just log and ignore the exception
				IStatus status = new Status(IStatus.ERROR, AntCorePlugin.PI_ANTCORE, AntCorePlugin.ERROR_MALFORMED_URL, InternalCoreAntMessages.getString("AntCorePreferences.Malformed_URL._1"), e);  //$NON-NLS-1$
				AntCorePlugin.getPlugin().getLog().log(status);
			}
		}
		return (URL[]) result.toArray(new URL[result.size()]);
	}

	/**
	 * Returns the array of URLs that is the default set of URLs defining
	 * the Ant classpath.
	 * The Xerces JARs are included when the plugin classloader for org.eclipse.ant.core
	 * is set as one of the plugin classloaders of the AntClassLoader.
	 * 
	 * Ant running through the command line tries to find tools.jar to help the
	 * user. Try emulating the same behaviour here.
	 *
	 * @return the default set of URLs defining the Ant classpath
	 */
	public URL[] getDefaultAntURLs() {
		if (defaultAntURLs == null) {
			List result = new ArrayList(3);
			Plugin antPlugin= Platform.getPlugin("org.apache.ant"); //$NON-NLS-1$
			if (antPlugin != null) {
				IPluginDescriptor descriptor = antPlugin.getDescriptor(); 
				addLibraries(descriptor, result);
			}
			
			URL toolsURL= getToolsJarURL();
			if (toolsURL != null) {
				result.add(toolsURL);
			}
			defaultAntURLs= (URL[]) result.toArray(new URL[result.size()]);
		}
		return defaultAntURLs;
	}
	
	/**
	 * Returns the array of URLs that is the set of URLs defining the Ant
	 * classpath.
	 * 
	 * @return the set of URLs defining the Ant classpath
	 */
	public URL[] getAntURLs() {
		return antURLs;
	}

	protected List computeDefaultTasks(List tasks) {
		List result = new ArrayList(tasks.size());
		for (Iterator iterator = tasks.iterator(); iterator.hasNext();) {
			IConfigurationElement element = (IConfigurationElement) iterator.next();
			if (runningHeadless) {
				String headless = element.getAttribute(AntCorePlugin.HEADLESS);
				if (headless != null) {
					boolean headlessType= Boolean.valueOf(headless).booleanValue();
					if (!headlessType) {
						continue;
					}
				}
			}
			Task task = new Task();
			task.setIsDefault(true);
			task.setTaskName(element.getAttribute(AntCorePlugin.NAME));
			task.setClassName(element.getAttribute(AntCorePlugin.CLASS));
			String library = element.getAttribute(AntCorePlugin.LIBRARY);
			if (library == null) {
				IStatus status = new Status(IStatus.ERROR, AntCorePlugin.PI_ANTCORE, AntCorePlugin.ERROR_LIBRARY_NOT_SPECIFIED, MessageFormat.format(InternalCoreAntMessages.getString("AntCorePreferences.Library_not_specified_for__{0}_4"), new String[]{task.getTaskName()}), null); //$NON-NLS-1$
				AntCorePlugin.getPlugin().getLog().log(status);
				continue;
			}
			
			IPluginDescriptor descriptor = element.getDeclaringExtension().getDeclaringPluginDescriptor();
			try {
				URL url = Platform.asLocalURL(new URL(descriptor.getInstallURL(), library));
				if (new File(url.getPath()).exists()) {
					if (!extraClasspathURLs.contains(url)) {
						extraClasspathURLs.add(url);
					}
					result.add(task);
					addPluginClassLoader(descriptor.getPluginClassLoader());
					task.setLibrary(url);
				} else {
					//task specifies a library that does not exist
					IStatus status = new Status(IStatus.ERROR, AntCorePlugin.PI_ANTCORE, AntCorePlugin.ERROR_LIBRARY_NOT_SPECIFIED, MessageFormat.format(InternalCoreAntMessages.getString("AntCorePreferences.No_library_for_task"), new String[]{url.toExternalForm(), descriptor.getLabel()}), null); //$NON-NLS-1$
					AntCorePlugin.getPlugin().getLog().log(status);
					continue;
				}
			} catch (Exception e) {
				// if the URL does not have a valid format, just log and ignore the exception
				IStatus status = new Status(IStatus.ERROR, AntCorePlugin.PI_ANTCORE, AntCorePlugin.ERROR_MALFORMED_URL, InternalCoreAntMessages.getString("AntCorePreferences.Malformed_URL._1"), e); //$NON-NLS-1$
				AntCorePlugin.getPlugin().getLog().log(status);
				continue;
			}
		}
		return result;
	}

	protected List computeDefaultTypes(List types) {
		List result = new ArrayList(types.size());
		for (Iterator iterator = types.iterator(); iterator.hasNext();) {
			IConfigurationElement element = (IConfigurationElement) iterator.next();
			if (runningHeadless) {
				String headless = element.getAttribute(AntCorePlugin.HEADLESS);
				if (headless != null) {
					boolean headlessTask= Boolean.valueOf(headless).booleanValue();
					if (!headlessTask) {
						continue;
					}
				}
			}
			Type type = new Type();
			type.setIsDefault(true);
			type.setTypeName(element.getAttribute(AntCorePlugin.NAME));
			type.setClassName(element.getAttribute(AntCorePlugin.CLASS));
			String library = element.getAttribute(AntCorePlugin.LIBRARY);
			if (library == null) {
				IStatus status = new Status(IStatus.ERROR, AntCorePlugin.PI_ANTCORE, AntCorePlugin.ERROR_LIBRARY_NOT_SPECIFIED, MessageFormat.format(InternalCoreAntMessages.getString("AntCorePreferences.Library_not_specified_for__{0}_4"), new String[]{type.getTypeName()}), null); //$NON-NLS-1$
				AntCorePlugin.getPlugin().getLog().log(status);
				continue;
			}
			IPluginDescriptor descriptor = element.getDeclaringExtension().getDeclaringPluginDescriptor();
			try {
				URL url = Platform.asLocalURL(new URL(descriptor.getInstallURL(), library));
				if (new File(url.getPath()).exists()) {
					if (!extraClasspathURLs.contains(url)) {
						extraClasspathURLs.add(url);
					}
					result.add(type);
					addPluginClassLoader(descriptor.getPluginClassLoader());
					type.setLibrary(url);
				} else {
					//type specifies a library that does not exist
					IStatus status = new Status(IStatus.ERROR, AntCorePlugin.PI_ANTCORE, AntCorePlugin.ERROR_LIBRARY_NOT_SPECIFIED, MessageFormat.format(InternalCoreAntMessages.getString("AntCorePreferences.No_library_for_type"), new String[]{url.toExternalForm(), descriptor.getLabel()}), null); //$NON-NLS-1$
					AntCorePlugin.getPlugin().getLog().log(status);
					continue;
				}
			} catch (Exception e) {
				// if the URL does not have a valid format, just log and ignore the exception
				IStatus status = new Status(IStatus.ERROR, AntCorePlugin.PI_ANTCORE, AntCorePlugin.ERROR_MALFORMED_URL, InternalCoreAntMessages.getString("AntCorePreferences.Malformed_URL._1"), e); //$NON-NLS-1$
				AntCorePlugin.getPlugin().getLog().log(status);
				continue;
			}
		}
		return result;
	}

	/**
	 * Computes the extra classpath entries defined plugins and fragments.
	 */
	protected void computeDefaultExtraClasspathEntries(List entries) {
		for (Iterator iterator = entries.iterator(); iterator.hasNext();) {
			IConfigurationElement element = (IConfigurationElement) iterator.next();
			if (runningHeadless) {
				String headless = element.getAttribute(AntCorePlugin.HEADLESS);
				if (headless != null) {
					boolean headlessEntry= Boolean.valueOf(headless).booleanValue();
					if (!headlessEntry) {
						continue;
					}
				}
			}
			String library = (String) element.getAttribute(AntCorePlugin.LIBRARY);
			IPluginDescriptor descriptor = element.getDeclaringExtension().getDeclaringPluginDescriptor();
			try {
				URL url = Platform.asLocalURL(new URL(descriptor.getInstallURL(), library));
				
				if (new File(url.getPath()).exists()) {
					if (!extraClasspathURLs.contains(url)) {
						extraClasspathURLs.add(url);
					}
					addPluginClassLoader(descriptor.getPluginClassLoader());
				} else {
					//extra classpath entry that does not exist
					IStatus status = new Status(IStatus.ERROR, AntCorePlugin.PI_ANTCORE, AntCorePlugin.ERROR_LIBRARY_NOT_SPECIFIED, MessageFormat.format(InternalCoreAntMessages.getString("AntCorePreferences.No_library_for_extraClasspathEntry"), new String[]{url.toExternalForm(), descriptor.getLabel()}), null); //$NON-NLS-1$
					AntCorePlugin.getPlugin().getLog().log(status);
					continue;
				}
			} catch (Exception e) {
				// if the URL does not have a valid format, just log and ignore the exception
				IStatus status = new Status(IStatus.ERROR, AntCorePlugin.PI_ANTCORE, AntCorePlugin.ERROR_MALFORMED_URL, InternalCoreAntMessages.getString("AntCorePreferences.Malformed_URL._1"), e); //$NON-NLS-1$
				AntCorePlugin.getPlugin().getLog().log(status);
				continue;
			}
		}
	}

	/**
	 * Returns the URL for the tools.jar associated with the "java.home"
	 * location. May return <code>null</code> if no tools.jar is found (e.g. "java.home"
	 * points to a JRE install).
	 * 
	 * @return URL tools.jar URL or <code>null</code>
	 */
	public URL getToolsJarURL() {
		IPath path = new Path(System.getProperty("java.home")); //$NON-NLS-1$
		if (path.lastSegment().equalsIgnoreCase("jre")) { //$NON-NLS-1$
			path = path.removeLastSegments(1);
		}
		path = path.append("lib").append("tools.jar"); //$NON-NLS-1$ //$NON-NLS-2$
		File tools = path.toFile();
		if (!tools.exists()) {
			//attempt to find in the older 1.1.* 
			path= path.removeLastSegments(1);
			path= path.append("classes.zip"); //$NON-NLS-1$
			tools = path.toFile();
			if (!tools.exists()) {
				return null;
			}
		}
		try {
			return new URL("file:" + tools.getAbsolutePath()); //$NON-NLS-1$
		} catch (MalformedURLException e) {
			// if the URL does not have a valid format, just log and ignore the exception
			IStatus status = new Status(IStatus.ERROR, AntCorePlugin.PI_ANTCORE, AntCorePlugin.ERROR_MALFORMED_URL, InternalCoreAntMessages.getString("AntCorePreferences.Malformed_URL._1"), e);  //$NON-NLS-1$
			AntCorePlugin.getPlugin().getLog().log(status);
		}
		return null;
	}

	protected void addLibraries(IPluginDescriptor source, List destination) {
		URL root = source.getInstallURL();
		ILibrary[] libraries = source.getRuntimeLibraries();
		for (int i = 0; i < libraries.length; i++) {
			try {
				URL url = new URL(root, libraries[i].getPath().toString());
				destination.add(Platform.asLocalURL(url));
			} catch (Exception e) {
				// if the URL does not have a valid format, just log and ignore the exception
				IStatus status = new Status(IStatus.ERROR, AntCorePlugin.PI_ANTCORE, AntCorePlugin.ERROR_MALFORMED_URL, InternalCoreAntMessages.getString("AntCorePreferences.Malformed_URL._1"), e);  //$NON-NLS-1$
				AntCorePlugin.getPlugin().getLog().log(status);
				continue;
			}
		}
	}

	protected void addPluginClassLoader(ClassLoader loader) {
		if (!pluginClassLoaders.contains(loader)) {
			pluginClassLoaders.add(loader);
		}
	}

	/**
	 * Returns the list of urls added to the classpath by the extra classpath
	 * entries extension point.
	 * 
	 * @return the list of extra classpath URLs
	 */
	public URL[] getExtraClasspathURLs() {
		return (URL[])extraClasspathURLs.toArray(new URL[extraClasspathURLs.size()]);
	}
	
	/**
	 * Returns the entire set of URLs that define the Ant runtime classpath.
	 * Includes the Ant URLs, the custom URLs and extra classpath URLs.
	 * 
	 * @return the entire runtime classpath of URLs
	 */
	public URL[] getURLs() {
		List result = new ArrayList(20);
		if (antURLs != null) {
			result.addAll(Arrays.asList(antURLs));
		}
		if (customURLs != null && customURLs.length > 0) {
			result.addAll(Arrays.asList(customURLs));
		}
		if (extraClasspathURLs != null) {
			result.addAll(extraClasspathURLs);
		}
		return (URL[]) result.toArray(new URL[result.size()]);
	}

	protected ClassLoader[] getPluginClassLoaders() {
		if (orderedPluginClassLoaders == null) {
			Iterator classLoaders= pluginClassLoaders.iterator();
			Map idToLoader= new HashMap(pluginClassLoaders.size());
			IPluginDescriptor[] descriptors= new IPluginDescriptor[pluginClassLoaders.size()];
			int i= 0;
			while (classLoaders.hasNext()) {
				PluginClassLoader loader = (PluginClassLoader) classLoaders.next();
				IPluginDescriptor descriptor= loader.getPluginDescriptor();
				idToLoader.put(descriptor.getUniqueIdentifier(), loader);
				descriptors[i]= descriptor;
				i++;
			}
			String[] ids= computePrerequisiteOrderPlugins(descriptors);
			orderedPluginClassLoaders= new ClassLoader[pluginClassLoaders.size()];
			for (int j = 0; j < ids.length; j++) {
				String id = ids[j];
				orderedPluginClassLoaders[j]= (ClassLoader)idToLoader.get(id);
			}
		}
		return orderedPluginClassLoaders;
	}

	/**
	 * Copied from org.eclipse.pde.internal.build.Utils
	 */
	private String[] computePrerequisiteOrderPlugins(IPluginDescriptor[] plugins) {
		List prereqs = new ArrayList(9);
		Set pluginList = new HashSet(plugins.length);
		for (int i = 0; i < plugins.length; i++) {
			pluginList.add(plugins[i].getUniqueIdentifier());
		}
		
		// create a collection of directed edges from plugin to prereq
		for (int i = 0; i < plugins.length; i++) {
			boolean boot = false;
			boolean runtime = false;
			boolean found = false;
			IPluginPrerequisite[] prereqList = plugins[i].getPluginPrerequisites();
			if (prereqList != null) {
				for (int j = 0; j < prereqList.length; j++) {
					// ensure that we only include values from the original set.
					String prereq = prereqList[j].getUniqueIdentifier();
					boot = boot || prereq.equals(BootLoader.PI_BOOT);
					runtime = runtime || prereq.equals(Platform.PI_RUNTIME);
					if (pluginList.contains(prereq)) {
						found = true;
						prereqs.add(new String[] { plugins[i].getUniqueIdentifier(), prereq });
					}
				}
			}

			// if we didn't find any prereqs for this plugin, add a null prereq
			// to ensure the value is in the output	
			if (!found) {
				prereqs.add(new String[] { plugins[i].getUniqueIdentifier(), null });
			}

			// if we didn't find the boot or runtime plugins as prereqs and they are in the list
			// of plugins to build, add prereq relations for them.  This is required since the 
			// boot and runtime are implicitly added to a plugin's requires list by the platform runtime.
			// Note that we should skip the xerces plugin as this would cause a circularity.
			if (plugins[i].getUniqueIdentifier().equals("org.apache.xerces")) //$NON-NLS-1$
				continue;
			if (!boot && pluginList.contains(BootLoader.PI_BOOT) && !plugins[i].getUniqueIdentifier().equals(BootLoader.PI_BOOT))
				prereqs.add(new String[] { plugins[i].getUniqueIdentifier(), BootLoader.PI_BOOT });
			if (!runtime && pluginList.contains(Platform.PI_RUNTIME) && !plugins[i].getUniqueIdentifier().equals(Platform.PI_RUNTIME) && !plugins[i].getUniqueIdentifier().equals(BootLoader.PI_BOOT))
				prereqs.add(new String[] { plugins[i].getUniqueIdentifier(), Platform.PI_RUNTIME });
		}

		// do a topological sort, insert the fragments into the sorted elements
		String[][] prereqArray = (String[][]) prereqs.toArray(new String[prereqs.size()][]);
		return computeNodeOrder(prereqArray);
	}
	
	/**
	 * Copied from org.eclipse.pde.internal.build.Utils
	 */
	private String[] computeNodeOrder(String[][] specs) {
		Map counts = computeCounts(specs);
		List nodes = new ArrayList(counts.size());
		while (!counts.isEmpty()) {
			List roots = findRootNodes(counts);
			if (roots.isEmpty())
				break;
			for (Iterator i = roots.iterator(); i.hasNext();)
				counts.remove(i.next());
			nodes.addAll(roots);
			removeArcs(specs, roots, counts);
		}
		String[] result = new String[nodes.size()];
		nodes.toArray(result);

		return result;
	}
	
	/**
	 * Copied from org.eclipse.pde.internal.build.Utils
	 */
	private void removeArcs(String[][] mappings, List roots, Map counts) {
		for (Iterator j = roots.iterator(); j.hasNext();) {
			String root = (String) j.next();
			for (int i = 0; i < mappings.length; i++) {
				if (root.equals(mappings[i][1])) {
					String input = mappings[i][0];
					Integer count = (Integer) counts.get(input);
					if (count != null)
						counts.put(input, new Integer(count.intValue() - 1));
				}
			}
		}
	}
	
	/**
	 * Copied from org.eclipse.pde.internal.build.Utils
	 */
	private List findRootNodes(Map counts) {
		List result = new ArrayList(5);
		for (Iterator i = counts.keySet().iterator(); i.hasNext();) {
			String node = (String) i.next();
			int count = ((Integer) counts.get(node)).intValue();
			if (count == 0)
				result.add(node);
		}
		return result;
	}
	
	/**
	 * Copied from org.eclipse.pde.internal.build.Utils
	 */
	private Map computeCounts(String[][] mappings) {
		Map counts = new HashMap(5);
		for (int i = 0; i < mappings.length; i++) {
			String from = mappings[i][0];
			Integer fromCount = (Integer) counts.get(from);
			String to = mappings[i][1];
			if (to == null)
				counts.put(from, new Integer(0));
			else {
				if (((Integer) counts.get(to)) == null)
					counts.put(to, new Integer(0));
				fromCount = fromCount == null ? new Integer(1) : new Integer(fromCount.intValue() + 1);
				counts.put(from, fromCount);
			}
		}
		return counts;
	}
	
	private void initializePluginClassLoaders() {
		pluginClassLoaders = new ArrayList(10);
		// ant.core should always be present (provides access to Xerces as well)
		pluginClassLoaders.add(Platform.getPlugin(AntCorePlugin.PI_ANTCORE).getDescriptor().getPluginClassLoader());
	}

	/**
	 * Returns the default and custom tasks.
	 * 
	 * @return the list of default and custom tasks.
	 */
	public List getTasks() {
		List result = new ArrayList(10);
		if (defaultTasks != null && !defaultTasks.isEmpty()) {
			result.addAll(defaultTasks);
		}
		if (customTasks != null && customTasks.length != 0) {
			result.addAll(Arrays.asList(customTasks));
		}
		return result;
	}

	/**
	 * Returns the user defined custom tasks
	 * @return the user defined tasks
	 */
	public Task[] getCustomTasks() {
		return customTasks;
	}

	/**
	 * Returns the user defined custom types
	 * @return the user defined types
	 */
	public Type[] getCustomTypes() {
		return customTypes;
	}

	/**
	 * Returns the custom user properties specified for Ant builds.
	 * 
	 * @return the properties defined for Ant builds.
	 */
	public Property[] getCustomProperties() {
		return customProperties;
	}
	
	/**
	 * Returns the custom property files specified for Ant builds.
	 * 
	 * @return the property files defined for Ant builds.
	 */
	public String[] getCustomPropertyFiles() {
		return customPropertyFiles;
	}
	
	/**
	 * Returns the custom URLs specified for the Ant classpath
	 * 
	 * @return the urls defining the Ant classpath
	 */
	public URL[] getCustomURLs() {
		return customURLs;
	}

	/**
	 * Sets the user defined custom tasks
	 * @param tasks
	 */
	public void setCustomTasks(Task[] tasks) {
		customTasks = tasks;
	}

	/**
	 * Sets the user defined custom types
	 * @param tasks
	 */
	public void setCustomTypes(Type[] types) {
		customTypes = types;
	}

	/**
	 * Sets the custom URLs specified for the Ant classpath.
	 * To commit the changes, updatePluginPreferences must be
	 * called.
	 * 
	 * @param the urls defining the Ant classpath
	 */
	public void setCustomURLs(URL[] urls) {
		customURLs = urls;
	}
	
	/**
	 * Sets the Ant URLs specified for the Ant classpath. To commit the changes,
	 * updatePluginPreferences must be called.
	 * 
	 * @param the urls defining the Ant classpath
	 */
	public void setAntURLs(URL[] urls) {
		antURLs = urls;
	}
	
	/**
	 * Sets the custom property files specified for Ant builds. To commit the
	 * changes, updatePluginPreferences must be called.
	 * 
	 * @param the absolute paths defining the property files to use.
	 */
	public void setCustomPropertyFiles(String[] paths) {
		customPropertyFiles = paths;
	}
	
	/**
	 * Sets the custom user properties specified for Ant builds. To commit the
	 * changes, updatePluginPreferences must be called.
	 * 
	 * @param the properties defining the Ant properties
	 */
	public void setCustomProperties(Property[] properties) {
		customProperties = properties;
	}

	/**
	 * Returns the default and custom types.
	 * 
	 * @return all of the defined types
	 */
	public List getTypes() {
		List result = new ArrayList(10);
		if (defaultTypes != null && !defaultTypes.isEmpty()) {
			result.addAll(defaultTypes);
		}
		if (customTypes != null && customTypes.length != 0) {
			result.addAll(Arrays.asList(customTypes));
		}
		return result;
	}
	
	/**
	 * Returns the default types defined via the type extension point
	 * 
	 * @return all of the default types
	 */
	public List getDefaultTypes() {
		List result = new ArrayList(10);
		if (defaultTypes != null && !defaultTypes.isEmpty()) {
			result.addAll(defaultTypes);
		}
		return result;
	}
	
	/**
	 * Returns the default tasks defined via the task extension point
	 * 
	 * @return all of the default tasks
	 */
	public List getDefaultTasks() {
		List result = new ArrayList(10);
		if (defaultTasks != null && !defaultTasks.isEmpty()) {
			result.addAll(defaultTasks);
		}
		return result;
	}

	/**
	 * Convert a list of tokens into an array using "," as the tokenizer.
	 */
	protected String[] getArrayFromString(String list) {
		String separator= ","; //$NON-NLS-1$
		if (list == null || list.trim().equals("")) { //$NON-NLS-1$
			return new String[0];
		}
		ArrayList result = new ArrayList();
		for (StringTokenizer tokens = new StringTokenizer(list, separator); tokens.hasMoreTokens();) {
			String token = tokens.nextToken().trim();
			if (!token.equals("")) { //$NON-NLS-1$
				result.add(token);
			}
		}
		return (String[]) result.toArray(new String[result.size()]);
	}

	/**
	 * Updates the underlying plugin preferences to the current state.
	 */
	public void updatePluginPreferences() {
		Preferences prefs = AntCorePlugin.getPlugin().getPluginPreferences();
		prefs.removePropertyChangeListener(this);
		updateTasks(prefs);
		updateTypes(prefs);
		updateAntURLs(prefs);
		updateURLs(prefs);
		updateProperties(prefs);
		updatePropertyFiles(prefs);
		AntCorePlugin.getPlugin().savePluginPreferences();
		prefs.addPropertyChangeListener(this);
	}

	protected void updateTasks(Preferences prefs) {
		if (customTasks.length == 0) {
			prefs.setValue(IAntCoreConstants.PREFERENCE_TASKS, ""); //$NON-NLS-1$
			return;
		}
		StringBuffer tasks = new StringBuffer();
		for (int i = 0; i < customTasks.length; i++) {
			tasks.append(customTasks[i].getTaskName());
			tasks.append(',');
			prefs.setValue(IAntCoreConstants.PREFIX_TASK + customTasks[i].getTaskName(), customTasks[i].getClassName() + "," + customTasks[i].getLibrary().toExternalForm()); //$NON-NLS-1$
		}
		prefs.setValue(IAntCoreConstants.PREFERENCE_TASKS, tasks.toString());
	}

	protected void updateTypes(Preferences prefs) {
		if (customTypes.length == 0) {
			prefs.setValue(IAntCoreConstants.PREFERENCE_TYPES, ""); //$NON-NLS-1$
			return;
		}
		StringBuffer types = new StringBuffer();
		for (int i = 0; i < customTypes.length; i++) {
			types.append(customTypes[i].getTypeName());
			types.append(',');
			prefs.setValue(IAntCoreConstants.PREFIX_TYPE + customTypes[i].getTypeName(), customTypes[i].getClassName() + "," + customTypes[i].getLibrary().toExternalForm()); //$NON-NLS-1$
		}
		prefs.setValue(IAntCoreConstants.PREFERENCE_TYPES, types.toString());
	}
	
	protected void updateProperties(Preferences prefs) {
		if (customProperties.length == 0) {
			prefs.setValue(IAntCoreConstants.PREFERENCE_PROPERTIES, ""); //$NON-NLS-1$
			return;
		}
		StringBuffer properties = new StringBuffer();
		for (int i = 0; i < customProperties.length; i++) {
			properties.append(customProperties[i].getName());
			properties.append(',');
			prefs.setValue(IAntCoreConstants.PREFIX_PROPERTY + customProperties[i].getName(), customProperties[i].getValue()); //$NON-NLS-1$
		}
		prefs.setValue(IAntCoreConstants.PREFERENCE_PROPERTIES, properties.toString());
	}

	protected void updateURLs(Preferences prefs) {
		StringBuffer urls = new StringBuffer();
		for (int i = 0; i < customURLs.length; i++) {
			urls.append(customURLs[i].toExternalForm());
			urls.append(',');
		}
		
		prefs.setValue(IAntCoreConstants.PREFERENCE_URLS, urls.toString());
		String prefAntHome= ""; //$NON-NLS-1$
		if (antHome != null) {
			prefAntHome= antHome;
		} 
		prefs.setValue(IAntCoreConstants.PREFERENCE_ANT_HOME, prefAntHome);
	}
	
	protected void updateAntURLs(Preferences prefs) {
		//see if the custom URLS are just the default URLS
		URL[] dcUrls= getDefaultAntURLs();
		boolean dflt= false;
		if (dcUrls.length == antURLs.length) {
			dflt= true;
			for (int i = 0; i < antURLs.length; i++) {
				if (!antURLs[i].equals(dcUrls[i])) {
					dflt= false;
					break;
				}
			}
		}
		if (dflt) {
			//always want to recalculate the default Ant urls
			//to pick up any changes in the default Ant classpath
			prefs.setValue(IAntCoreConstants.PREFERENCE_ANT_URLS, ""); //$NON-NLS-1$
			return;
		}
		StringBuffer urls = new StringBuffer();
		for (int i = 0; i < antURLs.length; i++) {
			urls.append(antURLs[i].toExternalForm());
			urls.append(',');
		}
		
		prefs.setValue(IAntCoreConstants.PREFERENCE_ANT_URLS, urls.toString());
	}
	
	protected void updatePropertyFiles(Preferences prefs) {
		StringBuffer files = new StringBuffer();
		for (int i = 0; i < customPropertyFiles.length; i++) {
			files.append(customPropertyFiles[i]);
			files.append(',');
		}
		
		prefs.setValue(IAntCoreConstants.PREFERENCE_PROPERTY_FILES, files.toString());
	}
	
	/**
	 * Sets the string that defines the Ant home set by the user.
	 * May be set to <code>null</code>.
	 * 
	 * @param the fully qualified path to Ant home
	 */
	public void setAntHome(String antHome) {
		this.antHome= antHome;
	}
	
	/**
	 * Returns the string that defines the Ant home set by the user.
	 * May be <code>null</code> if Ant home has not been set.
	 * 
	 * @return the fully qualified path to Ant home
	 */
	public String getAntHome() {
		return antHome;
	}
}
