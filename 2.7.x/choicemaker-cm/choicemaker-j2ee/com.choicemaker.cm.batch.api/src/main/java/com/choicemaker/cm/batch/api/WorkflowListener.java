package com.choicemaker.cm.batch.api;

public interface WorkflowListener {

	void jobUpdated(BatchProcessingNotification bpn);

}
