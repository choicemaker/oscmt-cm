/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.util;

import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * Some missing manifest constants and set/unset methods.
 * 
 * @author rphall
 */
public class SystemPropertyUtils {

	// -- Property names (PN_)

	/** Property Name for Property Name for Java Runtime Environment version */
	public static final String PN_JAVA_VERSION = "java.version";

	/** Property Name for Property Name for Java Runtime Environment vendor */
	public static final String PN_JAVA_VENDOR = "java.vendor";

	/** Property Name for Property Name for Java vendor URL */
	public static final String PN_JAVA_VENDOR_URL = "java.vendor.url";

	/** Property Name for Property Name for Java installation directory */
	public static final String PN_JAVA_HOME = "java.home";

	/** Property Name for Java Virtual Machine specification version */
	public static final String PN_JAVA_VM_SPECIFICATION_VERSION =
		"java.vm.specification.version";

	/** Property Name for Java Virtual Machine specification vendor */
	public static final String PN_JAVA_VM_SPECIFICATION_VENDOR =
		"java.vm.specification.vendor";

	/** Property Name for Java Virtual Machine specification name */
	public static final String PN_JAVA_VM_SPECIFICATION_NAME =
		"java.vm.specification.name";

	/** Property Name for Java Virtual Machine implementation version */
	public static final String PN_JAVA_VM_VERSION = "java.vm.version";

	/** Property Name for Java Virtual Machine implementation vendor */
	public static final String PN_JAVA_VM_VENDOR = "java.vm.vendor";

	/** Property Name for Java Virtual Machine implementation name */
	public static final String PN_JAVA_VM_NAME = "java.vm.name";

	/** Property Name for Java Runtime Environment specification version */
	public static final String PN_JAVA_SPECIFICATION_VERSION =
		"java.specification.version";

	/** Property Name for Java Runtime Environment specification vendor */
	public static final String PN_JAVA_SPECIFICATION_VENDOR =
		"java.specification.vendor";

	/** Property Name for Java Runtime Environment specification name */
	public static final String PN_JAVA_SPECIFICATION_NAME =
		"java.specification.name";

	/** Property Name for Java class format version number */
	public static final String PN_JAVA_CLASS_VERSION = "java.class.version";

	/** Property Name for Java class path */
	public static final String PN_JAVA_CLASS_PATH = "java.class.path";

	/** Property Name for List of paths to search when loading libraries */
	public static final String PN_JAVA_LIBRARY_PATH = "java.library.path";

	/** Property Name for Default temp file path */
	public static final String PN_JAVA_IO_TMPDIR = "java.io.tmpdir";

	/** Property Name for Name of JIT compiler to use */
	public static final String PN_JAVA_COMPILER = "java.compiler";

	/** Property Name for Path of extension directory or directories */
	public static final String PN_JAVA_EXT_DIRS = "java.ext.dirs";

	/** Property Name for Operating system name */
	public static final String PN_OS_NAME = "os.name";

	/** Property Name for Operating system architecture */
	public static final String PN_OS_ARCH = "os.arch";

	/** Property Name for Operating system version */
	public static final String PN_OS_VERSION = "os.version";

	/** Property Name for File separator ("/" on UNIX) */
	public static final String PN_FILE_SEPARATOR = "file.separator";

	/** Property Name for Path separator (":" on UNIX) */
	public static final String PN_PATH_SEPARATOR = "path.separator";

	/** Property Name for Line separator ("n" on UNIX) */
	public static final String PN_LINE_SEPARATOR = "line.separator";

	/** Property Name for User's account name */
	public static final String PN_USER_NAME = "user.name";

	/** Property Name for User's home directory */
	public static final String PN_USER_HOME = "user.home";

	/** Property Name for User's current working directory */
	public static final String PN_USER_DIR = "user.dir";

	// -- Property values (PV_)

	/** Property Value of Property Value of Java Runtime Environment version */
	public static final String PV_JAVA_VERSION = System
			.getProperty(PN_JAVA_VERSION);

	/** Property Value of Property Value of Java Runtime Environment vendor */
	public static final String PV_JAVA_VENDOR = System
			.getProperty(PN_JAVA_VENDOR);

	/** Property Value of Property Value of Java vendor URL */
	public static final String PV_JAVA_VENDOR_URL = System
			.getProperty(PN_JAVA_VENDOR_URL);

	/** Property Value of Property Value of Java installation directory */
	public static final String PV_JAVA_HOME = System.getProperty(PN_JAVA_HOME);

	/**
	 * Property Value of Property Value of Java Virtual Machine specification
	 * version
	 */
	public static final String PV_JAVA_VM_SPECIFICATION_VERSION = System
			.getProperty(PN_JAVA_VM_SPECIFICATION_VERSION);

	/**
	 * Property Value of Property Value of Java Virtual Machine specification
	 * vendor
	 */
	public static final String PV_JAVA_VM_SPECIFICATION_VENDOR = System
			.getProperty(PN_JAVA_VM_SPECIFICATION_VENDOR);

	/**
	 * Property Value of Property Value of Java Virtual Machine specification
	 * name
	 */
	public static final String PV_JAVA_VM_SPECIFICATION_NAME = System
			.getProperty(PN_JAVA_VM_SPECIFICATION_NAME);

	/**
	 * Property Value of Property Value of Java Virtual Machine implementation
	 * version
	 */
	public static final String PV_JAVA_VM_VERSION = System
			.getProperty(PN_JAVA_VM_VERSION);

	/**
	 * Property Value of Property Value of Java Virtual Machine implementation
	 * vendor
	 */
	public static final String PV_JAVA_VM_VENDOR = System
			.getProperty(PN_JAVA_VM_VENDOR);

	/**
	 * Property Value of Property Value of Java Virtual Machine implementation
	 * name
	 */
	public static final String PV_JAVA_VM_NAME = System
			.getProperty(PN_JAVA_VM_NAME);

	/**
	 * Property Value of Property Value of Java Runtime Environment
	 * specification version
	 */
	public static final String PV_JAVA_SPECIFICATION_VERSION = System
			.getProperty(PN_JAVA_SPECIFICATION_VERSION);

	/**
	 * Property Value of Property Value of Java Runtime Environment
	 * specification vendor
	 */
	public static final String PV_JAVA_SPECIFICATION_VENDOR = System
			.getProperty(PN_JAVA_SPECIFICATION_VENDOR);

	/**
	 * Property Value of Property Value of Java Runtime Environment
	 * specification name
	 */
	public static final String PV_JAVA_SPECIFICATION_NAME = System
			.getProperty(PN_JAVA_SPECIFICATION_NAME);

	/** Property Value of Java class format version number */
	public static final String PV_JAVA_CLASS_VERSION = System
			.getProperty(PN_JAVA_CLASS_VERSION);

	/** Property Value of Java class path */
	public static final String PV_JAVA_CLASS_PATH = System
			.getProperty(PN_JAVA_CLASS_PATH);

	/** Property Value of List of paths to search when loading libraries */
	public static final String PV_JAVA_LIBRARY_PATH = System
			.getProperty(PN_JAVA_LIBRARY_PATH);

	/** Property Value of Default temp file path */
	public static final String PV_JAVA_IO_TMPDIR = System
			.getProperty(PN_JAVA_IO_TMPDIR);

	/** Property Value of Name of JIT compiler to use */
	public static final String PV_JAVA_COMPILER = System
			.getProperty(PN_JAVA_COMPILER);

	/** Property Value of Path of extension directory or directories */
	public static final String PV_JAVA_EXT_DIRS = System
			.getProperty(PN_JAVA_EXT_DIRS);

	/** Property Value of Operating system name */
	public static final String PV_OS_NAME = System.getProperty(PN_OS_NAME);

	/** Property Value of Operating system architecture */
	public static final String PV_OS_ARCH = System.getProperty(PN_OS_ARCH);

	/** Property Value of Operating system version */
	public static final String PV_OS_VERSION = System
			.getProperty(PN_OS_VERSION);

	/** Property Value of File separator ("/" on UNIX) */
	public static final String PV_FILE_SEPARATOR = System
			.getProperty(PN_FILE_SEPARATOR);

	/** Property Value of Path separator (":" on UNIX) */
	public static final String PV_PATH_SEPARATOR = System
			.getProperty(PN_PATH_SEPARATOR);

	/** Property Value of Line separator ("n" on UNIX) */
	public static final String PV_LINE_SEPARATOR = System
			.getProperty(PN_LINE_SEPARATOR);

	/** Property Value of User's account name */
	public static final String PV_USER_NAME = System.getProperty(PN_USER_NAME);

	/** Property Value of User's home directory */
	public static final String PV_USER_HOME = System.getProperty(PN_USER_HOME);

	/** Property Value of User's current working directory */
	public static final String PV_USER_DIR = System.getProperty(PN_USER_DIR);

	private SystemPropertyUtils() {
	}

	/**
	 * Conditionally sets a System property only if it hasn't already been set.
	 * Same as invoking
	 * 
	 * <pre>
	 * setSystemProperty(false, key, value)
	 * </pre>
	 * 
	 * @param key
	 *            a non-null String
	 * @param value
	 *            a non-null String
	 */
	public static void setPropertyIfMissing(String key, String value) {
		setProperty(false, key, value);
	}

	/**
	 * Sets a System property.
	 * 
	 * @param key
	 *            a non-null String
	 * @param value
	 *            a non-null String
	 * @param force
	 *            if true, forces the property to be set, even if the property
	 *            has already been set to another value. If false, the property
	 *            is set only if it hasn't already been set.
	 */
	public static void setProperty(boolean force, String key, String value) {
		boolean doSet = force || System.getProperty(key) == null;
		if (doSet) {
			System.setProperty(key, value);
		}
	}

	public static void unsetProperty(String key) {
		Properties p = System.getProperties();
		p.remove(key);
	}

	public static SortedMap<String,String> listSystemProperties() {
		Properties p = System.getProperties();
		SortedMap<String,String> retVal = new TreeMap<>();
		Set<Object> keys = p.keySet();
		for (Object k : keys) {
			Object v = p.get(k);
			if (k instanceof String && v instanceof String) {
				retVal.put((String)k, (String)v);
			}
		}
		return retVal;
	}

	public static void logSystemProperties(Logger logger) {
		SortedMap<String,String> p = listSystemProperties();
		Set<String> sortedKeys = p.keySet();
		for (String key : sortedKeys) {
			String value = p.get(key);
			String msg = "System property '" + key + "'/'" + value + "'";
			logger.info(msg);
		}
	}

}
