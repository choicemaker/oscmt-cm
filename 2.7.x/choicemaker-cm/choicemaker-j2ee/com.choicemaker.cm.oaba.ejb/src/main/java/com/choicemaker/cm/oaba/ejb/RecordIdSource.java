/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

import java.io.EOFException;
import java.io.IOException;
import java.util.NoSuchElementException;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.core.EXTERNAL_DATA_FORMAT;
import com.choicemaker.cm.oaba.core.IRecordIdSource;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;
import com.choicemaker.cm.oaba.impl.BaseFileSource;

/**
 * @author pcheung
 *
 */
public class RecordIdSource<T extends Comparable<T>> extends BaseFileSource<T>
		implements IRecordIdSource<T> {

	protected RECORD_ID_TYPE dataType;
	protected T nextID;
	private boolean isFirst = true;

	public RecordIdSource(Class<T> c, String fileName) {
		super(fileName, EXTERNAL_DATA_FORMAT.STRING);
		this.dataType = RECORD_ID_TYPE.fromClass(c);
	}

	@Deprecated
	public RecordIdSource(String fileName) {
		super(fileName, EXTERNAL_DATA_FORMAT.STRING);
	}

	@Override
	protected void resetNext() {
		this.nextID = null;
		isFirst = true;
	}

	@Override
	public T next() {
		if (this.nextID == null) {
			try {
				this.nextID = readNext();
			} catch (EOFException x) {
				throw new NoSuchElementException("EOFException: "
						+ x.getMessage());
			} catch (IOException x) {
				throw new NoSuchElementException("BlockingException: "
						+ x.getMessage());
			}
		}
		T retVal = this.nextID;
		count++;

		this.nextID = null;

		return retVal;
	}

	@SuppressWarnings("unchecked")
	private T readNext() throws EOFException, IOException {
		assert getType() == EXTERNAL_DATA_FORMAT.STRING;

		T ret = null;

		String str;
		if (isFirst) {
			str = br.readLine();
			if (str != null && !str.equals("")) {
				if (dataType == null) {
					dataType = RECORD_ID_TYPE.fromValue(Integer.parseInt(str));
				} else {
					assert dataType == RECORD_ID_TYPE.fromValue(Integer
							.parseInt(str));
				}
			}
			isFirst = false;
		}

		str = br.readLine();
		if (str != null && !str.equals("")) {
			ret = (T) dataType.idFromString(str);
		} else {
			throw new EOFException();
		}

		return ret;
	}

	@Override
	public RECORD_ID_TYPE getRecordIDType() throws BlockingException {
		return dataType;
	}

	@Override
	public boolean hasNext() throws BlockingException {
		if (this.nextID == null) {
			try {
				this.nextID = readNext();
			} catch (EOFException x) {
				this.nextID = null;
			} catch (IOException x) {
				throw new BlockingException(x.toString());
			}
		}
		return this.nextID != null;
	}

}
