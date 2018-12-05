package com.choicemaker.cm.transitivity.ejb;

import com.choicemaker.cm.batch.api.BatchJob;

public class TransitivityJobFactoryForTest {

	public static BatchJob createTransitivityJobForTest() {
		TransitivityJobEntity job = new TransitivityJobEntity();
		return job;
	}

}
