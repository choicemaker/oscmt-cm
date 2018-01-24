/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.aba;

import java.io.Serializable;

public interface IBlockingValue extends Cloneable, Serializable {

	public Object clone();

	boolean containsBase(IBlockingSet bs);

	IBlockingField getBlockingField();

	IBlockingValue[][] getBase();

	String getValue();

	String getGroup();

	int getCount();

	int getTableSize();

	IGroupTable getGroupTable();

	void setTableSize(int tableSize);

	void setCount(int count);

	int compareTo(IBlockingValue obv);

	boolean equals(Object o);

	int hashCode();

}