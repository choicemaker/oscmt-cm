package com.choicemaker.cms.ejb;

import com.choicemaker.cm.batch.api.BatchJob;

public class UrmJobFactoryForTest {

	public static BatchJob createUrmJobForTest() {
		UrmJobEntity job = new UrmJobEntity();
		return job;
	}

}

