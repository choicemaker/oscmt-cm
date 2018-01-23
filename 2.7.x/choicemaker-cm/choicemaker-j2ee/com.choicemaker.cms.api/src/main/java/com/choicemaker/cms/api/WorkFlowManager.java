package com.choicemaker.cms.api;

import com.choicemaker.cm.batch.api.BatchProcessingNotification;

public interface WorkFlowManager {

	void jobUpdated(BatchProcessingNotification bpn);

}
