/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.core;

import java.util.List;

/**
 * Extends the IRecordIDTranslator2 interface by adding a lookup method that
 * returns the internal id of a specified record id.
 * 
 * @author rphall
 */
public interface ImmutableRecordIdTranslator<T extends Comparable<T>> {

	/**
	 * A magic value returned by {@link #getSplitIndex()} indicating that the
	 * translator is not split. Numerically equal to {@link #INVALID_INDEX}.
	 */
	public static final int NOT_SPLIT = -1;

	/** Returned from lookup if no internal index exists */
	public int INVALID_INDEX = -1;

	/** Minimum valid index that will be returned by lookup */
	public int MINIMUM_VALID_INDEX = INVALID_INDEX + 1;

	/** Returns the type of record identifier handled by this translator */
	RECORD_ID_TYPE getRecordIdType();

	/**
	 * This returns the internal id at which the second source begins. This is 0
	 * if there is only one source.
	 * 
	 * RecordSource1 would have internal id from 0 to splitIndex - 1. And
	 * RecordSource2 starts from splitIndex.
	 */
	public int getSplitIndex();

	/** Indicates whether the translator has been split */
	boolean isSplit();

	/**
	 * Returns the internal id (a.k.a. index) of a specified staging record id,
	 * or {@link #INVALID_INDEX} if the record id has not been indexed.
	 */
	int lookupStagingIndex(T recordID);

	/**
	 * Returns the internal id (a.k.a. index) of a specified master record id,
	 * or {@link #INVALID_INDEX} if the record id has not been indexed.
	 */
	int lookupMasterIndex(T recordID);

	/**
	 * This returns a List of record IDs from the first source. Usually, the
	 * staging source.
	 */
	public List<T> getList1();

	/**
	 * This returns a List of record IDs from the second source. Usually, the
	 * master source.
	 */
	public List<T> getList2();

	/**
	 * This method returns the original record ID associated with this internal
	 * ID. Make sure the method initReverseTranslation is called before this
	 * method.
	 * 
	 * @return Comparable<?> - the original record ID associated with this
	 *         internal ID.
	 */
	public Comparable<?> reverseLookup(int internalID);

}
