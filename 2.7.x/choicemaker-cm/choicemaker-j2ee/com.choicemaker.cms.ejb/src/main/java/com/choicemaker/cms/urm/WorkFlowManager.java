package com.choicemaker.cms.urm;

import com.choicemaker.cm.batch.BatchProcessingNotification;

public interface WorkFlowManager {

	void jobUpdated(BatchProcessingNotification bpn);

}
