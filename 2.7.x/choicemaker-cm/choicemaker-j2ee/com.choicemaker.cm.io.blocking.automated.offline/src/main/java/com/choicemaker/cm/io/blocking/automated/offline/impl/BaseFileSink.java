/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.blocking.automated.offline.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Logger;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.io.blocking.automated.offline.core.EXTERNAL_DATA_FORMAT;
import com.choicemaker.cm.io.blocking.automated.offline.core.ISink;
import com.choicemaker.cm.io.blocking.automated.offline.core.SIMPLE_RESOURCE_STATE;
import com.choicemaker.util.SystemPropertyUtils;

/**
 * This is a generic file based implementation of ISink. Each descendant must
 * call init.
 *
 * @author pcheung
 *
 */
public abstract class BaseFileSink implements ISink {
	
	public static final int MAX_STACK_TRACE_DEPTH = 7;

	private static final Logger logger = Logger.getLogger(BaseFileSink.class.getName());

	private SIMPLE_RESOURCE_STATE simple_resource_state = SIMPLE_RESOURCE_STATE.INITIAL;
	private String lastOpenedStackTrace;

	protected DataOutputStream dos;
	protected FileWriter fw;
	protected int count = 0;
	protected final EXTERNAL_DATA_FORMAT type;
	protected String fileName;

	/**
	 * The descendants should call this method in their constructor.
	 *
	 * @param fileName
	 *            - file name of the sink
	 * @param type
	 *            - indicates whether the file is a string or binary file.
	 */
	protected BaseFileSink(String fileName, EXTERNAL_DATA_FORMAT type) {
		if (type == null || fileName == null) {
			throw new IllegalArgumentException("null argument");
		}
		this.type = type;
		assert this.type != null;
		this.fileName = fileName;
		this.simple_resource_state = SIMPLE_RESOURCE_STATE.CONSTRUCTED;
	}

	// Move this method to a shared utility class
	public static String printStackTrace(String msg) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		Throwable t = new RuntimeException(msg);
		pw.println(msg);
		t.printStackTrace(pw);

		String s = sw.toString();
		StringReader sr = new StringReader(s);
		BufferedReader br = new BufferedReader(sr);

		// Full stack is the fall-back value
		String retVal = s;

		// Try to truncate the stack and message to MAX_STACK_TRACE_DEPTH
		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			String line = br.readLine();
			int lineCount = 0;
			while (line != null && lineCount < MAX_STACK_TRACE_DEPTH) {
				pw.println(line);
				++lineCount;
				line = br.readLine();
			}
			retVal = sw.toString();
		} catch (IOException x) {
			assert retVal.equals(s);
		}

		return retVal;
	}

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
				fw = new FileWriter(fileName, false);
				break;
			case BINARY:
				dos =
					new DataOutputStream(new FileOutputStream(fileName, false));
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

	@Override
	public boolean isOpen() {
		boolean retVal = false;
		switch (type) {
		case STRING:
			retVal = fw != null;
			if (!retVal) {
				// if fw doesn't exist for a STRING instance, it can't be open
				assert this.simple_resource_state != SIMPLE_RESOURCE_STATE.OPEN;
			}
			break;
		case BINARY:
			retVal = dos != null;
			if (!retVal) {
				// if dos doesn't exist for a BINARY instance, it can't be open
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
	public void append() throws BlockingException {
		try {
			switch (type) {
			case STRING:
				fw = new FileWriter(fileName, true);
				break;
			case BINARY:
				dos =
					new DataOutputStream(new FileOutputStream(fileName, true));
				break;
			default:
				throw new IllegalArgumentException("invalid type: " + type);
			}
			String msg = this.getClass().getName() + " opened for appending";
			this.lastOpenedStackTrace = printStackTrace(msg);
			this.simple_resource_state = SIMPLE_RESOURCE_STATE.OPEN;

		} catch (IOException ex) {
			throw new BlockingException(ex.toString());
		}
	}

	/**
	 * The base implementation flushes the sink before closing it.
	 */
	@Override
	public void close() throws BlockingException {
		flush();
		try {
			switch (type) {
			case STRING:
				if (fw != null) {
					fw.close();
					fw = null;
				}
				break;
			case BINARY:
				if (dos != null) {
					dos.close();
					dos = null;
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
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public String getInfo() {
		return fileName;
	}

	@Override
	public void remove() throws BlockingException {
		File file = new File(fileName);
		file.delete();
		count = 0;
	}

	@Override
	public void flush() throws BlockingException {
		try {
			switch (type) {
			case STRING:
				if (fw != null) {
					fw.flush();
				}
				break;
			case BINARY:
				if (dos != null) {
					dos.flush();
				}
				break;
			default:
				throw new IllegalArgumentException("invalid type: " + type);
			}
		} catch (IOException ex) {
			throw new BlockingException(ex.toString());
		}
	}

	@Override
	public String toString() {
		return "BaseFileSink [type=" + type + ", fileName=" + fileName
				+ ", exists=" + exists() + ", count=" + count + "]";
	}

}
