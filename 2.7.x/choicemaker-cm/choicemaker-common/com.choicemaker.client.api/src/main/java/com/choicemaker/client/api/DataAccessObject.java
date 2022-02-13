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
 *     ChoiceMaker Technologies, Inc  - initial API and implementation
 *     ChoiceMaker LLC - version 2.7 and following
 *******************************************************************************/
package com.choicemaker.client.api;

import java.io.Serializable;

/**
 * The interface implemented by generated record-holder classes, which are
 * serializable and identifiable (with serializable identifiers).
 */
public interface DataAccessObject<T extends Comparable<T> & Serializable>
		extends Identifiable<T>, Serializable {
}
