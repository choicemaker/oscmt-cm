/*******************************************************************************
 * Copyright (c) 2003, 2018 ChoiceMaker LLC and others.
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
package com.choicemaker.e2.platform;

import com.choicemaker.e2.CMConfigurationElement;
import com.choicemaker.e2.CMExtension;
import com.choicemaker.e2.CMExtensionPoint;
import com.choicemaker.e2.CMPluginDescriptor;

public class DoNothingExtensionPoint implements CMExtensionPoint {

	private static final DoNothingExtensionPoint instance =
		new DoNothingExtensionPoint();

	public static DoNothingExtensionPoint getInstance() {
		return instance;
	}

	private DoNothingExtensionPoint() {
	}

	@Override
	public CMConfigurationElement[] getConfigurationElements() {
		return new CMConfigurationElement[0];
	}

	@Override
	public CMPluginDescriptor getDeclaringPluginDescriptor() {
		return null;
	}

	@Override
	public CMExtension getExtension(String extensionId) {
		return null;
	}

	@Override
	public CMExtension[] getExtensions() {
		return new CMExtension[0];
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public String getSchemaReference() {
		return null;
	}

	@Override
	public String getSimpleIdentifier() {
		return null;
	}

	@Override
	public String getUniqueIdentifier() {
		return null;
	}

}
