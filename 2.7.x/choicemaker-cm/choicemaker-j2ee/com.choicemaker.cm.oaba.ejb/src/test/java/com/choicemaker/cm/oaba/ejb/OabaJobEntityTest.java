package com.choicemaker.cm.oaba.ejb;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.choicemaker.cm.batch.api.BatchJob;

public class OabaJobEntityTest {

	@Test
	public void testGetBatchJobType() {
		BatchJob job = OabaJobFactoryForTest.createOabaJobForTest();
		String expected = OabaJobJPA.DISCRIMINATOR_VALUE;
		String computed = job.getBatchJobType();
		assertTrue(expected.equals(computed));
	}

}
