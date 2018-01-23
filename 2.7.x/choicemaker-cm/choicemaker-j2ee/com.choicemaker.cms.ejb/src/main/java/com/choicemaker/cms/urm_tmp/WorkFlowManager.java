package com.choicemaker.cms.urm_tmp;

import com.choicemaker.cm.batch.api.BatchProcessingNotification;

public interface WorkFlowManager {

	void jobUpdated(BatchProcessingNotification bpn);

}
