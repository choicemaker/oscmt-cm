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
package com.choicemaker.cm.core.sort;

import com.choicemaker.cm.core.Descriptor;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.base.DescriptorCollection;

/**
 * Comment
 *
 * @author   Martin Buechi
 */
public class FieldCondition implements Condition {
	private Descriptor descriptor;
	private int col;
	private Object value;
	
	public FieldCondition(Descriptor rootDescriptor, String node, String field, Object value) {
		DescriptorCollection c = new DescriptorCollection(rootDescriptor);
		descriptor = c.getDescriptor(node);
		col = descriptor.getColumnIndexByName(field);
		this.value = value;
	}

	@Override
	public boolean accept(Record r, int row, Object val) {
		Object o = descriptor.getValue(r, row, col);
		return val == null ? o == null : value.equals(o);
	}
}
