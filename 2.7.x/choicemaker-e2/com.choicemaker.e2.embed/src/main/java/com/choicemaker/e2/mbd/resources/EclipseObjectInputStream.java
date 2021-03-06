/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.choicemaker.e2.mbd.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

/**
 * Comment
 *
 * @author   Martin Buechi
 */
public class EclipseObjectInputStream extends ObjectInputStream {
	public EclipseObjectInputStream(InputStream in) throws IOException {
		super(in);
	}

	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
		try {
			return super.resolveClass(desc);
		} catch (ClassNotFoundException ex) {
			return Class.forName(desc.getName(), false, AllPluginsClassLoader.getInstance());
		}
	}

	@Override
	protected Class<?> resolveProxyClass(String[] interfaces) throws IOException, ClassNotFoundException {
		try {
			return super.resolveProxyClass(interfaces);
		} catch (ClassNotFoundException ex) {
			ClassLoader allLoader = AllPluginsClassLoader.getInstance();
			ClassLoader nonPublicLoader = null;
			boolean hasNonPublicInterface = false;
			Class<?>[] classObjs = new Class[interfaces.length];
			for (int i = 0; i < interfaces.length; i++) {
				Class<?> cl = Class.forName(interfaces[i], false, allLoader);
				if ((cl.getModifiers() & Modifier.PUBLIC) == 0) {
					if (hasNonPublicInterface) {
						if (nonPublicLoader != cl.getClassLoader()) {
							throw new IllegalAccessError("conflicting non-public interface class loaders");
						}
					} else {
						nonPublicLoader = cl.getClassLoader();
						hasNonPublicInterface = true;
					}
				}
				classObjs[i] = cl;
			}
			try {
				return Proxy.getProxyClass(hasNonPublicInterface ? nonPublicLoader : allLoader, classObjs);
			} catch (IllegalArgumentException e) {
				throw new ClassNotFoundException(null, e);
			}
		}
	}
}
