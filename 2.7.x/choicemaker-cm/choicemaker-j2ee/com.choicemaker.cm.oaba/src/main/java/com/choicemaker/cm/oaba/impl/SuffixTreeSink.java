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
import com.choicemaker.cm.oaba.core.ISuffixTreeSink;
import com.choicemaker.cm.oaba.core.SuffixTreeNode;

/**
 * @author pcheung
 *
 */
public class SuffixTreeSink extends BaseFileSink implements ISuffixTreeSink {

	/**
	 * This constructor creates a String SuffixTreeSink with the given file
	 * name.
	 */
	public SuffixTreeSink(String fileName) {
		super(fileName, EXTERNAL_DATA_FORMAT.STRING);
	}

	@Override
	public void writeSuffixTree(SuffixTreeNode root) throws BlockingException {
		try {
			if (type == EXTERNAL_DATA_FORMAT.BINARY) {
				throw new BlockingException(
						"BINARY format of SuffixTreeSink is not yet supported");
			} else if (type == EXTERNAL_DATA_FORMAT.STRING) {
				StringBuffer sb = new StringBuffer();
				root.writeSuffixTree2(sb);
				sb.append(Constants.LINE_SEPARATOR);
				fw.write(sb.toString());
			}
			count++;
		} catch (IOException ex) {
			throw new BlockingException(ex.toString());
		}
	}

}
