/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.urm.ejb;

import com.choicemaker.cm.core.configure.eclipse.EclipseRegistries;
import com.choicemaker.cm.core.configure.eclipse.EclipseRegistry;
import com.choicemaker.cm.urm.IUpdateDerivedFieldsRegistration;

/**
 * This class defines an instance of an eclipse-based registry
 * for derived-field updators.
 * @author rphall
 */
public class DefaultUpdateDerivedFieldsRegistry
	extends EclipseRegistry
	implements IUpdateDerivedFieldsRegistration {

	public static EclipseRegistry getInstance() {
		return EclipseRegistries.getInstance(
			UPDATE_DERIVED_FIELD_EXTENSION_POINT);
	}

	// This constructor is never used
	private DefaultUpdateDerivedFieldsRegistry() {
		super(UPDATE_DERIVED_FIELD_EXTENSION_POINT);
	}

}
