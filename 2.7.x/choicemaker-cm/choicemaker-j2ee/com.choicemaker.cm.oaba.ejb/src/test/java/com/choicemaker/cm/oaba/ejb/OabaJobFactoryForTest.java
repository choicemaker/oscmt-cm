package com.choicemaker.cm.oaba.ejb;

import com.choicemaker.cm.batch.api.BatchJob;

public class OabaJobFactoryForTest {

	public static BatchJob createOabaJobForTest() {
		OabaJobEntity job = new OabaJobEntity();
		return job;
	}

}
