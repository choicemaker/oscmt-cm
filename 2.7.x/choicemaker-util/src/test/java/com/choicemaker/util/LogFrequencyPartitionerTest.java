package com.choicemaker.util;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.choicemaker.util.LogFrequencyPartitioner.ValueCountPair;

public class LogFrequencyPartitionerTest {
	
	public static final String AN_ILLEGAL_VALUE = null;
	public static final int AN_ILLEGAL_COUNT = -1;
	
	public static final String A_LEGAL_VALUE = "";
	public static final int A_LEGAL_COUNT = 0;
	
	public static String createLegalValue(int i) {
		return String.valueOf(i);
	}

	public static int createLegalCount(int i) {
		return Math.abs(i);
	}

	@Test
	public void testValueCountPair() {
		ValueCountPair vcp = null;
	
		try {
			vcp = new ValueCountPair(AN_ILLEGAL_VALUE, A_LEGAL_COUNT);
			fail("Failed to catch an illegal value");
		} catch (IllegalArgumentException x) {
			assertTrue(vcp == null);
		}
		
		try {
			vcp = new ValueCountPair(A_LEGAL_VALUE, AN_ILLEGAL_COUNT);
			fail("Failed to catch an illegal value");
		} catch (IllegalArgumentException x) {
			assertTrue(vcp == null);
		}
		
		vcp = new ValueCountPair(A_LEGAL_VALUE, A_LEGAL_COUNT);
	}

	@Test
	public void testLogFrequencyPartitionerListOfValueCountPair() {
		List<ValueCountPair> pairs;
		for (int listSize = 0; listSize < 4; listSize++) {
			pairs = new ArrayList<>();
			for (int i=0; i<listSize; i++) {
				ValueCountPair pair = new ValueCountPair(
						createLegalValue(i),
						createLegalCount(i));
				pairs.add(pair);
			}
			new LogFrequencyPartitioner(pairs);
		}
	}

	@Test
	public void testAddPairValueCountPair() {
		for (int listSize = 0; listSize < 4; listSize++) {
			LogFrequencyPartitioner lfp = new LogFrequencyPartitioner();
			for (int i=0; i<listSize; i++) {
				ValueCountPair pair = new ValueCountPair(
						createLegalValue(i),
						createLegalCount(i));
				lfp.addPair(pair);
			}
		}
	}

	@Test
	public void testAddPairStringInt() {
		for (int listSize = 0; listSize < 4; listSize++) {
			LogFrequencyPartitioner lfp = new LogFrequencyPartitioner();
			for (int i=0; i<listSize; i++) {
				lfp.addPair(createLegalValue(i),
						createLegalCount(i));
			}

			try {
				lfp.addPair(AN_ILLEGAL_VALUE, A_LEGAL_COUNT);
				fail("Failed to catch an illegal value");
			} catch (IllegalArgumentException x) {
				assertTrue(lfp.size() == );
			}
			
			try {
				vcp = new ValueCountPair(A_LEGAL_VALUE, AN_ILLEGAL_COUNT);
				fail("Failed to catch an illegal value");
			} catch (IllegalArgumentException x) {
				assertTrue(vcp == null);
			}
		}
		
	}

	@Test
	public void testComputeBoundaries() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFrequencyClass() {
		fail("Not yet implemented");
	}

}
