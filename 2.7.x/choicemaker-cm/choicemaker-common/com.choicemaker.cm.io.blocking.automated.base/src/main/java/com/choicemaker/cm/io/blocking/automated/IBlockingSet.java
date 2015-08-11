/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated;

import java.io.Serializable;

public interface IBlockingSet extends Serializable {

	int numFields();

	boolean containsBlockingValue(IBlockingValue bv);

	boolean containsField(IField f);

	long getExpectedCount();

	IGroupTable getGroupTable(IBlockingField bf);

	IBlockingValue[] getBlockingValues();

	IBlockingValue getBlockingValue(int i);

	IBlockingValue[] getBlockingValues(IGroupTable gt);

	long getMainTableSize();

	IGroupTable getTable(int i);

	IGroupTable[] getTables();

	int getNumTables();

	boolean returnsSupersetOf(IBlockingSet bs);

	void sortValues(boolean ascending);

	// last element decides
	// sorts like values
	void sortTables(boolean likeValues, boolean firstValueDecidesOrder);

}