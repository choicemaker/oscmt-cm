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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.*;

import org.eclipse.core.boot.BootLoader;
import org.eclipse.core.boot.IPlatformRunnable;

/**
 * Special boot loader class for the Eclipse Platform. This class cannot
 * be instantiated; all functionality is provided by static methods.
 * <p>
 * The Eclipse Platform makes heavy use of Java class loaders for
 * loading plug-ins. Even the Platform Core Runtime itself, including
 * the <code>Platform</code> class, needs to be loaded by a special 
 * class loader. The upshot is that a client program (such as a Java main
 * program, a servlet) cannot directly reference even the 
 * <code>Platform</code> class. Instead, a client must use this
 * loader class for initializing the platform, invoking functionality
 * defined in plug-ins, and shutting down the platform when done.
 * </p>
 *
 * @see org.eclipse.core.runtime.Platform
 */
public final class InternalBootLoader {
	private static boolean running = false;
	private static boolean starting = false;
	private static String[] commandLine;
	private static ClassLoader loader = null;
	private static String baseLocation = null; // -data argument (workspace location)
	private static String installLocation = null; // -install argument (product install location)
	private static String applicationR10 = null; // R1.0 compatibility
	private static URL installURL = null;
	private static boolean debugRequested = false;
	private static String devClassPath = null;
	private static String debugOptionsFilename = null;
	private static Properties options = null;
	private static boolean inDevelopmentMode = false;	
	private static PlatformConfiguration currentPlatformConfiguration = null;
	
	// state for tracking the Platform context (e.g., the OS, Window system, locale, architecture, ...)
	private static String nl = null;
	private static String ws = null;
	private static String os = null;
	private static String arch = null;
	
	private static final String PLATFORM_ENTRYPOINT = "org.eclipse.core.internal.runtime.InternalPlatform"; //$NON-NLS-1$
	private static final String BOOTNAME = "org.eclipse.core.boot"; //$NON-NLS-1$
	/*package*/ static final String RUNTIMENAME = "org.eclipse.core.runtime"; //$NON-NLS-1$
	private static final String PLUGINSDIR = "plugins/"; //$NON-NLS-1$
	private static final String LIBRARY = "library"; //$NON-NLS-1$
	private static final String EXPORT = "export"; //$NON-NLS-1$
	private static final String EXPORT_PUBLIC = "public"; //$NON-NLS-1$
	private static final String EXPORT_PROTECTED = "protected"; //$NON-NLS-1$
	private static final String META_AREA = ".metadata"; //$NON-NLS-1$
	private static final String WORKSPACE = "workspace"; //$NON-NLS-1$
	private static final String PLUGIN_PATH = ".plugin-path"; //$NON-NLS-1$
	private static String BOOTDIR;
	private static final String RUNTIMEDIR = PLUGINSDIR + RUNTIMENAME + "/"; //$NON-NLS-1$
	private static final String OPTIONS = ".options"; //$NON-NLS-1$
	// While we recognize the SunOS operating system, we change
	// this internally to be Solaris.
	private static final String INTERNAL_OS_SUNOS = "SunOS"; //$NON-NLS-1$
	// While we recognize the i386 architecture, we change
	// this internally to be x86.
	private static final String INTERNAL_ARCH_I386 = "i386"; //$NON-NLS-1$

	private static boolean useClassLoaderProperties = false;
	private static String classLoaderPropertiesFilename = null;

	/** 
	 * Execution options for the Runtime plug-in.  They are defined here because
	 * we need to load them into the PlatformClassLoader which is created by the
	 * boot system.  Users should see these options as Runtime options since there
	 * boot does not figure into normal Platform operation.
	 */
	private static final String PI_RUNTIME = "org.eclipse.core.runtime"; //$NON-NLS-1$
	private static final String OPTION_STARTTIME = PI_RUNTIME + "/starttime"; //$NON-NLS-1$
	private static final String OPTION_LOADER_DEBUG = PI_RUNTIME + "/loader/debug"; //$NON-NLS-1$
	private static final String OPTION_LOADER_SHOW_CREATE = PI_RUNTIME + "/loader/debug/create"; //$NON-NLS-1$
	private static final String OPTION_LOADER_PROPERTIES = PI_RUNTIME + "/loader/debug/properties"; //$NON-NLS-1$
	private static final String OPTION_LOADER_PACKAGE_PREFIXES = PI_RUNTIME + "/loader/debug/prefixes"; //$NON-NLS-1$
	private static final String OPTION_LOADER_PACKAGE_PREFIXES_SUCCESS = OPTION_LOADER_PACKAGE_PREFIXES + "/success"; //$NON-NLS-1$
	private static final String OPTION_LOADER_PACKAGE_PREFIXES_FAILURE = OPTION_LOADER_PACKAGE_PREFIXES + "/failure"; //$NON-NLS-1$
	private static final String OPTION_LOADER_SHOW_ACTIVATE = PI_RUNTIME + "/loader/debug/activateplugin"; //$NON-NLS-1$
	private static final String OPTION_LOADER_SHOW_ACTIONS = PI_RUNTIME + "/loader/debug/actions"; //$NON-NLS-1$
	private static final String OPTION_LOADER_SHOW_SUCCESS = PI_RUNTIME + "/loader/debug/success"; //$NON-NLS-1$
	private static final String OPTION_LOADER_SHOW_FAILURE = PI_RUNTIME + "/loader/debug/failure"; //$NON-NLS-1$
	private static final String OPTION_LOADER_FILTER_CLASS = PI_RUNTIME + "/loader/debug/filter/class"; //$NON-NLS-1$
	private static final String OPTION_LOADER_FILTER_LOADER = PI_RUNTIME + "/loader/debug/filter/loader"; //$NON-NLS-1$
	private static final String OPTION_LOADER_FILTER_RESOURCE = PI_RUNTIME + "/loader/debug/filter/resource"; //$NON-NLS-1$
	private static final String OPTION_LOADER_FILTER_NATIVE = PI_RUNTIME + "/loader/debug/filter/native"; //$NON-NLS-1$
	private static final String OPTION_URL_DEBUG = PI_RUNTIME+ "/url/debug"; //$NON-NLS-1$
	private static final String OPTION_URL_DEBUG_CONNECT = PI_RUNTIME+ "/url/debug/connect"; //$NON-NLS-1$
	private static final String OPTION_URL_DEBUG_CACHE_LOOKUP = PI_RUNTIME+ "/url/debug/cachelookup"; //$NON-NLS-1$
	private static final String OPTION_URL_DEBUG_CACHE_COPY = PI_RUNTIME+ "/url/debug/cachecopy"; //$NON-NLS-1$
	private static final String OPTION_UPDATE_DEBUG = PI_RUNTIME+ "/update/debug"; //$NON-NLS-1$
	private static final String OPTION_CONFIGURATION_DEBUG = PI_RUNTIME+ "/config/debug"; //$NON-NLS-1$

	// option names for spy
	private static final String OPTION_MONITOR_PLUGINS = BootLoader.PI_BOOT+"/monitor/plugins"; //$NON-NLS-1$
	private static final String OPTION_MONITOR_CLASSES = BootLoader.PI_BOOT+"/monitor/classes"; //$NON-NLS-1$
	private static final String OPTION_MONITOR_BUNDLES = BootLoader.PI_BOOT+"/monitor/bundles"; //$NON-NLS-1$

	private static final String OPTION_TRACE_CLASSES = BootLoader.PI_BOOT + "/trace/classLoading"; //$NON-NLS-1$
	private static final String OPTION_TRACE_FILENAME = BootLoader.PI_BOOT+"/trace/filename"; //$NON-NLS-1$
	private static final String OPTION_TRACE_FILTERS = BootLoader.PI_BOOT+"/trace/filters"; //$NON-NLS-1$
	private static final String OPTION_TRACE_PLUGINS = BootLoader.PI_BOOT+"/trace/pluginActivation"; //$NON-NLS-1$

	// command line arguments
	private static final String DEBUG = "-debug"; //$NON-NLS-1$
	private static final String DATA = "-data"; //$NON-NLS-1$
	private static final String INSTALL = "-install"; //$NON-NLS-1$
	private static final String DEV = "-dev"; //$NON-NLS-1$
	private static final String WS = "-ws"; //$NON-NLS-1$
	private static final String OS = "-os"; //$NON-NLS-1$
	private static final String ARCH = "-arch"; //$NON-NLS-1$
	private static final String NL = "-nl"; //$NON-NLS-1$
	private static final String CLASSLOADER_PROPERTIES = "-classloaderProperties"; //$NON-NLS-1$

/**
 * Private constructor to block instance creation.
 */
private InternalBootLoader() {
}
private static void assertNotRunning() {
	if (running)
		throw new RuntimeException(Policy.bind("platform.mustNotBeRunning")); //$NON-NLS-1$
}
private static void assertRunning() {
	if (!running)
		throw new RuntimeException(Policy.bind("platform.notRunning")); //$NON-NLS-1$
}
/**
 * Configure the class loader for the runtime plug-in.  
 */
private static PlatformClassLoader configurePlatformLoader() {
	Object[] loadPath = getPlatformClassLoaderPath();
	URL base = null;
	try {
		base = new URL(PlatformURLBaseConnection.PLATFORM_URL_STRING+RUNTIMEDIR);
	} catch (MalformedURLException e) {
		//proceed without runtime plugin in platform loader -- is this possible?
	}
	return new PlatformClassLoader((URL[]) loadPath[0], (URLContentFilter[]) loadPath[1], InternalBootLoader.class.getClassLoader(), base);
}
/**
 * @see BootLoader
 */
public static boolean containsSavedPlatform(String location) {
	return new File(location + "/" + META_AREA).exists(); //$NON-NLS-1$
}
/**
 * convert a list of comma-separated tokens into an array
 */
private static String[] getArrayFromList(String prop) {
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
private static boolean getBooleanOption(String option, boolean defaultValue) {
	String optionValue = options.getProperty(option);
	return (optionValue == null) ? defaultValue : optionValue.equalsIgnoreCase("true"); //$NON-NLS-1$
}
/**
 * @see BootLoader#getCommandLineArgs
 */
public static String[] getCommandLineArgs() {
	return commandLine;
}
/**
 * @see BootLoader
 */
public static PlatformConfiguration getCurrentPlatformConfiguration() {
	return PlatformConfiguration.getCurrent();
}
/**
 * @see BootLoader
 */
public static URL getInstallURL() {
	if (installURL != null)
		return installURL;
		
	// See if install location was explicitly specified
	// Note: in the regular launch sequence, if the argument was not specified
	//       on the launch, Main.java is actually defaulting this relative to
	//       itself. The resulting behavior is consistent with the default
	//       behavior in 1.0 (with all program files being co-located in the
	//       eclipse/ install directory). However, the new behavior takes into 
	//       account an update scenario where we may in fact be executing
	//       InternalBootLoader that was loaded from some other location.
	if (installLocation != null && !installLocation.trim().equals("")) { //$NON-NLS-1$
		try {
			installURL = new URL(installLocation);
			if (debugRequested) 
				System.out.println("Install URL:\n    "+installURL.toExternalForm()); //$NON-NLS-1$
			return installURL;
		} catch(MalformedURLException e) {
			//fall through to code below
		}
	}

	// Install location was not specified, or we failed to create a URL.
	// Get the location of this class and compute the install location.
	// this involves striping off last element (jar or directory) 
	URL url = InternalBootLoader.class.getProtectionDomain().getCodeSource().getLocation();
	String path = decode(url.getFile());
	if (path.endsWith("/")) //$NON-NLS-1$
		path = path.substring(0, path.length() - 1);
	int ix = path.lastIndexOf('/');
	//strip off boot jar/bin, boot plugin and plugins dir.  Be sure to leave a trailing /
	path = path.substring(0, ix);
	ix = path.lastIndexOf('/');
	path = path.substring(0, ix);
	ix = path.lastIndexOf('/');
	path = path.substring(0, ix + 1);

	try {
		if (url.getProtocol().equals("jar")) //$NON-NLS-1$
			installURL = new URL(path);
		else 
			installURL = new URL(url.getProtocol(), url.getHost(), url.getPort(), path);
		if (debugRequested) 
			System.out.println("Install URL: "+installURL.toExternalForm()); //$NON-NLS-1$
	} catch (MalformedURLException e) {
		throw new RuntimeException(Policy.bind("error.fatal", e.getMessage())); //$NON-NLS-1$
	}
	return installURL;
}
/**
 * Initialize the BOOTDIR variable. This code is copied from the getInstallURL() code and
 * should be merged.
 */
static void initializeBootDir() {
	URL url = InternalBootLoader.class.getProtectionDomain().getCodeSource().getLocation();
	BOOTDIR = decode(url.getFile());
	if (BOOTDIR.endsWith("/")) //$NON-NLS-1$
		BOOTDIR = BOOTDIR.substring(0, BOOTDIR.length() - 1);
	int index = BOOTDIR.lastIndexOf('/');
	//strip off boot jar/bin, boot plugin and plugins dir.  Be sure to leave a trailing /
	BOOTDIR = BOOTDIR.substring(0, index+1);
}
/**
 * Returns a string representation of the given URL String.  This converts
 * escaped sequences (%..) in the URL into the appropriate characters.
 * NOTE: due to class visibility there is a copy of this method
 *       in Main (the launcher)
 */
public static String decode(String urlString) {
	//try to use Java 1.4 method if available
	try {
		Class clazz = URLDecoder.class;
		Method method = clazz.getDeclaredMethod("decode", new Class[] {String.class, String.class});//$NON-NLS-1$
		//first encode '+' characters, because URLDecoder incorrectly converts 
		//them to spaces on certain class library implementations.
		if (urlString.indexOf('+') >= 0) {
			int len = urlString.length();
			StringBuffer buf = new StringBuffer(len);
			for (int i = 0; i < len; i++) {
				char c = urlString.charAt(i);
				if (c == '+')
					buf.append("%2B");//$NON-NLS-1$
				else
					buf.append(c);
			}
			urlString = buf.toString();
		}
		Object result = method.invoke(null, new Object[] {urlString, "UTF-8"});//$NON-NLS-1$
		if (result != null)
			return (String)result;
	} catch (Exception e) {
		//JDK 1.4 method not found -- fall through and decode by hand
	}
	//decode URL by hand
	boolean replaced = false;
	byte[] encodedBytes = urlString.getBytes();
	int encodedLength = encodedBytes.length;
	byte[] decodedBytes = new byte[encodedLength];
	int decodedLength = 0;
	for (int i = 0; i < encodedLength; i++) {
		byte b = encodedBytes[i];
		if (b == '%') {
			byte enc1 = encodedBytes[++i];
			byte enc2 = encodedBytes[++i];
			b = (byte) ((hexToByte(enc1) << 4) + hexToByte(enc2));
			replaced = true;
		}
		decodedBytes[decodedLength++] = b;
	}
	if (!replaced)
		return urlString;
	try {
		return new String(decodedBytes, 0, decodedLength, "UTF-8");//$NON-NLS-1$
	} catch (UnsupportedEncodingException e) {
		//use default encoding
		return new String(decodedBytes, 0, decodedLength);
	}
}
private static String[] getListOption(String option) {
	String filter = options.getProperty(option);
	if (filter == null)
		return new String[0];
	List result = new ArrayList(5);
	StringTokenizer tokenizer = new StringTokenizer(filter, " ,\t\n\r\f"); //$NON-NLS-1$
	while (tokenizer.hasMoreTokens())
		result.add(tokenizer.nextToken());
	return (String[]) result.toArray(new String[result.size()]);
}
/**
 * @see BootLoader
 */
public static String getOSArch() {
	return arch;
}
/**
 * @see BootLoader
 */
public static String getNL() {
	return nl;
}
/**
 * @see BootLoader
 */
public static String getOS() {
	return os;
}
/**
 * @see BootLoader
 */
public static PlatformConfiguration getPlatformConfiguration(URL url) throws IOException {
	return new PlatformConfiguration(url);
}

private static Object[] getPlatformClassLoaderPath() {

	PlatformConfiguration config = getCurrentPlatformConfiguration();
	PlatformConfiguration.BootDescriptor bd = config.getPluginBootDescriptor(RUNTIMENAME);
	String execBase = bd.getPluginDirectoryURL().toExternalForm();
	if (execBase == null)
		execBase = getInstallURL() + RUNTIMEDIR;

	// build a list alternating lib spec and export spec
	ArrayList libSpecs = new ArrayList(5);
	String[] exportAll = new String[] { "*" }; //$NON-NLS-1$

	// add in any development mode class paths and the export all filter
	if (DelegatingURLClassLoader.devClassPath != null) {
		String[] specs = getArrayFromList(DelegatingURLClassLoader.devClassPath);
		// convert dev class path into url strings
		for (int j = 0; j < specs.length; j++) {
			libSpecs.add(execBase + specs[j] + "/"); //$NON-NLS-1$
			libSpecs.add(exportAll);
		}
	}
	ArrayList list = new ArrayList(5);
	String[] libs = bd.getLibraries();
	for (int i=0; i<libs.length; i++) {
		list.add(libs[i]);
		list.add(exportAll);
	}

	// add in the class path entries spec'd in the config.
	for (Iterator i = list.iterator(); i.hasNext();) {
		String library = (String) i.next();
		String[] filters = (String[]) i.next();

		// convert plugin.xml library entries to url strings
		String libSpec = execBase + library.replace(File.separatorChar, '/');
		if (!libSpec.endsWith("/")) { //$NON-NLS-1$
			if (libSpec.startsWith(PlatformURLHandler.PROTOCOL + PlatformURLHandler.PROTOCOL_SEPARATOR))
				libSpec += PlatformURLHandler.JAR_SEPARATOR;
			else
				libSpec = PlatformURLHandler.JAR + PlatformURLHandler.PROTOCOL_SEPARATOR + libSpec + PlatformURLHandler.JAR_SEPARATOR;
		}
		libSpecs.add(libSpec);
		libSpecs.add(filters);
	}

	// create path entries for libraries
	ArrayList urls = new ArrayList(5);
	ArrayList cfs = new ArrayList(5);
	for (Iterator it = libSpecs.iterator(); it.hasNext();) {
		try {
			urls.add(new URL((String) it.next()));
			cfs.add(new URLContentFilter((String[]) it.next()));
		} catch (MalformedURLException e) {
			// skip bad URLs
		}
	}

	Object[] result = new Object[2];
	result[0] = urls.toArray(new URL[urls.size()]);
	result[1] = cfs.toArray(new URLContentFilter[cfs.size()]);
	return result;
}
/**
 * @see BootLoader
 */
/*
 * This method is retained for R1.0 compatibility because it is defined as API.
 * It's function matches the API description (returns <code>null</code> when
 * argument URL is <code>null</code> or cannot be read).
 */
public static URL[] getPluginPath(URL pluginPathLocation/*R1.0 compatibility*/) {
	InputStream input = null;
	// first try and see if the given plugin path location exists.
	if (pluginPathLocation == null)
		return null;
	try {
		input = pluginPathLocation.openStream();
	} catch (IOException e) {
		//fall through
	}

	// if the given path was null or did not exist, look for a plugin path
	// definition in the install location.
	if (input == null)
		try {
			URL url = new URL(PlatformURLBaseConnection.PLATFORM_URL_STRING + PLUGIN_PATH);
			input = url.openStream();
		} catch (MalformedURLException e) {
			//fall through
		} catch (IOException e) {
			//fall through
		}

	// nothing was found at the supplied location or in the install location
	if (input == null)
		return null;
	// if we found a plugin path definition somewhere so read it and close the location.
	URL[] result = null;
	try {
		try {
			result = readPluginPath(input);
		} finally {
			input.close();
		}
	} catch (IOException e) {
		//let it return null on failure to read
	}
	return result;
}
/**
 * @see BootLoader
 */
public static IPlatformRunnable getRunnable(String applicationName) throws Exception {
	assertRunning();
	Class platform = loader.loadClass(PLATFORM_ENTRYPOINT);
	Method method = platform.getDeclaredMethod("loaderGetRunnable", new Class[] {String.class}); //$NON-NLS-1$
	try {
		return (IPlatformRunnable) method.invoke(platform, new Object[] {applicationName});
	} catch (InvocationTargetException e) {
		if (e.getTargetException() instanceof Error)
			throw (Error) e.getTargetException();
		else
			throw e;
	}
}
/**
 * @see BootLoader
 */
public static IPlatformRunnable getRunnable(String pluginId, String className, Object args) throws Exception {
	assertRunning();
	Class platform = loader.loadClass(PLATFORM_ENTRYPOINT);
	Method method = platform.getDeclaredMethod("loaderGetRunnable", new Class[] {String.class, String.class, Object.class}); //$NON-NLS-1$
	try {
		return (IPlatformRunnable) method.invoke(platform, new Object[] {pluginId, className, args});
	} catch (InvocationTargetException e) {
		if (e.getTargetException() instanceof Error)
			throw (Error) e.getTargetException();
		else
			throw e;
	}
}
/**
 * @see BootLoader
 */
public static String getWS() {
	return ws;
}
/**
 * Converts an ASCII character representing a hexadecimal
 * value into its integer equivalent.
 */
private static int hexToByte(byte b) {
	switch (b) {
		case '0':return 0;
		case '1':return 1;
		case '2':return 2;
		case '3':return 3;
		case '4':return 4;
		case '5':return 5;
		case '6':return 6;
		case '7':return 7;
		case '8':return 8;
		case '9':return 9;
		case 'A':
		case 'a':return 10;
		case 'B':
		case 'b':return 11;
		case 'C':
		case 'c':return 12;
		case 'D':
		case 'd':return 13;
		case 'E':
		case 'e':return 14;
		case 'F':
		case 'f':return 15;
		default:
			throw new IllegalArgumentException("Switch error decoding URL"); //$NON-NLS-1$
	}
}
/**
 * @see BootLoader
 */
public static boolean inDebugMode() {
	return debugRequested;
}
/**
 * @see BootLoader
 */
public static boolean inDevelopmentMode() {
	return inDevelopmentMode;
}
private static String[] initialize(URL pluginPathLocation/*R1.0 compatibility*/, String location, String[] args) throws Exception {
	if (running)
		throw new RuntimeException(Policy.bind("platform.running")); //$NON-NLS-1$
	baseLocation = location;
	String[] appArgs = processCommandLine(args);
	// Do setupSystemContext() ASAP after processCommandLine
	setupSystemContext();

	// call before referencing DelegatingURLClassLoader
	initializeBootDir();

	// setup the devClassPath if any
	DelegatingURLClassLoader.devClassPath = devClassPath;

	// if a platform location was not found in the arguments, compute one.		
	if (baseLocation == null) {
		// Default location for the workspace is <user.dir>/workspace/
		baseLocation = System.getProperty("user.dir"); //$NON-NLS-1$
		if (!baseLocation.endsWith(File.separator))
			baseLocation += File.separator;
		baseLocation += WORKSPACE;
	}
	if (debugRequested)
		System.out.println("Workspace location:\n   " + baseLocation); //$NON-NLS-1$

	// load any debug options
	loadOptions();

	// initialize eclipse URL handling
	String metaPath = baseLocation.replace(File.separatorChar, '/');
	if (!metaPath.endsWith("/")) //$NON-NLS-1$
		metaPath += "/"; //$NON-NLS-1$
	metaPath += META_AREA;
	PlatformURLHandlerFactory.startup(metaPath);
	PlatformURLBaseConnection.startup(getInstallURL()); // past this point we can use eclipse:/platform/ URLs

	// load platform configuration and consume configuration-related arguments (must call after URL handler initialization)
	appArgs = PlatformConfiguration.startup(appArgs, pluginPathLocation/*R1.0 compatibility*/, applicationR10/*R1.0 compatibility*/, metaPath);

	// create and configure platform class loader
	loader = configurePlatformLoader();

	return appArgs;
}
/**
 * @see BootLoader
 */
public static boolean isRunning() {
	return running;
}
public static boolean isStarting() {
	return starting;
}
/**
 * Return a boolean value indicating whether or not the user requested
 * to read the classloader properties file for performance enhancement.
 */
public static boolean useClassLoaderProperties() {
	return useClassLoaderProperties;
}
/**
 * Return the name of the file to be used for the class loader properties. 
 */
static String getClassLoaderPropertiesFilename() {
	return classLoaderPropertiesFilename;
}
/**
 * Return the default name for the class loader properties file.
 */
static String getDefaultClassLoaderPropertiesFilename() {
	return BOOTDIR + "classloader.properties"; //$NON-NLS-1$
}
private static void loadOptions() {
	// if no debug option was specified, don't even bother to try.
	// Must ensure that the options slot is null as this is the signal to the
	// platform that debugging is not enabled.
	if (!debugRequested) {
		options = null;
		return;
	}
	options = new Properties();
	URL optionsFile;
	if (debugOptionsFilename == null) {
		// default options location is user.dir (install location may be r/o so
		// is not a good candidate for a trace options that need to be updatable by
		// by the user)
		String userDir = System.getProperty("user.dir").replace(File.separatorChar,'/'); //$NON-NLS-1$
		if (!userDir.endsWith("/")) //$NON-NLS-1$
			userDir += "/"; //$NON-NLS-1$
		debugOptionsFilename = "file:" + userDir + OPTIONS; //$NON-NLS-1$
	}
	try {
		optionsFile = getURL(debugOptionsFilename);
	} catch (MalformedURLException e) {
		System.out.println("Unable to construct URL for options file: " + debugOptionsFilename); //$NON-NLS-1$
		e.printStackTrace(System.out);
		return;
	}
	System.out.println("Debug-Options:\n    " + debugOptionsFilename); //$NON-NLS-1$
	try {
		InputStream input = optionsFile.openStream();
		try {
			options.load(input);
		} finally {
			input.close();
		}
	} catch (FileNotFoundException e) {
		//	Its not an error to not find the options file
	} catch (IOException e) {
		System.out.println("Could not parse the options file: " + optionsFile); //$NON-NLS-1$
		e.printStackTrace(System.out);
	}
	// trim off all the blanks since properties files don't do that.
	for (Iterator i = options.keySet().iterator(); i.hasNext();) {
		Object key = i.next();
		options.put(key, ((String) options.get(key)).trim());
	}
	InternalBootLoader.setupOptions();
}
private static String[] processCommandLine(String[] args) throws Exception {
	int[] configArgs = new int[100];
	configArgs[0] = -1; // need to initialize the first element to something that could not be an index.
	int configArgIndex = 0;
	for (int i = 0; i < args.length; i++) {
		boolean found = false;
		// check for args without parameters (i.e., a flag arg)

		// check if debug should be enabled for the entire platform
		// If this is the last arg or there is a following arg (i.e., arg+1 has a leading -), 
		// simply enable debug.  Otherwise, assume that that the following arg is
		// actually the filename of an options file.  This will be processed below.
		if (args[i].equalsIgnoreCase(DEBUG) && ((i + 1 == args.length) || ((i + 1 < args.length) && (args[i + 1].startsWith("-"))))) { //$NON-NLS-1$
			found = true;
			debugRequested = true;
		}

		// check to see if we should be using a the classloader package prefix file.
		// If this is the last arg or there is a following arg (i.e., arg+1 has a leading -), 
		// simply enable the option.  Otherwise, assume that that the following arg is
		// actually the filename of the file.  This will be processed below.
		if (args[i].equalsIgnoreCase(CLASSLOADER_PROPERTIES) && ((i + 1 == args.length) || ((i + 1 < args.length) && (args[i + 1].startsWith("-"))))) { //$NON-NLS-1$
			useClassLoaderProperties = true;
			found = true;
		}

		// check if development mode should be enabled for the entire platform
		// If this is the last arg or there is a following arg (i.e., arg+1 has a leading -), 
		// simply enable development mode.  Otherwise, assume that that the following arg is
		// actually some additional development time class path entries.  This will be processed below.
		if (args[i].equalsIgnoreCase(DEV) && ((i + 1 == args.length) || ((i + 1 < args.length) && (args[i + 1].startsWith("-"))))) { //$NON-NLS-1$
			inDevelopmentMode = true;
			found = true;
			continue;
		}

		if (found) {
			configArgs[configArgIndex++] = i;
			continue;
		}
		// check for args with parameters. If we are at the last argument or if the next one
		// has a '-' as the first character, then we can't have an arg with a parm so continue.
		if (i == args.length - 1 || args[i + 1].startsWith("-")) { //$NON-NLS-1$
			continue;
		}
		String arg = args[++i];

		// look for the debug options file location.  
		if (args[i - 1].equalsIgnoreCase(DEBUG)) {
			found = true;
			debugRequested = true;
			debugOptionsFilename = arg;
		}

		// look for the classloader package prefix file location.  
		if (args[i - 1].equalsIgnoreCase(CLASSLOADER_PROPERTIES)) {
			found = true;
			useClassLoaderProperties = true;
			classLoaderPropertiesFilename = arg;
		}

		// look for the development mode and class path entries.  
		if (args[i - 1].equalsIgnoreCase(DEV)) {
			inDevelopmentMode = true;
			devClassPath = arg;
			found = true;
			continue;
		}

		// look for the install location. 
		if (args[i - 1].equalsIgnoreCase(INSTALL)) {
			found = true;
			installLocation = arg;
		}

		// look for the platform location.  Only set it if not already set. This 
		// preserves the value set in the startup() parameter.  Be sure however
		// to consume the command-line argument.
		if (args[i - 1].equalsIgnoreCase(DATA)) {
			found = true;
			if (baseLocation == null)
				baseLocation = arg;
		}

		// look for the window system.  
		if (args[i - 1].equalsIgnoreCase(WS)) {
			found = true;
			ws = arg;
		}

		// look for the operating system
		if (args[i - 1].equalsIgnoreCase(OS)) {
			found = true;
			os = arg;
		}

		// look for the system architecture
		if (args[i - 1].equalsIgnoreCase(ARCH)) {
			found = true;
			arch = arg;
		}

		// look for the nationality/language
		if (args[i - 1].equalsIgnoreCase(NL)) {
			found = true;
			nl = arg;
		}

		// done checking for args.  Remember where an arg was found 
		if (found) {
			configArgs[configArgIndex++] = i - 1;
			configArgs[configArgIndex++] = i;
		}
	}

	// remove all the arguments consumed by this argument parsing
	if (configArgIndex == 0)
		return args;
	String[] passThruArgs = new String[args.length - configArgIndex];
	configArgIndex = 0;
	int j = 0;
	for (int i = 0; i < args.length; i++) {
		if (i == configArgs[configArgIndex])
			configArgIndex++;
		else
			passThruArgs[j++] = args[i];
	}
	return passThruArgs;
}
private static URL[] readPluginPath(InputStream input) {
	Properties ini = new Properties();
	try {
		ini.load(input);
	} catch (IOException e) {
		return null;
	}
	Vector result = new Vector(5);
	for (Enumeration groups = ini.propertyNames(); groups.hasMoreElements();) {
		String group = (String) groups.nextElement();
		for (StringTokenizer entries = new StringTokenizer(ini.getProperty(group), ";"); entries.hasMoreElements();) { //$NON-NLS-1$
			String entry = (String) entries.nextElement();
			if (!entry.equals("")) //$NON-NLS-1$
				try {
					result.addElement(new URL(entry));
				} catch (MalformedURLException e) {
					//intentionally ignore bad URLs
					System.err.println(Policy.bind("ignore.plugin", entry)); //$NON-NLS-1$
				}
		}
	}
	return (URL[]) result.toArray(new URL[result.size()]);
}
public static URL resolve(URL url) throws IOException {
	if (!url.getProtocol().equals(PlatformURLHandler.PROTOCOL))
		return url;
	URLConnection connection = url.openConnection();
	if (connection instanceof PlatformURLConnection)
		return ((PlatformURLConnection) connection).getResolvedURL();
	else
		return url;
}
/**
 * @see BootLoader
 * Retained for compatibility with R1.0 launchers
 */
public static Object run(String applicationName/*R1.0 compatibility*/, URL pluginPathLocation/*R1.0 compatibility*/, String location, String[] args) throws Exception {
	return run(applicationName, pluginPathLocation, location, args, null);
}
/**
 * @see BootLoader
 */
public static Object run(String applicationName/*R1.0 compatibility*/, URL pluginPathLocation/*R1.0 compatibility*/, String location, String[] args, Runnable handler) throws Exception {
	Object result = null;
	applicationR10 = applicationName; // for R1.0 compatibility
	String[] applicationArgs = startup(pluginPathLocation, location, args, handler);
	
	try {
		// check the version to ensure that the workspaces are compatible.
		if (!checkVersion()) {
			if (debugRequested)
				Policy.debug(true, "Version check returned false indicating an incompatible workspace version. Shutting down the platform..."); //$NON-NLS-1$
			shutdown();
			return result;
		}
	} catch (Throwable e) {
		e.printStackTrace();
		throw new InvocationTargetException(e);
	}
	
	String application = getCurrentPlatformConfiguration().getApplicationIdentifier();
	IPlatformRunnable runnable = getRunnable(application);
	if (runnable == null)
		throw new IllegalArgumentException(Policy.bind("application.notFound", application)); //$NON-NLS-1$
	try {
		result = runnable.run(applicationArgs);
	} catch (Throwable e) {
		e.printStackTrace();
		throw new InvocationTargetException(e);
	} finally {
		shutdown();
	}
	return result;
}

private static boolean checkVersion() throws Exception {
	assertRunning();
	Class platform = loader.loadClass(PLATFORM_ENTRYPOINT);
	Method method = platform.getDeclaredMethod("loaderCheckVersion", new Class[0]); //$NON-NLS-1$
	try {
		Object result = method.invoke(platform, new Object[0]);
		return Boolean.TRUE.equals(result);
	} catch (InvocationTargetException e) {
		if (e.getTargetException() instanceof Error)
			throw (Error) e.getTargetException();
		else
			throw e;
	}
}

/**
 * Setup the debug flags for the given debug options.  This method will likely
 * be called twice.  Once when loading the options file from the command
 * line or install dir and then again when we have loaded options from the
 * specific platform metaarea. 
 */
public static void setupOptions() {
	// if no debug option was specified, don't even bother to try.
	// Must ensure that the options slot is null as this is the signal to the
	// platform that debugging is not enabled.
	if (!debugRequested)
		return;
	options.put(OPTION_STARTTIME, Long.toString(System.currentTimeMillis()));
	DelegatingURLClassLoader.DEBUG = getBooleanOption(OPTION_LOADER_DEBUG, false);
	DelegatingURLClassLoader.DEBUG_SHOW_CREATE = getBooleanOption(OPTION_LOADER_SHOW_CREATE, true);
	DelegatingURLClassLoader.DEBUG_SHOW_ACTIVATE = getBooleanOption(OPTION_LOADER_SHOW_ACTIVATE, true);
	DelegatingURLClassLoader.DEBUG_SHOW_ACTIONS = getBooleanOption(OPTION_LOADER_SHOW_ACTIONS, true);
	DelegatingURLClassLoader.DEBUG_SHOW_SUCCESS = getBooleanOption(OPTION_LOADER_SHOW_SUCCESS, true);
	DelegatingURLClassLoader.DEBUG_SHOW_FAILURE = getBooleanOption(OPTION_LOADER_SHOW_FAILURE, true);
	DelegatingURLClassLoader.DEBUG_FILTER_CLASS = getListOption(OPTION_LOADER_FILTER_CLASS);
	DelegatingURLClassLoader.DEBUG_FILTER_LOADER = getListOption(OPTION_LOADER_FILTER_LOADER);
	DelegatingURLClassLoader.DEBUG_FILTER_RESOURCE = getListOption(OPTION_LOADER_FILTER_RESOURCE);
	DelegatingURLClassLoader.DEBUG_FILTER_NATIVE = getListOption(OPTION_LOADER_FILTER_NATIVE);
	PlatformURLConnection.DEBUG = getBooleanOption(OPTION_URL_DEBUG, false);
	PlatformURLConnection.DEBUG_CONNECT = getBooleanOption(OPTION_URL_DEBUG_CONNECT, true);
	PlatformURLConnection.DEBUG_CACHE_LOOKUP = getBooleanOption(OPTION_URL_DEBUG_CACHE_LOOKUP, true);
	PlatformURLConnection.DEBUG_CACHE_COPY = getBooleanOption(OPTION_URL_DEBUG_CACHE_COPY, true);
	PlatformConfiguration.DEBUG = getBooleanOption(OPTION_CONFIGURATION_DEBUG, false);
	
	DelegatingURLClassLoader.MONITOR_PLUGINS = getBooleanOption(OPTION_MONITOR_PLUGINS, DelegatingURLClassLoader.MONITOR_PLUGINS);
	DelegatingURLClassLoader.MONITOR_CLASSES = getBooleanOption(OPTION_MONITOR_CLASSES, DelegatingURLClassLoader.MONITOR_CLASSES);
	DelegatingURLClassLoader.MONITOR_BUNDLES = getBooleanOption(OPTION_MONITOR_BUNDLES, DelegatingURLClassLoader.MONITOR_BUNDLES);

	DelegatingURLClassLoader.TRACE_FILENAME = options.getProperty(OPTION_TRACE_FILENAME, DelegatingURLClassLoader.TRACE_FILENAME); 
	DelegatingURLClassLoader.TRACE_FILTERS = options.getProperty(OPTION_TRACE_FILTERS, DelegatingURLClassLoader.TRACE_FILTERS);
	DelegatingURLClassLoader.TRACE_CLASSES = getBooleanOption(OPTION_TRACE_CLASSES, DelegatingURLClassLoader.TRACE_CLASSES);		
	DelegatingURLClassLoader.TRACE_PLUGINS = getBooleanOption(OPTION_TRACE_PLUGINS, DelegatingURLClassLoader.TRACE_PLUGINS);

	DelegatingURLClassLoader.DEBUG_PROPERTIES = getBooleanOption(OPTION_LOADER_PROPERTIES, DelegatingURLClassLoader.DEBUG_PROPERTIES);
	DelegatingURLClassLoader.DEBUG_PACKAGE_PREFIXES = getBooleanOption(OPTION_LOADER_PACKAGE_PREFIXES, DelegatingURLClassLoader.DEBUG_PACKAGE_PREFIXES);
	DelegatingURLClassLoader.DEBUG_PACKAGE_PREFIXES_SUCCESS = getBooleanOption(OPTION_LOADER_PACKAGE_PREFIXES_SUCCESS, DelegatingURLClassLoader.DEBUG_PACKAGE_PREFIXES_SUCCESS);
	DelegatingURLClassLoader.DEBUG_PACKAGE_PREFIXES_FAILURE = getBooleanOption(OPTION_LOADER_PACKAGE_PREFIXES_FAILURE, DelegatingURLClassLoader.DEBUG_PACKAGE_PREFIXES_FAILURE);
}
/**
 * Initializes the execution context for this run of the platform.  The context
 * includes information about the locale, operating system and window system.
 * 
 * NOTE: The OS, WS, and ARCH values should never be null. The executable should
 * be setting these values and therefore this code path is obsolete for Eclipse
 * when run from the executable.
 */
private static void setupSystemContext() {
	// if the user didn't set the locale with a command line argument then
	// use the default.
	if (nl != null) {
		StringTokenizer tokenizer = new StringTokenizer(nl, "_"); //$NON-NLS-1$
		int segments = tokenizer.countTokens();
		try {
			switch (segments) {
				case 2:
					Locale userLocale = new Locale(tokenizer.nextToken(), tokenizer.nextToken());
					Locale.setDefault(userLocale);
					break;
				case 3:
					userLocale = new Locale(tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken());
					Locale.setDefault(userLocale);
					break;
			}
		} catch (NoSuchElementException e) {
			// fall through and use the default
		}
	}
	nl = Locale.getDefault().toString();

	// if the user didn't set the operating system with a command line 
	// argument then use the default.
	if (os == null) {
		String name = System.getProperty("os.name");//$NON-NLS-1$
		// check to see if the VM returned "Windows 98" or some other
		// flavour which should be converted to win32.
		if (name.regionMatches(true, 0, BootLoader.OS_WIN32, 0, 3))
			os = BootLoader.OS_WIN32;
		// EXCEPTION: All mappings of SunOS convert to Solaris
		if (os == null)
			os = name.equalsIgnoreCase(INTERNAL_OS_SUNOS) ? BootLoader.OS_SOLARIS : BootLoader.OS_UNKNOWN;
	}

	// if the user didn't set the window system with a command line 
	// argument then use the default.
	if (ws == null) {
		// setup default values for known OSes if nothing was specified
		if (os.equals(BootLoader.OS_WIN32))
			ws = BootLoader.WS_WIN32;
		else if (os.equals(BootLoader.OS_LINUX))
			ws = BootLoader.WS_MOTIF;
		else if (os.equals(BootLoader.OS_MACOSX))
			ws = BootLoader.WS_CARBON;
		else if (os.equals(BootLoader.OS_HPUX))
			ws = BootLoader.WS_MOTIF;
		else if (os.equals(BootLoader.OS_AIX))
			ws = BootLoader.WS_MOTIF;
		else if (os.equals(BootLoader.OS_SOLARIS))
			ws = BootLoader.WS_MOTIF;
		else
			ws = BootLoader.WS_UNKNOWN;
	}

	// if the user didn't set the system architecture with a command line 
	// argument then use the default.
	if (arch == null) {
		String name = System.getProperty("os.arch");//$NON-NLS-1$
		// Map i386 architecture to x86
		arch = name.equalsIgnoreCase(INTERNAL_ARCH_I386) ? BootLoader.ARCH_X86 : name;
	}
}
/**
 * @see BootLoader
 */
public static void shutdown() throws Exception {
	assertRunning();
	// no matter what happens, record that its no longer running
	running = false;
	Class platform = loader.loadClass(PLATFORM_ENTRYPOINT);
	Method method = platform.getDeclaredMethod("loaderShutdown", new Class[0]); //$NON-NLS-1$
	try {
		method.invoke(platform, new Object[0]);
	} catch (InvocationTargetException e) {
		if (e.getTargetException() instanceof Error)
			throw (Error) e.getTargetException();
		else
			throw e;
	} finally {
		PlatformURLHandlerFactory.shutdown();
		PlatformConfiguration.shutdown();
		loader = null;
	}
}

/**
 * @see BootLoader
 * Retained for compatibility with R1.0 launchers
 */
public static String[] startup(URL pluginPathLocation/*R1.0 compatibility*/, String location, String[] args) throws Exception {
	return startup(pluginPathLocation, location, args, null); 
}
/**
 * @see BootLoader
 */
public static String[] startup(URL pluginPathLocation/*R1.0 compatibility*/, String location, String[] args, Runnable handler) throws Exception {
	assertNotRunning();
	starting = true;
	commandLine = args;
	String[] applicationArgs = initialize(pluginPathLocation, location, args);
	Class platform = loader.loadClass(PLATFORM_ENTRYPOINT);
	Method method = platform.getDeclaredMethod("loaderStartup", new Class[] { URL[].class, String.class, Properties.class, String[].class, Runnable.class }); //$NON-NLS-1$
	try {
		URL[] pluginPath = getCurrentPlatformConfiguration().getPluginPath();
		method.invoke(platform, new Object[] { pluginPath, baseLocation, options, args, handler });
	} catch (InvocationTargetException e) {
		if (e.getTargetException() instanceof Error)
			throw (Error) e.getTargetException();
		else
			throw e;
	}
	// only record the platform as running if everything went swimmingly
	running = true;
	starting = false;
	return applicationArgs;
}

public  static String getBootDir()  {
	if (BOOTDIR == null) 
		initializeBootDir();
	return BOOTDIR;
}
/**
 * Helper method that creates an URL object from the given string
 * representation. The string must correspond to a valid URL or file system
 * path.
 */
private static URL getURL(String urlString) throws MalformedURLException {
	try {
		return new URL(urlString);
	} catch (MalformedURLException e) {
		// if it is not a well formed URL, tries to create a "file:" URL
		try {
			return new File(urlString).toURL();
		} catch (MalformedURLException ex) {
			// re-throw the original exception if nothing works
			throw e;
		}
	}
}
}
