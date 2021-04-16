package com.choicemaker.cm.transitivity.ejb;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TransitivityJobEntityTest {

	@Test
	public void testGetBatchJobType() {
		TransitivityJobEntity job = new TransitivityJobEntity();
		String expected = TransitivityJobJPA.DISCRIMINATOR_VALUE;
		String computed = job.getBatchJobType();
		assertTrue(expected.equals(computed));
	}

}
