/*******************************************************************************
 * Copyright (c) 2003, 2016 ChoiceMaker LLC and others.
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
/*
 * Created on Sep 19, 2009
 */
package com.choicemaker.cmit.online;

import com.choicemaker.cm.core.DerivedSource;
import com.choicemaker.cm.core.Record;


public class RecordStub implements Record {
	private static final long serialVersionUID = 271L;
	private Comparable<?> id;
	public RecordStub(Comparable<?> id) {
		this.id = id;
		if (id == null) {
			throw new IllegalArgumentException("invalid null argument");
		}
	}
	public DerivedSource getDerivedSource() {
		return DerivedSource.NONE;
	}
	public void computeValidityAndDerived() {
	}
	public void computeValidityAndDerived(DerivedSource unused) {
	}
	public void resetValidityAndDerived(DerivedSource unused) {
	}
	public Comparable<?> getId() {
		return this.id;
	}
}
