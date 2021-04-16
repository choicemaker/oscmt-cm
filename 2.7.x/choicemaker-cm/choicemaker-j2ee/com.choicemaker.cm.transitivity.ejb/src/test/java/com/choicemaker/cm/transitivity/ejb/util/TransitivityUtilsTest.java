package com.choicemaker.cm.transitivity.ejb.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.transitivity.ejb.TransitivityJobFactoryForTest;

public class TransitivityUtilsTest {

	@Test
	public void testIsTransitivityJob() {
		BatchJob job =
			TransitivityJobFactoryForTest.createTransitivityJobForTest();
		boolean computed = TransitivityUtils.isTransitivityJob(job);
		assertTrue(computed);
	}

}
