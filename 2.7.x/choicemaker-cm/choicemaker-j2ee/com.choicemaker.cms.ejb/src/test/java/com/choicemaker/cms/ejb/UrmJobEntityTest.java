package com.choicemaker.cms.ejb;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UrmJobEntityTest {

	@Test
	public void testGetBatchJobType() {
		UrmJobEntity job = new UrmJobEntity();
		String expected = UrmJobJPA.DISCRIMINATOR_VALUE;
		String computed = job.getBatchJobType();
		assertTrue(expected.equals(computed));
	}

}
