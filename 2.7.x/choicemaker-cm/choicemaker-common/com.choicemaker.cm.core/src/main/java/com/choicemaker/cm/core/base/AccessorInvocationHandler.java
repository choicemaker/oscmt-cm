/*
 * Copyright (c) 2001, 2022 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.core.base;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.choicemaker.cm.core.Accessor;

class AccessorInvocationHandler implements InvocationHandler {
	private Accessor accessor;
	private Map methodMap;
	AccessorInvocationHandler(Accessor accessor) {
		this.accessor = accessor;
		Method[] methods = accessor.getClass().getMethods();
		methodMap = new HashMap(methods.length);
		for (int i = 0; i < methods.length; i++) {
			methodMap.put(methods[i].getName(), methods[i]);
		}
	}
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Method targetMethod = (Method) methodMap.get(method.getName());
		try {
			return targetMethod.invoke(accessor, args);
		} catch (InvocationTargetException ex) {
			throw ex.getTargetException();
		}
	}
}
