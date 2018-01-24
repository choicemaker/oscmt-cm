/*******************************************************************************
 * Copyright (c) 2015 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.server.impl;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;
import com.choicemaker.cm.oaba.server.impl.RecordIdSink;

public class RecordIdTestUtils {

	private static final Random random = new Random();

	private static final int DEFAULT_MAX_COUNT = 100;

	private static final int MIN_COUNT = 1;

	private static int getRandomSize(int max) {
		if (max < MIN_COUNT) {
			throw new IllegalArgumentException("max < " + MIN_COUNT);
		}
		int retVal;
		if (max == 1) {
			retVal = 1;
		} else {
			retVal = random.nextInt(max - 1);
			retVal += 1;
		}
		assert retVal >= 1 && retVal <= max;
		return retVal;
	}

	@SuppressWarnings("rawtypes")
	private static final Class[] allowedTypes = new Class[] {
			Integer.class, Long.class, String.class };

	@SuppressWarnings("rawtypes")
	public static List<Class> getAllowedRecordIdTypes() {
		return Collections.unmodifiableList(Arrays.asList(allowedTypes));
	}

	public static List<Integer> getIdentifiersAsIntegers() {
		return getIdentifiersAsIntegers(DEFAULT_MAX_COUNT);
	}

	public static List<Integer> getIdentifiersAsIntegers(int maxCount) {
		Set<Integer> s = new HashSet<>();
		int count = Math.max(getRandomSize(maxCount), 2);
		for (int i = 0; i < count; i++) {
			s.add(random.nextInt());
		}
		List<Integer> retVal = new ArrayList<>();
		retVal.addAll(s);
		return Collections.unmodifiableList(retVal);
	}

	public static List<Long> getIdentifiersAsLongs() {
		List<Long> retVal = new ArrayList<>();
		for (Integer i : getIdentifiersAsIntegers()) {
			Long id = Long.valueOf(i);
			retVal.add(id);
		}
		return Collections.unmodifiableList(retVal);
	}

	public static List<String> getIdentifiersAsStrings() {
		List<String> retVal = new ArrayList<>();
		int count = DEFAULT_MAX_COUNT;
		for (int i = 0; i < count; i++) {
			retVal.add(UUID.randomUUID().toString());
		}
		return Collections.unmodifiableList(retVal);
	}

	public static <T extends Comparable<T>> List<String> getFileData(List<T> ids) {
		if (ids == null || ids.size() == 0) {
			throw new IllegalArgumentException("null or empty identifier list");
		}
		List<String> retVal = new ArrayList<>();
		int count = 0;
		T id = ids.get(count);
		RECORD_ID_TYPE rit = RECORD_ID_TYPE.fromInstance(id);
		retVal.add(rit.getStringSymbol());
		for (; count < ids.size(); ++count) {
			id = ids.get(count);
			retVal.add(String.valueOf(id));
		}
		return Collections.unmodifiableList(retVal);
	}

	public static String createTempFileName() {
		String fileName = null;
		try {
			File f =
				File.createTempFile(RecordIdSinkTest.SINK_PREFIX,
						RecordIdSinkTest.FILE_EXTENTION);
			fileName = f.getAbsolutePath();
			f.delete();
		} catch (IOException e) {
			fail(e.toString());
		}
		assert fileName != null;
		return fileName;
	}

	public static <T extends Comparable<T>> RecordIdSink writeIdentifiersToFile(
			String fileName, List<T> identifiers) throws IOException,
			BlockingException {
		RecordIdSink ris = new RecordIdSink(fileName);

		ris.open();
		for (T id : identifiers) {
			ris.writeRecordID(id);
		}
		ris.close();

		return ris;
	}

	private RecordIdTestUtils() {
	}

}
