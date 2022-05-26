/*******************************************************************************
 * Copyright (c) 2003, 2021 ChoiceMaker LLC and others.
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
package com.choicemaker.cms.api;

import com.choicemaker.util.TypedValue;

public interface NamedConfigurationExt extends NamedConfiguration {

	<T> void setTypedAttribute(String attributeName, TypedValue<T> typedValue);
	
	<T> TypedValue<T> getTypedAttributeValue(String attributeName);

}