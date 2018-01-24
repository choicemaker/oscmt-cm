/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.impl;

import static com.choicemaker.cm.oaba.impl.BaseFileSink.printStackTrace;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.core.EXTERNAL_DATA_FORMAT;
import com.choicemaker.cm.oaba.core.ISource;
import com.choicemaker.cm.oaba.core.SIMPLE_RESOURCE_STATE;
import com.choicemaker.util.SystemPropertyUtils;

/**
 * This is a generic file based implementation of ISource. Each descendant must
 * implement hasNext and getNext, and call init.
 *
 * @author pcheung
 *
 */
public abstract class BaseFileSource<T> implements ISource<T> {

	private static final Logger logger = Logger.getLogger(BaseFileSource.class.getName());

	private SIMPLE_RESOURCE_STATE simple_resource_state = SIMPLE_RESOURCE_STATE.INITIAL;
	private String lastOpenedStackTrace;

	protected DataInputStream dis = null;
	protected BufferedReader br = null;
	protected int count = 0;
	private final EXTERNAL_DATA_FORMAT type;
	protected final String fileName;

	/**
	 * The descendants should call this method in their constructor.
	 *
	 * @param fileName
	 *            - file name of the sink
	 * @param type
	 *            - indicates whether the file is a string or binary file.
	 */
	protected BaseFileSource(String fileName, EXTERNAL_DATA_FORMAT type) {
		if (type == null || fileName == null) {
			throw new IllegalArgumentException("null argument");
		}
		this.type = type;
		assert this.type != null;
		this.fileName = fileName;
		this.simple_resource_state = SIMPLE_RESOURCE_STATE.CONSTRUCTED;
		resetNext();
	}

	/**
	 * Subclasses must implement this method to reset the <code>next()</code>
	 * and <code>hasNext()</code> methods when a source instance is first
	 * constructed or when it is closed.
	 */
	protected abstract void resetNext();

	@Override
	protected void finalize() throws Throwable {
		if (this.simple_resource_state == SIMPLE_RESOURCE_STATE.OPEN) {
			String msg = this.getClass().getName() + " instance left opened";
			msg += SystemPropertyUtils.PV_LINE_SEPARATOR + this.lastOpenedStackTrace;
			logger.warning(msg);
		}
		super.finalize();
	}

	@Override
	public boolean exists() {
		File file = new File(fileName);
		boolean exists = file.exists();
		return exists;
	}

	@Override
	public void open() throws BlockingException {
		try {
			switch (type) {
			case STRING:
				br = new BufferedReader(new FileReader(fileName));
				break;
			case BINARY:
				dis = new DataInputStream(new FileInputStream(fileName));
				break;
			default:
				throw new IllegalArgumentException("invalid type: " + type);
			}
			String msg = this.getClass().getName() + " opened";
			this.lastOpenedStackTrace = printStackTrace(msg);
			this.simple_resource_state = SIMPLE_RESOURCE_STATE.OPEN;

		} catch (IOException ex) {
			throw new BlockingException(ex.toString());
		}
	}

	// @Override
	public boolean isOpen() {
		boolean retVal = false;
		switch (type) {
		case STRING:
			retVal = br != null;
			if (!retVal) {
				// if br doesn't exist for a STRING instance, it can't be open
				assert this.simple_resource_state != SIMPLE_RESOURCE_STATE.OPEN;
			}
			break;
		case BINARY:
			retVal = dis != null;
			if (!retVal) {
				// if dis doesn't exist for a BINARY instance, it can't be open
				assert this.simple_resource_state != SIMPLE_RESOURCE_STATE.OPEN;
			}
			break;
		default:
			throw new IllegalArgumentException("invalid type: " + type);
		}
		// if either is true, both are true
		if (retVal || this.simple_resource_state == SIMPLE_RESOURCE_STATE.OPEN) {
			assert (retVal && this.simple_resource_state == SIMPLE_RESOURCE_STATE.OPEN);
		}
		return retVal;
	}

	@Override
	public void close() throws BlockingException {
		try {
			switch (getType()) {
			case STRING:
				if (br != null) {
					br.close();
					br = null;
				}
				break;
			case BINARY:
				if (dis != null) {
					dis.close();
					dis = null;
				}
				break;
			default:
				throw new IllegalArgumentException("invalid type: " + type);
			}
			this.simple_resource_state = SIMPLE_RESOURCE_STATE.CLOSED;
		} catch (IOException ex) {
			throw new BlockingException(ex.toString());
		}
		count = 0;
		resetNext();
	}

	public int getCount() {
		return count;
	}

	@Override
	public String getInfo() {
		return fileName;
	}

	@Override
	public void delete() throws BlockingException {
		File file = new File(fileName);
		file.delete();
		count = 0;
	}

	/**
	 * This returns the location in the string str where charAt == key and
	 * location >= start. Returns -1 if no such location.
	 */
	protected int getNextLocation(String str, char key, int start) {
		int ret = -1;
		int i = start;
		int size = str.length();
		boolean found = false;
		while ((i < size) && !found) {
			if (str.charAt(i) == key) {
				found = true;
				ret = i;
			} else {
				i++;
			}
		}

		return ret;
	}

	@Override
	public String toString() {
		return "BaseFileSource [type=" + getType() + ", fileName=" + fileName
				+ ", exists=" + exists() + ", count=" + count + "]";
	}

	protected EXTERNAL_DATA_FORMAT getType() {
		return type;
	}

}
