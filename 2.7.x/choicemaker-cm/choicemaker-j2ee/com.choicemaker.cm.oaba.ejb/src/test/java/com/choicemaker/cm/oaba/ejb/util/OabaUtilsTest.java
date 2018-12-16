package com.choicemaker.cm.oaba.ejb.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.oaba.ejb.OabaEjbUtils;
import com.choicemaker.cm.oaba.ejb.OabaJobFactoryForTest;

public class OabaUtilsTest {

	@Test
	public void testIsOabaJob() {
		BatchJob job = OabaJobFactoryForTest.createOabaJobForTest();
		boolean computed = OabaEjbUtils.isOabaJob(job);
		assertTrue(computed);
	}

}
