/*******************************************************************************
 * Copyright (c) 2015, 2018 ChoiceMaker LLC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.choicemaker.cm.oaba.ejb;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.choicemaker.cm.core.BlockingException;
import com.choicemaker.cm.oaba.core.RECORD_ID_TYPE;

/**
 * This unit test relies on RecordIdSink working correctly. RecordIdSink is
 * tested by RecordIdSinkTest.
 */
public class RecordIdSourceTest {

	public static <T extends Comparable<T>> void testTypeParameter(
			final Class<T> c, List<T> identifiers) throws BlockingException {
		assertTrue(c != null);
		assertTrue(Comparable.class.isAssignableFrom(c));
		String fileName = RecordIdTestUtils.createTempFileName();
		RecordIdSink sink = new RecordIdSink(fileName);
		sink.open();
		for (T id : identifiers) {
			sink.writeRecordID(id);
		}
		sink.close();

		RecordIdSource<T> ris = new RecordIdSource<>(c, fileName);
		ris.open();
		for (T id : identifiers) {
			T id2 = ris.next();
			assertTrue(id.equals(id2));

		}

		assertTrue(ris.getCount() == identifiers.size());

		RECORD_ID_TYPE expected = RECORD_ID_TYPE.fromClass(c);
		assertTrue(expected == ris.getRecordIDType());

		assertTrue(ris.exists());

		ris.close();
	}

	@Test
	public void testIntegerSource() throws Exception {
		testTypeParameter(Integer.class,
				RecordIdTestUtils.getIdentifiersAsIntegers());
	}

	@Test
	public void testLongSource() throws Exception {
		testTypeParameter(Long.class,
				RecordIdTestUtils.getIdentifiersAsLongs());
	}

	@Test
	public void testStringSource() throws Exception {
		testTypeParameter(String.class,
				RecordIdTestUtils.getIdentifiersAsStrings());
	}

}
