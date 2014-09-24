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
package org.eclipse.core.internal.boot;

import java.io.*;
import java.net.*;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.*;
import org.eclipse.core.boot.BootLoader;

public abstract class DelegatingURLClassLoader extends URLClassLoader {

	// table to hold the set of all class loader prefixes.  This is a temporary solution until
	// we can integrate this value into the plugin's library declaration.
	protected static Properties prefixTable = new Properties();
	static {
		initializePrefixTable();
	}

	// loader base
	protected URL base;
	
	// class name prefixes that this loader can load
	protected String[] prefixes = null;
	
	// delegation chain
	protected DelegateLoader[] imports = null;

	// extra resource class loader
	protected URLClassLoader resourceLoader = null;

	// filter table
	private Hashtable filterTable = new Hashtable();

	// development mode class path additions
	public static String devClassPath = null;
	
	// control class load tracing
	public static boolean DEBUG = false;
	public static boolean DEBUG_SHOW_CREATE = true;
	public static boolean DEBUG_SHOW_ACTIVATE = true;
	public static boolean DEBUG_SHOW_ACTIONS = true;
	public static boolean DEBUG_SHOW_SUCCESS = true;
	public static boolean DEBUG_SHOW_FAILURE = true;
	public static String[] DEBUG_FILTER_CLASS = new String[0];
	public static String[] DEBUG_FILTER_LOADER = new String[0];
	public static String[] DEBUG_FILTER_RESOURCE = new String[0];
	public static String[] DEBUG_FILTER_NATIVE = new String[0];
	public static boolean DEBUG_PROPERTIES = false;
	public static boolean DEBUG_PACKAGE_PREFIXES = false;
	public static boolean DEBUG_PACKAGE_PREFIXES_SUCCESS = false;
	public static boolean DEBUG_PACKAGE_PREFIXES_FAILURE = false;

	// flag and file name for the runtime spy
	public static boolean MONITOR_PLUGINS = false;
	public static boolean MONITOR_CLASSES = false;
	public static boolean MONITOR_BUNDLES = false;
	public static String TRACE_FILENAME = "runtime.traces"; //$NON-NLS-1$
	public static String TRACE_FILTERS = "trace.properties"; //$NON-NLS-1$
	public static boolean TRACE_CLASSES = false;
	public static boolean TRACE_PLUGINS = false;
	
	public static final String PLUGIN = "plugin"; //$NON-NLS-1$

	//private static String[] JAR_VARIANTS = buildJarVariants();
	private static String[] LIBRARY_VARIANTS = buildLibraryVariants();

	// DelegateLoader. Represents a single class loader this loader delegates to.
	protected static class DelegateLoader {

		private DelegatingURLClassLoader loader;
		private boolean isExported;

		public DelegateLoader(DelegatingURLClassLoader loader, boolean isExported) {
			this.loader = loader;
			this.isExported = isExported;
		}

		public Class loadClass(String name, DelegatingURLClassLoader current, DelegatingURLClassLoader requestor, Vector seen) {
			if (isExported || current == requestor)
				return loader.loadClass(name, false, requestor, seen, false);
			else
				return null;
		}

		public URL findResource(String name, DelegatingURLClassLoader current, DelegatingURLClassLoader requestor, Vector seen) {
			if (isExported || current == requestor)
				return loader.findResource(name, requestor, seen);
			else
				return null;
		}
		public Enumeration findResources(String name, DelegatingURLClassLoader current, DelegatingURLClassLoader requestor, Vector seen) {
			if (isExported || current == requestor)
				return loader.findResources(name, requestor, seen);
			else
				return null;
		}
	}

	// unchecked DelegatingLoaderException
	protected static class DelegatingLoaderException extends RuntimeException {
		private static final long serialVersionUID = 271L;
		Exception e = null;

		public DelegatingLoaderException() {
			super();
		}

		public DelegatingLoaderException(String message) {
			super(message);
		}

		public DelegatingLoaderException(String message, Exception e) {
			super(message);
			this.e = e;
		}

		public Throwable getException() {
			return e;
		}
		
		public void printStackTrace() {
			printStackTrace(System.err);
		}
		
		public void printStackTrace(PrintStream output) {
			synchronized (output) {
				if (e != null) {
					output.print("org.eclipse.core.internal.boot.DelegatingLoaderException: "); //$NON-NLS-1$
					e.printStackTrace(output);
				} else
					super.printStackTrace(output);
			}
		}
		
		public void printStackTrace(PrintWriter output) {
			synchronized (output) {
				if (e != null) {
					output.print("org.eclipse.core.internal.boot.DelegatingLoaderException: "); //$NON-NLS-1$
					e.printStackTrace(output);
				} else
					super.printStackTrace(output);
			}
		}
	}

//private static String[] buildJarVariants() {
//	ArrayList result = new ArrayList();
//	
//	result.add("ws/" + InternalBootLoader.getWS() + "/"); //$NON-NLS-1$ //$NON-NLS-2$
//	result.add("os/" + InternalBootLoader.getOS() + "/" + InternalBootLoader.getOSArch() + "/"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//	result.add("os/" + InternalBootLoader.getOS() + "/"); //$NON-NLS-1$ //$NON-NLS-2$
//	String nl = InternalBootLoader.getNL();
//	nl = nl.replace('_', '/');
//	while (nl.length() > 0) {
//		result.add("nl/" + nl + "/"); //$NON-NLS-1$ //$NON-NLS-2$
//		int i = nl.lastIndexOf('/'); //$NON-NLS-1$
//		nl = (i < 0) ? "" : nl.substring(0, i); //$NON-NLS-1$
//	}
//	result.add(""); //$NON-NLS-1$
//	return (String[])result.toArray(new String[result.size()]);
//}

private static String[] buildLibraryVariants() {
	ArrayList result = new ArrayList();
	
	result.add("ws/" + InternalBootLoader.getWS() + "/"); //$NON-NLS-1$ //$NON-NLS-2$
	result.add("os/" + InternalBootLoader.getOS() + "/" + InternalBootLoader.getOSArch() + "/"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	result.add("os/" + InternalBootLoader.getOS() + "/"); //$NON-NLS-1$ //$NON-NLS-2$
	String nl = InternalBootLoader.getNL();
	nl = nl.replace('_', '/');
	while (nl.length() > 0) {
		result.add("nl/" + nl + "/"); //$NON-NLS-1$ //$NON-NLS-2$
		int i = nl.lastIndexOf('/');
		nl = (i < 0) ? "" : nl.substring(0, i); //$NON-NLS-1$
	}
	result.add (""); //$NON-NLS-1$
	return (String[])result.toArray(new String[result.size()]);
}

/**
 * The package prefix table is a mapping of plug-in id to the list of package
 * prefixes that the plug-in knows about. This information comes from a file
 * in java.utils.Properties format and could either be specified by the user or
 * is in a default location. Try to load this table from the file and initialize the values.
 * <p>
 * If there was problems loading the table then just return, the code elsewhere 
 * should handle the case where the table is not initialized. 
 */
private static void initializePrefixTable() {
	if (!InternalBootLoader.useClassLoaderProperties())
		return;
	InputStream input = null;

	String filename = InternalBootLoader.getClassLoaderPropertiesFilename();
	// If the user didn't specify a filename to use, use a default
	if (filename == null)
		filename = InternalBootLoader.getDefaultClassLoaderPropertiesFilename();

	String errorMessage = "Error opening: " + filename + ". Continuing execution without using classloader performance enhancement."; //$NON-NLS-1$ //$NON-NLS-2$

	// Try to convert the filename to a URL. If that fails then try a java.io.File
	try {
		input = new URL(filename).openStream();
	} catch (MalformedURLException e) {
		// ignore and try to create a file path from the arg
		try {
			input = new BufferedInputStream(new FileInputStream(filename));
		} catch (FileNotFoundException ex) {
			// inform the user that a problem occurred and return
			System.err.println(errorMessage);
			ex.printStackTrace();
			return;
		}
	} catch (IOException e) {
		// inform the user that a problem occurred and return
		System.err.println(errorMessage);
		e.printStackTrace();
		return;
	}
	
	// Load the properties file. It could be local or remote, specified by the user or not.
	try {
		try {
			prefixTable.load(input);
		} finally {
			input.close();
		}
	} catch (IOException e) {
		// Tell the user that there was a problem loading the file 
		// but don't let that stop us from continuing execution.
		System.err.println(errorMessage);
		e.printStackTrace();
	}
}

/**
 * convert a list of comma-separated tokens into an array
 */
protected static String[] getArrayFromList(String prop) {
	if (prop == null || prop.trim().equals("")) //$NON-NLS-1$
		return new String[0];
	Vector list = new Vector();
	StringTokenizer tokens = new StringTokenizer(prop, ","); //$NON-NLS-1$
	while (tokens.hasMoreTokens()) {
		String token = tokens.nextToken().trim();
		if (!token.equals("")) //$NON-NLS-1$
			list.addElement(token);
	}
	return list.isEmpty() ? new String[0] : (String[]) list.toArray(new String[0]);
}

public DelegatingURLClassLoader(URL[] codePath, URLContentFilter[] codeFilters, URL[] resourcePath, URLContentFilter[] resourceFilters, ClassLoader parent) {

//	Instead of constructing the loader with supplied classpath, create loader
//	with empty path, "fix up" jar entries and then explicitly add the classpath
//	to the newly constructed loader

	super(mungeJarURLs (codePath), parent);
	resourcePath = mungeJarURLs(resourcePath);

	if (resourcePath != null && resourcePath.length > 0)
		resourceLoader = new ResourceLoader(resourcePath);

	if (codePath != null) {
		if (codeFilters == null || codeFilters.length != codePath.length)
			throw new DelegatingLoaderException();
		for (int i = 0; i < codePath.length; i++) {
			if (codeFilters[i] != null)
				filterTable.put(codePath[i], codeFilters[i]);
		}
	}
	if (resourcePath != null) {
		if (resourceFilters == null || resourceFilters.length != resourcePath.length)
			throw new DelegatingLoaderException();
		for (int i = 0; i < resourcePath.length; i++) {
			if (resourceFilters[i] != null)
				filterTable.put(resourcePath[i], resourceFilters[i]);
		}
	}
}

/**
 * This method is to be used internally only for adding the proper class path and resource path
 * entries to the class loaders for Runtime and Xerces. They are special cases since they need
 * to be brought up before everything else. (and before the registry is loaded)
 */
public void addURLs(URL[] codePath, URLContentFilter[] codeFilters, URL[] resourcePath, URLContentFilter[] resourceFilters) {
	Set keys = filterTable.keySet();

	codePath = mungeJarURLs(codePath);
	resourcePath = mungeJarURLs(resourcePath);
	if (resourcePath != null && resourcePath.length > 0)
		resourceLoader = new ResourceLoader(resourcePath);

	if (codePath != null) {
		if (codeFilters == null || codeFilters.length != codePath.length)
			throw new DelegatingLoaderException();
		for (int i=0; i<codePath.length; i++) {
			URL path = codePath[i];
			if (!keys.contains(path)) {
				addURL(path);
				filterTable.put(path, codeFilters[i]);
			}
		}
	}
	
	if (resourcePath != null) {
		if (resourceFilters == null || resourceFilters.length != resourcePath.length)
			throw new DelegatingLoaderException();
		for (int i = 0; i < resourcePath.length; i++) {
			URL path = resourcePath[i];
			if (resourceFilters[i] != null && !keys.contains(path))
				filterTable.put(path, resourceFilters[i]);
		}
	}
}

/**
 * strip-off jar: protocol
 */ 
private static URL mungeJarURL(URL url) {
	if (url.getProtocol().equals("jar")) { //$NON-NLS-1$
		String file = url.getFile();
		if (file.startsWith("file:")) { //$NON-NLS-1$
			int ix = file.indexOf("!/"); //$NON-NLS-1$
			if (ix != -1) file = file.substring(0,ix);
			try {
				url = new URL(file);
			} catch (MalformedURLException e) {
				// just use the original if we cannot create a new one
			}
		}
	}
	return url;
}

private static URL[] mungeJarURLs(URL[] urls) {
	if (urls == null) 
		return null;
	for (int i = 0; i < urls.length; i++) 
		urls[i] = mungeJarURL(urls[i]);
	return urls;
}

/**
 * Returns the given class or <code>null</code> if the class is not visible to the
 * given requestor.  The <code>inCache</code> flag controls how this action is
 * reported if in debug mode.
 */
protected Class checkClassVisibility(Class result, DelegatingURLClassLoader requestor, boolean inCache) {
	if (result == null)
		return null;
	if (isClassVisible(result, requestor)) {
		if (DEBUG && DEBUG_SHOW_SUCCESS && debugClass(result.getName()))
			debug("found " + result.getName() + " in " + (inCache ? "cache" : getURLforClass(result).toExternalForm())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	} else {
		if (DEBUG && DEBUG_SHOW_ACTIONS && debugClass(result.getName()))
			debug("skip " + result.getName() + " in " + (inCache ? "cache" : getURLforClass(result).toExternalForm())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return null;
	}
	return result;
}
/**
 * Returns the given resource URL or <code>null</code> if the resource is not visible to the
 * given requestor.  
 */
protected URL checkResourceVisibility(String name, URL result, DelegatingURLClassLoader requestor) {
	if (result == null)
		return null;
	if (isResourceVisible(name, result, requestor)) {
		if (DEBUG && DEBUG_SHOW_SUCCESS && debugResource(name))
			debug("found " + result); //$NON-NLS-1$
	} else {
		if (DEBUG && DEBUG_SHOW_ACTIONS && debugResource(name))
			debug("skip " + result); //$NON-NLS-1$
		result = null;
	}
	return result;
}
protected void debug(String s) {
	System.out.println(toString()+"^"+Integer.toHexString(Thread.currentThread().hashCode())+" "+s); //$NON-NLS-1$ //$NON-NLS-2$
}
protected boolean debugClass(String name) {
	
	if (debugLoader()) {
		return debugMatchesFilter(name,DEBUG_FILTER_CLASS);
	}
	return false;
}
protected void debugConstruction() {
	if (DEBUG && DEBUG_SHOW_CREATE && debugLoader()) {
		URL[] urls = getURLs();
		debug("Class Loader Created"); //$NON-NLS-1$
		debug("> baseURL=" + base); //$NON-NLS-1$
		if (urls == null || urls.length == 0)
			debug("> empty search path"); //$NON-NLS-1$
		else {
			URLContentFilter filter;
			for (int i = 0; i < urls.length; i++) {
				debug("> searchURL=" + urls[i].toString()); //$NON-NLS-1$
				filter = (URLContentFilter) filterTable.get(urls[i]);
				if (filter != null)
					debug(">    export=" + filter.toString()); //$NON-NLS-1$
			}
		}
	}
}
protected String debugId() {
	return ""; //$NON-NLS-1$
}
protected boolean debugLoader() {
	
	return debugMatchesFilter(debugId(),DEBUG_FILTER_LOADER);
}
private boolean debugMatchesFilter(String name, String[] filter) {

	if (filter.length==0) return false;
	
	for (int i=0; i<filter.length; i++) {
		if (filter[i].equals("*")) return true; //$NON-NLS-1$
		if (name.startsWith(filter[i])) return true;
	}
	return false;
}
protected boolean debugNative(String name) {
	
	if (debugLoader()) {
		return debugMatchesFilter(name,DEBUG_FILTER_NATIVE);
	}
	return false;
}
protected boolean debugResource(String name) {
	
	if (debugLoader()) {
		return debugMatchesFilter(name,DEBUG_FILTER_RESOURCE);
	}
	return false;
}
/**
 * Looks for the requested class in the parent of this loader using
 * standard Java protocols.  If the parent is null then the system class
 * loader is consulted.  <code>null</code> is returned if the class could
 * not be found.
 */
protected Class findClassParents(String name, boolean resolve) {
	try {
		ClassLoader parent = getParent();
		if (parent == null)
			return findSystemClass(name);

		if (MONITOR_CLASSES)	
			ClassloaderStats.startLoadingClass(BootLoader.PI_BOOT, name);
		Class result = parent.loadClass(name);
		if (MONITOR_CLASSES)		
			ClassloaderStats.endLoadingClass(BootLoader.PI_BOOT, name, true);
		return result;
	} catch (ClassNotFoundException e) {
		if (MONITOR_CLASSES)		
			ClassloaderStats.endLoadingClass(BootLoader.PI_BOOT, name, false);
	}
	return null;
}
/**
 * Finds and loads the class with the specified name from the URL search
 * path. Any URLs referring to JAR files are loaded and opened as needed
 * until the class is found.   Search on the parent chain and then self.
 *
 * @param name the name of the class
 * @param resolve whether or not to resolve the class if found
 * @param requestor class loader originating the request
 * @param checkParents whether the parent of this loader should be consulted
 * @return the resulting class
 */
protected Class findClassParentsSelf(final String name, boolean resolve, DelegatingURLClassLoader requestor, boolean checkParents) {
	if (prefixes == null || prefixes.length == 0)
		return internalFindClassParentsSelf(name, resolve, requestor, checkParents);
	for (int i = 0; i < prefixes.length; i++) {
		if (name.startsWith(prefixes[i])) {
			if (DEBUG_PACKAGE_PREFIXES_SUCCESS)
				System.out.println("prefix: matched class: \"" + name + "\" with prefix: \"" + prefixes[i] + "\" loader: \"" + getPrefixId() + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			return internalFindClassParentsSelf(name, resolve, requestor, checkParents);
		}
	}
	if (DEBUG_PACKAGE_PREFIXES_FAILURE)
		System.out.println("prefix: no match for class: \"" + name + "\" loader: \"" + getPrefixId() + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	if (checkParents)
		return findClassParents(name, resolve);
	return null;
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
protected abstract Class internalFindClassParentsSelf(final String name, boolean resolve, DelegatingURLClassLoader requestor, boolean checkParents);



/**
 * Finds and loads the class with the specified name from the URL search
 * path. Any URLs referring to JAR files are loaded and opened as needed
 * until the class is found.  This method consults only the platform class loader.
 *
 * @param name the name of the class
 * @param resolve whether or not to resolve the class if found
 * @param requestor class loader originating the request
 * @param checkParents whether the parent of this loader should be consulted
 * @return the resulting class
 */
protected Class findClassPlatform(String name, boolean resolve, DelegatingURLClassLoader requestor, boolean checkParents) {
	DelegatingURLClassLoader platform = PlatformClassLoader.getDefault();
	if (this == platform)
		return null;
	return platform.findClassParentsSelf(name, resolve, requestor, false);
}
/**
 * Finds and loads the class with the specified name from the URL search
 * path. Any URLs referring to JAR files are loaded and opened as needed
 * until the class is found.  This method considers only the classes loadable
 * by its explicit prerequisite loaders.
 *
 * @param name the name of the class
 * @param requestor class loader originating the request
 * @param seen list of delegated class loaders already searched
 * @return the resulting class
 */
protected Class findClassPrerequisites(final String name, DelegatingURLClassLoader requestor, Vector seen) {
	if (imports == null)
		return null;
	if (seen == null)
		seen = new Vector(); // guard against delegation loops
	seen.addElement(this);
	// Grab onto the imports value to protect against concurrent write.
	DelegateLoader[] loaders = imports;
	for (int i = 0; i < loaders.length; i++) {
		Class result = loaders[i].loadClass(name, this, requestor, seen);
		if (result != null)
			return result;
	}
	return null;
}
/**
 * Finds the resource with the specified name on the URL search path.
 * This method is used specifically to find the file containing a class to verify
 * that the class exists without having to load it.
 * Returns a URL for the resource.  Searches only this loader's classpath.
 * <code>null</code> is returned if the resource cannot be found.
 *
 * @param name the name of the resource
 */
protected URL findClassResource(String name) {
	return super.findResource(name);
}
/**
 * Returns the absolute path name of a native library. The VM
 * invokes this method to locate the native libraries that belong
 * to classes loaded with this class loader. If this method returns
 * <code>null</code>, the VM searches the library along the path
 * specified as the <code>java.library.path</code> property.
 *
 * @param      libname   the library name
 * @return     the absolute path of the native library
 */
protected String findLibrary(String libName) {
	if (libName.length() == 0)
		return null;
	if (libName.charAt(0) == '/' || libName.charAt(0) == '\\')
		libName = libName.substring(1);
	libName = System.mapLibraryName(libName);

	if (DEBUG && DEBUG_SHOW_ACTIONS && debugNative(libName))
		debug("findLibrary(" + libName + ")"); //$NON-NLS-1$ //$NON-NLS-2$
	if (base == null)
		return null;
	String libFileName = null;
	if (base.getProtocol().equals(PlatformURLHandler.FILE)) {
		// directly access library	
		libFileName = (base.getFile() + libName).replace('/', File.separatorChar);
	} else {
		if (base.getProtocol().equals(PlatformURLHandler.PROTOCOL)) {
			URL[] searchList = getSearchURLs (base);
			if ((searchList != null) && (searchList.length != 0)) {
				URL foundPath = searchVariants(searchList, LIBRARY_VARIANTS, libName);
				if (foundPath != null) 
					libFileName = foundPath.getFile();
			}
		}
	}

	if (libFileName == null)
		return null;
		
	return new File(libFileName).getAbsolutePath();
}
/**
 * Finds the resource with the specified name on the URL search path.
 * Returns a URL for the resource. If resource is not found in own 
 * URL search path, delegates search to prerequisite loaders.
 * Null is returned if none of the loaders find the resource.
 *
 * @param name the name of the resource
 */
public URL findResource(String name) {
	return findResource(name, this, null);
}
/**
 * Delegated resource access call. 
 * Does not check prerequisite loader parent chain.
 */
protected URL findResource(String name, DelegatingURLClassLoader requestor, Vector seen) {
	// guard against delegation loops
	if (seen != null && seen.contains(this))
		return null;

	if (DEBUG && DEBUG_SHOW_ACTIONS && debugResource(name))
		debug("findResource(" + name + ")"); //$NON-NLS-1$ //$NON-NLS-2$

	// check the normal class path for self
	URL result = super.findResource(name);
	result = checkResourceVisibility(name, result, requestor);
	if (result != null)
		return result;

	// check our extra resource path if any
	if (resourceLoader != null) {
		result = resourceLoader.findResource(name);
		result = checkResourceVisibility(name, result, requestor);
		if (result != null)
			return result;
	}

	// delegate down the prerequisite chain if we haven't found anything yet.
	if (imports != null) {
		if (seen == null)
			seen = new Vector(); // guard against delegation loops
		seen.addElement(this);
		for (int i = 0; i < imports.length && result == null; i++)
			result = imports[i].findResource(name, this, requestor, seen);
	}
	return result;
}
/**
 * Returns an Enumeration of URLs representing all of the resources
 * on the URL search path having the specified name.
 *
 * @param name the resource name
 */
public Enumeration findResources(String name) throws IOException {
	return findResources(name, this, null);
}
/**
 * Delegated call to locate all named resources. 
 * Does not check prerequisite loader parent chain.
 */
private Enumeration findResources(String name, DelegatingURLClassLoader requestor, Vector seen) {
	// guard against delegation loops
	if (seen != null && seen.contains(this))
		return null;

	if (DEBUG && DEBUG_SHOW_ACTIONS && debugResource(name))
		debug("findResources(" + name + ")"); //$NON-NLS-1$ //$NON-NLS-2$

	// check own URL search path
	Enumeration e = null;
	try {
		e = super.findResources(name);
	} catch (IOException ioe) {
		//fall through and search prerequisites
	}
	ResourceEnumeration result = new ResourceEnumeration(name, e, this, requestor);

	// delegate down the prerequisite chain
	if (imports != null) {
		if (seen == null)
			seen = new Vector(); // guard against delegation loops
		seen.addElement(this);
		for (int i = 0; i < imports.length; i++)
			result.add(imports[i].findResources(name, this, requestor, seen));
	}

	return result;
}
protected String getFileFromURL(URL target) {
	try {
		URL url = InternalBootLoader.resolve(target);
		String protocol = url.getProtocol();
		// check only for the file protocol here.  Not interested in Jar files.
		if (protocol.equals(PlatformURLHandler.FILE))
			return url.getFile();
	} catch (IOException e) {
		//couldn't resolve the target - return null
	}
	return null;
}

private URL[] getSearchURLs (URL base) {
	URL[] auxList = null;
	ArrayList result = new ArrayList();

	PlatformURLConnection c = null;
	try {
		c = (PlatformURLConnection) base.openConnection();
		result.add(c.getURLAsLocal());
	} catch (IOException e) {
		// Catch intentionally left empty.  Skip 
		// poorly formed URLs
	}

	try {
		auxList = c.getAuxillaryURLs();
		int auxLength = (auxList == null) ? 0 : auxList.length;
	
		// Now add the fragment URLs to the result
		for (int i = 0; i < auxLength; i++) {
			try {
				c = (PlatformURLConnection) auxList[i].openConnection();
				result.add(c.getURLAsLocal());
			} catch (IOException e) {
				// Catch intentionally left empty.  Skip 
				// poorly formed URLs
			}
		}
	} catch (IOException e) {
		// Catch intentionally left empty.  Skip 
		// poorly formed URLs
	}
	
	return (URL[])result.toArray(new URL[result.size()]);
}

private URL searchVariants (URL[] basePaths, String[] variants, String path) {
	// This method assumed basePaths are 'resolved' URLs
	for (int i = 0; i < variants.length; i++) {
		for (int j = 0; j < basePaths.length; j++) {
			String fileName = basePaths[j].getFile() + variants[i] + path;
			File file = new File(fileName);
			if (!file.exists()) {
				if (DEBUG && DEBUG_SHOW_FAILURE)
					debug("not found " + file.getAbsolutePath()); //$NON-NLS-1$
			} else {	
				if (DEBUG && DEBUG_SHOW_SUCCESS)
					debug("found " + path + " as " + file.getAbsolutePath()); //$NON-NLS-1$ //$NON-NLS-2$
				try {
					return new URL ("file:" + fileName); //$NON-NLS-1$
				} catch (MalformedURLException e) {
					// Intentionally ignore this exception
					// so we continue looking for a matching
					// URL.
				}
			}		
		}
	}
	return null;
}
public URL getResource(String name) {
	if (DEBUG && DEBUG_SHOW_ACTIONS && debugResource(name))
		debug("getResource(" + name + ")"); //$NON-NLS-1$ //$NON-NLS-2$

	URL result = super.getResource(name);
	if (result == null) {
		if (DEBUG && DEBUG_SHOW_FAILURE && debugResource(name))
			debug("not found " + name); //$NON-NLS-1$
	}
	return result;
}
public void setPackagePrefixes(String[] prefixes) {
	this.prefixes = prefixes;
}
/**
 * Returns the id to use to lookup class prefixes for this loader
 */
public abstract String getPrefixId();
private URL getURLforClass(Class clazz) {
	ProtectionDomain pd = clazz.getProtectionDomain();
	if (pd != null) {
		CodeSource cs = pd.getCodeSource();
		if (cs != null)
			return cs.getLocation();
	}
	if (DEBUG && DEBUG_SHOW_ACTIONS && debugClass(clazz.getName()))
		debug("*** " + clazz.getName()); //$NON-NLS-1$
	return null;
}
public void initializeImportedLoaders() {
}
/**
 * check to see if class is visible (exported)
 */
boolean isClassVisible(Class clazz, DelegatingURLClassLoader requestor) {
	URL lib = getURLforClass(clazz);
	if (lib == null)
		return true; // have a system class (see comment below)

	URLContentFilter filter = (URLContentFilter) filterTable.get(lib);
	if (filter == null) {
		// This code path is being executed because some VMs (eg. Sun JVM)
		// return from the class cache classes that were not loaded
		// by this class loader. Consequently we do not find the 
		// corresponding jar filter. This appears to be a performance
		// optimization that we are defeating with our filtering scheme.
		// We return the class if it is a system class (see above). Otherwise
		// we reject the class which caused the load to be
		// delegated down the prerequisite chain until we find the
		// correct loader.
		if (DEBUG && DEBUG_SHOW_ACTIONS && debugClass(clazz.getName()))
			debug("*** Unable to find library filter for " + clazz.getName() + " from " + lib); //$NON-NLS-1$ //$NON-NLS-2$
		return false;
	} else
		return filter.isClassVisible(clazz, this, requestor);
}
/**
 * check to see if resource is visible (exported)
 */
boolean isResourceVisible(String name, URL resource, DelegatingURLClassLoader requestor) {
	URL lib = null;
	String file = resource.getFile();
	try {
		lib = new URL(resource.getProtocol(), resource.getHost(), file.substring(0, file.length() - name.length()));
	} catch (MalformedURLException e) {
		if (DEBUG)
			debug("Unable to determine resource lib for " + name + " from " + resource); //$NON-NLS-1$ //$NON-NLS-2$
		return false;
	}

	URLContentFilter filter = (URLContentFilter) filterTable.get(lib);
	// retry with non-jar URL if necessary
	if (filter == null) filter = (URLContentFilter) filterTable.get(mungeJarURL(lib));
	if (filter == null) {
		if (DEBUG)
			debug("Unable to find library filter for " + name + " from " + lib); //$NON-NLS-1$ //$NON-NLS-2$
		return false;
	} else
		return filter.isResourceVisible(name, this, requestor);
}
/**
 * Non-delegated load call.  This method is not synchronized.  Implementations of
 * findClassParentsSelf, and perhaps others, should synchronize themselves as
 * required.  Synchronizing this method is too coarse-grained.   It results in plugin
 * activation being synchronized and may cause deadlock.
 */
protected Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
	if (DEBUG && DEBUG_SHOW_ACTIONS && debugClass(name))
		debug("loadClass(" + name + ")"); //$NON-NLS-1$ //$NON-NLS-2$
	Class result = loadClass(name, resolve, this, null, true);
	if (result == null) {
		if (DEBUG && DEBUG_SHOW_FAILURE && debugClass(name))
			debug("not found " + name); //$NON-NLS-1$
		throw new ClassNotFoundException(name);
	}
	return result;
}
/**
 * Delegated load call.  This method is not synchronized.  Implementations of
 * findClassParentsSelf, and perhaps others, should synchronize themselves as
 * required.  Synchronizing this method is too coarse-grained.   It results in plugin
 * activation being synchronized and may cause deadlock.
 */
private Class loadClass(String name, boolean resolve, DelegatingURLClassLoader requestor, Vector seen, boolean checkParents) {
	// guard against delegation loops
	if (seen != null && seen.contains(this))
		return null;

	// look in the parents and self
	Class result = findClassParentsSelf(name, resolve, requestor, checkParents);

	// search platform
	if (result == null)
		result = findClassPlatform(name, resolve, requestor, false);

	// search prerequisites
	if (result == null)
		result = findClassPrerequisites(name, requestor, seen);

	// if we found a class, consider resolving it
	if (result != null && resolve)
		resolveClass(result);

	return result;
}
protected void setImportedLoaders(DelegateLoader[] loaders) {
	
	imports = loaders;
	
	if(DEBUG && DEBUG_SHOW_CREATE && debugLoader()) {
		debug("Imports"); //$NON-NLS-1$
		if (imports==null || imports.length==0) debug("> none"); //$NON-NLS-1$
		else {
			for (int i=0; i<imports.length; i++) {
				debug("> " + imports[i].loader.toString() + " export=" + imports[i].isExported); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
}
public String toString() {
	return "Loader [" + debugId() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
}

protected Class findClass(String name) throws ClassNotFoundException {
	boolean found = false;
	Class result = null;
	try {
		if (MONITOR_CLASSES)	
			ClassloaderStats.startLoadingClass(getClassloaderId(), name);
		result = super.findClass(name);
		found = true;
	} catch (ClassNotFoundException e) {
		throw e;
	} finally {
		if (MONITOR_CLASSES)		
			ClassloaderStats.endLoadingClass(getClassloaderId(), name, found);
	}
	return result;
}

protected abstract String getClassloaderId();

public InputStream getResourceAsStream(String name) {
	InputStream result = super.getResourceAsStream(name);
	if (MONITOR_BUNDLES) {
		if (result != null && name.endsWith(".properties")) { //$NON-NLS-1$
			ClassloaderStats.loadedBundle(getClassloaderId(), new BundleStats(getClassloaderId(), name, result));
			result = super.getResourceAsStream(name);
		}
	}
	return result;
}
}
