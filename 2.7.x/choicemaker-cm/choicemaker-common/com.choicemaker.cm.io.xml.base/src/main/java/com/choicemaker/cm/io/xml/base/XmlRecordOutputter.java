/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.xml.base;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

import com.choicemaker.cm.core.Record;

/**
 * Description
 *
 * @author    Martin Buechi
 */
public interface XmlRecordOutputter extends Serializable {
	void put(Writer w, Record r) throws IOException;
}
