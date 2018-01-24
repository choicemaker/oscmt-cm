package com.choicemaker.cm.oaba.ejb;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.oaba.core.ImmutableRecordIdTranslator;
import com.choicemaker.cm.oaba.ejb.RecordIdSink;
import com.choicemaker.cm.oaba.ejb.RecordIdSource;

public class ImmutableRecordIdTranslatorTest {

	public static RecordIdSource<String> createRecordIdSource(String fileName) {
		List<String> identifiers = RecordIdTestUtils.getIdentifiersAsStrings();
		RecordIdSource<String> retVal = null;
		try {
			RecordIdSink ridSink =
				RecordIdTestUtils.writeIdentifiersToFile(fileName, identifiers);
			assertTrue(ridSink != null);
			ridSink.close();
			retVal = new RecordIdSource<String>(String.class, fileName);
		} catch (Exception e) {
			fail(e.toString());
		}
		assertTrue(retVal != null);
		return retVal;
	}
	
	public static void cleanupFile(String fileName) {
		File f = new File(fileName);
		if (f.exists()) {
			f.delete();
		}
	}

	private String fileName1;
	private String fileName2;
	
	@Before
	public void setUp() throws Exception {
		fileName1 = RecordIdTestUtils.createTempFileName();
		fileName2 = RecordIdTestUtils.createTempFileName();
	}

	@After
	public void tearDown() throws Exception {
		cleanupFile(fileName1);
		cleanupFile(fileName2);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testImmutableRecordIdTranslatorImpl() {

		final boolean keepFiles = true;

		BatchJob stub = BatchJobTestUtils.createBatchJobStub();
		ImmutableRecordIdTranslator<String> irit = null;
		RecordIdSource<String> ridSrc1 = createRecordIdSource(fileName1);
		RecordIdSource<String> ridSrc2 = createRecordIdSource(fileName2);
		try {
			irit =
				new ImmutableRecordIdTranslatorImpl(stub, ridSrc1, ridSrc2,
						keepFiles);
			ridSrc1.close();
			ridSrc2.close();
		} catch (Exception e) {
			fail(e.toString());
		}

		try {
			ridSrc1.open();
			while (ridSrc1.hasNext()) {
				String id = ridSrc1.next();
				int index = irit.lookupStagingIndex(id);
				assertTrue(index >= 0);
				assertTrue(index < irit.getSplitIndex());
				String id2 = (String) irit.reverseLookup(index);
				assertTrue(id.equals(id2));
			}
		} catch (Exception e) {
			fail(e.toString());
		}
		
		try {
			ridSrc2.open();
			while (ridSrc2.hasNext()) {
				String id = ridSrc2.next();
				int index = irit.lookupMasterIndex(id);
				assertTrue(index >= irit.getSplitIndex());
				String id2 = (String) irit.reverseLookup(index);
				assertTrue(id.equals(id2));
			}
		} catch (Exception e) {
			fail(e.toString());
		}

	}

}
