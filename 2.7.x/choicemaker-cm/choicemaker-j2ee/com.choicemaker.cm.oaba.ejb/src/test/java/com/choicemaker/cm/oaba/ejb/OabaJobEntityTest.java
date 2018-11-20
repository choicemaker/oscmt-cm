package com.choicemaker.cm.oaba.ejb;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OabaJobEntityTest {

	@Test
	public void testGetBatchJobType() {
		OabaJobEntity job = new OabaJobEntity();
		String expected = OabaJobJPA.DISCRIMINATOR_VALUE;
		String computed = job.getBatchJobType();
		assertTrue(expected.equals(computed));
	}

}
