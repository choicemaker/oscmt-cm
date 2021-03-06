/*******************************************************************************
 * Copyright (c) 2015, 2019 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.impl;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.core.BlockSet;
import com.choicemaker.cm.oaba.core.EXTERNAL_DATA_FORMAT;
import com.choicemaker.cm.oaba.core.IBlockSource;
import com.choicemaker.util.IntArrayList;
import com.choicemaker.util.LongArrayList;

/**
 * This object encapsulates the reading of the oversized blocks file.
 *
 * @author pcheung
 *
 */
public class BlockSource extends BaseFileSource<BlockSet> implements
		IBlockSource {

	private BlockSet nextBS;

	public BlockSource(String fileName) {
		super(fileName, EXTERNAL_DATA_FORMAT.BINARY);
	}

	@Deprecated
	public BlockSource(String fileName, int type) {
		super(fileName, EXTERNAL_DATA_FORMAT.fromSymbol(type));
	}

	public BlockSource(String fileName, EXTERNAL_DATA_FORMAT type) {
		super(fileName, type);
	}

	@Override
	protected void resetNext() {
		nextBS = null;
	}

	private BlockSet readNext() throws EOFException, IOException {
		BlockSet ret = new BlockSet();

		if (getType() == EXTERNAL_DATA_FORMAT.STRING) {
			// read the columns
			String str = br.readLine();

			// if there is a blank line, return false
			if (str == null || str.equals(""))
				throw new EOFException();

			int ind1 = 0;
			int ind2 = getNextLocation(str, ' ', ind1);
			while (ind2 != -1) {
				ret.addColumn(Integer.parseInt(str.substring(ind1, ind2)));
				ind1 = ind2 + 1;
				ind2 = getNextLocation(str, ' ', ind1);
			}

			str = br.readLine();
			LongArrayList ids = new LongArrayList();
			ind1 = 0;
			ind2 = getNextLocation(str, ' ', ind1);
			while (ind2 != -1) {
				ids.add(Long.parseLong(str.substring(ind1, ind2)));
				ind1 = ind2 + 1;
				ind2 = getNextLocation(str, ' ', ind1);
			}

			ret.setRecordIDs(ids);
		} else if (getType() == EXTERNAL_DATA_FORMAT.BINARY) {
			// first read the number of blocking fields
			int size = dis.readInt();

			IntArrayList columns = new IntArrayList(size);

			// second read the blocking values
			for (int i = 0; i < size; i++) {
				int id = dis.readInt();
				columns.add(id);
			}

			ret.addColumns(columns);

			// third read the number of record ids
			size = dis.readInt();
			long[] data = new long[size];

			// fourth read the record ids
			for (int i = 0; i < size; i++) {
				long id2 = dis.readLong();
				data[i] = id2;
			}

			LongArrayList l = new LongArrayList(size, data);
			ret.setRecordIDs(l);
		}

		return ret;
	}

	@Override
	public boolean hasNext() {
		if (this.nextBS == null) {
			try {
				this.nextBS = readNext();
			} catch (EOFException x) {
				this.nextBS = null;
			} catch (IOException ex) {
				throw new IllegalStateException(ex.toString());
			}
		}
		return this.nextBS != null;
	}

	/**
	 * This method returns true if there is another block that is blocked by the
	 * given number of fields and the given maximum column.
	 *
	 * @param fields
	 *            - number of blocking fields this block needs to have
	 * @param col
	 *            - maximum column id that this block needs to have
	 * @return - an ArrayList of BlockSet with each containing the oversized
	 *         blocking set
	 */
	@Override
	public boolean hasNext(int fields, int col) throws BlockingException {
		boolean stop = false;

		while (!stop) {
			if (hasNext()) {
				BlockSet bs = next();

				IntArrayList columns = bs.getColumns();

				// check to see if this blockset has the correct number of
				// fields
				if (columns.size() == fields) {
					// check to see if the last (max) column is the same as the
					// param.
					if (columns.get(fields - 1) == col)
						stop = true;
				}
			} else {
				// no more blocks
				return false;
			}
		}

		return stop;
	}

	@Override
	public int getCount() {
		return count;
	}

	/** This method skips a number of elements in the source */
	@Override
	public void skip(int n) throws BlockingException {
		// 2014-04-24 rphall: Commented out unused local variables.
		// int count = 0;
		// boolean stop = false;
		for (int i = 0; i < n; i++) {
			next();
		}
	}

	/**
	 * This method gets the oversized blocks with the given number of fields and
	 * the maximum col id.
	 *
	 * @param fields
	 *            - number of blocking fields this need to have
	 * @param col
	 *            - maximum column id that this needs to have
	 * @return - an ArrayList of BlockSet with each containing the oversized
	 *         blocking set
	 */
	@Override
	public List<BlockSet> readOversizedInt(int fields, int col)
			throws BlockingException {
		List<BlockSet> oversized = new ArrayList<>();

		open();
		while (hasNext()) {
			BlockSet bs = next();

			IntArrayList columns = bs.getColumns();

			// check to see if this blockset has the correct number of fields
			if (columns.size() == fields) {

				// the columns ids are in ascending order so we only need to
				// look at the last one
				if (columns.get(fields - 1) == col)
					oversized.add(bs);
			}
		}
		close();
		return oversized;
	}

	@Override
	public BlockSet next() {
		if (this.nextBS == null) {
			try {
				this.nextBS = readNext();
			} catch (IOException x) {
				throw new NoSuchElementException(x.toString());
			}
		}
		BlockSet retVal = this.nextBS;
		count++;
		this.nextBS = null;

		return retVal;
	}

	@Override
	public String toString() {
		return "BlockSource [count=" + count + ", type=" + getType()
				+ ", fileName=" + fileName + "]";
	}

}
