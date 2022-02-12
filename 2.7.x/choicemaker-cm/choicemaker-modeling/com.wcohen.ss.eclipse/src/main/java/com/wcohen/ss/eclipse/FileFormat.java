/*******************************************************************************
 * Copyright (c) 2007, 2014 ChoiceMaker LLC and others.
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
package com.wcohen.ss.eclipse;

/**
 * Persisted via Java serialization
 * @author rphall
 */
public class FileFormat {
	
	private final String name;
	
	private FileFormat(String _name) {
		this.name = _name;
	}

	public static final String NAME_BINARY = "binary";
	
	/** Persisted via Java binary serialization */
	public static final FileFormat BINARY = new FileFormat(NAME_BINARY);
	
	public static final String NAME_BETWIXT = "betwixt";
	
	/** Persisted via Betwixt XML serialization */
	public static final FileFormat BETWIXT = new FileFormat(NAME_BETWIXT);

	public static FileFormat getInstance(String name) {
		if (NAME_BINARY.equalsIgnoreCase(name)) {
			return BINARY;
		} else if (NAME_BETWIXT.equalsIgnoreCase(name)) {
			return BETWIXT;
		} else {
			throw new RuntimeException("Unknown type: '" + name + "'");
		}
	}
	
	public String getName() {
		return NAME_BINARY;
	}
	
	public boolean equals(Object o) {
		boolean retVal = (o instanceof FileFormat)
			&& ((FileFormat)o).getName().equalsIgnoreCase(this.getName());
		return retVal;
	}
	
	public int hashCode() {
		return this.name.hashCode();
	}
	
}


