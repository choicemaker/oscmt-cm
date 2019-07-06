package com.choicemaker.cm.batch.api;

/**
 * Manages persistent, operational properties.
 * 
 * @author rphall
 */
public interface OperationalPropertyController extends OperationalPropertyMonitoring {

	void setJobProperty(BatchJob job, String pn, String pv);

	OperationalProperty save(OperationalProperty property);

	void remove(OperationalProperty property);

	/** Returns a count of the number of properties deleted */
	int deleteOperationalPropertiesByJobId(long jobId);

}
