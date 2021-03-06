/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.io.flatfile.base;

/**
 * Description
 *
 * @author    Martin Buechi
 */
public interface FlatFileAccessor {
	int[] getDescWidths();

	String[] getFlatFileFileNames();

	FlatFileReader getSingleFileFlatFileReader(Tokenizer tokenizer, boolean tagged, boolean singleLine);

	FlatFileReader getMultiFileFlatFileReader(Tokenizer[] tokenizer, boolean tagged);

	FlatFileRecordOutputter getFlatFileRecordOutputter(
		boolean multiFile,
		boolean singleLine,
		boolean fixedLength,
		char sep,
		boolean tagged,
		int tagWidth,
		boolean filter);
}
