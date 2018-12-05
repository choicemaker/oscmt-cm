package com.choicemaker.cms.ejb;

import static org.junit.Assert.*;

import org.junit.Test;

import com.choicemaker.cm.batch.api.BatchJob;
import com.choicemaker.cm.transitivity.ejb.TransitivityJobFactoryForTest;
import com.choicemaker.cm.transitivity.ejb.util.TransitivityUtils;

public class UrmUtilsTest {

	@Test
	public void testIsUrmJob() {
		BatchJob job =
				TransitivityJobFactoryForTest.createTransitivityJobForTest();
			boolean computed = TransitivityUtils.isTransitivityJob(job);
			assertTrue(computed);
	}

}
