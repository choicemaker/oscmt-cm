/*******************************************************************************
 * Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.cm.core.configure;

import com.choicemaker.cm.core.XmlConfException;

public interface ChoiceMakerConfigurator {

	/**
	 * Name of a public, static, final field referencing a non-null
	 * ChoiceMakerConfigurator instance
	 */
	public static final String INSTANCE = "instance";

	/**
	 * Creates and initializes a ChoiceMaker configuration using default values.
	 * The default values are obtained by methods that are dependent on the
	 * operating environment, and thus are specified by particular
	 * implementations of this interface.
	 *
	 * @throws XmlConfException
	 *             if a valid configuration can not be created and initialized
	 */
	ChoiceMakerConfiguration init() throws XmlConfException;

	/**
	 * Creates and initializes a ChoiceMaker configuration.
	 *
	 * @param fn
	 *            path to an XML configuration file
	 * @param reload
	 * @param initGui
	 * @return A non-null, valid ChoiceMaker configuration
	 * @throws XmlConfException
	 *             if a valid configuration can not be created and initialized
	 */
	ChoiceMakerConfiguration init(String fn, boolean reload, boolean initGui)
			throws XmlConfException;

	/**
	 * For backward compatibility with ChoiceMaker 2.3. The
	 * <code>logConfName</code> argument is ignored and should be null.
	 * Equivalent to invoking:
	 *
	 * <pre>
	 * init(fn, reload, initGui, (char[]) null)
	 * </pre>
	 *
	 * @param fn
	 *            path to an XML configuration file
	 * @param unusedLogConfName
	 *            ignored with a warning message
	 * @param reload
	 * @param initGui
	 * @param password
	 *            an optional password for decrypting sensitive configuration
	 *            values. If null or empty, the password is ignored.
	 * @return A non-null, valid ChoiceMaker configuration
	 * @throws XmlConfException
	 *             if a valid configuration can not be created and initialized
	 */
	ChoiceMakerConfiguration init(String fn, String unusedLogConfName,
			boolean reload, boolean initGui) throws XmlConfException;

	ChoiceMakerConfiguration init(String fn, boolean reload, boolean initGui,
			char[] password) throws XmlConfException;

}
