/*******************************************************************************
 * Copyright (c) 2017, 2020 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.client.api;

public interface Identifiable<T extends Comparable<T>> {

	/**
	 * Returns a key that uniquely identifies an entity. If two records have
	 * different identifiers, then they represent different entities (in the
	 * absence of duplicates).
	 *
	 * @return the key that identifies this instance
	 */
	T getId();

}
