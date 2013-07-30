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

import java.util.*;
import java.net.URL;

/**
 * Compound enumeration with visibility filtering.
 * Used in implementation of ClassLoader.getResources()
 */
 
class ResourceEnumeration implements Enumeration {
	private Vector enums = new Vector();
	private String name;
	private int ix = 0;
	private Object nextElement = null;
	private DelegatingURLClassLoader current;
	private DelegatingURLClassLoader requestor;
public ResourceEnumeration(String name, Enumeration e, DelegatingURLClassLoader current, DelegatingURLClassLoader requestor) {
	this.name = name;
	if (e == null)
		e = new Enumeration() {
		public boolean hasMoreElements() {
			return false;
		}
		public Object nextElement() {
			return null;
		}
	};
	enums.add(e);
	this.current = current;
	this.requestor = requestor;
}
public void add(Enumeration e) {
	if (e == null)
		return;
	enums.add(e);
}
public boolean hasMoreElements() {
	if (nextElement != null)
		return true;
	nextElement = nextVisibleElement();
	return nextElement != null;
}
public Object nextElement() {
	if (nextElement != null) {
		Object result = nextElement;
		nextElement = null;
		return result;
	} else
		return nextVisibleElement();
}
private Object nextVisibleElement() {
	Enumeration e;
	Object element = null;
	while (element == null && ix < enums.size()) {
		e = (Enumeration) enums.elementAt(ix);
		while (element == null && e.hasMoreElements()) {
			element = e.nextElement();
			if (ix == 0) {
				if (!current.isResourceVisible(name, (URL) element, requestor))
					element = null;
			}
		}
		if (element == null)
			ix++;
	}
	return element;
}
}
