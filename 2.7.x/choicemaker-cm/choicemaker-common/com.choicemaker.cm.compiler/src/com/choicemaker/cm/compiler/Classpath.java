/*
 * Copyright (c) 2001, 2009 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.compiler;

import java.io.File;

/**
 * Classpath management.
 * 
 * @author   Matthias Zenger
 * @author   Martin Buechi
 * @version  $Revision: 1.1.1.1 $ $Date: 2009/05/03 16:02:35 $
 */
public class Classpath {

	/** the file separator character
	 */
	protected static String FILE_SEP = File.separator;

	/** the string used as separator in class paths
	 */
	protected static String PATH_SEP = System.getProperty("path.separator");

	/** the boot class path
	 */
	protected static String CLASS_PATH = System.getProperty("java.class.path") + PATH_SEP;

	/** the boot class path
	 */
	protected static String BOOT_PATH = System.getProperty("sun.boot.class.path") + PATH_SEP;

	/** the extension path
	 */
	protected static String EXTENSION_PATH = System.getProperty("java.ext.dirs") + PATH_SEP;

	/** the classpath string
	 */
	protected String[] components;

	/** classpath constructor
	 */
	public Classpath(String cp, String classpath) {
		if (classpath == null) {
			classpath = addDefaults(cp); // CLASS_PATH
		} else {
			classpath = addDefaults(classpath);
		}
		components = decompose(classpath);
	}

	/** mix in boot class path and the jars in the extension directory
	 */
	protected String addDefaults(String path) {
		if (!path.endsWith(PATH_SEP))
			path += PATH_SEP;
		if (EXTENSION_PATH != null) {
			int length = EXTENSION_PATH.length();
			int i = 0;
			String prefix = "";
			while (i < length) {
				int k = EXTENSION_PATH.indexOf(PATH_SEP, i);
				String dirname = EXTENSION_PATH.substring(i, k);
				String[] ext;
				if ((dirname != null)
					&& (dirname.length() > 0)
					&& ((ext = new File(dirname).getAbsoluteFile().list()) != null)) {
					if (!dirname.endsWith(FILE_SEP))
						dirname += FILE_SEP;
					for (int j = 0; j < ext.length; j++)
						if (ext[j].toLowerCase().endsWith(".jar") || ext[j].toLowerCase().endsWith(".zip"))
							prefix += dirname + ext[j] + PATH_SEP;
				}
				i = k + 1;
			}
			path = prefix + path;
		}
		if (BOOT_PATH != null)
			path = BOOT_PATH + path;
		return path;
	}

	/** decompose path specification
	 */
	protected String[] decompose(String path) {
		int i = 0;
		int n = 0;
		while (i < path.length()) {
			i = path.indexOf(PATH_SEP, i) + 1;
			n++;
		}
		String[] components = new String[n];
		i = 0;
		n = 0;
		while (i < path.length()) {
			int j = path.indexOf(PATH_SEP, i);
			components[n++] = path.substring(i, j);
			i = j + 1;
		}
		return components;
	}

	/** return class path components
	 */
	public String[] getComponents() {
		return components;
	}

	/** return string representation of all components
	 */
	public String toString() {
		if (components.length == 0)
			return "";
		else if (components.length == 1)
			return components[0];
		String path = components[0];
		for (int i = 1; i < components.length; i++)
			path += PATH_SEP + components[i];
		return path;
	}
}