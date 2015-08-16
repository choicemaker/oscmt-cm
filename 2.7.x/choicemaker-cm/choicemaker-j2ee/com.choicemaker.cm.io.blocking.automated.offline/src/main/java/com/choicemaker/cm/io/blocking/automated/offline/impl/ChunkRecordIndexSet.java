/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.offline.impl;

import java.util.Arrays;
import java.util.BitSet;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.util.LongArrayList;
import com.choicemaker.util.Precondition;
import com.choicemaker.cm.io.blocking.automated.offline.core.EXTERNAL_DATA_FORMAT;
import com.choicemaker.cm.io.blocking.automated.offline.core.IChunkRecordIdSource;
import com.choicemaker.cm.io.blocking.automated.offline.core.IChunkRecordIndexSet;
import com.choicemaker.cm.io.blocking.automated.offline.core.ImmutableRecordIdTranslator;

/**
 * An implementation of IChunkRecordIndexSet that uses
 * {@link com.choicemaker.cm.io.blocking.automated.offline.impl.ChunkRecordIdSource ChunkRecordIdSource}.
 * @author rphall
 * @version $Revision$ $Date$
 */
public class ChunkRecordIndexSet implements IChunkRecordIndexSet {
	
	/**
	 * The default capacity of the storage backing this set, expected as
	 * a number of indices that can be added before the storage must
	 * automatically expand.
	 */
	public static final int DEFAULT_INITIAL_CAPACITY = 1000000;
	
	/**
	 * The default increment (expressed as the number of additional indices) by
	 * which storage will expand whose the capacity of the backing storage is
	 * exceeded.
	 */
	public static final int DEFAULT_CAPACITY_INCREMENT =
		DEFAULT_INITIAL_CAPACITY;

	private static final String errorMsg1 = "type must be "
			+ EXTERNAL_DATA_FORMAT.STRING + " or "
			+ EXTERNAL_DATA_FORMAT.BINARY;

	private static final String errorMsg2 =
		"A set must be opened before it is used.";

	private static boolean precondition2(long[] data) {
		boolean retVal = (data != null);
		return retVal;
	}

	/**
	 * This postcondition is checked at the end of the open method.
	 * Chunk files are supposed to be sorted when they are written
	 * to disk; this postcondition checks this.
	 */
	private static void postcondition1(long[] data) throws Error {
		// data must be non-null
		boolean retVal = (data != null);
		if (retVal) {
			// Data must be sorted in strictly ascending order.
			// First value must >= IRecordIDTranslator3.MINIMUM_VALID_INDEX
			long previous = ImmutableRecordIdTranslator.MINIMUM_VALID_INDEX - 1;
			for (int i=0; i<data.length; i++) {
				long current = data[i];
				if (previous >= current) {
					retVal = false;
					break;
				}
				previous = current;
			}
		}
		if (!retVal) {
			// An algorithm error that should never occur
			throw new Error("data has not been sorted");
		}
	}
	
	/** This postcondition is checked at the end of the open method */
	private static void postcondition2(
		long[] data,
		boolean isDebugEnabled,
		BitSet isChecked)
		throws Error {
		boolean retVal = data != null;
		if (retVal && isDebugEnabled) {
			retVal = isChecked != null;
			retVal = retVal && isChecked.size() >= data.length;
			retVal = retVal && isChecked.cardinality() == 0;
		} else if (retVal) {
			retVal = isChecked == null;
		}
		if (!retVal) {
			// An algorithm error that should never occur
			throw new Error("diagnostics are not set correctly");
		}
	}

	/** The fileName of the ChunkRecordIdSource that backs this set */
	private final String fileName;

	/** The type of the ChunkRecordIdSource that backs this set */
	private final EXTERNAL_DATA_FORMAT type;
	
	/** A flag indicating whether diagnostics are enabled */
	private final boolean isDebugEnabled = false;

	/**
	 * <em>Sorted</em> data from the ChunkRecordIdSource that backs this set.
	 * This field also acts a flag.  If this set is open, it is non-null; otherwise
	 * it is null.
	 */
	private long[] data;
	
	/**
	 * A cached snapshot of the source that backs this set.
	 * This field should always be non-null, but its value varies depending
	 * on whether this set is open or not.
	 */
	private IChunkRecordIdSource source;
	
	/**
	 * Bitmap of indices that <strong><em>have</em></strong> been checked.
	 * @see #getUncheckedIndices()
	 */
	private BitSet isChecked;
	
	public ChunkRecordIndexSet (String fileName, EXTERNAL_DATA_FORMAT type) {
		Precondition.assertNonEmptyString(fileName);
		Precondition.assertNonNullArgument(errorMsg1,type);
		this.fileName = fileName;
		this.type = type;
		this.source = new ChunkRecordIdSource(this.fileName,this.type);
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.IChunkRecordIndexSet#containsRecordIndex(long)
	 */
	public boolean containsRecordIndex(long recordIndex) throws BlockingException {
		Precondition.assertBoolean(errorMsg1,precondition2(this.data));
		int insertionPoint = Arrays.binarySearch(this.data,recordIndex);
		boolean retVal = (insertionPoint>=0);
		if (retVal && isDebugEnabled()) {
			this.isChecked.set(insertionPoint);
		}
		return retVal;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.IChunkRecordIndexSet#getSource()
	 */
	public IChunkRecordIdSource getSource() {
		return this.source;
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.IChunkRecordIndexSet#exists()
	 */
	public boolean exists() {
		return this.getSource().exists();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.IChunkRecordIndexSet#open()
	 */
	public void open() throws BlockingException {

		int capacity = DEFAULT_INITIAL_CAPACITY;
		LongArrayList list = new LongArrayList(capacity);

		IChunkRecordIdSource src = this.getSource();
		src.open();

		int count = 0;
		while (src.hasNext()) {
			++count;
			if (count > capacity) {
				capacity += DEFAULT_INITIAL_CAPACITY;
				list.ensureCapacity(capacity);
			}
			long index = src.next();
			list.add(index);
		}
		this.data = list.toArray();

		// Chunk files are supposed to be sorted when they
		// are written to disk; postcondition1 checks this.
		// There's no need for a redundant sort here.
		//Arrays.sort(this.data);
		
		this.source = createIndexSource(this.data);
		
		if (this.isDebugEnabled()) {
			this.isChecked = new BitSet(this.data.length);
		}

		postcondition1(this.data);
		postcondition2(this.data,this.isDebugEnabled(),this.isChecked);
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.IChunkRecordIndexSet#close()
	 */
	public void close() throws BlockingException {
		this.data = null;
		this.getSource().close();
		this.source = new ChunkRecordIdSource(this.fileName,this.type);
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.IChunkRecordIndexSet#getInfo()
	 */
	public String getInfo() {
		return this.getSource().getInfo();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.IChunkRecordIndexSet#remove()
	 */
	public void remove() throws BlockingException {
		if (this.data != null) {
			this.close();
		}
		this.getSource().delete();
	}

	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.IChunkRecordIndexSet#isDebugEnabled()
	 */
	public boolean isDebugEnabled() {
		return isDebugEnabled;
	}
	
	/* (non-Javadoc)
	 * @see com.choicemaker.cm.io.blocking.automated.offline.core.IChunkRecordIndexSet#isDebugEnabled()
	 */
	public IChunkRecordIdSource getUncheckedIndices() {
		IChunkRecordIdSource retVal;
		synchronized(this) {
			int countUnchecked = this.isChecked.size() - this.isChecked.cardinality();
			LongArrayList unchecked = new LongArrayList(countUnchecked);
			for (int i = this.isChecked.nextClearBit(0);
				i >= 0 && i < this.data.length;
				i = this.isChecked.nextClearBit(i + 1)) {
				long uncheckedIndex = this.data[i];
				unchecked.add(uncheckedIndex);
			}
			// assert unchecked.size() == countUnchecked ;
			long[] indices = unchecked.toArray();
			retVal = createIndexSource(indices);
		}
		return retVal;
	}
	
	private static IChunkRecordIdSource createIndexSource(final long[] indices) {
		final long[] copy;
		synchronized (indices) {
			copy = new long[indices.length];
			System.arraycopy(indices,0,copy,0,indices.length);
		}
		IChunkRecordIdSource retVal = new IChunkRecordIdSource() {
			private int current = 0;
			public Long next() throws BlockingException {
				if (current >= copy.length) {
					throw new ArrayIndexOutOfBoundsException(
						"current: " + current + "; length: " + copy.length);
				}
				return copy[current++];
			}

			public boolean exists() {
				return true;
			}

			public void open() throws BlockingException {
			}

			public boolean hasNext() throws BlockingException {
				return current < copy.length;
			}

			public void close() throws BlockingException {
			}

			public String getInfo() {
				return null;
			}

			public void delete() throws BlockingException {
			}
		};
		return retVal;
	}

}

