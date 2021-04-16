package com.choicemaker.cm.oaba.api;

import com.choicemaker.cm.batch.api.BatchJob;

public interface OabaJobMonitor {

	OabaJobInfo getOabaJobInfo(BatchJob batchJob);

}
