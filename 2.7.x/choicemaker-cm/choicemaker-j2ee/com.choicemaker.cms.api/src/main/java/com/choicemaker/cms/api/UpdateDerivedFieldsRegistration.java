/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cms.api;

/**
 * Defines the extension point for updateDerivedFields.
 * 
 * @see com.choicemaker.cm.core.configure.XmlConfigurablesRegistry
 * @author rphall
 * @since 2.5.206
 */
public interface UpdateDerivedFieldsRegistration {

	/**
	 * The extension point,
	 * <code>com.choicemaker.cm.urm.updateDerivedFields</code>
	 */
	public static final String UPDATE_DERIVED_FIELD_EXTENSION_POINT =
		// Avoid dependence on com.choicemaker.cm.core package
		// ChoiceMakerExtensionPoint.CM_URM_UPDATEDERIVEDFIELDS;
		"com.choicemaker.cm.urm.updateDerivedFields";

}
