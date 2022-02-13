/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.impl;

import java.io.IOException;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.core.Constants;
import com.choicemaker.cm.oaba.core.EXTERNAL_DATA_FORMAT;
import com.choicemaker.cm.oaba.core.IRecValSink;
import com.choicemaker.util.IntArrayList;

/**
 * @author pcheung
 *
 */
public class RecValSink extends BaseFileSink implements IRecValSink {

	@Deprecated
	public RecValSink(String fileName, int type) {
		super(fileName, EXTERNAL_DATA_FORMAT.fromSymbol(type));
	}

	public RecValSink(String fileName, EXTERNAL_DATA_FORMAT type) {
		super(fileName, type);
	}

	@Override
	public void writeRecordValue(long recID, IntArrayList values)
			throws BlockingException {
		try {
			if (type == EXTERNAL_DATA_FORMAT.BINARY) {
				dos.writeLong(recID);

				if (values != null) {
					// write the size
					dos.writeInt(values.size());

					for (int i = 0; i < values.size(); i++) {
						dos.writeInt(values.get(i));
					}
				} else {
					dos.writeInt(0);
				}
			} else if (type == EXTERNAL_DATA_FORMAT.STRING) {
				StringBuffer sb = new StringBuffer();
				sb.append(recID);
				sb.append(' ');
				if (values != null) {
					for (int i = 0; i < values.size(); i++) {
						sb.append(values.get(i));
						sb.append(' ');
					}
				}
				sb.append(Constants.LINE_SEPARATOR);
				fw.write(sb.toString());

				/*
				 * fw.write( Long.toString(recID)); fw.write(" ");
				 * 
				 * if (values != null) { for (int i=0; i<values.size(); i++) {
				 * fw.write( Integer.toString(values.get(i))); fw.write(" "); }
				 * } fw.write(Constants.LINE_SEPARATOR);
				 */
			}
		} catch (IOException ex) {
			throw new BlockingException(ex.toString());
		}
	}

	/*
	 * public void writeRecordValue(long recID, IntArrayList values) throws
	 * IOException { if (type == Constants.BINARY) { dos.writeLong(recID); for
	 * (int i=0; i<values.size(); i++) { dos.writeLong( values.get(i)); }
	 * dos.writeLong(Constants.NEWLINE); } else if (type == Constants.STRING) {
	 * fw.write( Long.toString(recID)); fw.write(" "); for (int i=0;
	 * i<values.size(); i++) { fw.write( Integer.toString(values.get(i)));
	 * fw.write(" "); } fw.write("\r\n"); }
	 * 
	 * }
	 */

}
