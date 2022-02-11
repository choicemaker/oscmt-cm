/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.choicemaker.e2.mbd.resources;

import java.net.URL;
import java.net.URLClassLoader;

import com.choicemaker.e2.mbd.spi.EclipseRMIClassLoaderSpi;

/**
 * Comment
 *
 * @author   Martin Buechi
 */
public class AllPluginsClassLoader {
	private static final String RMI_CLASS_LOADER_SPI = "java.rmi.server.RMIClassLoaderSpi";
	private static final String ECLIPSE_RMI_CLASS_LOADER_SPI = "org.eclipse.core.launcher.EclipseRMIClassLoaderSpi";

	public static synchronized ClassLoader getInstance() {
		return AllPluginsClassLoader.class.getClassLoader();
	}
	
	public static ClassLoader getInstance(URL[] classPath, ClassLoader parent) {
		return new Loader(classPath, parent);
	}

	public static void setRMIClassLoaderSpi() {
		EclipseRMIClassLoaderSpi.setClassLoader(getInstance());
		System.setProperty(RMI_CLASS_LOADER_SPI, ECLIPSE_RMI_CLASS_LOADER_SPI);
	}

	private static class Loader extends URLClassLoader {
		public Loader(URL[] path, ClassLoader parent) {
			super(path, parent);
		}
		
		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			try {
				return super.findClass(name);
			} catch (ClassNotFoundException ex) {
				return Class.forName(name);
			}
		}		
	}
}
