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
package com.choicemaker.cm.core.xmlconf;

import org.jasypt.encryption.StringEncryptor;
import org.jdom2.Element;

import com.choicemaker.cm.core.XmlConfException;

/**
 * Base interface for all XML module initializers.
 *
 * Classes that implement this interface should be singletons. The single
 * instance should be accessible as a public (final) static field
 * <code>instance</code>. This field must be intialized when the class is
 * loaded.
 *
 * @author Martin Buechi
 */
public interface XmlModuleInitializer {

	/**
	 * Initializes the non-GUI parts of the module. Equivalent to:
	 * 
	 * <pre>
	 * init(e, null)
	 * </pre>
	 */
	void init(Element e) throws XmlConfException;

	/**
	 * Initializes the non-GUI parts of the module.
	 *
	 * @param e
	 *            The JDOM element for the module from the configuration file.
	 * @param pw
	 *            An optional password that protects sensitive information.
	 * @throws XmlConfException
	 *             if any error occurs during the configuration.
	 */
	void init(Element e, StringEncryptor encryptor) throws XmlConfException;

}
