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
package com.choicemaker.e2.mbd.core.runtime.model;

/**
 * An object which represents a named URL in a component or configuration
 * manifest.
 * <p>
 * This class may be instantiated and further subclassed.
 * </p>
 */

public class URLModel extends PluginModelObject {
	// DTD properties (included in install manifest)
	private String url = null;

/**
 * Returns the URL specification.
 *
 * @return the URL specification or <code>null</code>.
 */
public String getURL() {
	return url;
}

/**
 * Sets the URL specification.
 * This object must not be read-only.
 *
 * @param value the URL specification.
 *		May be <code>null</code>.
 */
public void setURL(String value) {
	assertIsWriteable();
	url = value;
}

}
