/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated;

import java.util.Map;

public interface ICountField {

	void putValueCount(String value, Integer count);

	void putAll(Map<String, Integer> m);

	Integer getCountForValue(String value);

	int getDefaultCount();

	int getTableSize();

	String getColumn();

	String getView();

	String getUniqueId();

}