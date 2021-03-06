/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.mmdevtools.util;

import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.RecordData;

/**
 * @author Owner
 *
 */
public class AllFilter implements Filter {
	@Override
	public boolean satisfy(Record r) {
		return true;	
	}
	@Override
	public boolean satisfy(RecordData rd) {
		return true;
	}
}
