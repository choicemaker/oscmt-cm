/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.choicemaker.e2.mbd.spi;

import java.net.MalformedURLException;
import java.rmi.server.RMIClassLoader;
import java.rmi.server.RMIClassLoaderSpi;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Comment
 *
 * @author Martin Buechi
 */
public class EclipseRMIClassLoaderSpi extends RMIClassLoaderSpi {
	private static ClassLoader classLoader;

	public static void setClassLoader(ClassLoader v) {
		classLoader = v;
	}

	@Override
	public ClassLoader getClassLoader(String codebase)
			throws MalformedURLException {
		return RMIClassLoader.getDefaultProviderInstance().getClassLoader(
				codebase);
	}

	@Override
	public String getClassAnnotation(Class<?> cl) {
		RMIClassLoaderSpi defaultProvider =
			RMIClassLoader.getDefaultProviderInstance();
		if (cl.isPrimitive() || cl.isArray()) {
			return defaultProvider.getClassAnnotation(cl);
		} else {
			Set<String> codebaseElements = new HashSet<>();
			addClassAnnotations(codebaseElements, cl, defaultProvider);
			StringBuffer annotation = new StringBuffer();
			boolean first = true;
			for (Iterator<String> iCodeBaseElements =
				codebaseElements.iterator(); iCodeBaseElements.hasNext();) {
				if (first) {
					first = false;
				} else {
					annotation.append(' ');
				}
				annotation.append(iCodeBaseElements.next());
			}
			return annotation.toString();
		}
	}

	private void addClassAnnotations(Set<String> codebaseElements, Class<?> cl,
			RMIClassLoaderSpi defaultProvider) {
		String defaultAnnotation = defaultProvider.getClassAnnotation(cl);
		StringTokenizer st = new StringTokenizer(defaultAnnotation, " ");
		while (st.hasMoreTokens()) {
			codebaseElements.add(st.nextToken());
		}
		Class<?> superclass = cl.getSuperclass();
		if (superclass != null) {
			addClassAnnotations(codebaseElements, superclass, defaultProvider);
		}
		Class<?>[] interfaces = cl.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			addClassAnnotations(codebaseElements, interfaces[i],
					defaultProvider);
		}
	}

	@Override
	public Class<?> loadClass(String codebase, String name,
			ClassLoader defaultLoader) throws MalformedURLException,
			ClassNotFoundException {
		if (defaultLoader != null) {
			try {
				return defaultLoader.loadClass(name);
			} catch (ClassNotFoundException ex) {
			}
		}
		if (codebase != null) {
			try {
				return getClassLoader(codebase).loadClass(name);
			} catch (ClassNotFoundException ex) {
			}
		}
		return classLoader.loadClass(name);
	}

	@Override
	public Class<?> loadProxyClass(String codebase, String[] interfaces,
			ClassLoader defaultLoader) throws MalformedURLException,
			ClassNotFoundException {
		return RMIClassLoader.getDefaultProviderInstance().loadProxyClass(
				codebase, interfaces, classLoader);
	}

}
