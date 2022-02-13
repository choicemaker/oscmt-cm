/*
 * Copyright (c) 2001, 2015 ChoiceMaker Technologies, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ChoiceMaker Technologies, Inc. - initial API and implementation
 */
package com.choicemaker.cm.core.configure.xml;

/**
 * A registry for xml-configurable objects.
 * @author rphall
 * @since 2.5.206
 */
public interface SpecificationRegistry {

	/**
	 * Retrieves a configurable object
	 */
	XmlConfigurable get(String uniqueId);

	/**
	 * Registers a configurable object under an id that must be unique within
	 * the registry.
	 */
	void register(String uniqueId, XmlConfigurable spec);

	/**
	 * Updates an entry in the registry with the specified configurable object
	 */
	void update(String uniqueId, XmlConfigurable spec);

	/** Removes a configurable object from the registry */
	void remove(String uniqueId);

}

