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
package com.choicemaker.cms.ejb;

import java.io.Serializable;

import com.choicemaker.cm.core.Accessor;
import com.choicemaker.cm.core.ClueSet;
import com.choicemaker.cm.core.Descriptor;
import com.choicemaker.cm.core.Record;
import com.choicemaker.util.Precondition;

public class TestAccessor<T extends Comparable<T> & Serializable>
		implements Accessor {

	private static final long serialVersionUID = 1L;

	private TestClueSet<T> clueSet;

	public TestAccessor(TestModel<T> model) {
		Precondition.assertNonNullArgument(model);
		this.clueSet = new TestClueSet<T>(model);
	}

	@Override
	public ClueSet getClueSet() {
		return clueSet;
	}

	@Override
	public Descriptor getDescriptor() {
		throw new Error("not implemented");
	}

	@Override
	public String getSchemaFileName() {
		throw new Error("not implemented");
	}

	@Override
	public long getCreationDate() {
		return 0;
	}

	@Override
	public int getNumRecordTypes() {
		return 0;
	}

	@Override
	public String getSchemaName() {
		throw new Error("not implemented");
	}

	@Override
	public String getClueSetName() {
		throw new Error("not implemented");
	}

	@Override
	public Object toHolder(@SuppressWarnings("rawtypes") Record r) {
		if (!(r instanceof TestRecord)) {
			String msg = r == null ? "null record"
					: "Bad class: " + r.getClass().getName();
			throw new ClassCastException(msg);
		}
		return r;
	}

	@Override
	public Object toRecordHolder(@SuppressWarnings("rawtypes") Record r) {
		return toHolder(r);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Record toImpl(Object o) {
		if (!(o instanceof TestRecord)) {
			String msg = o == null ? "null record"
					: "Bad class: " + o.getClass().getName();
			throw new ClassCastException(msg);
		}
		return (TestRecord) o;
	}

}
