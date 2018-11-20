package com.choicemaker.cm.batch.ejb;

import static org.junit.Assert.*;

import org.junit.Test;

public class BatchJobEntityTest {

	@Test
	public void testGetBatchJobType() {
		BatchJobEntity entity = new BatchJobEntity() {
			private static final long serialVersionUID = 271L;
		};
		String computedType = entity.getBatchJobType();
		String expectedType = BatchJobJPA.DISCRIMINATOR_VALUE;
		assertTrue(expectedType.equals(computedType));
	}

}
