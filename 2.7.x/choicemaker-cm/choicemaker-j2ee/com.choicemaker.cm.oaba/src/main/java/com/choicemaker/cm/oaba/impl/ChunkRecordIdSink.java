/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
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
import com.choicemaker.cm.oaba.core.IChunkRecordIdSink;

/**
 * @author pcheung
 *
 */
public class ChunkRecordIdSink extends BaseFileSink implements
		IChunkRecordIdSink {

	@Deprecated
	public ChunkRecordIdSink(String fileName, int type) {
		super(fileName, EXTERNAL_DATA_FORMAT.fromSymbol(type));
	}

	public ChunkRecordIdSink(String fileName, EXTERNAL_DATA_FORMAT type) {
		super(fileName, type);
	}

	@Override
	public void writeRecordID(long recID) throws BlockingException {
		try {
			if (type == EXTERNAL_DATA_FORMAT.STRING) {
				fw.write(Long.toString(recID) + Constants.LINE_SEPARATOR);
			} else if (type == EXTERNAL_DATA_FORMAT.BINARY) {
				dos.writeLong(recID);
			}
			count++;
		} catch (IOException ex) {
			throw new BlockingException(ex.toString());
		}
	}

}
